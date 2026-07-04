package com.cpic.barsms.bpm.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmDimCalendar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface BpmDimCalendarMapper extends BaseMapper<BpmDimCalendar> {

    List<BpmDimCalendar> selectByDateRange(@Param("startDate") Date startDate,
                                           @Param("endDateExclusive") Date endDateExclusive);
}
