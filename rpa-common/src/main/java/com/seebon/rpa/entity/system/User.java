package com.seebon.rpa.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
public class User implements Serializable {

    public User(){

    }

    public User(Long id, String username, String name, Integer custId){
        this.id = id;
        this.username = username;
        this.name = name;
        this.custId = custId;
    }

    public User(Long id, String username, String name, Integer custId, List<String> roles){
        this.id = id;
        this.username = username;
        this.name = name;
        this.custId = custId;
        this.roles = roles;
    }

    public User(Long id, String username, String name, Integer custId, String custName, List<String> roles, Integer userType, Integer mainUser){
        this.id = id;
        this.username = username;
        this.name = name;
        this.custId = custId;
        this.roles = roles;
        this.userType = userType;
        this.mainUser = mainUser;
        this.custName = custName;
    }

    private long id;

    private String username;

    private String name;

    private Integer custId;

    private String custName;

    private Integer userType;

    private Integer mainUser;

    private Integer expiresIn;

    private Integer userCategory;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE ,pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh",timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Column
    @ApiModelProperty(value="用户手机号码")
    private String phoneNumber;


    @Column
    @ApiModelProperty(value="用户电子邮箱")
    private String email;

    @Column
    @ApiModelProperty(value="备注")
    private String comment;

    private List<String> roles;

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
