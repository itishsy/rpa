package com.seebon.rpa.repository.mysql;


import com.seebon.rpa.entity.robot.po.design.RobotWarnPerson;
import com.seebon.rpa.entity.robot.po.design.RobotWarnPersonType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Component
public interface RobotWarnPersonDao extends Mapper<RobotWarnPersonType>, MySqlMapper<RobotWarnPersonType>{
    List<RobotWarnPerson> getPersonInfo(@Param("personTypeId") Integer personTypeId);
}
