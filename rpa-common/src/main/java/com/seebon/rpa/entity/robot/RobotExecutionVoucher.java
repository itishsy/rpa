package com.seebon.rpa.entity.robot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 客户机器人执行凭证
 */
@Data
@Table(name = "robot_execution_voucher")
@ApiModel(value = "机器人执行记录")
public class RobotExecutionVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "client_id")
    @ApiModelProperty(value = "客户ID")
    private Integer clientId;

    @Column(name = "machine_code")
    @ApiModelProperty(value = "机器标识码")
    private String machineCode;

    @Column(name = "execution_code")
    @ApiModelProperty(value = "执行编码")
    private String executionCode;

    @Column(name = "addr_name")
    @ApiModelProperty(value = "申报地")
    private String addrName;

    @Column(name = "business_type")
    @ApiModelProperty(value = "业务类型 1：社保，2：公积金")
    private Integer businessType;

    @Column(name = "declare_type")
    @ApiModelProperty(value = "申报类型 1：增员，2：减员，3：调基，5：补缴")
    private Integer declareType;

    @Column(name = "account_number")
    @ApiModelProperty(value = "申报账户")
    private String accountNumber;

    @Column(name = "declare_month")
    @ApiModelProperty(value = "申报年月")
    private String declareMonth;

    @Column(name = "tpl_type_code")
    @ApiModelProperty(value = "系统类型，10007001：全险系统，10007002：养老系统，10007003：医疗系统，10007004：单工伤，10008001：公积金系统")
    private String tplTypeCode;

    @Column(name = "file_id")
    @ApiModelProperty(value = "文件id")
    private Integer fileId;

    @Column(name = "file_name")
    @ApiModelProperty(value = "文件名")
    private String fileName;

    @Column(name = "file_path")
    @ApiModelProperty(value = "文件路径")
    private String filePath;

    @Column(name = "file_url")
    @ApiModelProperty(value = "文件地址")
    private String fileUrl;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
