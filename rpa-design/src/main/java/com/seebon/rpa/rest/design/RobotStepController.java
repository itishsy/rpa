package com.seebon.rpa.rest.design;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.entity.robot.dto.design.RobotStepAppDescribeVO;
import com.seebon.rpa.entity.robot.vo.RobotFlowStepVO;
import com.seebon.rpa.service.RobotStepService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(value = "流程步骤", tags = "RPA应用设计")
@RestController
@RequestMapping("/flow/step")
public class RobotStepController {
    @Autowired
    private RobotStepService stepService;

    @ApiOperation(value = "流程步骤-查询")
    @PostMapping("/list/{flowCode}")
    public List<RobotFlowStepVO> list(@ApiParam("flowCode") @PathVariable String flowCode,
                                      @RequestParam(value = "stepCode", required = false) String stepCode,
                                      @RequestParam(value = "templateFlowCode", required = false) String templateFlowCode) {
        return stepService.list(flowCode, stepCode, templateFlowCode);
    }

    @ApiOperation(value = "流程步骤-查询")
    @GetMapping("/list")
    public List<RobotFlowStepVO> list(@ApiParam("flowCode") @RequestParam String flowCode) {
        return stepService.list(flowCode, null, null);
    }

    @ApiOperation(value = "流程步骤-保存")
    @PostMapping("/save")
    @MyLog(moduleName = "流程配置", pageName = "流程配置-保存流程步骤", operation = "保存流程步骤")
    public boolean save(@RequestBody List<RobotFlowStep> steps,
                        @RequestParam(value = "flowCode") String flowCode,
                        @RequestParam(value = "stepCode", required = false) String stepCode,
                        @RequestParam(value = "templateFlowCode", required = false) String templateFlowCode) {
        return stepService.save(steps, flowCode, stepCode, templateFlowCode);
    }

    @PostMapping("/queryDescribe")
    public RobotStepAppDescribeVO describeVO(Integer id) {
        return stepService.listDescribe(id);
    }
}
