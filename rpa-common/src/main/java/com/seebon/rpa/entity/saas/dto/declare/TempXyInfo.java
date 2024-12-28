package com.seebon.rpa.entity.saas.dto.declare;

import lombok.Data;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-30 10:15:15
 */
@Data
public class TempXyInfo {

    private Integer startRowIndex;

    private Integer endRowIndex;

    private Integer startColIndex;

    private Integer endColIndex;

    private Short fontColor;

    private Short backgroundColor;

    public TempXyInfo(Integer startRowIndex, Integer endRowIndex, Integer startColIndex,
                      Integer endColIndex, Short fontColor, Short backgroundColor){
        this.startRowIndex = startRowIndex;
        this.endRowIndex = endRowIndex;
        this.startColIndex = startColIndex;
        this.endColIndex = endColIndex;
        this.fontColor = fontColor;
        this.backgroundColor = backgroundColor;
    }

}
