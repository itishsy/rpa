package com.seebon.rpa.entity.saas.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LY
 * @date 2023/10/30 19:36
 */
@Data
@NoArgsConstructor
public class OfferPropertyVO {

    private String idCard;

    private String insureDate;

    private String name;

    public OfferPropertyVO(String idCard, String insureDate,String name) {
        this.idCard = idCard;
        this.insureDate = insureDate;
        this.name = name;
    }
}
