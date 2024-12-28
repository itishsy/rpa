package com.seebon.rpa.rest.design;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.po.design.RobotOperationMessageConfig;
import com.seebon.rpa.service.RobotWarnMessageService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * TODO
 *
 * @author zjf
 * @describe 预警消息业务实现
 * @date 2023/4/21 10:13
 */
@Slf4j
@Api(value = "消息管理", tags = "消息中心")
@RestController
@RequestMapping("/message/warn")
public class RobotWarnMessageController{

    @Autowired
    private RobotWarnMessageService robotWarnMessageService;

    @ApiOperation(value = "消息中心-分页查询列表")
    @PostMapping("/page")
    public IgGridDefaultPage<RobotOperationMessageConfig> page(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return robotWarnMessageService.listPage(map);
    }
    @ApiOperation(value = "消息中心-新增或更新消息")
    @PostMapping("/insertOrUpdateMessage")
    @MyLog(moduleName = "design消息中心", pageName = "消息中心", operation = "新增或更新消息")
    public void insertOrUpdateMessage(@RequestBody RobotOperationMessageConfig vo) {
         robotWarnMessageService.insertOrUpdateMessage(vo);
    }
    @ApiOperation(value = "消息中心-修改消息状态")
    @PostMapping("/updateMessageStatus")
    @MyLog(moduleName = "design消息中心", pageName = "消息中心", operation = "修改消息状态")
    public void updateMessageStatus(@RequestBody RobotOperationMessageConfig vo) {
        robotWarnMessageService.insertOrUpdateMessage(vo);
    }
    @ApiOperation(value = "消息中心-发送消息")
    @PostMapping("/send")
    public void send() {
       // robotWarnMessageService.sendMsg("10017002");
    }
}
