package com.seebon.rpa.rest.monitor;

import com.seebon.rpa.entity.robot.dto.design.MonitorCountVo;
import com.seebon.rpa.entity.robot.dto.design.TaskCountVo;
import com.seebon.rpa.service.MonitorStationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * TODO
 *
 * @author zjf
 * @describe 监控台数据统计
 * @date 2023-07-24 15:46
 */
@Api(value = "监控台", tags = "监控台")
@RestController
@RequestMapping("/monitor/station")
public class MonitorCountController{

    @Autowired
    private MonitorStationService monitorStationService;
    @PostMapping("/count")
    @ApiOperation(value = "监控台-数据统计")
    public MonitorCountVo findMonitorCount() {
        return monitorStationService.getMonitorCount();
    }
    @PostMapping("/getCity")
    @ApiOperation(value = "监控台-获取已开通城市")
    public Map getCity() {
        return monitorStationService.getCity();
    }

    @PostMapping("/getTaskCount")
    @ApiOperation(value = "监控台-获取任务统计")
    public TaskCountVo getTaskCount() {
        return monitorStationService.getTaskCount();
    }
}
