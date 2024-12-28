package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnConfigBase;
import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnMsgList;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-01 14:29:37
 */
public interface RobotWarnMsgListDao extends Mapper<RobotWarnMsgList>, MySqlMapper<RobotWarnMsgList> {

    int getCountByParams(Map<String, Object> params);

    List<RobotWarnMsgList> getListByParams(Map<String, Object> params);
}
