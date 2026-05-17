package com.gx.etl.flow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.flow.entity.PlanCategory;
import com.gx.etl.flow.entity.PlanDetail;
import com.gx.etl.flow.entity.TransPlan;
import com.gx.etl.flow.mapper.PlanCategoryMapper;
import com.gx.etl.flow.mapper.PlanDetailMapper;
import com.gx.etl.flow.mapper.TransPlanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlanService extends ServiceImpl<TransPlanMapper, TransPlan> {

    @Resource
    private PlanDetailMapper planDetailMapper;

    @Resource
    private PlanCategoryMapper planCategoryMapper;

    /**
     * 方案分页查询
     */
    public Page<TransPlan> getPlanPage(int pageIndex, int pageSize, Long groupId, String name, Integer flag) {
        Page<TransPlan> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<TransPlan> wrapper = new LambdaQueryWrapper<>();
        if (groupId != null) {
            wrapper.eq(TransPlan::getGroupId, groupId);
        }
        if (name != null && !name.isEmpty()) {
            wrapper.like(TransPlan::getName, name);
        }
        if (flag != null) {
            wrapper.eq(TransPlan::getFlag, flag);
        }
        wrapper.orderByDesc(TransPlan::getCreateTime);

        Page<TransPlan> result = page(page, wrapper);

        // 转换日期格式和状态信息
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (TransPlan plan : result.getRecords()) {
            if (plan.getCreateTime() != null) {
                plan.setCreatedDate(plan.getCreateTime().format(formatter));
            }
            plan.setStatusInfo("1".equals(plan.getStatus()) ? "未执行" : "已执行");
        }

        return result;
    }

    /**
     * 保存方案（含详情）
     */
    @Transactional
    public void savePlanAndDetail(TransPlan plan) {
        LocalDateTime now = LocalDateTime.now();

        if (plan.getId() != null && plan.getId() > 0) {
            // 修改方案
            plan.setUpdateTime(now);
            updateById(plan);
            // 删除旧详情
            planDetailMapper.delete(new LambdaQueryWrapper<PlanDetail>()
                    .eq(PlanDetail::getPlanId, plan.getId()));
        } else {
            // 新建方案
            plan.setCreateTime(now);
            plan.setUpdateTime(now);
            plan.setStatus("1");
            plan.setFlag(0);
            save(plan);
        }

        // 保存处理流程列表
        if (plan.getClList() != null) {
            for (int i = 0; i < plan.getClList().size(); i++) {
                PlanDetail detail = plan.getClList().get(i);
                detail.setPlanId(plan.getId());
                detail.setDetailType("cl");
                detail.setSortOrder(i);
                detail.setCreateTime(now);
                detail.setUpdateTime(now);
                planDetailMapper.insert(detail);
            }
        }

        // 保存引接列表
        if (plan.getYjList() != null) {
            for (int i = 0; i < plan.getYjList().size(); i++) {
                PlanDetail detail = plan.getYjList().get(i);
                detail.setPlanId(plan.getId());
                detail.setDetailType("yj");
                detail.setSortOrder(i);
                detail.setCreateTime(now);
                detail.setUpdateTime(now);
                planDetailMapper.insert(detail);
            }
        }
    }

    /**
     * 删除方案（含详情）
     */
    @Transactional
    public void deletePlanAndDetail(Long planId) {
        // 删除详情
        planDetailMapper.delete(new LambdaQueryWrapper<PlanDetail>()
                .eq(PlanDetail::getPlanId, planId));
        // 删除方案
        removeById(planId);
    }

    /**
     * 根据ID获取方案详情（含关联的流程和引接）
     */
    public TransPlan getPlanById(Long planId) {
        TransPlan plan = getById(planId);
        if (plan == null) {
            return null;
        }

        // 获取关联的详情
        List<PlanDetail> details = planDetailMapper.selectList(
                new LambdaQueryWrapper<PlanDetail>()
                        .eq(PlanDetail::getPlanId, planId)
                        .orderByAsc(PlanDetail::getSortOrder));

        // 分离处理流程和引接
        plan.setClList(details.stream()
                .filter(d -> "cl".equals(d.getDetailType()))
                .collect(Collectors.toList()));
        plan.setYjList(details.stream()
                .filter(d -> "yj".equals(d.getDetailType()))
                .collect(Collectors.toList()));

        return plan;
    }

    /**
     * 启动方案执行
     */
    public void startPlan(Long planId) {
        TransPlan plan = getById(planId);
        if (plan != null) {
            plan.setStatus("2");
            plan.setUpdateTime(LocalDateTime.now());
            updateById(plan);
        }
    }

    /**
     * 停止方案执行
     */
    public void stopPlan(Long planId) {
        TransPlan plan = getById(planId);
        if (plan != null) {
            plan.setStatus("1");
            plan.setUpdateTime(LocalDateTime.now());
            updateById(plan);
        }
    }

    /**
     * 获取已执行方案列表
     */
    public List<TransPlan> getRunList() {
        List<TransPlan> plans = list(new LambdaQueryWrapper<TransPlan>()
                .eq(TransPlan::getStatus, "2")
                .orderByDesc(TransPlan::getUpdateTime));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (TransPlan plan : plans) {
            if (plan.getCreateTime() != null) {
                plan.setCreatedDate(plan.getCreateTime().format(formatter));
            }
            plan.setStatusInfo("已执行");
        }

        return plans;
    }

    // ========== 方案分组相关 ==========

    /**
     * 获取方案分组树
     */
    public List<PlanCategory> getCategoryTree() {
        List<PlanCategory> allCategories = planCategoryMapper.selectList(
                new LambdaQueryWrapper<PlanCategory>()
                        .orderByAsc(PlanCategory::getSortOrder));
        return buildCategoryTree(allCategories, -1L);
    }

    private List<PlanCategory> buildCategoryTree(List<PlanCategory> allCategories, Long parentId) {
        return allCategories.stream()
                .filter(c -> parentId.equals(c.getParentId()))
                .peek(c -> c.setChildren(buildCategoryTree(allCategories, c.getId())))
                .collect(Collectors.toList());
    }

    /**
     * 保存方案分组
     */
    public void saveCategory(PlanCategory category) {
        if (category.getId() != null && category.getId() > 0) {
            category.setUpdateTime(LocalDateTime.now());
            planCategoryMapper.updateById(category);
        } else {
            category.setCreateTime(LocalDateTime.now());
            category.setUpdateTime(LocalDateTime.now());
            if (category.getParentId() == null) {
                category.setParentId(-1L);
            }
            planCategoryMapper.insert(category);
        }
    }

    /**
     * 删除方案分组
     */
    public void deleteCategory(Long id) {
        planCategoryMapper.deleteById(id);
    }
}
