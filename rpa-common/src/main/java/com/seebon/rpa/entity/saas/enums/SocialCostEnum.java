package com.seebon.rpa.entity.saas.enums;

import lombok.Getter;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2024-01-17 15:35
 */
@Getter
public enum SocialCostEnum {
    POOR(1,"贫困"),
    UNEMPLOYMENT(2,"失业");
    private Integer code;
    private String name;

    SocialCostEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
