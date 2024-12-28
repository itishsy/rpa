package com.seebon.rpa.rest.design;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.vo.RobotClientRuleVO;
import com.seebon.rpa.service.RobotClientRuleService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Api(value = "任务指定盒子条件规则", tags = "任务指定盒子条件规则")
@RestController
@RequestMapping("/clientRule")
public class RobotClientRuleController {
    @Autowired
    private RobotClientRuleService clientRuleService;

    @ApiOperation(value = "指定盒子条件规则-查询")
    @PostMapping("/page")
    public IgGridDefaultPage<RobotClientRuleVO> page(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return clientRuleService.page(map);
    }

    @ApiOperation(value = "指定盒子条件规则-启动禁用")
    @PostMapping("/enableOrStop")
    public Integer enableOrStop(@ApiParam(name = "id", value = "id", required = true) @RequestParam("id") Integer id,
                                @ApiParam(name = "status", value = "状态 1-启用 2-禁用", required = true) @RequestParam("status") Integer status) {
        return clientRuleService.enableOrStop(id, status);
    }

    @ApiOperation(value = "指定盒子条件规则-添加")
    @PostMapping("/save")
    public Integer save(@RequestBody RobotClientRuleVO ruleVO) {
        return clientRuleService.save(ruleVO);
    }

    @ApiOperation(value = "指定盒子条件规则-查看")
    @GetMapping("/getById")
    public RobotClientRuleVO getById(@ApiParam(name = "id", value = "id", required = true) @RequestParam("id") Integer id) {
        return clientRuleService.getById(id);
    }

    @ApiOperation(value = "指定盒子条件规则-优先级")
    @PostMapping("/updateSort")
    public Integer updateSort(@ApiParam(name = "id", value = "id", required = true) @RequestParam("id") Integer id,
                              @ApiParam(name = "sort", value = "优先级", required = true) @RequestParam("sort") Integer sort) {
        return clientRuleService.updateSort(id, sort);
    }
}
