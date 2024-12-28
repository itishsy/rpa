package com.seebon.rpa.entity.robot.device.response;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {

    private int records;

    private List<T> rows;

    public PageResponse(List<T> rows, int records) {
        this.rows = rows;
        this.records = records;
    }
}
