package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.CustomerCommand;
import com.seebon.rpa.entity.robot.vo.CustomerCommandVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CustomerCommandDao extends Mapper<CustomerCommand> {

    List<CustomerCommandVO> getRobotCommandExeList(@Param("clientId") Integer clientId);

    void updateByUuid(CustomerCommand command);
}
