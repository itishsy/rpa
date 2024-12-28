package com.seebon.rpa.context.enums;

import com.seebon.rpa.context.annotation.DataDictKey;
import org.apache.commons.lang3.StringUtils;

/**
 * @author LY
 * @date 2023/10/26 16:47
 */

@DataDictKey(value = "1098", name = "操作类型")
public enum OfferOperateType {

    increase("增员", 1),

    subtract("减员", 2),

    supplementing("补缴", 5),

    baseAdjust("调基", 3),

    doCheck("核验", 6);

    String name;

    Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    OfferOperateType(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static OfferOperateType getOfferOperateType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        for (OfferOperateType item : OfferOperateType.values()) {
            if (item.equals(OfferOperateType.valueOf(name))) {
                return item;
            }
        }
        return null;
    }
}
