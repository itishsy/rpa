package com.seebon.rpa.entity.robot.vo;

import lombok.Data;

/**
 * @Author hao
 * @Date 2022/8/25 10:33
 * @Version 1.0
 **/
@Data
public class OcrReqVO {
    /**
     * OCR识别方式：text-仅文本，detail-详细信息
     */
    private String ocrType;

    /**
     * 文件字节
     */
    private byte[] bytes;
}
