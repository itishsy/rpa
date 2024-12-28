package com.seebon.rpa.actions.impl.declare;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.flow.Wait;
import com.seebon.rpa.actions.impl.tool.Keyboard;
import com.seebon.rpa.actions.target.impl.TargetsTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.ElementDisType;
import com.seebon.rpa.context.enums.InputType;
import com.seebon.rpa.context.enums.QrCodeType;
import com.seebon.rpa.context.enums.ValidateType;
import com.seebon.rpa.context.runtime.RuntimeSkipTo;
import com.seebon.rpa.service.RobotCommonService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

@Slf4j
@ActionUtils
@RobotAction(name = "H5登录", targetType = TargetsTarget.class, comment = "H5登录")
public class H5Login extends AbstractAction {

    private int iframeIndex = 0;

    private boolean inputed = false;

    @Autowired
    private Keyboard keyboard;

    @Conditions({
            "sms:smsXpath,refreshXpath,inputType,loginXpath,recipientPhoneNumber,tips,successXpath,failXpath,elementDisType",
            "qrCode:qrCodeXpath,getQrCodeType,refreshXpath,recipientPhoneNumber,tips,successXpath,failXpath,elementDisType",
            "faceCode:qrCodeXpath,getQrCodeType,refreshXpath,recipientPhoneNumber,tips,successXpath,failXpath,elementDisType",
            "dynamicCode:loginXpath,recipientPhoneNumber,tips,successXpath,failXpath,elementDisType,inputXpath",
    })
    @ActionArgs(value = "推送方式", dict = ValidateType.class, required = true)
    private ValidateType validateType;

    @ActionArgs(value = "获取验证码", required = true, comment = "点击发送手机验证码的xpath（如要点击多个页面元素，则换行输入）")
    private String smsXpath;

    @ActionArgs(value = "二维码", required = true, comment = "二维码的的xpath路径")
    private String qrCodeXpath;

    @ActionArgs(value = "保存二维码方式", comment = "默认截图", dict = QrCodeType.class)
    private String getQrCodeType;

    @ActionArgs(value = "二次验证", required = false, comment = "点击刷新二维码或重新获取验证码的xpath路径（如要点击多个页面元素，则换行输入），不填则视为刷新页面刷新二维码")
    private String refreshXpath;

    @Setter
    @ActionArgs(value = "验证码输入方式", dict = InputType.class, required = true)
    @Conditions({
            "input:validateType,smsXpath,refreshXpath,inputXpath,loginXpath,recipientPhoneNumber,tips,successXpath,failXpath,elementDisType",
            "keyboard:validateType,smsXpath,refreshXpath,loginXpath,recipientPhoneNumber,tips,successXpath,failXpath,elementDisType"
    })
    private InputType inputType;

    @ActionArgs(value = "验证码输入框", required = true, comment = "手机验证码输入框的xpath")
    private String inputXpath;

    @ActionArgs(value = "登录按钮", required = true, comment = "登录按钮的xpath")
    private String loginXpath;

    @ActionArgs(value = "手机号码", required = true, comment = "短链接短信接收手机号码")
    private String recipientPhoneNumber;

    @ActionArgs(value = "提示信息", required = true, comment = "在H5页面中展示")
    private String tips;

    @ActionArgs(value = "登录成功标签", required = true, comment = "判断登录成功标签xpath")
    private String successXpath;

    @ActionArgs(value = "触发失败标签", comment = "判断触发短信校验失败是出现的标签xpath")
    private String failXpath;

    @ActionArgs(value = "登录成功判断", required = true, dict = ElementDisType.class, comment = "登录成功标签的显示状态作为H5登录是否成功的标志")
    private String elementDisType;


    @Autowired
    private RobotCommonService robotCommonService;

    @Autowired
    private Wait wait;

    private Integer totalWaitTime = 0;

    private Integer waitTime = 0;

    @Override
    public void run() {
        totalWaitTime = 0;
        waitTime = 0;
        try {
            String loginUuid = UUIDGenerator.uuidStringWithoutLine();

            if (validateType == null) {
                log.info("请选择H5登录方式");
                throw new RuntimeException("请选择H5登录方式");
            }

            String tplType = ctx.getTplType();
            if (StringUtils.isBlank(tplType)) {
                log.info("流程未配置申报系统，请检查流程配置");
                throw new RuntimeException("流程未配置申报系统，请检查流程配置");
            }

            String appCode = ctx.get("appCode");
            // 获取扫码登录提醒模板
            String loginNoticeResp = robotCommonService.getLoginNoticeTemp(appCode, tplType, validateType.getCode());
            if (StringUtils.isBlank(loginNoticeResp)) {
                throw new RuntimeException("获取H5登录提醒模板失败");
            }
            JSONObject loginTemp = JSON.parseObject(loginNoticeResp);
            if (null == loginTemp || loginTemp.getJSONObject("data") == null) {
                log.info("获取H5登录提醒模板失败，请检查模板是否正确");
                throw new RuntimeException("获取H5登录提醒模板失败，请检查模板是否正确");
            }
            if (ValidateType.sms.equals(validateType)) { // 短信验证码登录
                if (StringUtils.isBlank(smsXpath)) {
                    log.info("==请填写触发短信验证码的xpath====");
                    throw new RuntimeException("指令未填写触发短信验证码的xpath，请核查");
                }
                String[] xpaths = smsXpath.split("\n");
                for (String xpath : xpaths) {
                    if (xpath.contains("键盘操作")) {
                        String[] split = xpath.split("-");
                        keyboard.setKeyIn(split[1]);
                        keyboard.run();
                        wait.setTimeout(3);
                        wait.run();
                    } else {
                        WebElement smsElement = findByXpath(xpath, null);
                        if (smsElement != null) {
                            JavascriptExecutor js = ((JavascriptExecutor) ctx.getWebDriver());
                            js.executeScript("arguments[0].click();", smsElement);
                            this.releaseToParent();
                            wait.setTimeout(3);
                            wait.run();
                        } else {
                            log.info("==触发短信验证码的xpath有误，未获取的页面元素====");
                            throw new RuntimeException("触发短信验证码的xpath有误，未获取的页面元素");
                        }
                    }
                    totalWaitTime = totalWaitTime + 3;
                }
                if (StringUtils.isNotBlank(failXpath)) {
                    WebElement failElement = findByXpath(failXpath, 8);
                    log.info("触发短信验证码校验失败1,failElement=" + failElement);
                    if (failElement != null && failElement.isDisplayed()) {
                        this.releaseToParent();
                        log.info("触发短信验证码校验失败2,failElement=" + failElement);
                        if (StringUtils.isNotBlank(failedSkipTo)) {
                            throw new RuntimeSkipTo(failedSkipTo);
                        } else {
                            throw new RuntimeException("触发短信验证码校验失败！！！");
                        }
                    } else {
                        this.releaseToParent();
                    }
                    totalWaitTime = totalWaitTime + 8;
                }
            }
            if (ValidateType.dynamicCode.equals(validateType)) { // 动态码登录
                setInputType(InputType.input);
            }

            JSONObject data = loginTemp.getJSONObject("data"); // 模版信息

            doLogin(loginUuid, data);

        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof RuntimeSkipTo) {
                throw e;
            } else {
                if (e.getMessage() == null || !"H5登录成功".equals(e.getMessage())) {
                    throw new BusinessException(e.getMessage() + "，H5登录异常");
                }
            }
        }
    }

    private void doLogin(String loginUuid, JSONObject loginTemp) {
        inputed = false;

        WebDriver webDriver = ctx.getWebDriver();

        //推送扫码登录验证通知
        Map<String, Object> addNoticeMap = Maps.newHashMap();
        addNoticeMap.put("execBatchCode", loginUuid);
        addNoticeMap.put("tempId", loginTemp.get("id"));
        addNoticeMap.put("addrName", ctx.get("addrName"));
        addNoticeMap.put("companyName", ctx.get("companyName"));
        addNoticeMap.put("accountNumber", ctx.getAccountNumber());
        addNoticeMap.put("recipientPhoneNumber", recipientPhoneNumber);

        String tipsVal = parse(tips, String.class);
        addNoticeMap.put("tips", tipsVal);
        if (ValidateType.sms.equals(validateType) || ValidateType.dynamicCode.equals(validateType)) { // 短信验证码登录、动态码登录
            robotCommonService.addNotice(addNoticeMap);
            boolean loginFlag = this.checkSmsLoginSuccess(loginTemp, loginUuid);
            if (loginFlag) {
                log.info(ctx.getAccountNumber() + "H5登录成功");
                throw new BusinessException("H5登录成功");
            }
        } else if (ValidateType.qrCode.equals(validateType) || ValidateType.faceCode.equals(validateType)) { // 二维码或人脸二维码登录
            WebElement qrElement = findByXpath(qrCodeXpath, null);
            // 截图
            String imagePath = ctx.get(RobotConstant.DATA_PATH) + "\\qr_login_" + loginUuid + "_img.png";
            BufferedImage bufferedImage = null;
            String imgData = qrElement.getAttribute("src");
            if (imgData != null && getQrCodeType != null && QrCodeType.valueOf(getQrCodeType).equals(QrCodeType.Download)) {
                if (imgData.contains("base64,")) {
                    imgData = imgData.substring(imgData.indexOf(",") + 1);
                    try {
                        byte[] imgbytes = new BASE64Decoder().decodeBuffer(imgData);
                        InputStream stream = new ByteArrayInputStream(imgbytes);
                        bufferedImage = ImgUtil.read(stream);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new BusinessException("下载保存二维码失败！");
                    }
                } else {
                    // 创建一个URL对象
                    try {
                        URL url = new URL(imgData);
                        InputStream inputStream = url.openStream();
                        bufferedImage = ImgUtil.read(inputStream);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new BusinessException("下载保存二维码失败！");
                    }
                }
            } else {
                File file = qrElement.getScreenshotAs(OutputType.FILE);
                bufferedImage = ImgUtil.read(file);
            }
            File saveFile = new File(imagePath);
            this.releaseToParent();

            byte[] bytes = getBytes(bufferedImage);
            //保存截图
            FileUtil.writeBytes(bytes, saveFile);
            //文件上传
            String fileId = robotCommonService.fileUpload(new File(imagePath));
            addNoticeMap.put("imgFileId", fileId);
            robotCommonService.addNotice(addNoticeMap);

            boolean loginFlag = this.checkQrLoginSuccess(loginTemp, loginUuid);
            if (loginFlag) {
                log.info(ctx.getAccountNumber() + "H5登录成功");
                throw new BusinessException("H5登录成功");
            }
            totalWaitTime = totalWaitTime + 1;
        }

        //重置waitTime=0
        log.info("=====重置单次等待时间waitTime=0=====");
        waitTime = 0;
        doResend(loginUuid, webDriver, loginTemp);
    }

    /**
     * 获取重新推送
     *
     * @param loginUuid
     * @param webDriver
     * @param loginTemp
     */
    public void doResend(String loginUuid, WebDriver webDriver, JSONObject loginTemp) {
        log.info("=========获取重新发送短信验证码或二维码信号======");
        String signalResp = robotCommonService.getResendSignal(loginUuid);
        JSONObject resendMap = JSON.parseObject(signalResp);
        if (resendMap.getInteger("data") == 1) { // 获取到重新获取的信号
            if ((ValidateType.qrCode.equals(validateType) || ValidateType.faceCode.equals(validateType))
                    && StringUtils.isNotBlank(failXpath)) {
                WebElement failElement = findByXpath(failXpath, 8);
                log.info("触发二维码验证失败1,failElement=" + failElement);
                if (failElement != null && failElement.isDisplayed()) {
                    this.releaseToParent();
                    log.info("触发二维码验证失败2,failElement=" + failElement);
                    if (StringUtils.isNotBlank(failedSkipTo)) {
                        throw new RuntimeSkipTo(failedSkipTo);
                    } else {
                        throw new RuntimeException("触发二维码验证失败！！！");
                    }
                } else {
                    this.releaseToParent();
                }
                totalWaitTime = totalWaitTime + 8;
            }

            log.info("=========获取到重新发送短信验证码或二维码信号======");
            if (StringUtils.isNotBlank(refreshXpath)) {
                String[] xpaths = refreshXpath.split("\n");
                for (String xpath : xpaths) {
                    WebElement refreshElement = findByXpath(xpath, null);
                    if (refreshElement != null) {
                        JavascriptExecutor js = ((JavascriptExecutor) webDriver);
                        js.executeScript("arguments[0].click();", refreshElement);
                        this.releaseToParent();
                        wait.setTimeout(3);
                        wait.run();
                        totalWaitTime = totalWaitTime + 3;
                    }
                }
                // 点击重新获取验证码后等待一段时间
                wait.setTimeout(5);
                wait.run();
                totalWaitTime = totalWaitTime + 5;
            } else {
                webDriver.navigate().refresh();
                wait.setTimeout(5);
                wait.run();
                totalWaitTime = totalWaitTime + 5;
            }
            doLogin(loginUuid, loginTemp);
        }

        log.info("totalWaitTime ====== " + totalWaitTime);
        //等待已超过链接时效？
        if (totalWaitTime >= loginTemp.getInteger("linkAgeing")) {
            log.info(String.format("=========H5登录失败，已超登录链接最大等待时间%s秒======", loginTemp.getInteger("linkAgeing")));
            throw new RuntimeException(String.format("=========H5登录失败，已超登录链接最大等待时间%s秒======", loginTemp.getInteger("linkAgeing")));
        }
        log.info("=========未获取到重新发送短信验证码或二维码信号======");
        // 等待5秒钟
        wait.setTimeout(5);
        wait.run();

        totalWaitTime = totalWaitTime + 5;
        doResend(loginUuid, webDriver, loginTemp);
    }

    /**
     * 检查二维码登录是否成功
     *
     * @param loginTemp
     * @param loginUuid
     * @return
     */
    private Boolean checkQrLoginSuccess(JSONObject loginTemp, String loginUuid) {
        // 等待10秒钟
        wait.setTimeout(10);
        wait.run();
        waitTime = waitTime + 10;
        totalWaitTime = totalWaitTime + 10;

        WebElement successEle = null;
        for (String xpath : successXpath.split("\n")) {
            successEle = findByXpath(xpath, 8);
            totalWaitTime = totalWaitTime + 8;
            if (successEle != null) {
                break;
            }
        }
        String value = null;
        if (successEle != null) {
            value = successEle.getAttribute("value");
        }
        if (ElementDisType.display.equals(ElementDisType.valueOf(elementDisType)) && (successEle != null && successEle.isDisplayed())) {
            robotCommonService.loginSuccess(loginUuid);
            this.releaseToParent();
            return true;
        } else if (ElementDisType.notDisplay.equals(ElementDisType.valueOf(elementDisType)) && (successEle == null || !successEle.isDisplayed())) {
            robotCommonService.loginSuccess(loginUuid);
            this.releaseToParent();
            return true;
        } else if (ElementDisType.hasValue.equals(ElementDisType.valueOf(elementDisType)) && successEle != null && StringUtils.isNotBlank(value)) {
            robotCommonService.loginSuccess(loginUuid);
            this.releaseToParent();
            return true;
        }
        this.releaseToParent();

        //等待已超过链接时效？、
        log.info(String.format("总等待时间totalWaitTime===%s秒", totalWaitTime));
        if (totalWaitTime >= loginTemp.getInteger("linkAgeing")) {
            throw new RuntimeException("H5登录失败");
        }

        //未超过单次最大等待时间？？
        log.info(String.format("单次等待时间waitTime===%s秒", waitTime));
        if (waitTime < loginTemp.getInteger("singleAgeing")) {
            return this.checkQrLoginSuccess(loginTemp, loginUuid);
        }
        return false;
    }

    /**
     * 检查手机验证码登录是否成功
     *
     * @param loginTemp
     * @param loginUuid
     * @return
     */
    private boolean checkSmsLoginSuccess(JSONObject loginTemp, String loginUuid) {
        // 等待10秒钟
        wait.setTimeout(10);
        wait.run();
        waitTime = waitTime + 10;
        totalWaitTime = totalWaitTime + 10;

        // 获取短信验证码
        String smsCode = robotCommonService.getSmsCode(loginUuid);
        if (StringUtils.isNotBlank(smsCode) && !inputed) {
            if (inputType.equals(InputType.input)) {
                WebElement inputElement = findByXpath(inputXpath, null);
                if (inputElement != null) {
                    inputElement.clear();
                    inputElement.sendKeys((CharSequence) smsCode);
                }
                totalWaitTime = totalWaitTime + 1;
                this.releaseToParent();
            } else if (inputType.equals(InputType.keyboard)) {
                keyboard.setKeyIn("VK_DELETE");
                for (int i = 1; i <= 6; i++) {
                    keyboard.run();
                }
                keyboard.setKeyIn(null);
                keyboard.setKeyArgs(smsCode);
                keyboard.run();
            }
            ctx.setVariable("H5LoginSmsCode", smsCode);
            WebElement loginElement = findByXpath(loginXpath, null);
            totalWaitTime = totalWaitTime + 1;
            if (loginElement != null) {
                JavascriptExecutor js = ((JavascriptExecutor) ctx.getWebDriver());
                js.executeScript("arguments[0].click();", loginElement);
            }
            this.releaseToParent();
            inputed = true;

            wait.setTimeout(10);
            wait.run();
            waitTime = waitTime + 10;
            totalWaitTime = totalWaitTime + 10;
        }

        WebElement successEle = null;
        for (String xpath : successXpath.split("\n")) {
            successEle = findByXpath(xpath, null);
            totalWaitTime = totalWaitTime + 1;
            if (successEle != null) {
                break;
            }
        }
        String value = null;
        if (successEle != null) {
            value = successEle.getAttribute("value");
        }
        if (ElementDisType.display.equals(ElementDisType.valueOf(elementDisType)) && (successEle != null && successEle.isDisplayed())) {
            robotCommonService.loginSuccess(loginUuid);
            this.releaseToParent();
            return true;
        } else if (ElementDisType.notDisplay.equals(ElementDisType.valueOf(elementDisType)) && (successEle == null || !successEle.isDisplayed())) {
            robotCommonService.loginSuccess(loginUuid);
            this.releaseToParent();
            return true;
        } else if (ElementDisType.hasValue.equals(ElementDisType.valueOf(elementDisType)) && successEle != null && StringUtils.isNotBlank(value)) {
            robotCommonService.loginSuccess(loginUuid);
            this.releaseToParent();
            return true;
        }
        this.releaseToParent();

        //等待已超过链接时效？
        log.info(String.format("总等待时间totalWaitTime===%s秒", totalWaitTime));
        if (totalWaitTime >= loginTemp.getInteger("linkAgeing")) {
            throw new RuntimeException("H5登录失败");
        }

        //未超过单次最大等待时间
        log.info(String.format("单次等待时间waitTime===%s秒", waitTime));
        if (waitTime < loginTemp.getInteger("singleAgeing")) {
            return this.checkSmsLoginSuccess(loginTemp, loginUuid);
        }

        return false;
    }

    private String switchToFrame(String attrValue) {
        if (attrValue.startsWith("[iframe")) {
            for (int i = 0; i < 100; i++) {
                String iframeName = "[iframe" + i + "]";
                if (attrValue.startsWith(iframeName)) {
                    WebDriver driver = ctx.getWebDriver();
                    driver.switchTo().frame(i);
                    iframeIndex++;
                    attrValue = attrValue.substring(iframeName.length());
                    if (attrValue.startsWith("[iframe")) {
                        return switchToFrame(attrValue);
                    }
                    break;
                }
            }
        }
        return attrValue;
    }

    private void switchToParent() {
        if (iframeIndex > 0) {
            WebDriver driver = ctx.getWebDriver();
            driver.switchTo().parentFrame();
            iframeIndex--;
            switchToParent();
        }
    }

    private void releaseToParent() {
        switchToParent();
        iframeIndex = 0;
    }

    private byte[] getBytes(BufferedImage bufferedImage) {
        ByteArrayOutputStream os = null;
        byte[] bytes = null;
        try {
            os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            bytes = os.toByteArray();
        } catch (Exception e) {
            log.error("截屏异常." + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(os);
        }
        return bytes;
    }

    private WebElement findByXpath(String xpath, Integer timeOut) {
        if (timeOut == null) {
            timeOut = 1;
        }
        WebDriverWait driverWait = new WebDriverWait(ctx.getWebDriver(), Duration.ofSeconds(timeOut));
        WebElement element = null;
        try {
            String xpath1 = this.switchToFrame(xpath);
            element = driverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath1)));
        } catch (Exception e) {
            log.error("根据xpath获取element异常：{}", e, e.getMessage());
        }
        return element;
    }
}
