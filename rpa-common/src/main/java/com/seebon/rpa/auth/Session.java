package com.seebon.rpa.auth;

import com.seebon.rpa.entity.system.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class Session extends User {

    public Session(String sessionId, User user){
        setSessionId(sessionId);
        BeanUtils.copyProperties(user,this);
    }

    public Session(User user){
        BeanUtils.copyProperties(user,this);
    }

    private String sessionId;

    private Date loginTime;

    private String loginIp;

}
