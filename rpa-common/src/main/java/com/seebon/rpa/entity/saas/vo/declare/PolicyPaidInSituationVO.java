package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PolicyPaidInSituation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;


/**
 * @Author hao
 * @Date 2023/1/17 15:14
 * @Version 1.0
 **/
@Data
public class PolicyPaidInSituationVO extends PolicyPaidInSituation {

    private String fileName;

    @ApiModelProperty("申报账户")
    private String declareAccount;

    @ApiModelProperty("需要重复调用接口不走数据重复校验，默认开启校验")
    private Boolean checkRequirePostAgain = true;

    @ApiModelProperty("是否覆盖")
    private Boolean coverFlag = false;

    @ApiModelProperty("前缀, 字典值：10007001-社保系统，10007002-养老系统，10007003-医保系统，10007004-单工伤系统，10007005-工伤系统，10008001-公积金系统")
    private String prefix;

    @ApiModelProperty("状态（1--启用；2--停用）")
    private Integer status;

    @ApiModelProperty("明细数据")
    private List<LinkedHashMap<String, Object>> mapList;

    @ApiModelProperty("费用明细所在sheet")
    private Integer sheetIndex;

    @ApiModelProperty("证件号码所在列")
    private String idCardCol;

    @ApiModelProperty("表头有效开始行号")
    private Integer headerNumberStart;

    @ApiModelProperty("表头有效结束行号")
    private Integer headerNumberEnd;
}
