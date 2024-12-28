package com.seebon.rpa.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.seebon.rpa.entity.agent.enums.DeclareStatusEnum;
import com.seebon.rpa.entity.agent.enums.DeclareTypeEnum;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author hao
 * @Date 2022/4/27 18:17
 * @Version 1.0
 **/
public class EasyexcelConverter implements Converter<Integer> {
    @Override
    public Class supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Integer convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return null;
    }

    @Override
    public CellData convertToExcelData(Integer integer, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        Map<Integer, String> changeTypeMap = new HashMap<>();

        DeclareTypeEnum[] values1 = DeclareTypeEnum.values();
        for (DeclareTypeEnum item : values1) {
            changeTypeMap.put(item.getCode(), item.getName());
        }

        Map<Integer, String> statusMap = new HashMap<>();

        DeclareStatusEnum[] values = DeclareStatusEnum.values();

        for (DeclareStatusEnum item : values) {
            statusMap.put(item.getCode(), item.getName());
        }
        Field field = excelContentProperty.getField();
        String fieldName = field.getName();
        String excelValue = "";
        if ("changeType".equals(fieldName)) {
            excelValue = changeTypeMap.get(integer);
        }
        if ("status".equals(fieldName)) {
            excelValue = statusMap.get(integer);
        }
        return new CellData(StrUtil.isNotBlank(excelValue) ? excelValue : "---");
    }
}
