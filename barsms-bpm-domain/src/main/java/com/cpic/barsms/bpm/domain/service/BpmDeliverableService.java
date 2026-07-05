package com.cpic.barsms.bpm.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cpic.barsms.bpm.infra.model.entity.BpmDeliverable;

import java.util.Date;

public interface BpmDeliverableService extends IService<BpmDeliverable> {

    int generate(Date tDay, Date tDayBase, String versionName, Long sceneId, String nodeCodePrefix);
}
