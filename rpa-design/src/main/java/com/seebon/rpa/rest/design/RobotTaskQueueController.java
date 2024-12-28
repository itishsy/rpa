package com.seebon.rpa.rest.design;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;
import com.seebon.rpa.service.RobotTaskQueueService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@Slf4j
@Api(value = "执行队列", tags = "执行队列")
@RestController
@RequestMapping("/taskQueue")
public class RobotTaskQueueController {
    @Autowired
    private RobotTaskQueueService taskQueueService;

    @ApiOperation(value = "执行队列-查询")
    @PostMapping("/page")
    public IgGridDefaultPage<RobotTaskQueueVO> page(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return taskQueueService.page(map);
    }

    @ApiOperation(value = "执行队列-置顶")
    @PostMapping("/toTop")
    public Integer toTop(@RequestParam("id") Integer id) {
        return taskQueueService.toTop(id);
    }

    @ApiOperation(value = "执行队列-撤销")
    @PostMapping("/revoke")
    public Integer revoke(@RequestParam("ids") String ids) {
        return taskQueueService.revoke(ids);
    }
}
