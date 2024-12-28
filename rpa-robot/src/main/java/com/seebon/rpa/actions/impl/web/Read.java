package com.seebon.rpa.actions.impl.web;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.tool.StringUtil;
import com.seebon.rpa.actions.target.impl.ElementTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.AttributeType;
import com.seebon.rpa.context.enums.HeaderCellType;
import com.seebon.rpa.context.enums.ReadType;
import com.seebon.rpa.utils.ELParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-06 14:05:49
 */
@Slf4j
@RobotAction(name = "读取", targetType = ElementTarget.class)
public class Read extends AbstractAction {

    public static final ScriptEngine js = new ScriptEngineManager().getEngineByName("js");

    public static final String CONDITION_ITEM = "item";

    @Conditions({"table:condition,headerNumber,headerStr,headerCellTagName,conditionItemKey,cellTextTagName,customInfo,calculationMap,dataKey",
            "divTable:condition,headerStr,splitText,conditionItemKey,customInfo,calculationMap,dataKey,rowTagName,rowTagAttr,cellTagAttr,cellTagName",
            "select:condition,dataKey",
            "ul:condition,dataKey",
            "text:dataKey"})
    @ActionArgs(value = "读取类型", required = true, dict = ReadType.class)
    private String readType;

    @ActionArgs(value = "读取条件")
    private String condition;

    @ActionArgs(value = "表格头部行数")
    private Integer headerNumber;

    @ActionArgs(value = "表头信息", comment = "多个用英文逗号隔开，div表格必填")
    private String headerStr;

    @ActionArgs(value = "表格头部元素类型", required = true, dict = HeaderCellType.class)
    private String headerCellTagName;

    @ActionArgs(value = "对象变量名称")
    private String conditionItemKey;

    @ActionArgs(value = "自定义字段信息", comment = "读取表格时追加一些自定义字段")
    private LinkedHashMap<String, Object> customInfo;

    @ActionArgs(value = "计算字段信息", comment = "文件读取不到的字段，需要通过计算获取")
    private Map<String, String> calculationMap;

    @ActionArgs(value = "文本所在元素名称")
    private String cellTextTagName;

    @ActionArgs(value = "行记录元素属性", dict = AttributeType.class, comment = "不填默认为tagName")
    private AttributeType rowTagAttr;

    @ActionArgs(value = "行记录元素名称")
    private String rowTagName;

    @ActionArgs(value = "单元格元素属性", dict = AttributeType.class)
    private AttributeType cellTagAttr;

    @ActionArgs(value = "单元格元素名称")
    private String cellTagName;

    @ActionArgs(value = "数据分隔符", comment = "数据行之间的分隔符,行记录元素名称为空则必填")
    private String splitText;

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;

    @Override
    public void run() {
        WebElement element = getTarget();
        if (element == null) {
            return;
        }
        switch (ReadType.valueOf(readType)) {
            case text: {
                try {
                    String text = "";
                    String tagName = element.getTagName();
                    if ("span".equals(tagName) || "label".equals(tagName)) {
                        text = element.getAttribute("innerHTML");
                    } else if ("textarea".equals(tagName) || "input".equals(tagName)) {
                        text = element.getAttribute("value");
                    } else {
                        text = element.getText();
                        if (StringUtils.isBlank(text) && "div".equals(tagName)) {
                            text = element.getAttribute("innerHTML");
                        }
                    }
                    log.info("text1=" + text);
                    ctx.setVariable(dataKey, StrUtil.trimToEmpty(text.trim()));
                } catch (Exception e) {
                    log.error("读取文本异常." + e.getMessage(), e);
                }
                break;
            }
            case select: {
                List<WebElement> option = element.findElements(By.tagName("option"));
                List<String> optionTexts = Lists.newArrayList();
                Map<String, Object> ctxMap = Maps.newHashMap();
                ctxMap.putAll(ctx.getVariables());
                option.stream().forEach(item -> {
                    String itemText = item.getText();
                    ctxMap.put("itemText", itemText);
                    if (StringUtils.isBlank(condition) || ELParser.parse(condition, ctxMap, Boolean.class)) {
                        optionTexts.add(itemText);
                    }
                });
                ctx.setVariable(dataKey, optionTexts);
                break;
            }
            case ul: {
                List<WebElement> lis = element.findElements(By.tagName("li"));
                List<String> optionTexts = Lists.newArrayList();
                Map<String, Object> ctxMap = Maps.newHashMap();
                ctxMap.putAll(ctx.getVariables());
                lis.stream().forEach(item -> {
                    String itemText = item.getText();
                    ctxMap.put("itemText", itemText);
                    if (StringUtils.isBlank(condition) || ELParser.parse(condition, ctxMap, Boolean.class)) {
                        optionTexts.add(itemText);
                    }
                });
                ctx.setVariable(dataKey, optionTexts);
                break;
            }
            case table: {
                if (headerNumber == null) {
                    headerNumber = 1;
                }
                List<WebElement> tr = element.findElements(By.tagName("tr"));

                List<String> headers = Lists.newArrayList();
                // 读取表头
                if (StringUtils.isBlank(headerStr)) {
                    headers = readerHeader(tr.get(headerNumber - 1));
                } else {
                    headers = Lists.newArrayList(headerStr.split(","));
                }
                //读取表body
                List<LinkedHashMap<String, Object>> result = readerBody(headers, headerNumber, tr);
                ctx.setVariable(dataKey, result);
                log.info(result + "+++++++++++++++++++");
                break;
            }
            case divTable: {
                List<String> headers = Lists.newArrayList(headerStr.split(","));
                String[] rows = null;
                if (StringUtils.isBlank(rowTagName)) {
                    String text = element.getText();
                    rows = text.split(splitText);
                } else {
                    rows = getTableRows(element);
                }

                List<LinkedHashMap<String, Object>> result = Lists.newArrayList();
                if (rows != null) {
                    Map<String, Object> ctxMap = Maps.newHashMap();
                    ctxMap.putAll(ctx.getVariables());
                    for (String rowStr : rows) {
                        String[] row = rowStr.split("\n");
                        LinkedHashMap<String, Object> item = Maps.newLinkedHashMap();
                        for (int i = 0; i < headers.size(); i++) {
                            String key = headers.get(i);
                            if (key.contains(".")) {
                                String[] keys = key.split("\\.");
                                Map<String, Object> map = (Map<String, Object>) item.get(keys[0]);
                                if (!item.containsKey(keys[0]) || map == null) {
                                    map = Maps.newHashMap();
                                }
                                map.put(keys[1], i > row.length - 1 ? "" : row[i].trim());
                                item.put(keys[0], map);
                            } else {
                                item.put(headers.get(i), i > row.length - 1 ? "" : row[i].trim());
                            }

                        }
                        ctxMap.put(StringUtils.isBlank(conditionItemKey) ? CONDITION_ITEM : conditionItemKey, item);
                        if (StringUtils.isBlank(condition) || ELParser.parse(condition, ctxMap, Boolean.class)) {
                            if (customInfo != null && !customInfo.isEmpty()) {
                                item.putAll(customInfo);
                            }
                            if (calculationMap != null && !calculationMap.isEmpty()) {
                                Map<String, String> calculation = Maps.newHashMap();
                                try {
                                    for (String key : calculationMap.keySet()) {
                                        String calculationText = calculationMap.get(key);
                                        String parse = ELParser.parse(calculationText, ctxMap, String.class);
                                        String val = String.valueOf(js.eval(parse));
                                        calculation.put(key, val);
                                    }
                                } catch (ScriptException e) {
                                    e.printStackTrace();
                                } finally {
                                }
                                item.putAll(calculation);
                            }
                            result.add(item);
                        }
                    }
                }

                ctx.setVariable(dataKey, result);
                break;
            }
            default:
                break;
        }

    }

    private String[] getTableRows(WebElement element) {
        String[] rows = null;
        List<WebElement> rowEles = Lists.newArrayList();
        if (rowTagAttr!=null) {
            if (AttributeType.tagName.equals(rowTagAttr)) {
                rowEles = element.findElements(By.tagName(rowTagName));
            } else if (AttributeType._name.equals(rowTagAttr)) {
                rowEles = element.findElements(By.name(rowTagName));
            } else if (AttributeType.id.equals(rowTagAttr)) {
                rowEles = element.findElements(By.id(rowTagName));
            } else if (AttributeType.xpath.equals(rowTagAttr)) {
                rowEles = element.findElements(By.xpath(rowTagName));
            } else if (AttributeType.className.equals(rowTagAttr)) {
                rowEles = element.findElements(By.className(rowTagName));
            } else if (AttributeType.cssSelector.equals(rowTagAttr)) {
                rowEles = element.findElements(By.cssSelector(rowTagName));
            } else if (AttributeType.linkText.equals(rowTagAttr)) {
                rowEles = element.findElements(By.linkText(rowTagName));
            }
        } else {
            rowEles = element.findElements(By.tagName(rowTagName));
        }
        if (CollectionUtils.isNotEmpty(rowEles)) {
            rows = new String[rowEles.size()];
            if (cellTagAttr != null && StringUtils.isNotBlank(cellTagName)) {
                for (int i=0; i<rowEles.size(); i++) {
                    WebElement rowEle = rowEles.get(i);
                    List<String> rowList = Lists.newArrayList();
                    List<WebElement> cellEles = Lists.newArrayList();
                    if (AttributeType.tagName.equals(cellTagAttr)) {
                        cellEles = rowEle.findElements(By.tagName(cellTagName));
                    } else if (AttributeType._name.equals(cellTagAttr)) {
                        cellEles = rowEle.findElements(By.name(cellTagName));
                    } else if (AttributeType.id.equals(cellTagAttr)) {
                        cellEles = rowEle.findElements(By.id(cellTagName));
                    } else if (AttributeType.xpath.equals(cellTagAttr)) {
                        cellEles = rowEle.findElements(By.xpath(cellTagName));
                    } else if (AttributeType.className.equals(cellTagAttr)) {
                        cellEles = rowEle.findElements(By.className(cellTagName));
                    } else if (AttributeType.cssSelector.equals(cellTagAttr)) {
                        cellEles = rowEle.findElements(By.cssSelector(cellTagName));
                    } else if (AttributeType.linkText.equals(cellTagAttr)) {
                        cellEles = rowEle.findElements(By.linkText(cellTagName));
                    }
                    if (CollectionUtils.isNotEmpty(cellEles)) {
                        for (WebElement ele : cellEles) {
                            rowList.add(ele.getText().replaceAll("\n", ""));
                        }
                    }
                    rows[i] = StringUtils.join(rowList, "\n");
                }

            } else {
                for (int i=0; i<rowEles.size(); i++) {
                    String text = rowEles.get(i).getText();
                    rows[i] = text;
                }
            }

        }
        return rows;
    }

    private List<String> readerHeader(WebElement headerElement) {
        if (StringUtils.isBlank(headerCellTagName)) {
            headerCellTagName = "th";
        }
        List<WebElement> cells = headerElement.findElements(By.tagName(headerCellTagName));
        return cells.stream().map(item -> {
            String headerStr = item.getText().trim();
            if (StringUtils.isBlank(headerStr)) {
                headerStr = item.getAttribute("innerText").trim();
            }
            if (StringUtils.isBlank(headerStr)) {
                headerStr = item.getAttribute("innerHTML").trim();
            }
            return headerStr;
        }).collect(Collectors.toList());
    }

    private List<LinkedHashMap<String, Object>> readerBody(List<String> headers, Integer headerNumber, List<WebElement> tr) {
        WebDriver driver = ctx.getWebDriver();
        JavascriptExecutor jsExec = ((JavascriptExecutor) driver);
        String bodyCellTagName = "td";

        List<LinkedHashMap<String, Object>> result = Lists.newArrayList();
        Map<String, Object> ctxMap = Maps.newHashMap();
        for (int i = headerNumber; i < tr.size(); i++) {
            WebElement bodyTr = tr.get(i);

            List<WebElement> bodyCells = bodyTr.findElements(By.tagName(bodyCellTagName));
            LinkedHashMap<String, Object> item = Maps.newLinkedHashMap();
            for (int j = 0; j < bodyCells.size(); j++) {
                WebElement tdElement = bodyCells.get(j);
                if (StringUtils.isNotBlank(cellTextTagName)) {
                    String[] substring = cellTextTagName.split("-");
                    List<WebElement> elements = tdElement.findElements(By.tagName(substring[0]));
                    if (CollectionUtils.isNotEmpty(elements)) {
                        int index = 0;
                        if (substring.length > 1) {
                            index = Integer.valueOf(substring[1]);
                        }
                        tdElement = elements.get(index > elements.size() - 1 ? elements.size() - 1 : index);
                    }
                }
                String val = null;
                if (StringUtils.isBlank(val)) {
                    val = tdElement.getText();
                    if (StringUtils.isNotBlank(val)) {
                        val = val.trim();
                    }
                }
                if (StringUtils.isBlank(val)) {
                    val = tdElement.getAttribute("innerText");
                    if (StringUtils.isNotBlank(val)) {
                        val = val.trim();
                    }
                }
                if (StringUtils.isBlank(val)) {
                    val = tdElement.getAttribute("innerHTML");
                    if (StringUtils.isNotBlank(val)) {
                        val = val.trim();
                    }
                }
                if (StringUtils.isBlank(val)) {
                    val = tdElement.getAttribute("value");
                    if (StringUtils.isNotBlank(val)) {
                        val = val.trim();
                    }
                }
                if (StringUtils.isBlank(val)) {
                    val = (String) jsExec.executeScript("arguments[0].innerText", tdElement);
                    if (StringUtils.isNotBlank(val)) {
                        val = val.trim();
                    }
                }
                if (StringUtils.isNotBlank(val)) {
                    val = val.replaceAll("\\n", "");
                    val = val.trim();
                }
                String key = headers.get(j);
                if (key.contains(".")) {
                    String[] keys = key.split("\\.");
                    Map<String, Object> map = (Map<String, Object>) item.get(keys[0]);
                    if (!item.containsKey(keys[0]) || map == null) {
                        map = Maps.newHashMap();
                    }
                    map.put(keys[1], val);
                    item.put(keys[0], map);
                    log.info("表格 行item：" + JSON.toJSONString(item));
                } else {
                    item.put(headers.get(j), val);
                    log.info("表格 行item：" + JSON.toJSONString(item));
                }
                if (j == headers.size() - 1) {
                    break;
                }
            }
            ctxMap.putAll(ctx.getVariables());
            ctxMap.put(StringUtils.isBlank(conditionItemKey) ? CONDITION_ITEM : conditionItemKey, item);
            if (StringUtils.isBlank(condition) || ELParser.parse(condition, ctxMap, Boolean.class)) {
                if (customInfo != null && !customInfo.isEmpty()) {
                    item.putAll(customInfo);
                }
                if (calculationMap != null && !calculationMap.isEmpty()) {
                    Map<String, String> calculation = Maps.newHashMap();
                    try {
                        for (String key : calculationMap.keySet()) {
                            String calculationText = calculationMap.get(key);
                            String parse = ELParser.parse(calculationText, ctxMap, String.class);
                            String val = String.valueOf(js.eval(parse));
                            calculation.put(key, val);
                        }
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    } finally {
                    }
                    item.putAll(calculation);
                }
                result.add(item);
            }
        }
        return result;
    }
}
