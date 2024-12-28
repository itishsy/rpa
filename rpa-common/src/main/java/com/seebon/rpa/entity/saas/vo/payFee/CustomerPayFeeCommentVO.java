package com.seebon.rpa.entity.saas.vo.payFee;

import com.seebon.rpa.entity.saas.po.payFee.CustomerPayFeeList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Hey man，take your time！
 *
 * @Author: 李松洋
 * @Date: Create in 11:32 2023/10/24
 */
@ApiModel("获取缴费失败数据")
@Data
public class CustomerPayFeeCommentVO extends CustomerPayFeeList {
    @ApiModelProperty("失败类型")
    private String failType;
}
