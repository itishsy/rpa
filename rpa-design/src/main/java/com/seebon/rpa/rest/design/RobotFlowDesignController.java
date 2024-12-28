package com.seebon.rpa.rest.design;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.robot.RobotFlow;
import com.seebon.rpa.entity.robot.dto.design.*;
import com.seebon.rpa.service.RobotFlowDesignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(value = "应用管理", tags = "RPA应用设计")
@RestController
@RequestMapping("/flowDesign")
public class RobotFlowDesignController {
    @Autowired
    private RobotFlowDesignService flowDesignService;

    @ApiOperation("流程设计-新增")
    @PostMapping(value = "/add")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-流程设计-新增", operation = "流程设计-新增")
    public String add(@RequestBody RobotFlowDesignVO flowDesign) {
        return flowDesignService.add(flowDesign);
    }

    @ApiOperation(value = "流程设计-获取")
    @GetMapping("/getByCode")
    public RobotFlowDesign getByCode(RobotFlowDesignExVO exVO) {
        return flowDesignService.getByCode(exVO);
    }

    @ApiOperation(value = "流程设计-获取流程打开方式")
    @GetMapping("/getFlowOpenType")
    public RobotFlowOpenTypeVO getByCode(@RequestParam(value = "appCode") String appCode, @RequestParam(value = "flowCode") String flowCode, @RequestParam(value = "stepCode", required = false) String stepCode) {
        return flowDesignService.getFlowOpenType(appCode, flowCode, stepCode);
    }

    @ApiOperation(value = "流程设计-复制")
    @GetMapping("/copyByCode")
    public RobotFlowDesign copyByCode(RobotFlowDesignCopyVO exVO) {
        return flowDesignService.copyByCode(exVO);
    }

    @ApiOperation(value = "流程设计-获取子流程")
    @GetMapping("/getSubByAppCode")
    public List<RobotFlow> getSubByAppCode(@RequestParam(value = "appCode") String appCode) {
        return flowDesignService.getSubByAppCode(appCode);
    }

    @ApiOperation(value = "流程设计-同步接受")
    @PostMapping("/syncIn")
    @MyLog(moduleName = "流程设计", pageName = "流程设计-接受同步", operation = "接受同步")
    public int syncIn(@RequestBody RobotFlowDesignSyncVO syncVO) {
        return flowDesignService.syncIn(syncVO);
    }

    @ApiOperation(value = "流程设计-同步对外")
    @PostMapping("/syncOut")
    @MyLog(moduleName = "流程设计", pageName = "流程设计-对外同步", operation = "对外同步")
    public int syncOut(@RequestParam(value = "comment") String comment) {
        return flowDesignService.syncOut(comment);
    }
}
