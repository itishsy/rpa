package com.seebon.rpa.entity.saas.dto.register;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("驾驶仓-在职人数解析数据量")
@Data
public class RegisterStatisticsGroupDTO implements Serializable {

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("趋势数据")
    private List<RegisterStatisticsDTO> datas;

}
