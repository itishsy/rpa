package com.seebon.rpa.actions.impl.data;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.GroupBy;
import com.seebon.rpa.context.enums.ListAction;
import com.seebon.rpa.utils.ELParser;
import com.seebon.rpa.utils.ELStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RobotAction(name = "列表操作", order = 10)
public class ListOperate extends AbstractAction {

    private static final String group_item_key = "groupName";
    private static final String group_item_value = "list";

    @Conditions({"remove:index",
            "removeByCond:cond",
            "get:index,dataKey",
            "filter:cond,dataKey",
            "update:cond,updateList,updates",
            "add:addObj",
            "addAll:addAllOthersKey",
            "orderFiled: orderFiledName,orderType,dataKey",
            "addItem:addObj,fieldObj",
            "getField:cond,fieldName,dataKey",
            "group:groupBy,groupValueKey,itemKeyName,itemValueName,size,dataKey",
            "distinct: distinctKey,dataKey",
            "map: itemKeyName,itemValueName,dataKey",
            "merge:onlyFieldName,mergeName,dataKey",
            "decode:fieldName",
            "addAllJoin:dataKey"
    })
    @ActionArgs(value = "操作类型", required = true, dict = ListAction.class)
    private String actionType;

    @ActionArgs(value = "方法参数")
    private String actionArgs;

    @ActionArgs(value = "条件列表")
    private List<Map<String, Object>> updateList;

    @ActionArgs(value = "字段对象")
    private Map<String, Object> addObj;

    @ActionArgs(value = "字段操作")
    private Map<String, Object> fieldObj;

    @ActionArgs(value = "修改信息", required = true, comment = "Json")
    private Map<String, Object> updates;

    @ActionArgs(value = "序号")
    private String index;

    @ActionArgs(value = "获取字段", required = true)
    private String fieldName;

    @ActionArgs(value = "唯一性字段", required = true)
    private String onlyFieldName;

    @ActionArgs(value = "合并字段", required = true)
    private String mergeName;

    @ActionArgs(value = "排序字段", required = true)
    private String orderFiledName;

    @ActionArgs(value = "排序类型", comment = "1：升序，0：降序", required = true)
    private String orderType;

    @ActionArgs(value = "条件")
    private String cond;

    @ActionArgs(value = "分组类型", dict = GroupBy.class)
    private String groupBy;

    @ActionArgs(value = "分组字段")
    private String groupValueKey;

    @ActionArgs(value = "分组对象key名")
    private String itemKeyName;

    @ActionArgs(value = "分组对象value名")
    private String itemValueName;

    @ActionArgs(value = "分批大小")
    private Integer size;

    @ActionArgs(value = "去重字段", required = true, comment = "字段值相同时去重")
    private String distinctKey;

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;

    @ActionArgs(value = "目标数组key", required = true)
    private String addAllOthersKey;

    @Override
    public void run() {
        List<Object> list = getTarget();
        switch (ListAction.valueOf(actionType)) {
            case remove: {
                String[] ss = index.split(",");
                for (String s : ss) {
                    if (NumberUtil.isInteger(s)) {
                        Integer index = Integer.parseInt(s);
                        list.remove(index);
                    }
                }
                break;
            }
            case removeByCond: {
                final int size = list.size();
                for (int i = size - 1; i >= 0; i--) {
                    Map<String, Object> item = (Map<String, Object>) list.get(i);
                    variable("item", item);
                    if (parse(cond, Boolean.class)) {
                        list.remove(i);
                    }
                }
                break;
            }
            case get: {
                String[] ss = index.split(",");
                int len = ss.length;
                if (len == 1) {
                    ctx.setVariable(dataKey, list.get(Integer.parseInt(ss[0])));
                } else {
                    List<Object> objects = new ArrayList<>();
                    for (String s : ss) {
                        if (NumberUtil.isInteger(s)) {
                            objects.add(list.get(Integer.parseInt(s)));
                        }
                    }
                    ctx.setVariable(dataKey, objects);
                }
                break;
            }
            case add: {
                list.add(addObj);
                System.out.println(list + "添加的数据");
                break;
            }
            case addItem: {
                for (Object o : list) {
                    Map<String, Object> obj = (Map<String, Object>) o;
                    if (addObj != null && !addObj.isEmpty()) {
                        for (Map.Entry<String, Object> entry : addObj.entrySet()) {
                            obj.put(entry.getKey(), entry.getValue());
                        }
                    }
                    if (fieldObj != null && !fieldObj.isEmpty()) {
                        for (Map.Entry<String, Object> entry : fieldObj.entrySet()) {
                            String[] split = entry.getValue().toString().split(",");
                            Double sum = 0d;
                            for (String s : split) {
                                sum += Double.valueOf(obj.get(s).toString());
                            }
                            obj.put(entry.getKey(), sum.toString());
                        }
                    }

                }
                break;
            }
            case getField: {
                List<Map<String, Object>> castList = list.stream().map(item -> (Map<String, Object>) item).collect(Collectors.toList());
                Set<Object> result = Sets.newHashSet();
                if (CollectionUtils.isNotEmpty(castList)) {
                    if (StringUtils.isNotBlank(cond)) {

                        result = ELStream.of(castList).filter(cond, ctx.getVariables()).list().stream().
                                map(item -> item.get(fieldName)).collect(Collectors.toSet());
                    } else {
                        result = castList.stream().map(item -> item.get(fieldName)).collect(Collectors.toSet());
                    }
                }
                ctx.setVariable(dataKey, Lists.newArrayList(result));
                break;
            }
            case order: {
                CollUtil.reverse(list);
                break;
            }
            case orderFiled: {
                List<Map<String, Object>> castList = list.stream().map(item -> (Map<String, Object>) item).collect(Collectors.toList());
                if ("1".equals(orderType)) { // 升序
                    castList.sort((o1, o2) -> Double.valueOf((String) o1.get(orderFiledName)) - Double.valueOf((String) o2.get(orderFiledName)) > 0 ? 1 : -1);
                } else {
                    castList.sort((o1, o2) -> Double.valueOf((String) o2.get(orderFiledName)) - Double.valueOf((String) o1.get(orderFiledName)) > 0 ? 1 : -1);
                }
                ctx.setVariable(dataKey, castList);
                break;
            }
            case filter: {
                List<Map<String, Object>> castList = list.stream().map(item -> (Map<String, Object>) item).collect(Collectors.toList());
                List<Map<String, Object>> res = ELStream.of(castList).filter(cond, ctx.getVariables()).list();
                ctx.setVariable(dataKey, Lists.newArrayList(res));
                log.info("filter resultMap=" + JSON.toJSONString(Lists.newArrayList(res)));
                break;
            }
            case update: {
                final int size = list.size();
                for (int i = 0; i < size; i++) {
                    Map<String, Object> item = (Map<String, Object>) list.get(i);
                    ctx.setVariable("row", item);
                    boolean flag;
                    if (StringUtils.isBlank(cond)) {
                        flag = true;
                    } else {
                        if (updateList != null && updateList.size() > 0) {
                            flag = ELStream.of(updateList).contains(cond, ctx.getVariables());
                            if (flag) {
                                for (Map<String, Object> map : updateList) {
                                    ctx.setVariable("map", map);
                                    if (ELParser.parse(cond, ctx.getVariables(), Boolean.class)) {
                                        break;
                                    }
                                }
                            }
                        } else {
                            flag = ELParser.parse(cond, ctx.getVariables(), Boolean.class);
                        }
                    }

                    if (flag) {
                        List<String> updateKeys = Lists.newArrayList(updates.keySet());
                        reloadUpdates();
                        updateKeys.stream().forEach(key -> {
                            /*   Object val = ELParser.parseObject(updates.get(key), ctx.getVariables(), null);*/
                            String _key = ELParser.parse(key, ctx.getVariables(), String.class);
                            Object val = ELParser.parseObject(updates.get(_key), ctx.getVariables(), null);
                            item.put(_key, val);
                        });
                    }
                    ctx.remove("row");
                    ctx.remove("map");
                }
                break;
            }
            case group: {
                if (GroupBy.valueOf(groupBy).equals(GroupBy.byValue)) {
                    if (StringUtils.isBlank(itemKeyName)) {
                        itemKeyName = group_item_key;
                    }
                    if (StringUtils.isBlank(itemValueName)) {
                        itemValueName = group_item_value;
                    }
                    List<Map<String, Object>> castList = list.stream().map(item -> (Map<String, Object>) item).collect(Collectors.toList());
                    Map<String, List<Map<String, Object>>> groupMap = Maps.newHashMap();
                    if (groupValueKey.contains(",")) {
                        groupMap = castList.stream().collect(Collectors.groupingBy(item -> (String) item.get(groupValueKey.split(",")[0]) + "," + (String) item.get(groupValueKey.split(",")[1])));
                    } else {
                        groupMap = castList.stream().collect(Collectors.groupingBy(item -> (String) item.get(groupValueKey)));
                    }
                    List<Map<String, Object>> result = Lists.newArrayList();
                    List<String> keys = Lists.newArrayList(groupMap.keySet());
                    for (String key : keys) {
                        Map<String, Object> item = Maps.newHashMap();
                        item.put(itemKeyName, key);
                        item.put(itemValueName, groupMap.get(key));
                        result.add(item);
                    }
                    ctx.setVariable(dataKey, result);
                } else if (GroupBy.valueOf(groupBy).equals(GroupBy.bySize)) {
                    List<List<Map<String, Object>>> result = Lists.newArrayList();
                    List<Map<String, Object>> castList = list.stream().map(item -> (Map<String, Object>) item).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(castList)) {
                        int page = castList.size() % size > 0 ? (castList.size() / size + 1) : castList.size() / size;
                        for (int i = 0; i < page; i++) {
                            int toIndex = (i + 1) * size > castList.size() ? castList.size() : (i + 1) * size;
                            result.add(Lists.newArrayList(castList.subList(i * size, toIndex)));
                        }
                    }
                    ctx.setVariable(dataKey, result);
                }
                break;
            }
            case map: {
                Map<String, Object> resultMap = Maps.newHashMap();
                final int size = list.size();
                for (int i = size - 1; i >= 0; i--) {
                    Map<String, Object> item = (Map<String, Object>) list.get(i);
                    ctx.getVariables().put("item", item);
                    resultMap.put(parseObject(itemKeyName).toString(), parseObject(itemValueName));
                }
                ctx.getVariables().remove("item");
                ctx.setVariable(dataKey, resultMap);
                log.info("map resultMap=" + JSON.toJSONString(resultMap));
                break;
            }
            case distinct: {
                List<Map<String, Object>> distinctList = list.stream().map(item -> (Map<String, Object>) item).collect(Collectors.collectingAndThen
                        (Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> (String) o.get(distinctKey)))), ArrayList::new));
                ctx.setVariable(dataKey, distinctList);
                break;
            }
            case merge: {
                List<Map<String, Object>> castList = list.stream().map(item -> (Map<String, Object>) item).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(castList)) {
                    Map<String, Object> first = castList.get(0);
                    if (containsKey(first)) {
                        List<Map<String, Object>> result = Lists.newArrayList();
                        Map<String, List<Map<String, Object>>> groupMap = castList.stream().collect(Collectors.groupingBy(item -> (String) item.get(onlyFieldName)));
                        Set<String> idCards = groupMap.keySet();
                        idCards.forEach(idCard -> {
                            List<Map<String, Object>> maps = groupMap.get(idCard);
                            if (maps.size() > 1) {
                                Map<String, Object> groupFirst = maps.get(0);
                                if (mergeName.contains(".")) {
                                    String[] split = mergeName.split("\\.");
                                    List<Map<String, Object>> mergeObjList = maps.stream().filter(it -> {
                                        Map<String, Object> o = (Map<String, Object>) it.get(split[0]);
                                        return o != null;
                                    }).map(it -> {
                                        return (Map<String, Object>) it.get(split[0]);
                                    }).collect(Collectors.toList());
                                    if (CollectionUtils.isNotEmpty(mergeObjList)) {
                                        List<String> mergeValues = mergeObjList.stream().map(it -> {
                                            return (String) it.get(split[1]);
                                        }).collect(Collectors.toList());
//                                        groupFirst.put(mergeName, StringUtils.join(mergeValues, "|"));
                                        if (CollectionUtils.isNotEmpty(mergeValues)) {
                                            ((Map<String, Object>) groupFirst.get(split[0])).put(split[1], StringUtils.join(mergeValues, "|"));
                                        }
                                    }
                                } else {
                                    List<String> mergeValues = maps.stream().filter(it -> {
                                        String o = (String) it.get(mergeName);
                                        return StringUtils.isNotBlank(o);
                                    }).map(it -> {
                                        return (String) it.get(mergeName);
                                    }).collect(Collectors.toList());
                                    if (CollectionUtils.isNotEmpty(mergeValues)) {
                                        groupFirst.put(mergeName, StringUtils.join(mergeValues, "|"));
                                    }
                                }
                                result.add(groupFirst);
                            } else {
                                result.addAll(maps);
                            }
                        });
                        ctx.setVariable(dataKey, result);
                    } else {
                        ctx.setVariable(dataKey, castList);
                    }
                }
                break;
            }
            case addAll:
                list.addAll(ctx.getVariableToList(addAllOthersKey));
                break;
            case decode:
                for (Object item : list) {
                    Map<String, Object> dataMap = (Map<String, Object>) item;
                    Object valueObj = dataMap.get(fieldName);
                    if (valueObj == null) {
                        continue;
                    }
                    Base64.Decoder decoder = Base64.getDecoder();
                    String value = new String(decoder.decode(valueObj.toString()));
                    dataMap.put(fieldName, value);
                }
                break;
            case addAllJoin:
                List<Object> dataList = ctx.get(dataKey);
                if (CollectionUtils.isEmpty(dataList)) {
                    if (CollectionUtils.isEmpty(list)) {
                        ctx.setVariable(dataKey, Lists.newArrayList());
                    } else {
                        ctx.setVariable(dataKey, list);
                    }
                } else {
                    if (CollectionUtils.isNotEmpty(list)) {
                        dataList.addAll(list);
                    }
                    ctx.setVariable(dataKey, dataList);
                }
                break;
            default:
                break;
        }
    }

    private boolean containsKey(Map<String, Object> first) {
        if (mergeName.contains(".")) {
            String[] split = mergeName.split("\\.");
            return first.get(split[0]) != null && ((Map<String, Object>) first.get(split[0])).keySet().contains(split[1]);
        } else {
            return first.keySet().contains(mergeName);
        }
    }


    /**
     * 判断是否有表达式  如果有  则支持
     */
    private void reloadUpdates() {
        if (MapUtils.isNotEmpty(updates)) {
            if (!updates.keySet().toString().contains("$")) return;
            LinkedHashMap<String, Object> newMap = Maps.newLinkedHashMapWithExpectedSize(updates.size());
            for (String key : updates.keySet()) {
                newMap.put(ELParser.parse(key, ctx.getVariables(), String.class), updates.get(key));//key值支持表达式
            }
            updates = newMap;
        }
    }
}


