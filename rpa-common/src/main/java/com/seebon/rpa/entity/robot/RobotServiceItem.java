package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2024-02-21 21:22
 */
@Data
@ApiModel("流程服务项目")
@Table(name = "robot_service_item")
public class RobotServiceItem{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column
    @ApiModelProperty(value = "应用编码")
    protected String appCode;

    @Column
    @ApiModelProperty(value = "服务项目1：增员，2：减员，3：调基，5：补缴，6：缴费，7：名册名单，8：费用明细 9：政策通知 10：基数申报")
    private Integer serviceItem;

    @Column
    @ApiModelProperty(value = "服务是否实现 0不实现 1已实现 2未实现")
    private Integer serviceStatus;

    @Column
    @ApiModelProperty(value = "服务备注")
    private String serviceRemark;
}
