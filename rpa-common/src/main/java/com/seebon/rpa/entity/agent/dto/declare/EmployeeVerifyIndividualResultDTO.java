package com.seebon.rpa.entity.agent.dto.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@ApiModel("参保接口录入mongo数据信息")
@Data
@Document(collection = "employee_verify_individual_result")
public class EmployeeVerifyIndividualResultDTO implements Serializable {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("身份证号")
    private String idNo;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("二要素校验结果")
    private Integer verifyStatus;

    @ApiModelProperty("校验保存时间")
    private Date createTime;

}
