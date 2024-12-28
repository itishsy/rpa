package com.seebon.rpa.repository.mapper;

import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.entity.robot.vo.RobotFlowVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

public interface RobotFlowMapper extends Mapper<RobotFlow>, MySqlMapper<RobotFlow> {

    /**
     * 根据taskCode查询flow
     *
     * @param params 任务编码
     * @return flowDTO集合
     */
    List<RobotFlowVO> findFlows(Map<String, Object> params);
}
