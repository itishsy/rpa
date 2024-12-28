package com.seebon.rpa.entity.po.declare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Document(collection = "paid_in_mongo_field")
public class PolicyPaidInMongoField {

    @MongoId(targetType = FieldType.OBJECT_ID)
    private String id;

    private Integer custId;

    private String addressName;

    @ApiModelProperty("关联mysql policy_paid_in_situation表")
    private Integer mysqlId;

    @ApiModelProperty("业务类型；1--社保；2--公积金")
    private Integer businessType;

    @ApiModelProperty("前缀-code")
    private String prefix;

    @ApiModelProperty("前缀-名称")
    private String prefixName;

    @ApiModelProperty("网站/本地字段")
    private LinkedHashMap<String,Object> fields;
}
