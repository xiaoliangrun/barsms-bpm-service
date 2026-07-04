package com.cpic.barsms.bpm.domain.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cpic.barsms.bpm.common.enums.OrgLevelEnum;
import com.cpic.barsms.bpm.common.utils.StringSplitter;
import com.cpic.barsms.bpm.domain.service.DeliverableService;
import com.cpic.barsms.bpm.infra.mapper.BpmDeliverableMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceFormatMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmDeliverable;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstance;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstanceFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeliverableServiceImpl implements DeliverableService {

    @Autowired
    private BpmNodeInstanceMapper bpmNodeInstanceMapper;
    @Autowired
    private BpmNodeInstanceFormatMapper bpmNodeInstanceFormatMapper;
    @Autowired
    private BpmDeliverableMapper bpmDeliverableMapper;

    @Override
    public int generate(Date tDay, Date tDayBase, String versionName, Long sceneId, String nodeCodePrefix) {
        List<BpmNodeInstance> instances = bpmNodeInstanceMapper.selectList(
                new QueryWrapper<BpmNodeInstance>()
                        .eq("T_DAY", tDay)
                        .eq("SCENE_ID", sceneId)
                        .like(StringUtils.hasText(nodeCodePrefix),
                                "node_code", nodeCodePrefix + "%")
        );

        if (instances.isEmpty()) {
            return 0;
        }

        List<BpmNodeInstanceFormat> templates = new ArrayList<>();
        templates.addAll(bpmNodeInstanceFormatMapper.selectTemplates(tDayBase, versionName, null, "%" + OrgLevelEnum.ROOT_COMPANY.getFullName() + "%", nodeCodePrefix));
        templates.addAll(bpmNodeInstanceFormatMapper.selectTemplates(tDayBase, versionName, null, "%" + OrgLevelEnum.BRANCH_COMPANY.getFullName() + "%", nodeCodePrefix));
        templates.addAll(bpmNodeInstanceFormatMapper.selectTemplates(tDayBase, versionName, null, "%" + OrgLevelEnum.CENTER_BRANCH_COMPANY.getFullName() + "%", nodeCodePrefix));
        templates.addAll(bpmNodeInstanceFormatMapper.selectTemplates(tDayBase, versionName, null, "%" + OrgLevelEnum.SUB_BRANCH_COMPANY.getFullName() + "%", nodeCodePrefix));

        Map<String, BpmNodeInstanceFormat> templateMap = templates.stream()
                .collect(Collectors.toMap(
                        BpmNodeInstanceFormat::getNodeCode, t -> t, (a, b) -> a));

        List<BpmDeliverable> deliverables = new ArrayList<>();
        for (BpmNodeInstance instance : instances) {
            BpmNodeInstanceFormat template = templateMap.get(instance.getNodeCode());
            if (template == null) continue;

            int levelIndex = getLevelIndex(instance.getDataOrgLevel());
            String deliveryStr = StringSplitter.splitByPipe(template.getDeliveryInfo(), levelIndex);
            List<String> deliveryNames = StringSplitter.splitByComma(deliveryStr);

            for (String name : deliveryNames) {
                if (!StringUtils.hasText(name)) continue;
                BpmDeliverable d = new BpmDeliverable();
                d.setNodeInstanceId(instance.getId());
                d.setDeliverableName(name);
                d.setCreateBy("ADM");
                d.setCreateTime(new Date());
                d.setUpdateTime(new Date());
                d.setDeleteFlag("0");
                deliverables.add(d);
            }
        }

        if (!deliverables.isEmpty()) {
            bpmDeliverableMapper.batchInsert(deliverables);
            log.info("交付物生成 {} 条", deliverables.size());
        }

        return deliverables.size();
    }

    private int getLevelIndex(String shortName) {
        if ("总".equals(shortName)) return 0;
        if ("分".equals(shortName)) return 1;
        if ("中".equals(shortName)) return 2;
        return 3;
    }
}
