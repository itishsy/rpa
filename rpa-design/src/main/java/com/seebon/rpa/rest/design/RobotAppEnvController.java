package com.seebon.rpa.rest.design;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.RobotAppEnv;
import com.seebon.rpa.entity.robot.dto.design.RobotAppEnvVO;
import com.seebon.rpa.service.RobotAppEnvService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Api(value = "应用环境", tags = "应用环境")
@RestController
@RequestMapping("/app/env")
public class RobotAppEnvController {
    @Autowired
    private RobotAppEnvService appEnvService;

    @ApiOperation(value = "应用环境-列表")
    @PostMapping("/page")
    public IgGridDefaultPage<RobotAppEnv> page(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return appEnvService.listPage(map);
    }

    @ApiOperation("应用环境-新增")
    @PostMapping(value = "/add")
    @MyLog(moduleName = "应用环境", pageName = "客户应用列表-新增证书环境", operation = "新增证书环境")
    public Integer addRobotApp(@RequestBody RobotAppEnv robotAppEnv) {
        return appEnvService.addRobotAppEnv(robotAppEnv);
    }

    @ApiOperation("应用环境-新增")
    @PostMapping(value = "/addList")
    @MyLog(moduleName = "应用环境", pageName = "客户应用列表-批量新增证书环境", operation = "新增证书环境")
    public Integer addRobotApps(@RequestBody RobotAppEnvVO robotAppEnvVO) {
        return appEnvService.addRobotApps(robotAppEnvVO);
    }

    @ApiOperation("应用环境-获取")
    @GetMapping(value = "/getById")
    public RobotAppEnv getById(@RequestParam(value = "id") Integer id) {
        return appEnvService.getById(id);
    }

    @ApiOperation("应用环境-删除")
    @PostMapping(value = "/deleteById")
    @MyLog(moduleName = "应用环境", pageName = "客户应用列表-删除证书环境", operation = "删除证书环境")
    public Integer deleteById(@RequestParam(value = "id") Integer id) {
        return appEnvService.deleteById(id);
    }
}
