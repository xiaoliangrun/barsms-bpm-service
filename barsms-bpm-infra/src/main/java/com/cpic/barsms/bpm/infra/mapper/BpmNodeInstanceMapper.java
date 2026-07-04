package com.cpic.barsms.bpm.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface BpmNodeInstanceMapper extends BaseMapper<BpmNodeInstance> {

    int batchInsert(@Param("list") List<BpmNodeInstance> list);

    int deleteByTargetMonth(@Param("tDay") Date tDay,
                            @Param("sceneId") Long sceneId,
                            @Param("nodeCodePrefix") String nodeCodePrefix);

    int updateReadOnlyStatus(@Param("tDay") Date tDay,
                             @Param("sceneId") Long sceneId,
                             @Param("nodeCodePrefix") String nodeCodePrefix,
                             @Param("readonlyTypeIds") List<Long> readonlyTypeIds);

    int updateBranchReference(@Param("tDay") Date tDay,
                              @Param("sceneId") Long sceneId,
                              @Param("nodeCodePrefix") String nodeCodePrefix);

    int updateCenterReference(@Param("tDay") Date tDay,
                              @Param("sceneId") Long sceneId,
                              @Param("nodeCodePrefix") String nodeCodePrefix);

    int updateSubReference(@Param("tDay") Date tDay,
                           @Param("sceneId") Long sceneId,
                           @Param("nodeCodePrefix") String nodeCodePrefix);
}
