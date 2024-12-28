package com.seebon.rpa.web.rest;

import com.seebon.rpa.service.RobotArgsDefineService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/robot/task")
public class RobotTaskController {
    @Autowired
    private RobotArgsDefineService argsDefineService;

    @Value("${rpa.token}")
    private String token;

    @PostMapping("/getToken")
    public String getToken() {
        return token;
    }

    @ApiOperation("判断客户的机器人是否有任务")
    @PostMapping("/haveTask")
    public Boolean haveTask() {
        return argsDefineService.haveTask();
    }

    @ApiOperation("子流程复制到主流程")
    @GetMapping("/copyFlowStep")
    public String copyFlowStep(@RequestParam("flowCode") String flowCode) {
        return argsDefineService.copyFlowStep(flowCode);

    }

    @ApiOperation("复制流程")
    @GetMapping("/copyFlow")
    public String copyFlow(@RequestParam("sourceFlowCode") String sourceFlowCode, @RequestParam("targetFlowCode") String targetFlowCode) {
        return argsDefineService.copyFlow(sourceFlowCode, targetFlowCode);
    }

    @ApiOperation("复制应用")
    @GetMapping("/copyApp")
    public String copyApp(@RequestParam("sourceAppCode") String sourceAppCode, @RequestParam("targetAppCode") String targetAppCode) {
        return argsDefineService.copyApp(sourceAppCode, targetAppCode);
    }
}
