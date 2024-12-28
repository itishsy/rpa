package com.seebon.rpa.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * el表达式解析器
 */
@Slf4j
public class ELParser {
    private static DefaultConversionService typeConverter = new DefaultConversionService();

    public static <T> T parse(String str, Map<String, Object> vars, Class<T> expectedType) {
        Object obj = parseObject(str, vars, expectedType);
        return typeConverter.convert(obj, expectedType);
    }

    /**
     * 字符串表达式解析成对象
     *
     * @param obj  需要解析的对象
     * @param vars 用于解析对象的变量集合
     * @return 解析结果
     */
    public static Object parseObject(Object obj, Map<String, Object> vars, Class clazz) {
        try {
            if (obj instanceof String) {
                final String expStr = obj.toString().trim();
                Map<String, Object> map = convertToMap(expStr, clazz);
                if (map != null) {
                    return parseObject(map, vars, clazz);
                } else if (isExpression(expStr)) {
                    try {
                        return getValue(expStr, Object.class, vars);
                    } catch (Exception e) {
                        return expStr;
                    }
                } else if (hasExpression(expStr)) {
                    return parseString(expStr, vars);
                }
            } else if (obj instanceof Map) {
                Map map = (Map) obj;
                parseMapValue(map, vars);
                return map;
            } else if (obj instanceof List) {
                List list = (List) obj;
                parseListValue(list, vars);
                return list;
            }
            return obj;
        } catch (Exception e) {
            log.warn("parse object " + obj + " error:" + e.getMessage());
            return obj;
        }
    }

    private static Map<String, Object> convertToMap(String jsonString, Class clazz) {
        Map<String, Object> map = null;
        try {
            if (jsonString.startsWith("{")) {
                map = JSONObject.parseObject(jsonString, clazz==null?Map.class:clazz);
            }
        } catch (Exception e) {
            log.info("json to map failed." + e.getMessage());
        }
        return map;
    }

    /**
     * 解析Map的value
     *
     * @param map
     * @param ctx
     */
    private static void parseMapValue(Map<String, Object> map, Map<String, Object> ctx) {
        Map<String, Object> ctxMap = Maps.newHashMap(ctx);
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            Object value = map.get(key);
            if (hasExpression(key)) {
//                map.remove(key);
                key = parseString(key, ctxMap);
            }
            if (value instanceof Map) {
                parseMapValue((Map<String, Object>) value, ctx);
            } else if (value instanceof String) {
                String valStr = value.toString();
                if (isExpression(valStr)) {
                    map.put(key, parseObject(valStr, ctxMap, null));
                } else if (hasExpression(valStr)) {
                    map.put(key, parseString(valStr, ctxMap));
                }
            } else if (value instanceof List || value instanceof JSONArray) {
                List list = Lists.newArrayList();
                List array = (List) value;
                for (Object item : array) {
                    if (item instanceof String) {
                        String valStr = item.toString();
                        if (isExpression(valStr)) {
                            list.add(parseObject(valStr, ctxMap, null));
                        } else if (hasExpression(valStr)) {
                            list.add(parseString(valStr, ctxMap));
                        } else {
                            list.add(item);
                        }
                    } else if (item instanceof Map) {
                        parseMapValue((Map<String, Object>) item, ctx);
                        list.add(item);
                    }
                }
                map.put(key, list);
            }
        }
    }

    /**
     * 解析List的value
     *
     * @param list
     * @param vars
     */
    private static void parseListValue(List<Object> list, Map<String, Object> vars) {
        List<Object> objects = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) object;
                parseMapValue(map, vars);
                objects.add(map);
            } else if (object instanceof String) {
                String str = object.toString();
                if (isExpression(str)) {
                    objects.add(getValue(str, Object.class, vars));
                } else if (hasExpression(str)) {
                    objects.add(parseString(str, vars));
                } else {
                    objects.add(str);
                }
            } else if (object instanceof List) {
                parseListValue((ArrayList) object, vars);
            } else {
                objects.add(object);
            }
        }
        list = objects;
    }


    private static String parseString(final String str, Map<String, Object> ctx) {

        StringBuilder sb = new StringBuilder();
        String expression = str;
        Matcher m = Pattern.compile("\\$\\{").matcher(expression);
        boolean flag = true;
        while (m.find()) {
            if (flag) {
                sb.append(expression.substring(0, m.start()));
                expression = expression.substring(m.start());
                m = Pattern.compile("\\}").matcher(expression);
                flag = false;
            } else {
                String expr = expression.substring(0, m.end());
                String value = "";
                try {
                    value = getValue(expr, String.class, ctx);
                } catch (Exception e) {
                    value = expr;
                }
                sb.append(value);
                expression = expression.substring(m.end());
                m = Pattern.compile("\\$\\{").matcher(expression);
                flag = true;
            }
        }
        sb.append(expression);
        return sb.toString();
    }


    /**
     * 解析表达式
     *
     * @param expression
     * @param expectedType
     * @param vars
     * @param <T>
     * @return
     */
    private static <T> T getValue(String expression, Class<T> expectedType, Map<String, Object> vars) {
        TemplateParserContext templateParserContext = new TemplateParserContext("${", "}");
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        MapAccessor propertyAccessor = new MapAccessor();
        evaluationContext.setVariables(vars);
        evaluationContext.setPropertyAccessors(Arrays.asList(propertyAccessor));
        SpelExpression spelExpression = (SpelExpression) parser.parseExpression(expression, templateParserContext);
        spelExpression.setEvaluationContext(evaluationContext);
        return spelExpression.getValue(vars, expectedType);
    }


    /**
     * 是否包含表达式
     *
     * @param expression
     * @return
     */
    private static boolean hasExpression(String expression) {
        if (StringUtils.isBlank(expression)) {
            return false;
        }
        if (!expression.contains("${") || !expression.contains("}")) {
            return false;
        }
        Matcher m = Pattern.compile("\\$\\{[\\s\\S]*\\}").matcher(expression);
        return m.find();
    }

    /**
     * 是否表达式格式
     *
     * @param expression
     * @return
     */
    public static boolean isExpression(String expression) {
        if (StringUtils.isBlank(expression)) {
            return false;
        }
        if (expression.startsWith("${") && expression.endsWith("}")) {
            return true;
        }
        return false;
    }

}
