package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.po.design.RobotOperationMessageConfig;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;


@Component
public interface RobotWarnMessageDao extends Mapper<RobotOperationMessageConfig>, MySqlMapper<RobotOperationMessageConfig>{
    /**
     * 分页查询消息列表
     *
     * @param map
     * @return
     */
    List<RobotOperationMessageConfig> findByMessage(Map<String, Object> map);
}
