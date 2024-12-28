package com.seebon.rpa.entity.robot.vo.design;

import com.seebon.rpa.entity.robot.RobotAppConfigGroup;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/10/26 15:21
 * @Version 1.0
 **/
@Data
public class RobotAppGroupVO extends RobotAppConfigGroup {

    private Integer sort;

    private List<RobotArgsDefineVO> argsDefineVOS;

}
