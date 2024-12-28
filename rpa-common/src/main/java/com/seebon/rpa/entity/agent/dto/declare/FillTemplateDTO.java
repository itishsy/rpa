package com.seebon.rpa.entity.agent.dto.declare;

import lombok.Data;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/7/7 15:34
 * @Version 1.0
 **/
@Data
public class FillTemplateDTO {
    private String addrName;
    private List<TrimExcelTemplateDTO> dtos;
}
