package com.seebon.rpa.entity.saas.po.declare;

import com.seebon.rpa.entity.Identity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author ZhenShijun
 * @dateTime 2022-03-31 10:43:47
 */
@ApiModel(value = "报盘设置基础信息-主表")
@Table(name = "policy_declare_base_setting")
@Data
public class PolicyDeclareBaseSetting extends Identity {

    @Column
    @ApiModelProperty("uuid")
    private String uuid;

    @Column
    @ApiModelProperty("参保地Id")
    private Integer addrId;

    @Column
    @ApiModelProperty("参保地名称")
    private String addrName;

    @Column
    @ApiModelProperty("业务类型 1-社保，2-公积金")
    private Integer bussinssType;

    @Column
    @ApiModelProperty("申报类型 1-增员，2-减员，3-调基，4-变更，5-补缴")
    private Integer changeType;

    @Column
    @ApiModelProperty("费用年月显示规则 1-合并连续月显示，2-月份单条显示）")
    private Integer fyRuleType;

    @Column
    @ApiModelProperty("有效状态（1：有效，0：失效）")
    private Integer status;

    @Column
    @ApiModelProperty("申报注意事项")
    private String declareItem;

    @Column
    @ApiModelProperty("按险种分行显示（1：是，0：否）")
    private Integer showType;

    @Column
    @ApiModelProperty("自动解析户籍省市区 1：是，0：否")
    private Integer autoParse;

    @Column
    @ApiModelProperty("行政区划返回格式：1：国标.名称，2：国标-名称，3：国标_名称，4：国标 名称，5国标，6：名称.国标，7：名称-国标，:8：名称_国标，9：名称 国标,10:国标名称,11:名称国标,12:国标--名称,13:名称--国标")
    private Integer divisionsFormat;

}
