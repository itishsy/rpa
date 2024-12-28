package com.seebon.rpa.entity.robot.dto.design;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * TODO
 *
 * @author zjf
 * @describe rpa应用设计请求参数
 * @date 2024-01-08 17:00
 */
@Data
public class RobotAppRequestVo{
    @ApiModelProperty(value="业务类型集合")
    private List<Integer> businessList;

    @ApiModelProperty(value="上线标记集合")
    private List<Integer> onlineList;

    @ApiModelProperty(value="应用状态集合")
    private List<Integer> appStatusList;

    @ApiModelProperty(value="服务项目集合")
    private List<String> serviceItemList;

    @ApiModelProperty(value="关键字")
    private String keyWord;

    private Integer page=1;

    private Integer size=15;

    private Integer start=0;

}
