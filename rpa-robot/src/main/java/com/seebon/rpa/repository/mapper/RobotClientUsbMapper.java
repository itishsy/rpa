package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotClientUsb;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface RobotClientUsbMapper extends Mapper<RobotClientUsb> {

    /**
     * 清除数据
     *
     * @param machineCode
     * @return
     */
    void clean(@Param("machineCode") String machineCode);
}