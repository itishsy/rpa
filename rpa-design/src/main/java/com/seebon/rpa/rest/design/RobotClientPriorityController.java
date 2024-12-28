package com.seebon.rpa.rest.design;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.robot.RobotClientApp;
import com.seebon.rpa.entity.robot.RobotClientPriority;
import com.seebon.rpa.service.RobotClientPriorityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(value = "执行优先级", tags = "客户机器人应用执行优先级")
@RestController
@RequestMapping("/priority")
public class RobotClientPriorityController {
    @Autowired
    private RobotClientPriorityService clientPriorityService;

    @ApiOperation(value = "执行优先级-查询")
    @PostMapping("/list")
    public List<RobotClientPriority> list() {
        return clientPriorityService.list();
    }

    @ApiOperation(value = "执行优先级-查询客户应用")
    @PostMapping("/listApp")
    public List<RobotClientApp> listApp() {
        return clientPriorityService.listApp();
    }

    @ApiOperation(value = "执行优先级-保存")
    @PostMapping("/save")
    @MyLog(moduleName = "执行优先级", pageName = "执行优先级-保存执行优先级", operation = "保存执行优先级")
    public int save(@RequestBody List<RobotClientPriority> priorities) {
        return clientPriorityService.save(priorities);
    }
}
