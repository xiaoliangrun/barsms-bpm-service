package com.cpic.barsms.bpm.domain.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cpic.barsms.bpm.domain.service.ReferenceNodeService;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceMapper;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeTypeMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ReferenceNodeServiceImpl extends ServiceImpl<BpmNodeInstanceMapper, BpmNodeInstance> implements ReferenceNodeService {

    @Autowired
    private BpmNodeInstanceMapper bpmNodeInstanceMapper;
    @Autowired
    private BpmNodeTypeMapper bpmNodeTypeMapper;

    @Override
    public void updateReadOnlyStatus(Date tDay, Long sceneId, String nodeCodePrefix) {
        List<Long> readonlyTypeIds = bpmNodeTypeMapper.selectIdsByTypeKey("READONLY");

        if (readonlyTypeIds == null || readonlyTypeIds.isEmpty()) {
            log.warn("未找到READONLY类型的节点类型，跳过只读状态更新");
            return;
        }

        int updated = bpmNodeInstanceMapper.updateReadOnlyStatus(
                tDay, sceneId, nodeCodePrefix, readonlyTypeIds);
        log.info("只读状态更新 {} 条", updated);
    }

    @Override
    public void updateReferenceNodes(Date tDay, Long sceneId, String nodeCodePrefix) {
        int branchUpdated = bpmNodeInstanceMapper.updateBranchReference(tDay, sceneId, nodeCodePrefix);
        log.info("分公司引用节点更新 {} 条", branchUpdated);

        int centerUpdated = bpmNodeInstanceMapper.updateCenterReference(tDay, sceneId, nodeCodePrefix);
        log.info("中支引用节点更新 {} 条", centerUpdated);

        int subUpdated = bpmNodeInstanceMapper.updateSubReference(tDay, sceneId, nodeCodePrefix);
        log.info("支公司引用节点更新 {} 条", subUpdated);
    }
}
