package com.seebon.rpa.entity.agent.dto.declare;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Data
public class EmpDeclareInsuredDTO {

    private Integer id;

    private String uuid;

    private Integer addrId;

    private String addrName;

    private String compAccount;

    private BigDecimal empTbBase;

    private String idCard;

    private Integer declareType;

    private Integer declareStatus;

    private String cardType;

    private Integer businessType;

    @ApiModelProperty("0--保存  1--保存并提交")
    private Integer saveType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date insuredDate;

    private String strInsuredDate;

    // 是否校验减员其他申报账户 0校验  1校验
    private Integer igCheckOtherAccount;

    @ApiModelProperty("员工申报信息")
    private Map<String, String> declareInfo;

}
