package com.cpic.barsms.bpm.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmBatchExecLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface BpmBatchExecLogMapper extends BaseMapper<BpmBatchExecLog> {

    boolean existsSuccess(@Param("batchDate") Date batchDate,
                          @Param("versionName") String versionName,
                          @Param("nodeCode") String nodeCode);
}
