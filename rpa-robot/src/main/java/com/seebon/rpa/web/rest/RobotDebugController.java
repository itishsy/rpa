package com.seebon.rpa.web.rest;

import com.seebon.rpa.entity.robot.vo.DebugFlowStepVO;
import com.seebon.rpa.service.RobotDebugService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/robot/debug/")
public class RobotDebugController {
    @Autowired
    private RobotDebugService robotDebugService;

    @ApiOperation(value = "启动任务的流程。本地调试调用(断点、运行此步骤、从此步骤开始运行)")
    @PostMapping("/debugFlowStep")
    public void debugFlowStep(@RequestBody DebugFlowStepVO flowStep) {
        robotDebugService.debugFlowStep(flowStep);
    }

    @ApiOperation(value = "启动任务的流程。本地调试调用(插入执行步骤)")
    @PostMapping("/addFlowStep")
    public void addFlowStep(@RequestBody DebugFlowStepVO flowStep) {
        robotDebugService.addFlowStep(flowStep);
    }

    @ApiOperation(value = "启动任务的流程。本地调试调用(上一步(back)、下一步(next)、暂停(suspend)、继续(continue)、停止(stop))")
    @PostMapping("/sendCommand/{command}")
    public void sendCommand(@PathVariable("command") String command) {
        robotDebugService.sendCommand(command);
    }
}
