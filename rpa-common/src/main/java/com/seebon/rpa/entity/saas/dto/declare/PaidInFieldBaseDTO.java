package com.seebon.rpa.entity.saas.dto.declare;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.saas.vo.declare.PaidInFieldVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ApiModel("费用配置dto")
@Data
public class PaidInFieldBaseDTO implements Serializable {

    @ApiModelProperty("字段信息")
    private List<PaidInFieldVO> fields;

    @ApiModelProperty("修改人姓名")
    private String updateName;

    @ApiModelProperty("修改时间")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
