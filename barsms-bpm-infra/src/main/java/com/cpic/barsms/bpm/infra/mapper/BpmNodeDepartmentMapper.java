package com.cpic.barsms.bpm.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cpic.barsms.bpm.infra.model.entity.BpmNodeDepartment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BpmNodeDepartmentMapper extends BaseMapper<BpmNodeDepartment> {

    String selectDeptCode(@Param("deptName") String deptName,
                          @Param("orgLevel") String orgLevel,
                          @Param("intJobBranch") String intJobBranch,
                          @Param("intJobCenBranch") String intJobCenBranch,
                          @Param("intJobBusiBranch") String intJobBusiBranch);
}
