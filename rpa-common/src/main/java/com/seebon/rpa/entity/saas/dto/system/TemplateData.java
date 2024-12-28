package com.seebon.rpa.entity.saas.dto.system;

import lombok.Data;

@Data
public class TemplateData {
    private String value;
    private String color;

    public TemplateData(String value) {
        this.value = value;
        this.color="#173177";
    }
}
