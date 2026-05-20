package com.gx.etl.scheme.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gx.etl.scheme.entity.Scheme;
import com.gx.etl.scheme.entity.SchemeDetail;
import com.gx.etl.scheme.mapper.SchemeDetailMapper;
import com.gx.etl.scheme.mapper.SchemeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SchemeProcessService {

    private static final Logger log = LoggerFactory.getLogger(SchemeProcessService.class);

    @Resource
    private SchemeDetailMapper schemeDetailMapper;

    @Resource
    private SchemeMapper schemeMapper;

    /** 进度缓存：schemeId -> ProcessProgress */
    private final Map<Long, ProcessProgress> progressMap = new ConcurrentHashMap<>();

    public static class ProcessProgress {
        private final int total;
        private final AtomicInteger completed = new AtomicInteger(0);
        private volatile boolean finished = false;
        private volatile boolean running = false;

        public ProcessProgress(int total) {
            this.total = total;
        }

        public int getTotal() { return total; }
        public int getCompleted() { return completed.get(); }
        public boolean isFinished() { return finished; }
        public boolean isRunning() { return running; }

        public void setRunning(boolean running) { this.running = running; }
        public void setFinished(boolean finished) { this.finished = finished; }
        public int incrementAndGet() { return completed.incrementAndGet(); }
    }

    /**
     * 获取进度
     */
    public ProcessProgress getProgress(Long schemeId) {
        return progressMap.get(schemeId);
    }

    @Value("${batch.execution.parallel-size:5}")
    private int poolSize;

    /**
     * 初始化进度（同步，调用异步方法前执行）
     */
    public void initProgress(Long schemeId) {
        long count = schemeDetailMapper.selectCount(
                new LambdaQueryWrapper<SchemeDetail>()
                        .eq(SchemeDetail::getSchemeId, schemeId)
                        .eq(SchemeDetail::getStatus, 0)
        );
        log.info("initProgress: schemeId={}, count={}", schemeId, count);
        ProcessProgress progress = new ProcessProgress((int) count);
        progress.setRunning(true);
        progressMap.put(schemeId, progress);
    }

    /**
     * 异步执行引接处理（多线程并发）
     */
    @Async
    public void processScheme(Long schemeId) {
        List<SchemeDetail> details = schemeDetailMapper.selectList(
                new LambdaQueryWrapper<SchemeDetail>()
                        .eq(SchemeDetail::getSchemeId, schemeId)
                        .eq(SchemeDetail::getStatus, 0)
        );

        log.info("processScheme: schemeId={}, details.size={}", schemeId, details.size());

        if (details.isEmpty()) {
            return;
        }

        ProcessProgress progress = progressMap.get(schemeId);
        log.info("processScheme: progress={}", progress);

        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        CountDownLatch latch = new CountDownLatch(details.size());

        try {
            for (SchemeDetail detail : details) {
                executor.submit(() -> {
                    try {
                        log.info("开始处理样本: id={}, ybName={}", detail.getId(), detail.getYbName());
                        Thread.sleep(2000);
                        log.info("完成处理样本: id={}, ybName={}", detail.getId(), detail.getYbName());

                        detail.setStatus(1);
                        schemeDetailMapper.updateById(detail);
                    } catch (Exception e) {
                        log.error("处理样本失败: id={}", detail.getId(), e);
                    } finally {
                        progress.incrementAndGet();
                        latch.countDown();
                    }
                });
            }

            // 等待所有任务完成
            latch.await();

            // 所有子表完成后，更新主表状态
            boolean allDone = schemeDetailMapper.selectCount(
                    new LambdaQueryWrapper<SchemeDetail>()
                            .eq(SchemeDetail::getSchemeId, schemeId)
                            .eq(SchemeDetail::getStatus, 0)
            ) == 0;

            if (allDone) {
                Scheme scheme = schemeMapper.selectById(schemeId);
                if (scheme != null) {
                    scheme.setStatus(1);
                    schemeMapper.updateById(scheme);
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("引接处理被中断: schemeId={}", schemeId, e);
        } finally {
            executor.shutdown();
            progress.setFinished(true);
            progress.setRunning(false);
        }
    }
}
