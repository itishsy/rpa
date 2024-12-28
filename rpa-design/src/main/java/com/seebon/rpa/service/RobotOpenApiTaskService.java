package com.seebon.rpa.service;


import com.seebon.rpa.entity.robot.dto.ServiceDataStatisticsDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionDetailMo;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskRunVO;
import com.seebon.rpa.entity.robot.vo.design.RobotTaskNumVO;
import com.seebon.rpa.entity.saas.dto.register.CityRegisterDTO;
import com.seebon.rpa.entity.saas.dto.register.RegisterStatisticsGroupDTO;
import com.seebon.rpa.entity.saas.vo.declare.customer.CustomerMapInfoVO;

import java.util.List;

/**
 * @author LY
 * @date 2023/4/3 13:39
 */
public interface RobotOpenApiTaskService {
    /**
     * 运行任务
     *
     * @param runVO
     * @return
     */
    Integer taskRun(RobotTaskRunVO runVO);

    /**
     * 获取运行状态
     *
     * @param runVO
     * @return
     */
    String getRunStatus(RobotTaskRunVO runVO);

    /**
     * 获取执行明细
     *
     * @param runVO
     * @return
     */
    List<RobotExecutionDetailMo> getRunDetail(RobotTaskRunVO runVO);

    /**
     * 获取机器人
     *
     * @param runVO
     * @return
     */
    List<RobotTaskNumVO> getRobot(RobotTaskRunVO runVO);

    List<RobotTaskNumVO> getRobotMapData(RobotTaskRunVO runVO);

    ServiceDataStatisticsDTO statistics();

    List<RegisterStatisticsGroupDTO> registerStatistics();

    List<CityRegisterDTO> cityRegister();

    CustomerMapInfoVO mapInfo();
}
