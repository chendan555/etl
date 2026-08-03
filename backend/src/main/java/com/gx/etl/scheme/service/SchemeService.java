package com.gx.etl.scheme.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.flow.entity.Transformation;
import com.gx.etl.flow.mapper.TransformationMapper;
import com.gx.etl.scheme.entity.*;
import com.gx.etl.scheme.mapper.*;
import com.gx.etl.scheme.vo.SchemeDetailDataVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchemeService extends ServiceImpl<SchemeMapper, Scheme> {

    @Resource
    private SchemeDetailMapper schemeDetailMapper;

    @Resource
    private CjConfigMapper cjConfigMapper;

    @Resource
    private TransformationMapper transformationMapper;

    @Resource
    private SchemeDetailDataBuilder schemeDetailDataBuilder;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ========== 主题配置 ==========

    @Resource
    private TopicConfigMapper topicConfigMapper;

    public List<TopicConfig> getAllTopics() {
        return topicConfigMapper.selectList(null);
    }

    // ========== 数据采集配置 ==========

    public List<CjConfig> getAllCjConfigs() {
        return cjConfigMapper.selectList(null);
    }

    // ========== 仿真方案 CRUD ==========

    public Page<Scheme> getSchemePage(Integer pageIndex, Integer pageSize, String schemeName) {
        Page<Scheme> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<Scheme> wrapper = new LambdaQueryWrapper<>();
        if (schemeName != null && !schemeName.isEmpty()) {
            wrapper.like(Scheme::getSchemeName, schemeName);
        }
        wrapper.orderByDesc(Scheme::getCreateTime);
        page = page(page, wrapper);

        // 填充数据采集配置名称和处理流程名称
        List<CjConfig> allCj = cjConfigMapper.selectList(null);
        List<Transformation> allTrans = transformationMapper.selectList(null);
        for (Scheme scheme : page.getRecords()) {
            if (scheme.getCjListId() != null) {
                allCj.stream()
                        .filter(cj -> cj.getId().equals(scheme.getCjListId()))
                        .findFirst()
                        .ifPresent(cj -> scheme.setCjName(cj.getYbName()));
            }
            if (scheme.getTransId() != null) {
                allTrans.stream()
                        .filter(t -> t.getId().equals(scheme.getTransId()))
                        .findFirst()
                        .ifPresent(t -> scheme.setTransName(t.getName()));
            }
        }
        return page;
    }

    public Scheme getSchemeById(Long id) {
        Scheme scheme = getById(id);
        if (scheme == null) return null;

        // 查子表
        List<SchemeDetail> details = schemeDetailMapper.selectList(
                new LambdaQueryWrapper<SchemeDetail>().eq(SchemeDetail::getSchemeId, id)
        );
        scheme.setDetailList(details);

        // 填充cjName
        if (scheme.getCjListId() != null) {
            CjConfig cj = cjConfigMapper.selectById(scheme.getCjListId());
            if (cj != null) scheme.setCjName(cj.getYbName());
        }
        // 填充transName
        if (scheme.getTransId() != null) {
            Transformation trans = transformationMapper.selectById(scheme.getTransId());
            if (trans != null) scheme.setTransName(trans.getName());
        }
        return scheme;
    }

    @Transactional
    public void saveSchemeAndDetail(Scheme scheme) {
        // 保存主表
        saveOrUpdate(scheme);

        // 保存子表
        if (scheme.getDetailList() != null) {
            // 先删除旧的
            schemeDetailMapper.delete(
                    new LambdaQueryWrapper<SchemeDetail>().eq(SchemeDetail::getSchemeId, scheme.getId())
            );
            // 再插入新的
            for (SchemeDetail detail : scheme.getDetailList()) {
                detail.setId(null);
                detail.setSchemeId(scheme.getId());
                detail.setStatus(0);
                schemeDetailMapper.insert(detail);
            }
        }
    }

    @Transactional
    public void deleteSchemeAndDetail(Long id) {
        // 删子表
        schemeDetailMapper.delete(
                new LambdaQueryWrapper<SchemeDetail>().eq(SchemeDetail::getSchemeId, id)
        );
        // 删主表
        removeById(id);
    }

    // ========== 方案详细数据 ==========

    /**
     * 根据方案ID查询方案明细列表（T_SCHEME_DETAIL）
     */
    public List<SchemeDetail> getSchemeDetails(Long schemeId) {
        return schemeDetailMapper.selectList(
                new LambdaQueryWrapper<SchemeDetail>()
                        .eq(SchemeDetail::getSchemeId, schemeId)
                        .orderByAsc(SchemeDetail::getCreateTime)
        );
    }

    /**
     * 获取方案详细数据（红蓝方/装备/4类信息）。装备树为固定数据，方案头部取自数据库。
     * 方案不存在时返回 null，由 Controller 返回失败。
     */
    public SchemeDetailDataVO getSchemeDetailData(Long schemeId) {
        Scheme scheme = getById(schemeId);
        if (scheme == null) {
            return null;
        }
        SchemeDetailDataVO vo = new SchemeDetailDataVO();
        vo.setSchemeId(scheme.getId());
        vo.setSchemeName(scheme.getSchemeName());
        vo.setStatus(scheme.getStatus() == null ? 0 : scheme.getStatus());
        vo.setCreateTime(scheme.getCreateTime() != null
                ? scheme.getCreateTime().format(DATE_FORMATTER)
                : null);
        vo.setSides(schemeDetailDataBuilder.buildFixedSides());
        return vo;
    }
}
