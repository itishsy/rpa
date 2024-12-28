package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.RobotClientApp;
import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.entity.robot.vo.RobotTaskSessionKeepVO;
import com.seebon.rpa.entity.saas.po.system.SysDataDict;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface RobotTaskSessionKeepService {

    IgGridDefaultPage<RobotTaskSessionKeepVO> list(Map<String, Object> map);

    Integer save(RobotTaskSessionKeep sessionKeep);

    RobotTaskSessionKeepVO getById(Integer id);

    void againSync(Integer id);

    List<RobotClientApp> listApp(Integer clientId, String runSupport);

    List<SysDataDict> listDeclareSystem(String appCode);

    List<RobotTaskSessionKeepVO> listTask(RobotTaskSessionKeep sessionKeep);

    void disabled(RobotTaskSessionKeep sessionKeep);

    Boolean startLogin(Integer id);
}
