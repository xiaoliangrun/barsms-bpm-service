package com.cpic.barsms.bpm.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstance;

import java.util.Date;

public interface BpmNodeInstanceService extends IService<BpmNodeInstance> {

    int deleteByTargetMonth(Date tDay, Long sceneId, String nodeCodePrefix);

    int countByTargetMonth(Date tDay, Long sceneId, String nodeCodePrefix);
}
