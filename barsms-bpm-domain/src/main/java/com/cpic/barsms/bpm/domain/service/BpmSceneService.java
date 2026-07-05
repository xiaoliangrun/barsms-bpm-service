package com.cpic.barsms.bpm.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cpic.barsms.bpm.common.exception.BizBatchException;
import com.cpic.barsms.bpm.infra.model.entity.BpmScene;

public interface BpmSceneService extends IService<BpmScene> {

    Long selectIdByName(String sceneName);

    Long resolveSceneId(String versionName);
}
