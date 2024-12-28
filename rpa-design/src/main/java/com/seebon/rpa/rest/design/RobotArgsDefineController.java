package com.seebon.rpa.rest.design;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.robot.RobotArgsDefine;
import com.seebon.rpa.entity.robot.vo.RobotAppArgsVO;
import com.seebon.rpa.service.RobotArgsDefineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(value = "信息配置", tags = "RPA应用设计")
@RestController
@RequestMapping("/appArgs")
public class RobotArgsDefineController {
    @Autowired
    private RobotArgsDefineService robotAppArgsService;

    @ApiOperation(value = "信息配置-新增")
    @PostMapping("/args/save")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-新增", operation = "新增信息配置")
    public int saveArgsDefine(@RequestBody RobotArgsDefine robotArgsDefine) {
        return robotAppArgsService.saveAppArgs(robotArgsDefine);
    }

    @ApiOperation(value = "信息配置-删除")
    @PostMapping("/args/delete")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-删除", operation = "删除信息配置")
    public int deleteArgsDefine(@RequestParam String fieldKey, @RequestParam String appCode) {
        return robotAppArgsService.deleteArgsDefine(fieldKey, appCode);
    }

    @ApiOperation(value = "信息配置-更新")
    @PostMapping("/args/update")
    @MyLog(moduleName = "信息配置", pageName = "信息配置-更新", operation = "更新信息配置")
    public int updateArgsDefine(@RequestBody RobotAppArgsVO RobotAppArgsVO, @RequestParam String fieldKey) {
        return robotAppArgsService.updateArgsDefine(RobotAppArgsVO, fieldKey);
    }

    @ApiOperation(value = "信息配置-根据appCode查询")
    @PostMapping("/args/list")
    public List<RobotArgsDefine> findArgsDefine(@RequestParam(value = "appCode") String appCode) {
        return robotAppArgsService.listRobotArgsDefine(appCode);
    }
}
