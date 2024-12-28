package com.seebon.rpa.remote;

import com.seebon.rpa.entity.robot.dto.design.RobotFlowDesignSyncVO;
import com.seebon.rpa.entity.robot.dto.history.RobotAppHistory;
import com.seebon.rpa.http.HttpService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@HttpService("${rpa.design.prod.server}")
public interface RpaDesignProdService {

    /**
     * 同步申报流程配置到生产环境
     *
     * @param robotAppHistory
     * @return
     */
    @PostMapping("/robot/app/syncIn")
    int syncIn(@RequestBody RobotAppHistory robotAppHistory);

    /**
     * 同步申报流程模板配置到生产环境
     *
     * @param syncVO
     * @return
     */
    @PostMapping("/robot/flowDesign/syncIn")
    int syncInDesign(@RequestBody RobotFlowDesignSyncVO syncVO);
}
