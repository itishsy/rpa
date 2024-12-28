package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.DeclareAddr;
import com.seebon.rpa.entity.saas.po.declare.PolicyAccfundSuppRule;
import com.seebon.rpa.entity.saas.po.declare.PolicySocialSuppRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-04-21 09:43:21
 */
@ApiModel("参保城市vo")
@Data
public class DeclareAddrVO extends DeclareAddr {

    @Column
    @ApiModelProperty("省id")
    private Integer provinceId;

    @Column
    @ApiModelProperty("省名称")
    private String provinceName;

    @Column
    @ApiModelProperty("参保地拼音")
    private String pinyin;

    @Column
    @ApiModelProperty("参保地拼音缩写")
    private String pinyinShort;

    @Column
    @ApiModelProperty("是否热门地区 1：是，0：否")
    private Integer isHot;

    @ApiModelProperty(value = "内部处理参数",required = true,hidden = true)
    private List<PolicySocialSuppRule> socialSuppRules;

    @ApiModelProperty(value = "内部处理参数",required = true,hidden = true)
    private PolicyAccfundSuppRule accfundSuppRule;

}
