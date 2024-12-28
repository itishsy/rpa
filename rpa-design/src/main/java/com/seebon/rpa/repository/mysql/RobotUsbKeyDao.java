package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotUsbKey;
import com.seebon.rpa.entity.robot.vo.RobotUsbKeyVO;
import feign.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

public interface RobotUsbKeyDao extends Mapper<RobotUsbKey>, MySqlMapper<RobotUsbKey> {

    int countByParams(Map<String, Object> params);

    List<RobotUsbKeyVO> selectByParams(Map<String, Object> params);

    void deleteByUsbKeyId(@Param("usbKeyId") Integer usbKeyId);

    int insertUsbKey(RobotUsbKey record);
}
