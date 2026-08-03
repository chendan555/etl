package com.gx.etl.scheme.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gx.etl.flow.entity.Transformation;
import com.gx.etl.flow.mapper.TransformationMapper;
import com.gx.etl.scheme.entity.*;
import com.gx.etl.scheme.mapper.*;
import com.gx.etl.scheme.vo.SchemeEquipVO;
import com.gx.etl.scheme.vo.SchemeDetailDataVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SchemeService extends ServiceImpl<SchemeMapper, Scheme> {

    @Resource
    private SchemeDetailMapper schemeDetailMapper;

    @Resource
    private SchemeEquipMapper schemeEquipMapper;

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
     * 根据方案ID查询装备树（T_SCHEME_EQUIP）
     */
    public List<SchemeEquipVO> getSchemeEquipTree(String schemeId) {
        System.out.println("getSchemeEquipTree: schemeId=" + schemeId);
        List<SchemeEquip> allEquips = schemeEquipMapper.selectList(
                new LambdaQueryWrapper<SchemeEquip>()
                        .eq(SchemeEquip::getSchemeId, schemeId)
        );
        System.out.println("getSchemeEquipTree: found " + allEquips.size() + " equips");
        return buildEquipTree(allEquips, null);
    }

    /**
     * 构建装备树（使用 id 和 pid 构建，非递归方式）
     * 根节点：pid 为空或 "0"
     * 子节点：pid 等于父节点的 id
     */
    private List<SchemeEquipVO> buildEquipTree(List<SchemeEquip> allEquips, String parentEquipId) {
        // 将所有节点转换为 VO，并建立 id -> VO 的映射
        Map<String, SchemeEquipVO> voMap = new LinkedHashMap<>();
        for (SchemeEquip equip : allEquips) {
            SchemeEquipVO vo = new SchemeEquipVO();
            vo.setId(equip.getId());
            vo.setEquipId(equip.getEquipId());
            vo.setEquipName(equip.getEquipName());
            vo.setPid(equip.getPid());
            vo.setChildren(new ArrayList<>());
            voMap.put(equip.getId().toString(), vo);
        }

        List<SchemeEquipVO> roots = new ArrayList<>();
        for (SchemeEquipVO vo : voMap.values()) {
            String pid = vo.getPid();
            if (pid == null || pid.isEmpty() || "0".equals(pid)) {
                // 根节点
                roots.add(vo);
            } else {
                // 子节点，找到父节点并添加
                SchemeEquipVO parent = voMap.get(pid);
                if (parent != null) {
                    parent.getChildren().add(vo);
                }
            }
        }
        return roots;
    }

    /**
     * 获取方案详细数据（红蓝方/装备/信息分类）。装备树为固定数据，方案头部取自数据库。
     * 方案不存在时返回 null，由 Controller 返回失败。
     * @param schemeId 方案ID
     * @param ybList 逗号分隔的 SCHEME_ID 列表，用于 IN 查询过滤
     * @param equipId 装备ID，用于过滤 TABLE 数据
     */
    public SchemeDetailDataVO getSchemeDetailData(Long schemeId, String ybList, String equipId) {
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
        vo.setSides(schemeDetailDataBuilder.buildFixedSides(ybList, equipId));
        return vo;
    }
}
