package com.seebon.rpa.entity.saas.po.ai;

import com.seebon.rpa.entity.saas.dto.ai.AiPersonMessageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-08-28 10:54
 */
@Document(collection = "ai_person_declare")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIPersonDeclare {

    @ApiModelProperty("客户id")
    private Integer customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("参保主体")
    private String insuredName;

    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ApiModelProperty("业务名称")
    private String businessName;

    @ApiModelProperty("申报单位")
    private String declareUnit;

    @ApiModelProperty("申报账号")
    private String declareAccount;

    @ApiModelProperty("导入返回信息")
    AiPersonMessageDTO aiPersonMessageDTO;

    @ApiModelProperty("导入返回信息")
    private String uuid;


}
