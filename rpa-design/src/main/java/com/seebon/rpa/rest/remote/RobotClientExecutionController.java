package com.seebon.rpa.rest.remote;

import com.seebon.rpa.entity.robot.RobotExecutionData;
import com.seebon.rpa.entity.robot.RobotExecutionMonitor;
import com.seebon.rpa.entity.robot.RobotExecutionVoucher;
import com.seebon.rpa.entity.robot.dto.RobotExecutionDTO;
import com.seebon.rpa.schedule.RobotCleanDataTask;
import com.seebon.rpa.service.RobotExecutionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(value = "机器人执行记录同步", tags = "通讯中心")
@RestController
@RequestMapping("/client/execution")
public class RobotClientExecutionController {
    @Autowired
    private RobotExecutionService executionService;
    @Autowired
    private RobotCleanDataTask robotCleanDataTask;

    @ApiOperation(value = "添加执行记录")
    @PostMapping("/add")
    public int addExecution(@RequestBody RobotExecutionDTO executionDTO) {
        return executionService.addExecution(executionDTO);
    }

    @ApiOperation(value = "添加执行监控")
    @PostMapping("/addMonitor")
    public int addMonitor(@RequestBody List<RobotExecutionMonitor> monitorList) {
        return executionService.addMonitor(monitorList);
    }

    @ApiOperation(value = "保存执行数据")
    @PostMapping("/saveExecutionData")
    public int saveExecutionData(@RequestBody List<RobotExecutionData> executionDataList) {
        return executionService.saveExecutionData(executionDataList);
    }

    @ApiOperation(value = "保存执行凭证")
    @PostMapping("/saveExecutionVoucher")
    public int saveExecutionVoucher(@RequestBody RobotExecutionVoucher voucher) {
        return executionService.saveExecutionVoucher(voucher);
    }

    /**
     * 清除日志-调用接口
     */
    @ApiOperation(value = "清除日志-定时任务调用接口")
    @GetMapping("/cleanDataTask")
    public void cleanDataTask() {
        robotCleanDataTask.run();
    }
}
