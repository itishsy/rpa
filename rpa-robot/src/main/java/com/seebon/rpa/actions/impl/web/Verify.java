package com.seebon.rpa.actions.impl.web;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ElementTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.DragType;
import com.seebon.rpa.context.enums.OCRType;
import com.seebon.rpa.context.enums.QrCodeType;
import com.seebon.rpa.context.enums.VerifyType;
import com.seebon.rpa.entity.robot.vo.OcrReqVO;
import com.seebon.rpa.entity.sms.vo.GetSmsCodeVO;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.HttpUtils;
import com.seebon.rpa.utils.ocr.OcrApiUtil;
import com.seebon.rpa.utils.sms.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2023-01-04 10:26:09
 */
@Slf4j
@RobotAction(name = "验证码", targetType = ElementTarget.class)
public class Verify extends AbstractAction {
    @Autowired
    private RpaDesignService rpaDesignService;

    private int iframeIndex = 0;

    private static int top = 0;

    @Conditions({"drag:dragType,dragMap,dataKey",
            "verifyCode:ocrType,getQrCodeType,dataKey,imgPath",
            "QRCode:mailTo,title,smsTo,smsContent",
            "smsCode:dataKey,smsContent"})
    @ActionArgs(value = "验证码类型", required = true, dict = VerifyType.class)
    private String verifyType;

    @ActionArgs(value = "图像识别类型", required = true, dict = OCRType.class)
    private String ocrType;

    @ActionArgs(value = "滑动类型", required = true, dict = DragType.class)
    private String dragType;

    @ActionArgs(value = "图片路径", required = false)
    private String imgPath;

    @ActionArgs(value = "扫码人电子邮箱", required = true)
    private String mailTo;

    @ActionArgs(value = "邮件标题", required = true)
    private String title;

    @ActionArgs(value = "扫码人手机号码", required = false)
    private String smsTo;

    @ActionArgs(value = "短信内容", required = false)
    private String smsContent;

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;

    @ActionArgs(value = "滑块信息", required = true)
    private Map<String, Object> dragMap;

    @ActionArgs(value = "保存图片方式", comment = "默认截图", dict = QrCodeType.class)
    private String getQrCodeType;

    @Override
    public void run() {
        String image = "";
        WebElement element = getTarget();
        File file = null;

        switch (VerifyType.valueOf(verifyType)) {
            case verifyCode: { // 图片验证码
                String imagePath = null;
                if (element != null) {
                    // 截图
                    String imgData = element.getAttribute("src");
                    if (imgData!=null && StringUtils.isNotBlank(getQrCodeType) && QrCodeType.valueOf(getQrCodeType).equals(QrCodeType.Download)) {
                        InputStream stream = null;
                        imagePath = ctx.get(RobotConstant.DATA_PATH) + "\\qr_login_" + UUIDGenerator.uuidStringWithoutLine() + "_img.png";
                        if (imgData.contains("base64,")) {
                            imgData = imgData.substring(imgData.indexOf(",") + 1);
                            try {
                                byte[] imgbytes = new BASE64Decoder().decodeBuffer(imgData);
                                stream = new ByteArrayInputStream(imgbytes);
                            } catch (IOException e) {
                                e.printStackTrace();
                                throw new BusinessException("下载保存二维码失败！");
                            }
                        } else {
                            // 创建一个URL对象
                            try {
                                URL url = new URL(imgData);
                                stream = url.openStream();
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new BusinessException("下载保存二维码失败！");
                            }
                        }
                        file = new File(imagePath);
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            int read;
                            byte[] bytes = new byte[1024];
                            while ((read = stream.read(bytes)) != -1) {
                                fileOutputStream.write(bytes, 0, read);
                            }
                        } catch (Exception e) {
                            throw new BusinessException("下载保存二维码失败！");
                        }
                    } else {
                        file = element.getScreenshotAs(OutputType.FILE);
                    }
                } else if (element == null && StringUtils.isNotBlank(imgPath)) {
                    file = new File(imgPath);
                }

                String orcResult = null;
                if (OCRType.autonomousRecognition.equals(OCRType.valueOf(ocrType))) {
                    //自主识别
                    orcResult = this.autonomousOCR(file);
                    if (StringUtils.isNotBlank(orcResult)) {
                        orcResult = orcResult.replaceAll("-", "").replaceAll("\\.", "")
                                .replaceAll("\\:", "").replaceAll(" ", "");
                    }
                } else if (OCRType.localRecognition.equals(OCRType.valueOf(ocrType))) {
                    /*OcrReqVO reqVO = new OcrReqVO();
                    reqVO.setBytes(FileUtil.readBytes(file));
                    try {
                        orcResult = rpaDesignService.ocrCaptchaNew(reqVO);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    try {
                        BufferedImage bufferedImage = ImgUtil.read(file);
                        String base64 = ImgUtil.toBase64(bufferedImage, "jpg");
                        System.out.println("====== 验证码识别开始 ======");
                        Map<String, String> header = Maps.newHashMap();
                        header.put("contentType", "application/json; charset=UTF-8");
                        String post = HttpUtils.post("https://rpa.seebon.com/api/verifyOcr", base64, header);
                        if (StringUtils.isNotBlank(post)) {
                            JSONObject jsonObject = JSONObject.parseObject(post);
                            orcResult = jsonObject.getString("code").toLowerCase();
                        }
                    } catch (Exception e) {
                        log.error("ocr验证码识别异常" + e.getMessage(), e);
                    }
                } else if (OCRType.ddddocrRecognition.equals(OCRType.valueOf(ocrType))) {
                    orcResult = Convert.ocrText(file, "https://rpa.seebon.com/api/ocr/file/json");
                    if (StringUtils.isBlank(orcResult) || orcResult.length() < 4) {
                        orcResult = Convert.ocrText(file, "https://rpa.seebon.com/api/ocr/file/json2");
                    }
                } else {
                    BufferedImage bufferedImage = ImgUtil.read(file);
                    image = ImgUtil.toBase64(bufferedImage, "jpg");
                    //调用第三方ocr识别工具
                    orcResult = OcrApiUtil.doOCR(image, OCRType.valueOf(ocrType).getCode());
                }
                if (StringUtils.isNotBlank(orcResult)) {
                    ctx.setVariable(dataKey, orcResult);
                }
                break;
            }
            case QRCode: { // 扫码验证
                file = element.getScreenshotAs(OutputType.FILE);
                BufferedImage bufferedImage = ImgUtil.read(file);
                image = ImgUtil.toBase64(bufferedImage, "jpg");
                if (StringUtils.isNotBlank(mailTo)) {
                    SmsUtils.setImgEmail(StringUtils.isBlank(title) ? "" : title, image, mailTo);
                }
                if (StringUtils.isNotBlank(smsTo) && StringUtils.isNotBlank(smsContent)) {
                    com.seebon.common.utils.SmsUtils.send(smsTo, smsContent);
                }
                break;
            }
            case drag: { // 滑块拖到验证
                int distance = 0;
                if (StringUtils.isNotBlank(dragType) && StringUtils.isNotBlank((String) dragMap.get("sXpath")) && StringUtils.isNotBlank((String) dragMap.get("bXpath"))) {
                    if (DragType.valueOf(dragType).equals(DragType.slideFill)) {
                        distance = getDistanceForSlideFill();
                    } else if (DragType.valueOf(dragType).equals(DragType.singleSlider)) {
                        distance = getDistanceForSingleSlider();
                    }
                }
                ctx.setVariable(dataKey, distance);
                break;
            }
            case smsCode: {
                String receiverPhone = ctx.getVariable("receiverPhone");
                GetSmsCodeVO smsCodeVO = new GetSmsCodeVO();
                smsCodeVO.setSmsKeyword(smsContent);
                smsCodeVO.setPhone(receiverPhone);
                String smsCode = rpaDesignService.getSmsCode(smsCodeVO);
                ctx.setVariable(dataKey, smsCode);
                break;
            }
            default:
                break;

        }
        try {
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {

        }
    }

    private Integer getDistanceForSlideFill() {
        top = 0;
        WebDriver driver = ctx.getWebDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        BufferedImage sRead = null;
        BufferedImage bRead = null;

        //调整系数（乘数）
        Integer multiplyCoefficient = (Integer) dragMap.get("multiplyCoefficient");
        if (multiplyCoefficient == null) {
            multiplyCoefficient = 1;
        }

        //调整系数（除数）
        Integer divideCoefficient = (Integer) dragMap.get("divideCoefficient");
        if (divideCoefficient == null) {
            divideCoefficient = 1;
        }

        //原图与页面图片的比值
        Integer zoomProportion = (Integer) dragMap.get("zoomProportion");
        if (zoomProportion == null) {
            zoomProportion = 1;
        }

        //加项调整值
        Integer addCoefficient = (Integer) dragMap.get("addCoefficient");
        if (addCoefficient == null) {
            addCoefficient = 0;
        }

        String sFilePath = (String) dragMap.get("sFilePath");
        File sFile = new File(sFilePath);

        String bFilePath = (String) dragMap.get("bFilePath");
        File bFile = new File(bFilePath);


        WebDriverWait wait = new WebDriverWait(ctx.getWebDriver(), Duration.ofSeconds(1));


        String sXpath = (String) dragMap.get("sXpath");
        sXpath = this.switchToFrame(sXpath);

        WebElement sElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(sXpath)));
        this.releaseToParent();

        String bXpath = (String) dragMap.get("bXpath");
        bXpath = this.switchToFrame(bXpath);
        WebElement bElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(bXpath)));

        String srcType = (String) dragMap.get("srcType");
        if (StringUtils.isBlank(srcType)) {
            srcType = "url";
        }

        bRead = getImage(bElement, bFile, srcType, js, Boolean.FALSE, sElement);
        sRead = getImage(sElement, sFile, srcType, js, Boolean.TRUE, bElement);

        Integer baseTop = (Integer) dragMap.get("baseTop");
        if (baseTop == null) {
            baseTop = 0;
        }
        int totalTop = (top + baseTop)*zoomProportion;
        int h = sRead.getHeight();
        int height = bRead.getHeight();
        if (totalTop + sRead.getHeight() >height) {
            totalTop = height - sRead.getHeight();
        } else if(height >= totalTop + sRead.getHeight() + 4) {
            totalTop = totalTop-1;
            h = h + 3;
        }

        // 大图裁剪
        bRead = bRead.getSubimage(0, totalTop, bRead.getWidth(), h);
        try {
            ImageIO.write(bRead, "png", bFile);
            setWhite(sRead);
            ImageIO.write(sRead, "png", sFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("无法获取图像信息");
        }

        Integer type = (Integer) dragMap.get("type");
        if (type == null || type == 1) {
            return getDistance(sFile, bFile, zoomProportion, multiplyCoefficient, divideCoefficient, addCoefficient);
        } else {
            return getDistance1(sFile, bFile, zoomProportion, multiplyCoefficient, divideCoefficient, addCoefficient);
        }

        /*System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat s_mat = Imgcodecs.imread(sFile.getPath());
        Mat b_mat = Imgcodecs.imread(bFile.getPath());

        // 转灰度图像
        Mat s_newMat = new Mat();
//        Mat b_newMat = new Mat();
        Imgproc.cvtColor(s_mat, s_newMat, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.cvtColor(b_mat, b_newMat, Imgproc.COLOR_BGR2GRAY);
        Imgcodecs.imwrite(sFile.getPath(), s_newMat);
//        Imgcodecs.imwrite(bFile.getPath(), b_newMat);
        // 自适应阈值化
        Mat s_nMat = new Mat();
        Imgproc.adaptiveThreshold(s_newMat, s_nMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 7, -4);
        Imgcodecs.imwrite(sFile.getPath(), s_nMat);
//        Mat b_nMat = new Mat();
//        Imgproc.adaptiveThreshold(b_newMat, b_nMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 7, -4);
//        Imgcodecs.imwrite(bFile.getPath(), b_nMat);

//        b_mat = Imgcodecs.imread(bFile.getPath());
        s_mat = Imgcodecs.imread(sFile.getPath());


        int result_rows = b_mat.rows() - s_mat.rows() + 1;
        int result_cols = b_mat.cols() - s_mat.cols() + 1;
        Mat g_result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        Imgproc.matchTemplate(b_mat, s_mat, g_result, Imgproc.TM_CCOEFF); // 相关系数匹配法
        Core.normalize(g_result, g_result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        Point matchLocation = new org.opencv.core.Point();
        Core.MinMaxLocResult mmlr = Core.minMaxLoc(g_result);
        matchLocation = mmlr.maxLoc; // 此处使用maxLoc还是minLoc取决于使用的匹配算法
//        Imgproc.rectangle(b_mat, matchLocation, new Point(matchLocation.x + s_mat.cols(), matchLocation.y + s_mat.rows()), new Scalar(0, 255, 0, 0));

        Double distance = matchLocation.x / zoomProportion;
        BigDecimal val = new BigDecimal(distance).multiply(new BigDecimal(multiplyCoefficient))
                .divide(new BigDecimal(divideCoefficient), 0, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(addCoefficient));

        System.out.println("length === " + distance + "                ====");
        System.out.println("val === " + val + "                ====");
        //sFile.delete();
        //bFile.delete();
        this.releaseToParent();
        System.gc();
        return val.intValue();*/
    }

    private int getDistance(File sFile, File bFile, Integer zoomProportion, Integer multiplyCoefficient, Integer divideCoefficient, Integer addCoefficient) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat s_mat = Imgcodecs.imread(sFile.getPath());
        Mat b_mat = Imgcodecs.imread(bFile.getPath());
        // 转灰度图像
        Mat s_newMat = new Mat();
        Mat b_newMat = new Mat();
        Imgproc.cvtColor(s_mat, s_newMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(b_mat, b_newMat, Imgproc.COLOR_BGR2GRAY);
        Imgcodecs.imwrite(sFile.getPath(), s_newMat);
        Imgcodecs.imwrite(bFile.getPath(), b_newMat);
        // 自适应阈值化
        Mat s_nMat = new Mat();
        Imgproc.adaptiveThreshold(s_newMat, s_nMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 7, -4);
        Imgcodecs.imwrite(sFile.getPath(), s_nMat);
        Mat b_nMat = new Mat();
        Imgproc.adaptiveThreshold(b_newMat, b_nMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 7, -4);
        Imgcodecs.imwrite(bFile.getPath(), b_nMat);

        b_mat = Imgcodecs.imread(bFile.getPath());
        s_mat = Imgcodecs.imread(sFile.getPath());


        int result_rows = b_mat.rows() - s_mat.rows() + 1;
        int result_cols = b_mat.cols() - s_mat.cols() + 1;
        Mat g_result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        Imgproc.matchTemplate(b_mat, s_mat, g_result, Imgproc.TM_CCOEFF); // 相关系数匹配法
        Core.normalize(g_result, g_result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        Point matchLocation = new org.opencv.core.Point();
        Core.MinMaxLocResult mmlr = Core.minMaxLoc(g_result);
        matchLocation = mmlr.maxLoc; // 此处使用maxLoc还是minLoc取决于使用的匹配算法
        Imgproc.rectangle(b_mat, matchLocation, new Point(matchLocation.x + s_mat.cols(), matchLocation.y + s_mat.rows()), new Scalar(0, 255, 0, 0));

        Double distance = matchLocation.x / zoomProportion;
        BigDecimal val = new BigDecimal(distance).multiply(new BigDecimal(multiplyCoefficient))
                .divide(new BigDecimal(divideCoefficient), 0, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(addCoefficient));

        System.out.println("length === " + distance + "                ====");
        System.out.println("val === " + val + "                ====");
        sFile.delete();
        bFile.delete();
        this.releaseToParent();
        System.gc();
        return val.intValue();
    }

    private int getDistance1(File sFile, File bFile, Integer zoomProportion, Integer multiplyCoefficient, Integer divideCoefficient, Integer addCoefficient) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat s_mat = Imgcodecs.imread(sFile.getPath());
        Mat b_mat = Imgcodecs.imread(bFile.getPath());

        // 转灰度图像
        Mat s_newMat = new Mat();
        Imgproc.cvtColor(s_mat, s_newMat, Imgproc.COLOR_BGR2GRAY);
        Imgcodecs.imwrite(sFile.getPath(), s_newMat);

        // 自适应阈值化
        Mat s_nMat = new Mat();
        Imgproc.threshold(s_newMat, s_nMat, 127, 255, Imgproc.THRESH_BINARY);
//        Imgcodecs.imwrite(sFile.getPath(), s_nMat);
//        s_mat = Imgcodecs.imread(sFile.getPath());


        int result_rows = b_mat.rows() - s_mat.rows() + 1;
        int result_cols = b_mat.cols() - s_mat.cols() + 1;
        Mat g_result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        Imgproc.matchTemplate(b_mat, s_mat, g_result, Imgproc.TM_SQDIFF); // 相关系数匹配法
        Core.normalize(g_result, g_result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        Core.MinMaxLocResult mmlr = Core.minMaxLoc(g_result);
        Point matchLocation = mmlr.maxLoc; // 此处使用maxLoc还是minLoc取决于使用的匹配算法

        Double distance = matchLocation.x / zoomProportion;
        BigDecimal val = new BigDecimal(distance).multiply(new BigDecimal(multiplyCoefficient))
                .divide(new BigDecimal(divideCoefficient), 0, BigDecimal.ROUND_HALF_UP).add(new BigDecimal(addCoefficient));

        System.out.println("length === " + distance + "                ====");
        System.out.println("val === " + val + "                ====");
        sFile.delete();
        bFile.delete();
        this.releaseToParent();
        System.gc();
        return val.intValue();
    }

    private Integer getDistanceForSingleSlider() {
        WebDriver driver = ctx.getWebDriver();
        String sXpath = (String) dragMap.get("sXpath");
        sXpath = this.switchToFrame(sXpath);

        WebElement sElement = driver.findElement(By.xpath(sXpath));
        this.releaseToParent();

        String bXpath = (String) dragMap.get("bXpath");
        bXpath = this.switchToFrame(bXpath);
        WebElement bElement = driver.findElement(By.xpath(bXpath));

        String bWidth = bElement.getCssValue("width");

        String sWidth = sElement.getCssValue("width");

        int bw = 0;
        int sw = 0;

        if (StringUtils.isNotBlank(bWidth)) {
            bw = Integer.valueOf(bWidth.trim().replace("px", ""));
        }

        if (StringUtils.isNotBlank(sWidth)) {
            sw = Integer.valueOf(sWidth.trim().replace("px", ""));
        }
        this.releaseToParent();
        return bw - sw;
    }

    private BufferedImage getImage(WebElement element, File file, String srcType, JavascriptExecutor js, boolean needTop, WebElement needHideElement) {
        BufferedImage bufferedImage = null;
        if ("canvas".equals(element.getTagName())) {
            String sImgInfo = js.executeScript("return arguments[0].toDataURL(\"image/png\");", element).toString();

            if (sImgInfo != null && sImgInfo.contains("data")) {
                sImgInfo = sImgInfo.substring(sImgInfo.indexOf(",") + 1);
                byte[] data = new byte[0];
                try {
                    data = new BASE64Decoder().decodeBuffer(sImgInfo);
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                    bufferedImage = ImageIO.read(byteArrayInputStream);
                    ImageIO.write(bufferedImage, "png", file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                throw new BusinessException("无法获取图像信息");
            }
        } else {
            if (needTop) {
                String cssTop = element.getCssValue("top");
                if (StringUtils.isNotBlank(cssTop)) {
                    String px = cssTop.trim().replace("px", "");
                    if (px.contains(".")) {
                        top = Integer.valueOf(px.substring(0, px.lastIndexOf(".")));
                    } else {
                        try {
                            top = Integer.valueOf(px);
                        }catch (Exception e) {

                        }
                    }
                }
            }
            String imgData = element.getAttribute("src");
            try {
                if ("action".equals(srcType)) {
                    boolean b = element != null && !element.isDisplayed();
                    if (b) {
                        showElement(element, true);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    File file1 = element.getScreenshotAs(OutputType.FILE);
                    bufferedImage = ImgUtil.read(file1);
                    ImageIO.write(bufferedImage, "png", file);
                } else if ("url".equals(srcType)) {
                    if (imgData.contains("base64,")) {
                        try {
                            imgData = imgData.substring(imgData.indexOf(",") + 1);
                            byte[] imgbytes = new BASE64Decoder().decodeBuffer(imgData);
                            InputStream stream = new ByteArrayInputStream(imgbytes);
                            bufferedImage = ImgUtil.read(stream);
                            ImageIO.write(bufferedImage, "png", file);
                        } catch (Exception e) {
                            String style = needHideElement.getAttribute("style");
                            js.executeScript("arguments[0].style.cssText=arguments[1]", needHideElement, StringUtils.isBlank(style) ? "display:none;" : style.concat("display:none;"));
                            File file1 = element.getScreenshotAs(OutputType.FILE);
                            bufferedImage = ImgUtil.read(file1);
                            ImageIO.write(bufferedImage, "png", file);
                            js.executeScript("arguments[0].style.cssText=arguments[1]", needHideElement, StringUtils.isBlank(style) ? "" : style);
                        }

                    } else {
                        bufferedImage = ImgUtil.read(new URL(imgData));
                        ImageIO.write(bufferedImage, "png", file);
                    }

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new BusinessException("无法获取图像信息");
            } catch (IOException e) {
                e.printStackTrace();
                throw new BusinessException("无法获取图像信息");
            }
        }
        return bufferedImage;
    }

    /**
     * 透明区域变白
     *
     * @param image
     * @throws IOException
     */
    private void setWhite(BufferedImage image) throws IOException {
        if (image == null) {
            return;
        } else {
            int rgb;
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    rgb = image.getRGB(i, j);
                    int A = (rgb & 0xFF000000) >>> 24;
                    if (A < 100) {
                        image.setRGB(i, j, new Color(255, 255, 255).getRGB());
                    }
                }
            }
        }
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

    private String autonomousOCR(File file) {
        OcrReqVO reqVO = new OcrReqVO();
        reqVO.setBytes(FileUtil.readBytes(file));
        try {
            String text = rpaDesignService.ocrCaptcha(reqVO);
            log.info("ocr识别验证码 返回：" + text);
            if (StringUtils.isNotBlank(text)) {
                return text;
            }
        } catch (Exception e) {
            log.error("ocr识别验证码异常." + e.getMessage(), e);
        }
        return null;
    }

    private void showElement(WebElement element, boolean show) {
        String script = "display: block!important;";
        if (!show) {
            script = "display: none!important;";
        }
        String style = element.getAttribute("style");
        WebDriver driver = ctx.getWebDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        if (StringUtils.isNotBlank(style)) {
            String[] split = script.split(":");
            Map<String, String> map = Maps.newHashMap();
            map.put(split[0].trim(), split[1].replace(";", ""));

            Map<String, String> map1 = Maps.newHashMap();
            String[] split1 = style.split(";");
            for (String s : split1) {
                if (StringUtils.isNotBlank(s)) {
                    String[] split2 = s.split(":");
                    map1.put(split2[0].trim(), split2[1].trim());
                }
            }

            for(String key : map.keySet()) {
                map1.put(key, map.get(key));
            }
            List<String> cssList = Lists.newArrayList();
            for(String key : map1.keySet()) {
                cssList.add(key.concat(":").concat(map1.get(key)));
            }
            System.out.println("修改后的样式：" + StringUtils.join(cssList, ";"));
            js.executeScript("arguments[0].style.cssText=arguments[1]", element, StringUtils.join(cssList, ";"));
        } else {
            js.executeScript("arguments[0].style.cssText=arguments[1]", element, script);
        }
    }
}
