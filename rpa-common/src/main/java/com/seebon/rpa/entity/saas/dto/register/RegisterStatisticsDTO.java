package com.seebon.rpa.entity.saas.dto.register;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@ApiModel("驾驶仓-在职人数解析数据量")
@Data
public class RegisterStatisticsDTO implements Serializable {

    private String dataMonth;

    private Integer registerNumber;

}
