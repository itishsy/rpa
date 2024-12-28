package com.seebon.rpa.entity.saas.po.system;

import com.seebon.rpa.entity.Identity;
import lombok.Data;

/**
 * @Author: tanyong
 * @Description:
 * @Date: 2022/11/28 20:27
 * @Modified By:
 */
@Data
public class SysDataPreset extends Identity {
    /**
     * DataPresetEnum枚举
     */
    private Integer type;

    /**
     * 内容
     */
    private String context;

    /**
     * 描述
     */
    private String comment;
}
