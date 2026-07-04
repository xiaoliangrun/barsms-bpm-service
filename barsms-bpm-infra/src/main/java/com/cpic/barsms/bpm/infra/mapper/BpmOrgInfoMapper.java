package com.cpic.barsms.bpm.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cpic.barsms.bpm.infra.dto.OrgRelationDTO;
import com.cpic.barsms.bpm.infra.model.entity.BpmOrgInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BpmOrgInfoMapper extends BaseMapper<BpmOrgInfo> {

    List<BpmOrgInfo> selectBranchList();

    List<OrgRelationDTO> selectCenterWithBranch(
            @Param("excludeCodes") List<String> excludeCodes);

    List<OrgRelationDTO> selectSubWithCenterAndBranch();
}
