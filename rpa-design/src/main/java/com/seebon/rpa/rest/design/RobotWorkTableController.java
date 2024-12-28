package com.seebon.rpa.rest.design;

import com.seebon.rpa.entity.robot.vo.RobotClientAppVO;
import com.seebon.rpa.entity.robot.vo.RobotClientVO;
import com.seebon.rpa.service.RobotTaskService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/worktable")
public class RobotWorkTableController {

    @Resource
    private RobotTaskService robotTaskService;


    @ApiOperation("客户下所有机器人任务")
    @PostMapping("/listRobotTask")
    public List<RobotClientAppVO> listRobotTask() {
        return robotTaskService.listTaskByCustId();
    }

    @ApiOperation("所有城市")
    @PostMapping("/getCustAccount")
    public Integer getCustAccount(){
        return robotTaskService.selectCustAccount();
    }

    @ApiOperation("申报配置")
    @PostMapping("/listDeclareAccount")
    public List<RobotClientVO> listDeclareAccount(@RequestParam(value = "keyword", required = false) String keyword,
                                                  @RequestParam(value = "hasNotice", required = false) String hasNotice) {
        return robotTaskService.listTaskTableByCustId(keyword, hasNotice);
    }
}
