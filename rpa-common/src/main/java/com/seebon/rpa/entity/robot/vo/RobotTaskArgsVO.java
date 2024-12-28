package com.seebon.rpa.entity.robot.vo;

import com.alibaba.fastjson.JSONObject;
import com.seebon.rpa.entity.robot.RobotTaskArgs;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2023/2/15 15:37
 * @Version 1.0
 **/
@Data
public class RobotTaskArgsVO extends RobotTaskArgs {

    private List<JSONObject> fileObjs;
}
