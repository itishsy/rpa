package com.seebon.rpa.entity.saas.dto.bill;

import lombok.Data;

import java.io.Serializable;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2024-01-26 16:23
 */
@Data
public class UploadFileDTO implements Serializable {

    private Integer fileId;

    private String fileName;

    private Integer fileType;
}
