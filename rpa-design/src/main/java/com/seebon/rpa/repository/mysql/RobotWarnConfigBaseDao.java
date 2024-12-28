package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.po.robotWarn.RobotWarnConfigBase;
import com.seebon.rpa.entity.robot.vo.robotWarn.RobotWarnConfigBaseVO;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-01 14:28:18
 */
public interface RobotWarnConfigBaseDao extends Mapper<RobotWarnConfigBase>, MySqlMapper<RobotWarnConfigBase> {

    int selectCountByParams(Map<String, Object> params);

    List<RobotWarnConfigBaseVO> selectListByParams(Map<String, Object> params);

}
