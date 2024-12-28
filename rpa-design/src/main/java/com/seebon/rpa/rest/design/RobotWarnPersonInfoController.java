package com.seebon.rpa.rest.design;

import com.seebon.rpa.entity.robot.po.design.RobotWarnPersonType;
import com.seebon.rpa.service.RobotWarnPersonService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023/4/21 15:19
 */

@Api(value = "预警人员信息管理", tags = "预警人员信息管理")
@RestController
@RequestMapping("/warn/person")
public class RobotWarnPersonInfoController{
    @Autowired
    private RobotWarnPersonService robotWarnPersonService;

    @RequestMapping("/getPersonType")
    public Map<String,List<RobotWarnPersonType>> getPersonType(){
        Map<String,List<RobotWarnPersonType>> map=new HashMap<>();
        List<RobotWarnPersonType> personType=robotWarnPersonService.getPersonType();
        map.put("personListType",personType);
        return map;

    }
}
