package com.seebon.rpa.rest.monitor;


import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.RobotClient;
import com.seebon.rpa.entity.robot.RobotComponent;
import com.seebon.rpa.entity.robot.dto.design.RobotComponentVO;
import com.seebon.rpa.entity.robot.vo.RobotClientLogVO;
import com.seebon.rpa.entity.robot.vo.RobotClientVO;
import com.seebon.rpa.service.RobotClientService;
import com.seebon.rpa.service.RobotComponentService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(value = "机器人版本管理", tags = "运维监控")
@RestController
@RequestMapping("/version")
public class RobotComponentController {

    @Autowired
    private RobotClientService clientService;

    @Resource
    private RobotComponentService componentService;

    @PostMapping("/list")
    @ApiOperation(value = "版本管理-列表")
    public List<RobotComponentVO> findCurrent() {
        return componentService.getByRecord();
    }

    @PostMapping("/add")
    @ApiOperation(value = "版本管理-添加组件")
    @MyLog(moduleName = "机器人版本管理", pageName = "版本管理-添加组件", operation = "添加组件")
    public int addComponents(String component, String comment, MultipartFile installJar) throws IOException {
        return componentService.addComponents(component, comment, installJar);
    }

    @PostMapping("/history")
    @ApiOperation(value = "版本管理-历史版本")
    public List<RobotComponentVO> getHistory(String component) {
        return componentService.getByHistory(component);
    }

    @PostMapping("/activate")
    @ApiOperation(value = "版本管理-使用此版本")
    @MyLog(moduleName = "历史版本", pageName = "历史版本-使用此版本", operation = "使用此版本")
    public int activate(String component, String version) throws IOException {
        return componentService.activate(component, version);
    }

    @PostMapping("/upgrade")
    @ApiOperation(value = "版本管理-升级")
    @MyLog(moduleName = "历史版本", pageName = "历史版本-升级版本", operation = "升级版本")
    public RobotComponentVO upgrade(@RequestParam(value = "installJar") MultipartFile installJar, String component, String version, String comment) throws IOException {
        return componentService.upgrade(installJar, component, version, comment);
    }

    @PostMapping("/enableStop")
    @ApiOperation(value = "版本管理-停止启用")
    @MyLog(moduleName = "历史版本", pageName = "历史版本-停止启用版本", operation = "停止启用版本")
    public int enableStop(Integer id) {
        return componentService.enableStop(id);
    }

    @ApiOperation("版本管理-根据发布说明查询")
    @PostMapping(value = "/queryByComment")
    public List<RobotComponent> queryByComment(@RequestParam(value = "comment") String comment) {
        return componentService.queryByComment(comment);
    }

    @ApiOperation("客户设备维护-列表")
    @PostMapping("/client/list")
    public IgGridDefaultPage<RobotClientVO> listRobotClient(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return clientService.listRobotClient(map);
    }

    @ApiOperation("客户设备维护-更新")
    @PostMapping("/client/updateById")
    public void updateById(RobotClientVO clientVO) {
        clientService.updateById(clientVO);
    }

    @ApiOperation("客户设备维护-列表")
    @PostMapping("/client/getByClientId/{clientId}")
    public List<RobotClientVO> getByClientId(@PathVariable("clientId") Integer clientId) {
        return clientService.getByClientId(clientId);
    }

    @ApiOperation("客户设备维护-添加")
    @PostMapping("/client/add")
    @MyLog(moduleName = "客户设备维护", pageName = "客户设备维护-添加设备", operation = "添加设备")
    public int addRobotClient(@RequestBody RobotClient client) {
        return clientService.add(client);
    }

    @ApiOperation("客户设备维护-生成机器码")
    @PostMapping("/client/generateCode")
    public String generateCode() {
        return clientService.generateCode();
    }





    @ApiOperation("客户设备维护-日志列表")
    @PostMapping("/client/log/list")
    public List<RobotClientLogVO> listRobotClientLog(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return clientService.listRobotClientLog(map);
    }

    @ApiOperation("客户设备维护-日志添加")
    @PostMapping("/client/log/save")
    public Integer saveRobotClientLog(@RequestBody RobotClientLogVO logVO) {
        return clientService.saveRobotClientLog(logVO);
    }

}
