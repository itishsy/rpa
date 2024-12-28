package com.seebon.rpa.entity.saas.enums.declare;

/**
 * @Author: tanyong
 * @Description:
 * @Date: 2022/11/28 20:20
 * @Modified By:
 */
public enum DataPresetEnum {
    SyscSpfDataTypePower(101), // 政策指引目录需要排除的数据
    AreaDataParseType(104); // 身份证地址解析特殊配置

    private Integer type;

    DataPresetEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
