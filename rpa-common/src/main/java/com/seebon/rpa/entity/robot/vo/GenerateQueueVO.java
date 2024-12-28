package com.seebon.rpa.entity.robot.vo;

import com.google.common.collect.Lists;
import com.seebon.rpa.entity.robot.RobotTaskQueue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class GenerateQueueVO {

    private List<RobotLoginAuthVO> robotLoginAuthVOS;

    private List<RobotTaskQueue> robotTaskQueueS;

    private Integer count;

    public GenerateQueueVO(){
        robotLoginAuthVOS = Lists.newArrayList();
        robotTaskQueueS = Lists.newArrayList();
    }

}

