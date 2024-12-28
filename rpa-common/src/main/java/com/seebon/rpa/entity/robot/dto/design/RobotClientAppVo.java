package com.seebon.rpa.entity.robot.dto.design;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.robot.vo.design.RobotTaskVO1;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@ApiModel("机器人客户端应用")
@Data
@ExcelIgnoreUnannotated
@ContentRowHeight(20)
@HeadRowHeight(20)
public class RobotClientAppVo {

    private Integer id;

    private Integer clientId;

    private String taskCode;

    @ApiModelProperty("序号")
//    @ExcelProperty(value = "序号",index = 0)
//    @ColumnWidth(8)
    private Integer index;

    @ApiModelProperty("平台方")
    private String platform;

    @ApiModelProperty("客户名称")
    @ExcelProperty(value = "客户名称",index = 0)
    @ColumnWidth(30)
    private String customerName;

    @ApiModelProperty(value = "应用编码")
    private String appCode;

    @ApiModelProperty("客户任务列表")
    private String taskName;

    @ApiModelProperty(value = "应用名称")
    @ExcelProperty(value = "应用名称",index = 1)
    @ColumnWidth(20)
    private String appName;

    @ApiModelProperty(value = "版本")
    private String version;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    private Integer status;

    /**
     * 最新执行时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date lastExecutionTime;

    private String concatLastExecutionTime;

    /**
     * 状态
     */
    private String state;

    private Integer successTotal;

    private Integer failureTotal;

    private Integer dataNumberTotal;

    private List<RobotTaskVO1> robotTaskVOS;
    /**
     * 单位社保号/公积金信息
     */
    private String accountNumbers;

    /**
     * 机器标识码
     */
    @ExcelProperty(value = "所在设备",index = 2)
    @ColumnWidth(20)
    @ApiModelProperty("设备编码")
    private String machineCode;

    @ExcelProperty(value = "设备厂商",index = 3)
    @ColumnWidth(20)
    @ApiModelProperty("设备厂商")
    private String machineFactory;

    @ExcelProperty(value = "设备状态",index = 4)
    @ColumnWidth(20)
    @ApiModelProperty("设备状态")
    private String machineStatus;

    @ExcelProperty(value = "申报单位",index = 5)
    @ColumnWidth(30)
    @ApiModelProperty("申报单位")
    private String orgName;


    @ApiModelProperty("申报账户")
    private String accountNumber;

    @ExcelProperty(value = "申报账号",index = 6)
    @ColumnWidth(30)
    @ApiModelProperty("申报账户-传参用")
    private String accNumber;

    @ExcelProperty(value = "账户状态",index = 7)
    @ColumnWidth(20)
    @ApiModelProperty("申报账户状态")
    private String taskStatus;

    @ExcelProperty(value = "停用账户原因",index = 8)
    @ColumnWidth(30)
    @ApiModelProperty("申报账户停用原因")
    private String taskComment;

    @ExcelProperty(value = "执行计划",index = 13)
    @ColumnWidth(30)
    @ApiModelProperty("执行计划")
    private String execPlant;

    @ExcelProperty(value = "是否存在自定义计划",index = 14)
    @ColumnWidth(30)
    @ApiModelProperty("是否存在自定义执行计划流程")
    private String haveCustom;


    @ApiModelProperty("业务类型，1：社保，2：公积金")
    private Integer businessType;

    @ApiModelProperty("参保城市")
    private Integer addrId;

    @ExcelProperty(value = "本月已完成",index = 9)
    @ColumnWidth(20)
    private Integer declaredCount;

    @ExcelProperty(value = "今日已完成",index = 10)
    @ColumnWidth(20)
    private Integer todayDeclaredCount;

    @ExcelProperty(value = "本月未完成",index = 11)
    @ColumnWidth(20)
    private Integer noDeclaredCount;

    @ExcelProperty(value = "网站审核中",index = 12)
    @ColumnWidth(20)
    private Integer auditCount;

    @ExcelProperty(value = "今日执行次数",index = 15)
    @ColumnWidth(20)
    private Integer execCount;

    /**
     * 应用参数值
     */
    private String appArgs;

    @ApiModelProperty(value = "指定此设备执行 0-否 1-是")
    private Integer fixMachine;

    @ApiModelProperty(value = "指定执行原因")
    private String fixRemark;

    public Integer getNoDeclaredTotal() {
        return (noDeclaredCount==null?0:noDeclaredCount) + (auditCount==null?0:auditCount);
    }

    public Integer getNoDeclaredNumber() {
        return noDeclaredCount==null?0:noDeclaredCount;
    }
}
