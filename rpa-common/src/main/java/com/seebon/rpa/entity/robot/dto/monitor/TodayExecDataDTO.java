package com.seebon.rpa.entity.robot.dto.monitor;

import com.seebon.rpa.entity.robot.dto.RobotTaskScheduleDTO;
import com.seebon.rpa.entity.saas.vo.PolicyAddrDeadlineSettingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@ApiModel("今日执行信息列表")
@Data
public class TodayExecDataDTO implements Serializable {

    @ApiModelProperty("客户id")
    private Integer clientId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("应用编码")
    private String appCode;

    @ApiModelProperty("应用参数")
    private String appArgs;

    @ApiModelProperty("任务编号")
    private String taskCode;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("申报账户状态")
    private String taskStatus;

    @ApiModelProperty("申报账户操作人")
    private String taskEditName;

    @ApiModelProperty("申报账户停用原因")
    private String taskComment;

    @ApiModelProperty("所在设备")
    private String machineCode;

    @ApiModelProperty("所在厂商")
    private String machineFactory;

    @ApiModelProperty("设备状态")
    private String machineStatus;

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("参保城市")
    private String addrName;

    @ApiModelProperty("业务类型")
    private String businessTypeName;

    @ApiModelProperty("申报单位")
    private String orgName;

    @ApiModelProperty("申报账户")
    private String accountNumber;

    @ApiModelProperty("临期标志，1：临期，0：未临期")
    private Integer flag;

    @ApiModelProperty("未申报数")
    private Integer noDeclaredCount;

    @ApiModelProperty("未申报时长")
    private String noDeclareTimeLong;

    @ApiModelProperty("临期天数")
    private Integer adventDays;

    @ApiModelProperty("申报期")
    private String declareCycle;

    @ApiModelProperty("执行计划")
    private String execPlant;

    @ApiModelProperty("当日校验失败数")
    private Integer validateFailCount;

    @ApiModelProperty("当月成功数")
    private Integer successCount;

    @ApiModelProperty("当月失败数")
    private Integer failCount;

    @ApiModelProperty("上线阶段")
    private String state;

    @ApiModelProperty("开发人员")
    private String devUserName;

    @ApiModelProperty("测试人员")
    private String testUserName;

    @ApiModelProperty("运维人员")
    private String ywUserName;

    @ApiModelProperty("调研人员")
    private String xqUserName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("执行计划")
    private List<RobotTaskScheduleDTO> schedules;

    @ApiModelProperty("申报期")
    private List<PolicyAddrDeadlineSettingVO> deadlineSettings;

    @ApiModelProperty("未申报数据")
    private List<NoDeclareDTO> noDeclareList;

    @ApiModelProperty("是否在执行计划内，1：是，0：否")
    private Integer innerPlant;

}
