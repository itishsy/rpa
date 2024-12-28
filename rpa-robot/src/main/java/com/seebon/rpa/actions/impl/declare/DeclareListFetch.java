package com.seebon.rpa.actions.impl.declare;

import com.google.common.collect.Maps;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@RobotAction(name = "申报数据补全", order = 40, targetType = NoneTarget.class, comment = "申报数据补全")
public class DeclareListFetch extends AbstractAction {

    @ActionArgs(value = "数据源列表", comment = "数据源列表", style = DynamicFieldType.text)
    private List<Map<String, Object>> sourceList;

    @ActionArgs(value = "更新数据列表", comment = "更新数据列表", style = DynamicFieldType.text)
    private List<Map<String, Object>> updateList;

    @ActionArgs(value = "数据源匹配属性名", comment = "数据源匹配属性名", required = true, style = DynamicFieldType.text)
    private String sourceFieldName;

    @ActionArgs(value = "更新数据匹配属性名", comment = "更新数据匹配属性名", required = true, style = DynamicFieldType.text)
    private String updateFieldName;

    @ActionArgs(value = "数据源获取属性名", comment = "数据源获取属性名", style = DynamicFieldType.text)
    private String getFieldName;

    @ActionArgs(value = "更新数据添加属性名", comment = "更新数据添加属性名", style = DynamicFieldType.text)
    private String setFieldName;

    @Override
    public void run() {
        Map<String, Object> dataMap = Maps.newHashMap();
        for (Map<String, Object> sourceMap : sourceList) {
            Object sourceFieldValue = sourceMap.get(sourceFieldName);
            if (sourceFieldValue == null) {
                continue;
            }
            Object getFieldValue = sourceMap.get(getFieldName);
            dataMap.put(sourceFieldValue.toString(), getFieldValue);
        }
        for (Map<String, Object> updateMap : updateList) {
            Object updateFieldValue = updateMap.get(updateFieldName);
            if (updateFieldValue == null) {
                continue;
            }
            Object dataValue = dataMap.get(updateFieldValue.toString());
            if (dataValue == null) {
                continue;
            }
            updateMap.put(setFieldName, dataValue);
        }
    }
}
