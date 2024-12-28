package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotServiceItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/8/29 14:06
 * @Version 1.0
 **/
@Data
public class RobotAppAddVO {
    /**
     * 应用名称
     */
    private String appName;

    private String robotName;

    private String appArgs;

    @ApiModelProperty(value = "应用备注")
    private String appRemark;

    private Integer id;

    private String appCode;

   List<RobotServiceItem> robotServiceItemList;


}
