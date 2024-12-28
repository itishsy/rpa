package com.seebon.rpa.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApiModel(description = "分页对象")
@Getter
@Setter
public class IgRequestObject {
    @ApiModelProperty(value = "页数", dataType = "Integer")
    private Integer page = 1;

    @ApiModelProperty(value = "每页条数", dataType = "Integer")
    private Integer size = 20;

    @ApiModelProperty(value = "开始值", dataType = "Integer")
    private Integer start = 0;

    @ApiModelProperty(value = "sidx", dataType = "String")
    private String sidx;

    @ApiModelProperty(value = "排序", dataType = "String")
    private String sort;

    @ApiModelProperty(value = "页面数", dataType = "String")
    private String $inlinecount;

    @ApiModelProperty(value = "filters", dataType = "List")
    private List<IgFilter> filters;

    @ApiModelProperty(value = "query", dataType = "List")
    private List<IgQuery> query;

    public void wrapExportParam(Map<String, Object> params){
        this.setSize(10000);
        this.setPage(0);
        this.setQuery(new ArrayList<>());
        if(params!=null && params.size()>0){
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                this.getQuery().add(new IgQuery(entry.getKey(), entry.getValue()));
            }
        }
    }
}
