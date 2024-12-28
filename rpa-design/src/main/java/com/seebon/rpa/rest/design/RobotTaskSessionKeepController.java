package com.seebon.rpa.rest.design;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.RobotClientApp;
import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.entity.robot.vo.RobotTaskSessionKeepVO;
import com.seebon.rpa.entity.saas.po.system.SysDataDict;
import com.seebon.rpa.service.RobotTaskSessionKeepService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Api(value = "会话维持", tags = "客户机器人会话维持")
@RestController
@RequestMapping("/sessionKeep")
public class RobotTaskSessionKeepController {
    @Autowired
    private RobotTaskSessionKeepService taskSessionKeepService;

    @ApiOperation(value = "会话维持-查询")
    @PostMapping("/page")
    public IgGridDefaultPage<RobotTaskSessionKeepVO> page(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return taskSessionKeepService.list(map);
    }

    @ApiOperation(value = "会话维持-保存")
    @PostMapping("/save")
    public Integer save(@RequestBody RobotTaskSessionKeep sessionKeep) {
        return taskSessionKeepService.save(sessionKeep);
    }

    @ApiOperation(value = "会话维持-获取")
    @GetMapping("/getById")
    public RobotTaskSessionKeepVO getById(@RequestParam("id") Integer id) {
        return taskSessionKeepService.getById(id);
    }

    @ApiOperation(value = "会话维持-重新同步")
    @PostMapping("/againSync")
    public void againSync(@RequestParam(value = "id", required = false) Integer id) {
        taskSessionKeepService.againSync(id);
    }

    @ApiOperation(value = "会话维持-获取应用")
    @GetMapping("/listApp")
    public List<RobotClientApp> listApp(@RequestParam("clientId") Integer clientId, @RequestParam("runSupport") String runSupport) {
        return taskSessionKeepService.listApp(clientId, runSupport);
    }

    @ApiOperation(value = "会话维持-获取申报系统")
    @GetMapping("/listDeclareSystem")
    public List<SysDataDict> listDeclareSystem(@RequestParam("appCode") String appCode) {
        return taskSessionKeepService.listDeclareSystem(appCode);
    }

    @ApiOperation(value = "会话维持-获取申报账户")
    @PostMapping("/listTask")
    public List<RobotTaskSessionKeepVO> listTask(RobotTaskSessionKeep sessionKeep) {
        return taskSessionKeepService.listTask(sessionKeep);
    }

    @ApiOperation(value = "会话维持-启用状态")
    @PostMapping("/disabled")
    public void disabled(RobotTaskSessionKeep sessionKeep) {
        taskSessionKeepService.disabled(sessionKeep);
    }

    @ApiOperation(value = "启动登录")
    @PostMapping("/startLogin")
    public Boolean startLogin(Integer id) {
        return taskSessionKeepService.startLogin(id);
    }
}
