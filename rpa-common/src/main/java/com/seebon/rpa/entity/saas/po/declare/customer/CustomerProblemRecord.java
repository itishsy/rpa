package com.seebon.rpa.entity.saas.po.declare.customer;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @Author hao
 * @Date 2022/6/23 15:07
 * @Version 1.0
 **/
@Data
@Table(name = "customer_problem_record")
@ApiModel("用户问题登记表")
public class CustomerProblemRecord extends Identity {

    @Column
    @ApiModelProperty("关联customer_interflow id")
    private Integer interflowId;

    @Column
    @ApiModelProperty("客户id")
    private Integer custId;

    @Column
    private Integer searchTimes;

    @Column
    @ApiModelProperty("问题")
    private String question;

    @Column
    @ApiModelProperty("问题状态 1--未解决；2--已解决")
    private Integer status;

}
