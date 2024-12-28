package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotClientComponent;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface RobotClientComponentDao extends Mapper<RobotClientComponent> {
    /**
     * 根据robotCode查询机器人客户端组件
     *
     * @param robotCode
     * @return
     */
    RobotClientComponent selectClientComponentByRobotCode(@Param("robotCode") String robotCode, @Param("component") String component);

    /**
     * 修改机器人组件信息
     *
     * @param component
     * @return
     */
    int updateClientComponent(RobotClientComponent component);

    /**
     * 添加机器人信息、
     *
     * @param clientComponent
     * @return
     */
    int insertClientComponent(RobotClientComponent clientComponent);
}
