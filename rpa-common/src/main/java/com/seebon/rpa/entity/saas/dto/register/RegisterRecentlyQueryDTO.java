package com.seebon.rpa.entity.saas.dto.register;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class RegisterRecentlyQueryDTO implements Serializable {

    /**
     * 地区
     */
    String addrName;

    /**
     * 业务类型
     */
    Integer businessType;

    /**
     * 申报账户
     */
    String accountNumber;

}
