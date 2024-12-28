package com.seebon.rpa.rest.offerRule;

import com.google.common.collect.Maps;
import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.vo.RobotOfferRuleVO;
import com.seebon.rpa.schedule.RobotExecutionDataTask;
import com.seebon.rpa.service.RobotOfferRuleService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Api(value = "回盘规则", tags = "回盘规则")
@RestController
@RequestMapping("/offerRule")
public class RobotOfferRuleController {
    @Autowired
    private RobotOfferRuleService robotOfferRuleService;
    @Autowired
    private RobotExecutionDataTask robotExecutionDataTask;

    @ApiOperation("回盘规则查询")
    @PostMapping("/page")
    public IgGridDefaultPage<RobotOfferRuleVO> page(@RequestBody IgRequestObject requestObject) {
        Map<String, Object> params = RequestParamsUtil.toMap(requestObject);
        return robotOfferRuleService.page(params);
    }

    @ApiOperation("回盘规则查询")
    @PostMapping("/list")
    public List<RobotOfferRuleVO> list(@RequestBody RobotOfferRuleVO ruleVO) {
        return robotOfferRuleService.list(ruleVO);
    }

    @ApiOperation("通过Id获取回盘规则")
    @PostMapping("/getByRuleId/{ruleId}")
    public RobotOfferRuleVO getByRuleId(@PathVariable("ruleId") Integer ruleId) {
        return robotOfferRuleService.getByRuleId(ruleId);
    }

    @ApiOperation("回盘规则新增或修改")
    @PostMapping("/save")
    @MyLog(moduleName = "回盘规则", pageName = "回盘规则-新增或修改", operation = "新增或修改")
    public void save(@RequestBody RobotOfferRuleVO robotOfferRuleVO) {
        robotOfferRuleService.save(robotOfferRuleVO);
    }

    @ApiOperation("回盘规则删除")
    @PostMapping("/deleteById")
    @MyLog(moduleName = "回盘规则", pageName = "回盘规则-删除回盘规则", operation = "删除回盘规则")
    public void deleteById(Integer itemId) {
        robotOfferRuleService.deleteById(itemId);
    }

    @ApiOperation("回盘")
    @PostMapping("/declareCounterOfferTask")
    public void declareCounterOfferTask() {
        robotExecutionDataTask.handle();
    }
}
