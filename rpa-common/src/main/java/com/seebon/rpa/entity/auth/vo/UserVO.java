package com.seebon.rpa.entity.auth.vo;

import com.seebon.rpa.entity.system.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserVO extends User {

    public UserVO(){}

    public UserVO(Long id, String username, String name, Integer custId){
        super(id,username,name,custId);
    }

    @ApiModelProperty(value = "password")
    private String password;

    @ApiModelProperty(value = "salt")
    private String salt;
}
