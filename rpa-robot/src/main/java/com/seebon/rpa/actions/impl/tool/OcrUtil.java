package com.seebon.rpa.actions.impl.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ObjectTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.OcrOriginType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ActionUtils
@RobotAction(name = "ocr坐标解析", targetType = ObjectTarget.class, comment = "ocr坐标解析")
public class OcrUtil extends AbstractAction {

    @ActionArgs(value = "匹配文本", required = true)
    private String matchText;

    @ActionArgs(value = "结果变量", required = true)
    private String resultKey;

    @Override
    public void run() {
        String ocrText = getTarget();
        Map<String, Integer> pointMap = Maps.newHashMap();
        int yPoint = 0;
        int xPoint = 0;
        if (StringUtils.isNotBlank(ocrText) && StringUtils.isNotBlank(matchText)) {
            matchText=parse(matchText,String.class);
            String finalOcrText = ocrText;
            OcrOriginType originType = Arrays.stream(OcrOriginType.values()).filter(x -> finalOcrText.startsWith(x.toString())).findFirst().orElse(null);
            if (originType == null || OcrOriginType.SELF_DDDC_OCR.equals(originType)) {
                List<String> textArr = Lists.newArrayList(ocrText.split("\\[\\('"));
                List<String> matchTexts = Arrays.stream(matchText.split(",")).map(item -> "'".concat(item).concat("'")).collect(Collectors.toList());
                Optional<String> first = textArr.stream().filter(it -> {
                    String[] split = it.split("\\), \\[\\[");
                    boolean isMatch = false;
                    for (String mt : matchTexts) {
                        isMatch = mt.equals("'" + (split[0].split("', ")[0]) + "'");
                        if (isMatch) {
                            break;
                        }
                    }
                    return isMatch;
                }).findFirst();
                if (first.isPresent()) {
                    String text = first.get();
                    String[] split = text.split("\\), \\[\\[");
                    //xPoint = Double.valueOf(split[1].split("\\]")[0].split(",")[0]).intValue();
                    //yPoint = Double.valueOf(split[1].split("\\]")[0].split(",")[1]).intValue();
                    //python 返回的是元组 + 二维列表
                    //[('四费款缴纳', 0.9780427), [[9.0, 256.0], [95.0, 256.0], [95.0, 280.0], [9.0, 280.0]]]
                    String twoDimensionStr = twoDimensionStr="[["+split[1].replace("]]],","]]");
                    Double[][] twoDimension = JSON.parseObject(twoDimensionStr, Double[][].class);
                    Double x = 0D;
                    Double y = 0D;
                    for (Double[] aLong : twoDimension) {
                        x=x+aLong[0];
                        y=y+aLong[1];
                    }
                    xPoint = (int) (x/4);
                    yPoint = (int) (y/4);
                }
            } else {
                ocrText = ocrText.replaceFirst(originType.toString(), "");
                if (StringUtils.isNotEmpty(ocrText)) {
                    JSONArray jsonArray = JSON.parseArray(ocrText);
                    List<String> matchTexts = Arrays.stream(matchText.split(",")).collect(Collectors.toList());
                    Object o = jsonArray.parallelStream().filter(x -> {
                        JSONArray nodeInfo = (JSONArray) ((JSONArray) x).get(1);
                        String nodeName = nodeInfo.getString(0);
                        boolean isMatch = false;
                        for (String mt : matchTexts) {
                            isMatch = mt.equals(nodeName);
                            if (isMatch) {
                                break;
                            }
                        }
                        return isMatch;
                    }).findFirst().orElse(null);
                    if (o != null) {
                        JSONArray nodeDimension = (JSONArray)((JSONArray) o).get(0);
                        Double x = 0D;
                        Double y = 0D;
                        for (int i = 0;i<4;i++) {
                            JSONArray curDimension = (JSONArray) nodeDimension.get(i);

                            x=x+curDimension.getBigDecimal(0).doubleValue();
                            y=y+curDimension.getBigDecimal(1).doubleValue();
                        }
                        xPoint = (int) (x/4);
                        yPoint = (int) (y/4);
                    }
                }
            }
        }
        pointMap.put("xPoint", xPoint);
        pointMap.put("yPoint", yPoint);
        log.info("xPoint=" + xPoint + ",yPoint=" + yPoint);
        ctx.setVariable(resultKey, pointMap);
    }
}
