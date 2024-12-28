package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.SocialHuhehaoteBank;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("内蒙社保申报网站银行信息VO")
@Data
public class SocialHuhehaoteBankVO extends SocialHuhehaoteBank {

    @ApiModelProperty("支行信息")
    private List<SocialHuhehaoteBank> subBanks;

}
