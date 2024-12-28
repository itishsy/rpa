package com.seebon.rpa.entity.saas.po.declare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

/**
 * @author ZhenShijun
 * @dateTime 2022-12-07 15:00:55
 */
@ApiModel("地区增员申报规则")
@Data
@Table(name = "policy_addr_merge_add_rule")
public class PolicyAddrMergeAddRule {

    @ApiModelProperty("主键id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("参保城市id")
    @Column
    private Integer addrId;

    @ApiModelProperty("参保城市名称")
    @Column
    private String addrName;

    @ApiModelProperty("业务类型，1：社保，2：公积金")
    @Column
    private Integer businessType;

    @ApiModelProperty("模板类型，10007001：全险系统，10007002：养老系统，10007003：医疗系统，10007004：单工伤，10008001：公积金系统")
    @Column
    private String tplTypeCode;

    @ApiModelProperty("合并后基数取值类型，1：取增员基数，5：取补缴基数")
    @Column
    private Integer baseType;

    @ApiModelProperty("合并类型，1：日期合并，2：附加补缴日期")
    @Column
    private Integer mergeType;

    @ApiModelProperty("合并类型为附加补缴日期时的附加补缴字段规则")
    @Column
    private String bjFieldRule;

    @ApiModelProperty("申报合并规则，0：默认合并，1：补缴险种跟增员险种完全一致才合并")
    @Column
    private Integer mergeRule;

    @ApiModelProperty("在爬回在册数据后是否开启申报数据是否可申报，1：开启校验，0：关闭校验")
    @Column
    private Integer validateRule;

    @ApiModelProperty("在数据回盘时是否根据在册数据对回盘数据进行差异核对，1：开启，0：关闭")
    @Column
    private Integer differenceRule;

}
