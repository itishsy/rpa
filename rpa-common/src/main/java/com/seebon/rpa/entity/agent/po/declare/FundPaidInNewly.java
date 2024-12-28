package com.seebon.rpa.entity.po.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;
import java.util.Map;

@Data
@Document(collection = "fund_paid_in_newly")
public class FundPaidInNewly {

    @MongoId(targetType = FieldType.OBJECT_ID)
    private String id;

    @ApiModelProperty("客户id")
    private Integer custId;

    @ApiModelProperty("地区名")
    private String addressName;

    @ApiModelProperty("公司名称")
    private String orgName;

    @ApiModelProperty("单位公积金号")
    private String accountNumber;

    @ApiModelProperty("网站源文件id")
    private Integer fileId;

    @ApiModelProperty("费用所属期")
    private String periodOfExpense;

    @ApiModelProperty("关联mysql policy_paid_in_situation id")
    private Integer mysqlId;

    @ApiModelProperty("前缀")
    private String prefix;

    @ApiModelProperty("实缴people数据")
    private List<Map<String,Object>> personDatas;

    @ApiModelProperty("实缴信息")
    private Map<String,Object> personData;

    @ApiModelProperty("实缴原始信息")
    private Map<String, Object> originalData;
}
