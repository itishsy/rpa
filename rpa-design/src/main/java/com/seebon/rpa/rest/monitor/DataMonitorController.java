package com.seebon.rpa.rest.monitor;

import com.seebon.rpa.entity.robot.dto.monitor.TodayExecDTO;
import com.seebon.rpa.entity.robot.dto.monitor.TodayExecQueryDTO;
import com.seebon.rpa.service.DataMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "数据监控", tags = "数据监控")
@RequestMapping("/data/monitor")
@RestController
public class DataMonitorController {

    @Autowired
    private DataMonitorService dataMonitorService;

    @ApiOperation("数据监控-今日执行")
    @PostMapping("today/exec/list")
    public TodayExecDTO todayExecList(@ApiParam(value = "查询条件信息", name = "dto", required = true) @RequestBody TodayExecQueryDTO dto) {
        return dataMonitorService.getTodayExecList(dto);
    }
}
