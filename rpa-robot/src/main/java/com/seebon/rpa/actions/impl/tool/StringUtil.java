package com.seebon.rpa.actions.impl.tool;

import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.JsonUtils;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ObjectTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.StringMethod;
import com.seebon.rpa.context.runtime.RobotConfigException;
import com.seebon.rpa.utils.ELParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@ActionUtils
@RobotAction(name = "字符操作", targetType = ObjectTarget.class, comment = "字符操作")
public class StringUtil extends AbstractAction {

    @Conditions({
            "split:actionArgs,resultKey",
            "contains:actionArgs,resultKey",
            "replace:actionArgs,resultKey",
            "infoExtraction:templateText,splitText,infoMap,resultKey",
            "join:stringList,joinChar,resultKey",
            "encode:actionArgs,resultKey",
            "getPosition:actionArgs,resultKey",
            "regReplace:regularStr,newStr,resultKey"
    })
    @ActionArgs(value = "操作类型", required = true, dict = StringMethod.class)
    private String actionType;

    @ActionArgs(value = "操作参数")
    private String actionArgs;

    @ActionArgs(value = "信息提取模板", required = true)
    private String templateText;

    @ActionArgs(value = "数据分隔符")
    private String splitText;

    @ActionArgs(value = "提取内容信息", required = true)
    private Map<String, Integer> infoMap;

    @ActionArgs(value = "字符集合", required = true)
    private List<String> stringList;

    @ActionArgs(value = "拼接符")
    private String joinChar;

    @ActionArgs(value = "正则表达式")
    private String regularStr;

    @ActionArgs(value = "替换内容")
    private String newStr;

    @ActionArgs(value = "结果变量")
    private String resultKey;


    @Override
    public void run() {
        Object str = getTarget();
        switch (StringMethod.valueOf(actionType)) {
            case split: {
                if (StringUtils.isBlank(actionArgs) || StringUtils.isBlank(resultKey)) {
                    throw new RobotConfigException("字符串切分方法，参数不能为空");
                }
                ctx.setVariable(resultKey, split(str.toString(), actionArgs));
                break;
            }
            case join: {
                if (CollectionUtils.isEmpty(stringList) || StringUtils.isBlank(resultKey)) {
                    throw new RobotConfigException("参数不能为空");
                }

                ctx.setVariable(resultKey, "\"" + StringUtils.join(stringList, "\" \"") + "\"");
                break;
            }
            case contains: {
                if (StringUtils.isBlank(actionArgs) || StringUtils.isBlank(resultKey)) {
                    throw new RobotConfigException("字符串包含方法，参数不能为空");
                }
                ctx.setVariable(resultKey, contains(str.toString(), actionArgs));
                break;
            }
            case replace: {
                String[] arr = actionArgs.split(",");
                if (StringUtils.isBlank(actionArgs) || arr.length != 2) {
                    throw new RobotConfigException("字符串替换方法，参数格式为:oldStr,newStr");
                }
                ctx.setVariable(resultKey, replace(str.toString(), arr[0], arr[1]));
                break;
            }
            case infoExtraction: {
                List<Map<String, String>> result = Lists.newArrayList();
                if (StringUtils.isNotBlank(str.toString())) {
                    List<String> fails = Lists.newArrayList();
                    if (StringUtils.isNotBlank(splitText)) {
                        fails = Lists.newArrayList(str.toString().split(splitText));
                    } else {
                        fails = Lists.newArrayList(str.toString());
                    }
                    for (int i = 0; i < fails.size(); i++) {
                        String info = fails.get(i).trim();
                        Pattern pattern = Pattern.compile(templateText);
                        Matcher matcher = pattern.matcher(info);
                        if (matcher.find()) {
                            Map<String, String> itemInfo = Maps.newHashMap();
                            if (infoMap != null && !infoMap.isEmpty()) {
                                try {
                                    for (String key : infoMap.keySet()) {
                                        String msg = matcher.group(infoMap.get(key));
                                        itemInfo.put(key, msg);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (!itemInfo.isEmpty()) {
                                result.add(itemInfo);
                            }
                        }
                    }
                }
                ctx.setVariable(resultKey, result);
                break;
            }
            case encode: {
                String enCodeVal = this.encode(actionArgs);
                log.info("encode=" + enCodeVal);
                ctx.setVariable(resultKey, enCodeVal);
                break;
            }
            case getPosition: {
                int index = str.toString().indexOf(actionArgs);
                ctx.setVariable(resultKey, index);
                break;
            }
            case regReplace: {
                String s = str.toString();
                log.info("target=" + s);
                String parse = parse(newStr, String.class);
                log.info("newStr=" + parse);
                ctx.setVariable(resultKey, replaceAll(s, regularStr, parse));
            }
            default:
                break;
        }
    }

    public String[] split(String str, String regex) {
        return str.split(regex);
    }

    public boolean contains(String str, String keyword) {
        return str.contains(keyword);
    }

    public String replace(String str, String oldStr, String newStr) {
        if(StringUtils.isBlank(str)){
            return StringUtils.EMPTY;
        }
        return str.replace(oldStr, newStr);
    }
    public String replaceAll(String str, String oldStr, String newStr) {
        if(StringUtils.isBlank(str)){
            return StringUtils.EMPTY;
        }
        return str.replaceAll(oldStr, newStr);
    }

    public String list2String(List<Map<String, Object>> list) {
        return JsonUtils.toJSon(list);
    }

    public List<Map<String, Object>> json2List(String json){
        if (StringUtils.isNotBlank(json)) {
            try {
                List<Map> maps = JsonUtils.strToList(json, Map.class);
                return maps.stream().map(item -> (Map<String, Object>) item).collect(Collectors.toList());
            }catch (Exception e) {
                return Lists.newArrayList();
            }
        } else {
            return Lists.newArrayList();
        }
    }

    public Integer str2Number(String str) {
        return Integer.valueOf(str);
    }

    public String number2ChineseUpper(String number) {
        return NumberChineseFormatter.format(new Double(number), true, true);
    }

    public String max(String... numbers) {
        List<String> nbs = Lists.newArrayList(numbers);
        nbs.sort((o1, o2) -> {
            Double aDouble = Double.valueOf(o1);
            Double aDouble1 = Double.valueOf(o2);
            return aDouble1 - aDouble > 0d ? 1 : -1;
        });
        return nbs.get(0);
    }

    public String getFirstContentByRegex(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        m.find();
        return m.group();
    }

    public String numberFormat(String number, String pattern) {
        return new DecimalFormat(pattern).format(new BigDecimal(number));
    }

    private boolean matchValue(List<Map<String, Object>> list, String value) {
        if (StringUtils.isBlank(value) || CollectionUtils.isEmpty(list)) {
            return false;
        }
        //匹配规则：1-equals 2-contains  3-startsWith 4-endsWith
        Boolean isMatch = false;
        for (Map<String, Object> map : list) {
            Integer matchRule = (Integer) map.get("matchRule");
            String resultMsg = (String) map.get("resultMsg");
            if ("1".equals(matchRule)) {
                if (value.equals(resultMsg)) {
                    isMatch = true;
                }
            }
            if ("2".equals(matchRule)) {
                if (value.contains(resultMsg)) {
                    isMatch = true;
                }
            }
            if ("3".equals(matchRule)) {
                if (value.startsWith(resultMsg)) {
                    isMatch = true;
                }
            }
            if ("4".equals(matchRule)) {
                if (value.endsWith(resultMsg)) {
                    isMatch = true;
                }
            }
        }
        return isMatch;
    }

    public boolean isBlank(String value) {
        log.info("isBlank value=" + value);
        return StringUtils.isBlank(value);
    }

    public boolean isNotBlank(String value) {
        log.info("isNotBlank value=" + value);
        return StringUtils.isNotBlank(value);
    }

    public String encode(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        Map<String, Object> map = ELParser.parse(value, ctx.getVariables(), Map.class);
        try {
            return DatatypeConverter.printBase64Binary(JSON.toJSONString(map).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("encode异常" + e.getMessage(), e);
        }
        return null;
    }

    public boolean isNumber(String str) {
        return NumberUtil.isNumber(str);
    }

    public static int getPosition(String text, String str) {
        return text.indexOf(str);
    }

    public static String subAfterLast(String text, String separator) {
        return StringUtils.substringAfterLast(text, separator);
    }

    public static String join(List<String> list, String joinChar) {
        return StringUtils.join(list, joinChar);
    }
}
