package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotTaskQueue;
import com.seebon.rpa.entity.robot.RobotTaskQueueItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RobotTaskQueueVO extends RobotTaskQueue {

    @ApiModelProperty(value = "参保城市")
    private String addrName;

    @ApiModelProperty(value = "业务类型 1：申报，2：公积金")
    private String businessTypeName;

    @ApiModelProperty(value = "执行事项，1：增减员（增员、减员、调基、补缴），6：缴费，7：名册名单，8：费用明细，9：政策通知 10：基数申报")
    private String queueItemName;

    @ApiModelProperty(value = "服务项目，1：增员，2：减员，3：调基，5：补缴, 7：名册名单 8：费用明细 9：政策通知 10：基数申报  12：查审核结果")
    private String serviceItemName;

    @ApiModelProperty(value = "申报系统")
    private String declareSystemName;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "应用参数值")
    private String appArgs;

    @ApiModelProperty(value = "客户名称")
    private String customerName;

    @ApiModelProperty(value = "刷新预估时间")
    private Boolean refreshPreTime = false;

    @ApiModelProperty(value = "盒子链接码")
    private String machineFactory;

    private List<RobotTaskQueueItem> taskQueueItemList;

    private List<String> flowCodeList;
}
