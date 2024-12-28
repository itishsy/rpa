package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnRecipient;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-01 14:28:59
 */
public interface RobotWarnRecipientDao extends Mapper<RobotWarnRecipient>, MySqlMapper<RobotWarnRecipient> {
}
