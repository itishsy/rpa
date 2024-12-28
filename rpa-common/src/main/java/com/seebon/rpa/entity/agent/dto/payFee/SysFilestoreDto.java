package com.seebon.rpa.entity.agent.dto.payFee;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class SysFilestoreDto implements Serializable {

    protected Integer id;

    private Integer createId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private Integer updateId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("文件名称")
    private String clientFileName;

    @ApiModelProperty("文件路径")
    private String serverFilePath;

    @ApiModelProperty("文件http地址")
    private String fileUrl;

    @ApiModelProperty("文件类型")
    private Integer filestoreType;

    private Integer payFeeId;

    private String payUuid;

    private String orgName;

    private String accountNumber;

    private String addrName;

    private String systemTypeName;

    private String periodOfPayment;

}
