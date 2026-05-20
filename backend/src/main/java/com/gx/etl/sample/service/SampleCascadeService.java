package com.gx.etl.sample.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.sample.entity.SampleData;
import com.gx.etl.sample.entity.SampleDetail;
import com.gx.etl.sample.entity.SamplePlan;
import com.gx.etl.sample.entity.SampleSubData;
import com.gx.etl.sample.mapper.SampleDataMapper;
import com.gx.etl.sample.mapper.SampleDetailMapper;
import com.gx.etl.sample.mapper.SamplePlanMapper;
import com.gx.etl.sample.mapper.SampleSubDataMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SampleCascadeService extends ServiceImpl<SamplePlanMapper, SamplePlan> {

    @Resource
    private SampleDataMapper sampleDataMapper;

    @Resource
    private SampleSubDataMapper sampleSubDataMapper;

    @Resource
    private SampleDetailMapper sampleDetailMapper;

    /**
     * 获取所有样本方案（一级）
     */
    public List<SamplePlan> getAllPlans() {
        return list();
    }

    /**
     * 根据方案ID获取样本数据（二级）
     */
    public List<SampleData> getDataByPlanId(Long planId) {
        return sampleDataMapper.selectList(
                new LambdaQueryWrapper<SampleData>().eq(SampleData::getPlanId, planId)
        );
    }

    /**
     * 根据样本数据ID获取样本子数据（三级）
     */
    public List<SampleSubData> getSubDataByDataId(Long dataId) {
        return sampleSubDataMapper.selectList(
                new LambdaQueryWrapper<SampleSubData>().eq(SampleSubData::getDataId, dataId)
        );
    }

    /**
     * 根据样本子数据ID获取样本明细（四级）
     */
    public List<SampleDetail> getDetailBySubDataId(Long subDataId) {
        return sampleDetailMapper.selectList(
                new LambdaQueryWrapper<SampleDetail>().eq(SampleDetail::getSubDataId, subDataId)
        );
    }
}
