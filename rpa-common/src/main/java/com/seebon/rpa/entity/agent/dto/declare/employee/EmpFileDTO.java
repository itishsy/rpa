package com.seebon.rpa.entity.agent.dto.declare.employee;

import com.seebon.rpa.entity.agent.dto.declare.FilestoreDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-30 14:50:11
 */
@ApiModel("人员附件保存信息DTO")
@Data
public class EmpFileDTO {

    private String uuid;

    private Integer businessType;

    @ApiModelProperty("参保城市id")
    private Integer addrId;

    @ApiModelProperty("参保城市名称")
    private String addrName;

    @ApiModelProperty("人员证件号码")
    private String idCard;

    @ApiModelProperty("人员姓名")
    private String empName;

    @ApiModelProperty("各文件类型的文件信息")
    List<FilestoreDTO> files;

    @ApiModelProperty("批量上传时必填，选中的申报数据，数据格式：{uuid:增员数据uuid, businessType:业务类型（1社保，2公积金）}")
    List<Map<String, Object>> declareList;

}
