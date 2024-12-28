package com.seebon.rpa.entity.robot.vo;

import lombok.Data;

@Data
public class OfferCheckVO {

    private String idCard;

    private String personNo;

    private String status;

    public OfferCheckVO() {
    }

    public OfferCheckVO(String idCard,String status) {
        this.idCard = idCard;
        this.status = status;
    }

    public OfferCheckVO(String idCard,String personNo, String status) {
        this.idCard = idCard;
        this.personNo = personNo;
        this.status = status;
    }

}
