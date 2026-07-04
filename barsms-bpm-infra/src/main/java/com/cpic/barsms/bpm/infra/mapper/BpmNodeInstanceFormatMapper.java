package com.cpic.barsms.bpm.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cpic.barsms.bpm.common.enums.ProcDateTypeEnum;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeInstanceFormat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface BpmNodeInstanceFormatMapper extends BaseMapper<BpmNodeInstanceFormat> {

    List<BpmNodeInstanceFormat> selectTemplates(
            @Param("tDay") Date tDay,
            @Param("versionName") String versionName,
            @Param("procDateType") ProcDateTypeEnum procDateType,
            @Param("orgLevelLike") String orgLevelLike,
            @Param("nodeCodePrefix") String nodeCodePrefix);
}
