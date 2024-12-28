package com.seebon.rpa.entity.agent.dto.openApi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-28 10:14:39
 */
@ApiModel("获取人员附件所需参数实体信息")
@Data
public class DeclareEmployeeFileParamsDTO {

    @ApiModelProperty("参保地名称 例：广州")
    private String addrName;

    @ApiModelProperty("附件类型 10014001：身份证复印件，10014002：个人照片")
    private String fileType;

    @ApiModelProperty("人员证件号码")
    private List<String> idCards;

}
