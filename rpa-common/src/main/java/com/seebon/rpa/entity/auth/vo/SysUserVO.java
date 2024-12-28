package com.seebon.rpa.entity.auth.vo;

import com.seebon.common.utils.EmailUtils;
import com.seebon.rpa.entity.auth.po.SysRole;
import com.seebon.rpa.entity.auth.po.SysUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 18:10:01
 */
@ApiModel("用户vo")
@Data
public class SysUserVO extends SysUser {

    @ApiModelProperty("使用角色")
    private List<SysRole> roles;

    @ApiModelProperty("负责申报账户数")
    private Integer declareAccountNumber;

    @ApiModelProperty("负责城市数")
    private Integer cityNumber;

    private List<String> moduleCodes;

    @ApiModelProperty("邮箱列表")
    private List<String> emails;

    public List<String> getEmails() {
        if (this.emails != null) {
            return this.emails;
        } else if(StringUtils.isNotEmpty(this.getEmail())) {
            this.setEmails(Arrays.asList(this.getEmail().split(",")));
        }
        return this.emails;
    }

    /**
     * 蒋邮箱列表转成以逗号分隔的字符串
     * @return
     */
    public String getEmailsStr(){
        if(this.getEmails() != null && this.getEmails().size()>0){
            return String.join(",",this.getEmails());
        }
        return null;
    }

}
