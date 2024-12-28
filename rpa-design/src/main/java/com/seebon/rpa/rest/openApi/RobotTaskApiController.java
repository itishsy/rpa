package com.seebon.rpa.rest.openApi;

import com.seebon.rpa.entity.robot.dto.ServiceDataStatisticsDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionDetailMo;
import com.seebon.rpa.entity.robot.dto.design.RobotTaskRunVO;
import com.seebon.rpa.entity.robot.vo.design.RobotTaskNumVO;
import com.seebon.rpa.entity.saas.dto.register.CityRegisterDTO;
import com.seebon.rpa.entity.saas.dto.register.RegisterStatisticsGroupDTO;
import com.seebon.rpa.entity.saas.vo.declare.customer.CustomerMapInfoVO;
import com.seebon.rpa.service.RobotOpenApiTaskService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/openApi/task")
public class RobotTaskApiController {
    @Autowired
    private RobotOpenApiTaskService robotOpenApiTaskService;

    @ApiOperation(value = "执行任务")
    @PostMapping("/run")
    public Integer taskRun(@RequestBody RobotTaskRunVO runVO) {
        return robotOpenApiTaskService.taskRun(runVO);
    }

    @ApiOperation(value = "获取执行状态")
    @PostMapping("/getRunStatus")
    public String getRunStatus(@RequestBody RobotTaskRunVO runVO) {
        return robotOpenApiTaskService.getRunStatus(runVO);
    }

    @ApiOperation(value = "获取执行名称")
    @PostMapping("/getRunDetail")
    public List<RobotExecutionDetailMo> getRunDetail(@RequestBody RobotTaskRunVO runVO) {
        return robotOpenApiTaskService.getRunDetail(runVO);
    }

    @ApiOperation(value = "获取机器人数量")
    @PostMapping("/getRobot")
    public List<RobotTaskNumVO> getRobot(@RequestBody RobotTaskRunVO runVO) {
        return robotOpenApiTaskService.getRobot(runVO);
    }

    @ApiOperation(value = "驾驶仓-大地图")
    @PostMapping("/getRobotMapData")
    public List<RobotTaskNumVO> getRobotMapData(@RequestBody RobotTaskRunVO runVO) {
        return robotOpenApiTaskService.getRobotMapData(runVO);
    }

    @ApiOperation(value = "驾驶仓-机器人数据概括")
    @PostMapping("/statistics")
    public ServiceDataStatisticsDTO statistics() {
        return robotOpenApiTaskService.statistics();
    }

    @ApiOperation(value = "驾驶仓-在职人数解析数据量")
    @PostMapping("/registerStatistics")
    public List<RegisterStatisticsGroupDTO> registerStatistics() {
        return robotOpenApiTaskService.registerStatistics();
    }

    @ApiOperation(value = "驾驶仓-城市社保公积金数据量")
    @PostMapping("/cityRegister")
    public List<CityRegisterDTO> cityRegister() {
        return robotOpenApiTaskService.cityRegister();
    }

    @ApiOperation(value = "驾驶仓-获取标题，中心城市信息")
    @PostMapping("/mapInfo")
    public CustomerMapInfoVO mapInfo() {
        return robotOpenApiTaskService.mapInfo();
    }
}
