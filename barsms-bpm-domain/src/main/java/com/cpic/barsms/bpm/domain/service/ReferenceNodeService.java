package com.cpic.barsms.bpm.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstance;

import java.util.Date;

public interface ReferenceNodeService extends IService<BpmNodeInstance> {

    void updateReadOnlyStatus(Date tDay, Long sceneId, String nodeCodePrefix);

    void updateReferenceNodes(Date tDay, Long sceneId, String nodeCodePrefix);
}
