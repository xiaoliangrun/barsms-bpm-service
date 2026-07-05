package com.cpic.barsms.bpm.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstanceOffice;

import java.util.Date;

public interface OfficeMappingService extends IService<BpmNodeInstanceOffice> {

    int generate(Date tDay, Date tDayBase, Long sceneId, String nodeCodePrefix, String versionName);

    void markLogicDelete(Date tDay, Long sceneId, String nodeCodePrefix);
}
