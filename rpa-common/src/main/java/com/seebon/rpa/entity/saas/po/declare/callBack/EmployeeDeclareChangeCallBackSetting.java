package com.seebon.rpa.entity.saas.po.declare.callBack;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@ApiModel("增减员申报状态变更回调频率配置表")
@Data
@Table(name = "employee_declare_change_call_back_setting")
public class EmployeeDeclareChangeCallBackSetting extends Identity {

    @ApiModelProperty("回调频率")
    @Column
    private String cron;


    @ApiModelProperty("描述内容")
    @Column
    private String comment;


    @ApiModelProperty("回调开启状态，1：开启，0：关闭")
    @Column
    private Integer status;

}
