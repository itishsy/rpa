package com.seebon.rpa.actions.impl.win;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.OcrDetailType;
import com.seebon.rpa.context.enums.TextType;
import com.seebon.rpa.entity.robot.vo.OcrReqVO;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.utils.HttpUtils;
import com.seebon.rpa.utils.python.PythonUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RobotAction(name = "win文本", targetType = NoneTarget.class)
public class Text extends AbstractAction {
    @Autowired
    private RpaDesignService designService;

    @Conditions({"text:inputArgs,dataKey",
            "ocr_text:inputArgs,ocrDetailType,dataKey",
            "ocr_baidu_text:inputArgs,ocrDetailType,dataKey"
    })
    @ActionArgs(value = "方式", required = true, dict = TextType.class)
    private String textType;

    @ActionArgs(value = "输入参数")
    private String inputArgs;

    @ActionArgs(value = "OCR识别程度", dict = OcrDetailType.class)
    private String ocrDetailType;

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;

    @Value("${ocr.baidu.api_key:Q8hBRnUVsdZXtcjZZ7ZloIYq}")
    private String baiduApiKey;

    @Value("${ocr.baidu.secret_key:nxNV2qRGVeHkNr9kmVjoeSXPNyHjIja1}")
    private String baiduSecretKey;

    @Override
    public void run() {
        String filePath = ctx.get("dataPath") + "\\images\\";
        FileUtil.mkdir(filePath);
        filePath = filePath + "\\win_" + UUIDGenerator.uuidStringWithoutLine() + ".png";
        List<String> commands = Lists.newArrayList();
        commands.add(RobotContext.pythonPath);
        commands.add(RobotContext.workPath + "\\python\\Text.py");
        commands.add(inputArgs);
        commands.add(textType);
        commands.add(RobotContext.workPath + "\\python\\model");
        commands.add(filePath);

        List<String> texts = PythonUtil.runCommand("win文本", null, this.getTimeout(), commands);
        if (TextType.ocr_text.equals(TextType.valueOf(textType))) {
            OcrReqVO reqVO = new OcrReqVO();
            reqVO.setBytes(FileUtil.readBytes(filePath));
            String ocrText = designService.ocrText(reqVO);
            texts = Lists.newArrayList(ocrText);
            log.info("文本OCR识别 返回：" + ocrText);
        } else if (TextType.ocr_baidu_text.equals(TextType.valueOf(textType))) {
            String ocrText = ocrTextByBaidu(getFileContentAsBase64(filePath, true));
            JSONObject jsonObject = JSONObject.parseObject(ocrText);
            JSONArray wordsResult = jsonObject.getJSONArray("words_result");
            texts = Lists.newArrayList();
            for (Object o : wordsResult) {
                JSONObject object = (JSONObject) o;
                texts.add(object.getString("words"));
            }
            log.info("百度文本OCR识别 返回：" + ocrText);
        }
        log.info("文本识别完成，结果:" + JSON.toJSONString(texts));
        if (CollectionUtils.isEmpty(texts)) {
            ctx.setVariable(dataKey, "");
        } else {
            ctx.setVariable(dataKey, texts.stream().collect(Collectors.joining(",")));
        }
    }

    /**
     * 使用百度OCR接口进行文字识别。
     * <p>
     * 该方法通过百度OCR API 将给定的图片（经过Base64编码）发送到百度OCR服务进行文字识别，并返回识别结果。
     *
     * @param base64 包含需要识别的图片数据的请求对象。图片数据应为字节数组格式，通过 {@link #toBase64(byte[])} 方法进行Base64编码。
     * @return OCR 识别结果的 JSON 字符串，表示识别出的文字信息。
     * @throws BusinessException 如果在调用百度OCR服务时发生异常，将抛出业务异常。
     * @see #toBase64(byte[]) 用于将图片数据编码为Base64格式。
     * @see <a href="https://ai.baidu.com/ai-doc/OCR/1k3h7y3db">百度OCR API 文档</a>
     */
    public String ocrTextByBaidu(String base64) {
        try {
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "image=" + base64);
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic?access_token=" + getBaiduAccessToken())
                    .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = new OkHttpClient().newBuilder().build().newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                return responseBody.string();
            }
            return "";
        } catch (Exception e) {
            log.error("百度ocr识别异常." + e.getMessage(), e);
            throw new BusinessException("百度ocr识别异常." + e.getMessage());
        }
    }

    /**
     * 获取文件base64编码
     *
     * @param path      文件路径
     * @param urlEncode 如果Content-Type是application/x-www-form-urlencoded时,传true
     * @return base64编码信息，不带文件头
     * @throws IOException IO异常
     */
    static String getFileContentAsBase64(String path, boolean urlEncode) {
        byte[] b = new byte[0];
        try {
            b = Files.readAllBytes(Paths.get(path));
            String base64 = Base64.getEncoder().encodeToString(b);
            if (urlEncode) {
                base64 = URLEncoder.encode(base64, "utf-8");
            }
            return base64;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }

    private String getBaiduAccessToken() {
        String json = HttpUtils.post("https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + baiduApiKey + "&client_secret=" + baiduSecretKey);
        JSONObject jsonObj = JSON.parseObject(json, JSONObject.class);
        return jsonObj.getString("access_token");
    }
}
