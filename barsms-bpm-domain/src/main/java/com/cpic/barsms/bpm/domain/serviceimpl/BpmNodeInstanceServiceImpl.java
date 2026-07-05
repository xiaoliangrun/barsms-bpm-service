package com.cpic.barsms.bpm.domain.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cpic.barsms.bpm.domain.service.BpmNodeInstanceService;
import com.cpic.barsms.bpm.infra.mapper.BpmNodeInstanceMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class BpmNodeInstanceServiceImpl extends ServiceImpl<BpmNodeInstanceMapper, BpmNodeInstance> implements BpmNodeInstanceService {

    @Autowired
    private BpmNodeInstanceMapper bpmNodeInstanceMapper;

    @Override
    public int deleteByTargetMonth(Date tDay, Long sceneId, String nodeCodePrefix) {
        return bpmNodeInstanceMapper.deleteByTargetMonth(tDay, sceneId, nodeCodePrefix);
    }

    @Override
    public int countByTargetMonth(Date tDay, Long sceneId, String nodeCodePrefix) {
        return bpmNodeInstanceMapper.selectCount(
                new QueryWrapper<BpmNodeInstance>()
                        .eq("T_DAY", tDay)
                        .eq("SCENE_ID", sceneId)
                        .like(nodeCodePrefix != null && !nodeCodePrefix.isEmpty(),
                                "node_code", nodeCodePrefix + "%")
        ).intValue();
    }
}
