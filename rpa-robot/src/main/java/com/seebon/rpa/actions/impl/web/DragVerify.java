package com.seebon.rpa.actions.impl.web;

import cn.hutool.core.img.ImgUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.DragStyle;
import com.seebon.rpa.context.enums.DragType;
import com.seebon.rpa.context.enums.MethodType;
import com.seebon.rpa.context.enums.SrcType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@RobotAction(name = "滑块验证", targetType = NoneTarget.class)
public class DragVerify extends AbstractAction {

    private int iframeIndex = 0;

    private static int top = 0;

    @Conditions({"singleSlider:bXpath,sXpath,dragXpath,dragStyle,dataKey",
            "slideFill:bXpath,bFilePath,sXpath,sWidth,sFilePath,srcType,baseTop,adjustCoefficient,addCoefficient,methodType,dragXpath,dragStyle,dataKey",
            "QRCode:mailTo,title,smsTo,smsContent"})
    @ActionArgs(value = "滑动类型", required = true, dict = DragType.class)
    private String dragType;

    @ActionArgs(value = "大图xpath", required = true, comment = "大图xpath路径")
    private String bXpath;

    @ActionArgs(value = "大图保存路径", required = true, comment = "在计算距离是需要将大图图保存值指定的目录")
    private String bFilePath;

    @ActionArgs(value = "小图xpath", required = true, comment = "小图xpath路径")
    private String sXpath;

    @ActionArgs(value = "小图有效宽度", comment = "小图有效宽度，单位px，不填默认全部宽度")
    private String sWidth;

    @ActionArgs(value = "小图保存路径", required = true, comment = "在计算距离是需要将小图保存值指定的目录")
    private String sFilePath;

    @ActionArgs(value = "图片链接类型",dict = SrcType.class, comment = "默认url")
    private String srcType;

    @ActionArgs(value = "小图相对大图顶端的距离", comment = "数值，单位是像素px，默认0")
    private String baseTop;

    @ActionArgs(value = "调整系数", comment = "原图相对于真实展示的图片的大小的倍数，数值，默认1")
    private String adjustCoefficient;

    @ActionArgs(value = "调整值", comment = "在计算出的距离的基础上，额外的距离调整值，可加可减，默认不调整")
    private String addCoefficient;

    @ActionArgs(value = "匹配规则系数", comment = "计算滑块距离的规则系数, 默认TM_CCOEFF", dict = MethodType.class)
    private String methodType;

    @ActionArgs(value = "滑块xpath", comment = "滑块xpath路径，如有填写则自动滑动")
    private String dragXpath;

    @ActionArgs(value = "滑动速度", comment = "默认常规", dict = DragStyle.class)
    private String dragStyle;

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;


    @Override
    public void run() {
        int distance = 0;
        if (DragType.valueOf(dragType).equals(DragType.slideFill)) {
            distance = getDistanceForSlideFill();
        } else if (DragType.valueOf(dragType).equals(DragType.singleSlider)) {
            distance = getDistanceForSingleSlider();
        }
        ctx.setVariable(dataKey, distance);
        if (StringUtils.isNotBlank(dragXpath)) {
            WebDriver driver = ctx.getWebDriver();
            dragXpath = this.switchToFrame(dragXpath);

            WebElement element = driver.findElement(By.xpath(dragXpath));

            Actions action = new Actions(driver);
            action.clickAndHold(element);

            int t = 3;
            int w = 5;

            if (StringUtils.isNotBlank(dragStyle) && "FAST".equals(dragStyle)) {
                t = 1;
                w = 3;
            } else if (StringUtils.isNotBlank(dragStyle) && "GENTLE".equals(dragStyle)) {
                t = 3;
                w = 11;
            }
            int[] times = splitInto(distance, new int[t]);
            for (int i = 0; i < times.length; i++) {
                action.moveByOffset(times[i], i%2==0?-2:2);
            }
            for (int i = 0; i < w; i++) {
                action.moveByOffset(0, i%2==0?-1:1);
            }
            action.moveToElement(element).pause(800).release();
            org.openqa.selenium.interactions.Action actions = action.build();
            actions.perform();

            try {
                Thread.sleep(2000);
                this.releaseToParent();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Integer getDistanceForSlideFill() {
        top = 0;
        WebDriver driver = ctx.getWebDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        BufferedImage sRead = null;
        BufferedImage bRead = null;

        String adjustCoefficient1 = "1";
        if (StringUtils.isNotBlank(adjustCoefficient)) {
            adjustCoefficient1 = adjustCoefficient;
        }

        //加项调整值
        String addCoefficient1 = "0";
        if (StringUtils.isNotBlank(addCoefficient)) {
            addCoefficient1 = addCoefficient;
        }

        WebDriverWait wait = new WebDriverWait(ctx.getWebDriver(), Duration.ofSeconds(1));

        sXpath = this.switchToFrame(sXpath);
        WebElement sElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(sXpath)));
        this.releaseToParent();

        bXpath = this.switchToFrame(bXpath);
        WebElement bElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(bXpath)));

        if (StringUtils.isBlank(srcType)) {
            srcType = "url";
        }

        File sFile = new File(sFilePath);

        File bFile = new File(bFilePath);

        bRead = getImage(bElement, bFile, srcType, js, Boolean.FALSE, sElement);
        sRead = getImage(sElement, sFile, srcType, js, Boolean.TRUE, bElement);

        this.releaseToParent();

        if (StringUtils.isBlank(baseTop)) {
            baseTop = "0";
        }
        BigDecimal totalTop = (new BigDecimal(top).add(new BigDecimal(baseTop))).multiply(new BigDecimal(adjustCoefficient1));
        int h = sRead.getHeight();
        int height = bRead.getHeight();
        if (totalTop.add(new BigDecimal(sRead.getHeight())).intValue() >height) {
            totalTop = new BigDecimal(height).subtract(new BigDecimal(sRead.getHeight()));
        } else if(height >= totalTop.add(new BigDecimal(sRead.getHeight() + 4)).intValue()) {
            totalTop = totalTop.subtract(new BigDecimal(1));
            h = h + 3;
        }

        // 大图裁剪
        bRead = bRead.getSubimage(0, totalTop.intValue(), bRead.getWidth(), h);
        try {
            ImageIO.write(bRead, "png", bFile);
            setWhite(sRead);
            ImageIO.write(sRead, "png", sFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("无法获取图像信息");
        }


        if (StringUtils.isBlank(methodType) || "TM_CCOEFF".equals(methodType)) {
            return getDistance(sFile, bFile, adjustCoefficient1, addCoefficient1);
        } else {
            return getDistance1(sFile, bFile, adjustCoefficient1, addCoefficient1);
        }

    }

    private Integer getDistanceForSingleSlider() {
        WebDriver driver = ctx.getWebDriver();
        sXpath = this.switchToFrame(sXpath);

        WebElement sElement = driver.findElement(By.xpath(sXpath));
        this.releaseToParent();

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

    private int getDistance(File sFile, File bFile, String adjustCoefficient, String addCoefficient) {
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

        BigDecimal distance = new BigDecimal(matchLocation.x).divide(new BigDecimal(adjustCoefficient), BigDecimal.ROUND_HALF_UP);
        BigDecimal val = distance.add(new BigDecimal(addCoefficient));

        System.out.println("length === " + distance + "                ====");
        System.out.println("val === " + val + "                ====");
        sFile.delete();
        bFile.delete();
        this.releaseToParent();
        System.gc();
        return val.intValue();
    }

    private int getDistance1(File sFile, File bFile, String adjustCoefficient, String addCoefficient) {
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

        BigDecimal distance = new BigDecimal(matchLocation.x).divide(new BigDecimal(adjustCoefficient), BigDecimal.ROUND_HALF_UP);
        BigDecimal val = distance.add(new BigDecimal(addCoefficient));

        System.out.println("length === " + distance + "                ====");
        System.out.println("val === " + val + "                ====");
        sFile.delete();
        bFile.delete();
        this.releaseToParent();
        System.gc();
        return val.intValue();
    }


    private int[] splitInto(int number, int[] numberOfSplits) {
        Random random = new Random();
        for (int i = 0; i < numberOfSplits.length; i++) {
            if (number > 1) {
                int j = random.nextInt(number - 1) + 1;
                number -= j;
                if (i == numberOfSplits.length - 1) {
                    numberOfSplits[i] = j + number;
                } else {
                    numberOfSplits[i] = j;
                }
            } else if (number == 1) {
                numberOfSplits[i] = number;
                break;
            }
        }
        return numberOfSplits;
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
                    if (needTop && StringUtils.isNotBlank(sWidth) && new BigDecimal(sWidth).intValue()<bufferedImage.getWidth()) {
                        bufferedImage = bufferedImage.getSubimage(0, 0, new BigDecimal(sWidth).intValue(), bufferedImage.getHeight());
                    }
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
                    if (needTop && StringUtils.isNotBlank(sWidth) && new BigDecimal(sWidth).intValue()<bufferedImage.getWidth()) {
                        bufferedImage = bufferedImage.getSubimage(0, 0, new BigDecimal(sWidth).intValue(), bufferedImage.getHeight());
                    }
                    ImageIO.write(bufferedImage, "png", file);
                } else if ("url".equals(srcType)) {
                    if (imgData.contains("base64,")) {
                        try {
                            imgData = imgData.substring(imgData.indexOf(",") + 1);
                            byte[] imgbytes = new BASE64Decoder().decodeBuffer(imgData);
                            InputStream stream = new ByteArrayInputStream(imgbytes);
                            bufferedImage = ImgUtil.read(stream);
                            if (needTop && StringUtils.isNotBlank(sWidth) && new BigDecimal(sWidth).intValue()<bufferedImage.getWidth()) {
                                bufferedImage = bufferedImage.getSubimage(0, 0, new BigDecimal(sWidth).intValue(), bufferedImage.getHeight());
                            }
                            ImageIO.write(bufferedImage, "png", file);
                        } catch (Exception e) {
                            String style = needHideElement.getAttribute("style");
                            js.executeScript("arguments[0].style.cssText=arguments[1]", needHideElement, StringUtils.isBlank(style) ? "display:none;" : style.concat("display:none;"));
                            File file1 = element.getScreenshotAs(OutputType.FILE);
                            bufferedImage = ImgUtil.read(file1);
                            if (needTop && StringUtils.isNotBlank(sWidth) && new BigDecimal(sWidth).intValue()<bufferedImage.getWidth()) {
                                bufferedImage = bufferedImage.getSubimage(0, 0, new BigDecimal(sWidth).intValue(), bufferedImage.getHeight());
                            }
                            ImageIO.write(bufferedImage, "png", file);
                            js.executeScript("arguments[0].style.cssText=arguments[1]", needHideElement, StringUtils.isBlank(style) ? "" : style);
                        }

                    } else {
                        bufferedImage = ImgUtil.read(new URL(imgData));
                        if (needTop && StringUtils.isNotBlank(sWidth) && new BigDecimal(sWidth).intValue()<bufferedImage.getWidth()) {
                            bufferedImage = bufferedImage.getSubimage(0, 0, new BigDecimal(sWidth).intValue(), bufferedImage.getHeight());
                        }
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
