package com.seebon.rpa.entity.saas.vo.social;

import com.seebon.rpa.entity.saas.enums.SocialCostEnum;
import com.seebon.rpa.entity.saas.po.social.SocialCost;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@ApiModel("社保费用表(威哥)")
@Data
public class SocialCostVO extends SocialCost {

    @ApiModelProperty("文件Ids")
    private List<Integer> fileIds;

    @ApiModelProperty("符合贫困人员条数")
    private Integer costCount;

    @ApiModelProperty("符合贫困人员数(身份证去重)")
    private Integer costNum;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("客户ID")
    private Integer custId;

    @ApiModelProperty("贫困或失业")
    private String costEnum;
    @ApiModelProperty("1贫困 2失业")
    private Integer type;
    @ApiModelProperty("execl客户名称")
    private Set<String> fileCustName;
}
