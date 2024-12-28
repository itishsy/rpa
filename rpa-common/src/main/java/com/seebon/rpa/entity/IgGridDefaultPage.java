package com.seebon.rpa.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by Martin on 2016/6/6.
 * igGrid的分页对象
 */
@Data
@ApiModel(value = "igGridDefaultPage对象", description = "igGrid的分页对象igGridDefaultPage")
public class IgGridDefaultPage<T> {

    @ApiModelProperty(name = "records", value = "总记录数")
    private int records;

    @ApiModelProperty(name = "rows", value = "当前页的记录")
    private List<T> rows;


    public IgGridDefaultPage(List<T> rows, int records) {
        this.records = records;
        this.rows = rows;
    }

    public IgGridDefaultPage() {

    }
}
