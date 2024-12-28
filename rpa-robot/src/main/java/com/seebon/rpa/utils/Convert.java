package com.seebon.rpa.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpConfig;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.runtime.RobotConfigException;
import com.seebon.rpa.context.runtime.RobotRuntimeException;
import com.seebon.rpa.entity.robot.RobotExecution;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.entity.robot.RobotOfferRuleItem;
import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.entity.robot.vo.RobotOfferRuleVO;
import com.seebon.rpa.entity.saas.vo.DeclareTempColInfoDTO;
import com.seebon.rpa.entity.saas.vo.DeclareTempInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v114.fetch.model.RequestPaused;
import org.openqa.selenium.devtools.v114.network.model.Headers;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.springframework.jdbc.core.RowMapper;

import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
public final class Convert {


    public static RobotFlowStep getNextStep(List<RobotFlowStep> steps, String stepCode) {
        Optional<RobotFlowStep> step = steps.stream().filter(s -> s.getStepCode().equals(stepCode)).findFirst();
        if (step.isPresent()) {
            return step.get();
        }
        return null;
    }

    public static boolean isContains(List<RobotFlowStep> steps) {
        if (CollectionUtils.isEmpty(steps)) {
            return false;
        }
        return steps.stream().anyMatch(s -> RobotConstant.START_ACTION_CODE.equals(s.getActionCode()));
    }

    public static void sleep(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getKeyEventValue(Character c) {
        switch (c) {
            case 'A':
                return KeyEvent.VK_A;
            case 'B':
                return KeyEvent.VK_B;
            case 'C':
                return KeyEvent.VK_C;
            case 'D':
                return KeyEvent.VK_D;
            case 'E':
                return KeyEvent.VK_E;
            case 'F':
                return KeyEvent.VK_F;
            case 'G':
                return KeyEvent.VK_G;
            case 'H':
                return KeyEvent.VK_H;
            case 'I':
                return KeyEvent.VK_I;
            case 'J':
                return KeyEvent.VK_J;
            case 'K':
                return KeyEvent.VK_K;
            case 'L':
                return KeyEvent.VK_L;
            case 'M':
                return KeyEvent.VK_M;
            case 'N':
                return KeyEvent.VK_N;
            case 'O':
                return KeyEvent.VK_O;
            case 'P':
                return KeyEvent.VK_P;
            case 'Q':
                return KeyEvent.VK_Q;
            case 'R':
                return KeyEvent.VK_R;
            case 'S':
                return KeyEvent.VK_S;
            case 'T':
                return KeyEvent.VK_T;
            case 'U':
                return KeyEvent.VK_U;
            case 'V':
                return KeyEvent.VK_V;
            case 'W':
                return KeyEvent.VK_W;
            case 'X':
                return KeyEvent.VK_X;
            case 'Y':
                return KeyEvent.VK_Y;
            case 'Z':
                return KeyEvent.VK_Z;
            case '1':
                return KeyEvent.VK_1;
            case '2':
                return KeyEvent.VK_2;
            case '3':
                return KeyEvent.VK_3;
            case '4':
                return KeyEvent.VK_4;
            case '5':
                return KeyEvent.VK_5;
            case '6':
                return KeyEvent.VK_6;
            case '7':
                return KeyEvent.VK_7;
            case '8':
                return KeyEvent.VK_8;
            case '9':
                return KeyEvent.VK_9;
            case '0':
                return KeyEvent.VK_0;
            case '/':
                return KeyEvent.VK_SLASH;
            case '-':
                return KeyEvent.VK_MINUS;
            case ':':
                return KeyEvent.VK_COLON;
            case '.':
                return KeyEvent.VK_PERIOD;
            default:
                return 0;
        }
    }

    public static String appendPath(String... paths) {
        String filePath = "";
        for (String path : paths) {
            filePath = filePath + File.separator + path;
        }
        return StringUtils.substringAfter(filePath, File.separator);
    }

    public static String getNewPath(String downloadPath, List<String> fileNames) {
        List<File> files = FileUtil.loopFiles(downloadPath);
        for (File file : files) {
            if (file.getPath().endsWith(".png")) {
                continue;
            }
            List<String> fileNameList = fileNames.stream().filter(name -> name.equals(file.getName())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(fileNameList)) {
                continue;
            }
            log.info("新的文件：filePath=" + file.getPath());
            return file.getPath();
        }
        return null;
    }

    public static RowMapper newRowMapper() {
        return new RowMapper<Map<String, Object>>() {
            @Override
            public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
                Map<String, Object> map = Maps.newHashMap();
                try {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    for (int j = 1; j <= metaData.getColumnCount(); j++) {
                        String columnName = metaData.getColumnLabel(j);
                        Object columnValue = resultSet.getObject(columnName);
                        map.put(columnName, columnValue);
                    }
                } catch (SQLException e) {
                    log.error("【Exception】" + e.getMessage(), e);
                }
                return map;
            }
        };
    }

    public static String ToHexStrFromByte(byte[] byteDatas) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < byteDatas.length; i++) {
            builder.append(String.format("{0:X2} ", byteDatas[i]));
        }
        return builder.toString().trim();
    }

    /**
     * 创建SSL上下文，忽略证书验证
     */
    public static SSLConnectionSocketFactory newSSLSocketFactory() {
        try {
            SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadTrustMaterial((chain, authType) -> true);
            return new SSLConnectionSocketFactory(sslContextBuilder.build(), NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            log.error("创建SSL上下文，忽略证书验证异常." + e.getMessage(), e);
        }
        return null;
    }

    public static RequestConfig getRequestConfig(int timeout) {
        int socketTimeout = 300 * 1000;//默认 5分钟
        if (timeout > 120) {
            socketTimeout = timeout * 1000;//默认 秒
        }
        return RequestConfig.custom().setConnectTimeout(30 * 1000)// 连接超时时间 30秒
                .setSocketTimeout(socketTimeout)// 数据传输超时时间
                .setConnectionRequestTimeout(30 * 1000)//请求超时时间 30秒
                .build();
    }

    public static HttpConfig getHttpConfig(int timeout) {
        int socketTimeout = 300 * 1000;//默认 5分钟
        if (timeout > 120) {
            socketTimeout = timeout * 1000;//默认 秒
        }
        return HttpConfig.create().setConnectionTimeout(30 * 1000)// 连接超时时间 30秒
                .setReadTimeout(socketTimeout);// 数据传输超时时间
    }

    public static Integer getBusinessType(String businessType) {
        if ("1001001".equals(businessType)) {
            return 1;
        }
        if ("1001002".equals(businessType)) {
            return 2;
        }
        return null;
    }

    public static void respToFile(CloseableHttpResponse response, String filePath) {
        FileOutputStream fos = null;
        try {
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            File file = new File(filePath);
            fos = new FileOutputStream(file);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            inputStream.close();
            fos.flush();
        } catch (Exception e) {
            if (e instanceof RobotRuntimeException) {
                throw (RobotRuntimeException) e;
            } else {
                log.error("【Exception】", e);
                throw new RobotRuntimeException("http download failed. info:" + e.getMessage());
            }
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                log.error("【Exception】", e);
            }
        }
    }

    public static String resp2File(CloseableHttpResponse response, String filePath) {
        FileOutputStream fos = null;
        try {
            HttpEntity entity = response.getEntity();
            Header[] fileNameHeader = response.getHeaders("FileName");
            if (fileNameHeader == null || fileNameHeader.length < 1) {
                return "";
            }
            filePath = filePath + fileNameHeader[0].getValue();
            System.out.println("filePath" + filePath);
            InputStream inputStream = entity.getContent();
            File file = new File(filePath);
            fos = new FileOutputStream(file);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            inputStream.close();
            fos.flush();
        } catch (Exception e) {
            if (e instanceof RobotRuntimeException) {
                throw (RobotRuntimeException) e;
            } else {
                log.error("【Exception】", e);
                throw new RobotRuntimeException("http download failed. info:" + e.getMessage());
            }
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                log.error("【Exception】", e);
            }
        }
        return filePath;
    }

    //    String date1 = "202310";
    //    String date2 = "2023-10-01";
    //    String date3 = "2023-10";
    //    String date4 = "20231001";
    //    String date5 = "2023/10/01";
    //    String date6 = "2023/10";
    public static String parseDate(String date) {
        if (date.contains("-")) {
            if (date.split("-").length == 3) {
                String[] split = date.split("-");
                return split[0] + "-" + split[1];
            }
            return date;
        } else if (date.contains("/")) {
            String[] split = date.split("/");
            return split[0] + "-" + split[1];
        } else {
            if (date.length() == 8) {
                DateTime parse = DateUtil.parse(date, "yyyyMMdd");
                return parse.toString("yyyy-MM");
            }
            return date.substring(0, 4) + "-" + date.substring(4);
        }
    }

    public static RobotOfferRuleItem getRuleItem(List<RobotOfferRuleVO> ruleList, String failReason) {
        for (RobotOfferRuleVO rule : ruleList) {
            for (RobotOfferRuleItem itemVo : rule.getRuleItemList()) {
                if (itemVo.getStatus() != 1) {
                    continue;
                }
                Integer matchRule = itemVo.getMatchRule();
                String content = itemVo.getContent();
                Boolean isMatch = Convert.matchContent(matchRule, content, failReason);
                if (isMatch) {
                    log.info("失败原因：" + failReason + ",匹配的规则：" + JSON.toJSONString(itemVo));
                    return itemVo;
                }
            }
        }
        log.info("失败原因：" + failReason + ",没有匹配的规则");
        return null;
    }

    /**
     * 匹配规则：1-equals 2-contains  3-startsWith 4-endsWith
     */
    public static boolean matchContent(Integer matchRule, String content, String failReason) {
        if (StringUtils.isBlank(failReason)) {
            return false;
        }
        Boolean isMatch = false;
        if (1 == matchRule) {
            if (failReason.equals(content)) {
                isMatch = true;
            }
        }
        if (2 == matchRule) {
            if (failReason.contains(content)) {
                isMatch = true;
            }
        }
        if (3 == matchRule) {
            if (failReason.startsWith(content)) {
                isMatch = true;
            }
        }
        if (4 == matchRule) {
            if (failReason.endsWith(content)) {
                isMatch = true;
            }
        }
        return isMatch;
    }

    public static void fetchMap(String[] headerRow, String[] dataRow, Map<String, Object> map) {
        if (ArrayUtil.isEmpty(headerRow) || ArrayUtil.isEmpty(dataRow)) {
            return;
        }
        for (int i = 0; i < headerRow.length; i++) {
            if (!map.containsKey(headerRow[i]) || StringUtils.isNotBlank(dataRow[i])) {
                map.put(headerRow[i], dataRow[i]);
            }
        }
    }

    public static Object getCellVal(Row row, Integer value) {
        Object val = null;
        if (row.getCell(value) == null) {
            return val;
        }
        Cell cell = row.getCell(value);
        switch (cell.getCellType().name()) {
            case "STRING":
                val = cell.getRichStringCellValue().getString();
                break;
            case "NUMERIC":
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    Date dateCellValue = cell.getDateCellValue();
                    val = new SimpleDateFormat("yyyy/MM/dd").format(dateCellValue);
                } else {
                    String s2 = new DecimalFormat("#.##").format(cell.getNumericCellValue());
                    val = new BigDecimal(s2).stripTrailingZeros().toPlainString();
                }
                break;
            case "BOOLEAN":
                val = cell.getBooleanCellValue();
                break;
            case "BLACK":
                val = "";
                break;
            default:
                val = cell.toString();
                break;
        }
        return val;
    }

    public static String getCellVal(Cell cell) {
        String val = "";
        if (cell == null) {
            return val;
        }
        switch (cell.getCellType().name()) {
            case "STRING":
                val = cell.getRichStringCellValue().getString();
                break;
            case "NUMERIC":
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    Date dateCellValue = cell.getDateCellValue();
                    val = new SimpleDateFormat("yyyy/MM/dd").format(dateCellValue);
                } else {
                    String s2 = new DecimalFormat("#.##").format(cell.getNumericCellValue());
                    val = new BigDecimal(s2).stripTrailingZeros().toPlainString();
                }
                break;
            case "BLACK":
                val = "";
                break;
            default:
                val = cell.toString();
                break;
        }
        return val;
    }

    public static DeclareTempColInfoDTO getByFieldCode(DeclareTempInfoDTO declareInfo, String fieldCode) {
        Optional<DeclareTempColInfoDTO> first = declareInfo.getCols().stream().filter(col -> fieldCode.equals(col.getFieldCode())).findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }

    public static boolean isContainUrl(String urls, String responseUrl) {
        if (StringUtils.isBlank(urls) || StringUtils.isBlank(responseUrl)) {
            return false;
        }
        for (String url : urls.split(",")) {
            if (responseUrl.contains(url)) {
                return true;
            }
        }
        return false;
    }

    public static String[] getHeaderRow(DeclareTempInfoDTO declareInfo, String[] excelHeaderRow) {
        if (CollectionUtils.isEmpty(declareInfo.getCols())) {
            return excelHeaderRow;
        }
        List<DeclareTempColInfoDTO> cols = declareInfo.getCols();
        Integer colIndexMax = cols.stream().map(vo -> vo.getColIndex()).reduce(Integer::max).get();
        String[] headerRow = new String[colIndexMax + 1];
        for (DeclareTempColInfoDTO col : cols) {
            headerRow[col.getColIndex()] = col.getFieldName();
        }
        for (int i = 0; i < headerRow.length; i++) {
            String header = headerRow[i];
            if (StringUtils.isBlank(header)) {
                headerRow[i] = excelHeaderRow[i];
            }
        }
        return headerRow;
    }

    public static void releaseExcel(Workbook workbook, String filePath) {
        if (workbook == null) {
            return;
        }
        try {
            log.info("被改写了，保存workbook。");
            byte[] bytes = workbook2Byte(workbook);
            //先关闭
            workbook.close();

            BufferedOutputStream bos = null;
            try {
                File file = new File(filePath);
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bos.write(bytes);
                bos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(bos);
            }
        } catch (Exception e) {
            log.error("【Exception】", e);
        }
    }

    public static byte[] workbook2Byte(Workbook workbook) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        baos.flush();
        return baos.toByteArray();
    }

    public static void writeFile(byte[] bytes, String filePath) {
        BufferedOutputStream bos = null;
        try {
            File file = new File(filePath);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(bytes);
            bos.flush();
        } catch (Exception e) {
            log.error("写入文件异常." + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(bos);
        }
    }

    public static CellStyle getCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    public static boolean isAddressAvailable(String address) {
        String[] parts = address.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid debugger address format: " + address);
        }
        String host = parts[0];
        int port;
        try {
            port = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port number in debugger address: " + address, e);
        }

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 10000);  // 超时设置为2秒
            return true;
        } catch (Exception e) {
            log.error("debugger address is not available: {}", e.getMessage());
        }
        return false;
    }

    public static String getDevice(List<String> devices, String orginal) {
        for (String device : devices) {
            String deviceMark = device.split("\\\\")[2];
            if (!deviceMark.equals(orginal)) {
                continue;
            }
            return device;
        }
        throw new RobotConfigException("[uKey异常] usb端口未插入key，请检查.");
    }

    public static boolean isBing(Map<String, String> usbMap, String device) {
        for (String value : usbMap.values()) {
            if (!device.endsWith(value)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public static String ocrText(File file, String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("image", file, ContentType.MULTIPART_FORM_DATA, file.getName());
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity == null) {
                return null;
            }
            String resp = EntityUtils.toString(responseEntity);
            if (StringUtils.isBlank(resp)) {
                return null;
            }
            JSONObject jsonObject = JSONObject.parseObject(resp);
            if (jsonObject.containsKey("result")) {
                return jsonObject.getString("result").toLowerCase();
            }
        } catch (IOException e) {
            log.error("ocr验证码识别异常" + e.getMessage(), e);
        }
        return null;
    }

    public static byte[] getBytes(BufferedImage bufferedImage) {
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

    public static String screenShot(RobotTaskSessionKeep task, ChromeDriver remoteDriver) {
        try {
            String dataPath = RobotContext.workPath + "\\cache\\images\\";
            FileUtil.mkdir(dataPath);
            String fileName = "会话已丢失_" + task.getTaskCode() + "_" + task.getPort() + "_" + UUIDGenerator.uuidStringWithoutLine() + ".png";
            String filePath = StrUtil.appendIfMissing(dataPath, "\\", "\\") + fileName;

            File file = remoteDriver.getScreenshotAs(OutputType.FILE);
            BufferedImage bufferedImage = ImgUtil.read(file);
            File saveFile = new File(filePath);
            byte[] bytes = Convert.getBytes(bufferedImage);
            FileUtil.writeBytes(bytes, saveFile);
            return filePath;
        } catch (Exception e) {
            log.error("【session维持】 截图异常." + e.getMessage(), e);
        }
        return null;
    }

    public static void quit(WebDriver driver, String openInterceptApi) {
        if (driver == null) {
            log.info("driver is null.");
            return;
        }
        try {
            if (StringUtils.isNotBlank(openInterceptApi)) {
                if (driver instanceof ChromeDriver) {
                    DevTools devTools = ((ChromeDriver) driver).getDevTools();
                    devTools.clearListeners();
                    devTools.close();
                    log.info("关闭 DevTool成功.");
                }
            }
        } catch (Exception e) {
            log.error("关闭 DevTool异常." + e.getMessage(), e);
        }
        try {
            for (String windowHandle : driver.getWindowHandles()) {
                try {
                    driver.switchTo().window(windowHandle);
                    driver.close();
                    log.info("关闭窗口成功.");
                } catch (Exception e) {
                    log.error("关闭窗口异常." + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("关闭窗口异常." + e.getMessage(), e);
        }
        try {
            driver.quit();
            log.info("退出浏览器成功.");
        } catch (Exception e) {
            log.error("退出浏览器异常." + e.getMessage(), e);
        }
        if (driver instanceof InternetExplorerDriver) {
            List<String> commandsList = Lists.newArrayList();
            commandsList.add("CMD");
            commandsList.add("/C");
            commandsList.add("taskkill /F /IM iexplore.exe");
            String[] commands = new String[commandsList.size()];
            commands = commandsList.toArray(commands);
            try {
                Process pid = Runtime.getRuntime().exec(commands);
                CompletableFuture.runAsync(() -> {
                    log.info("shell exec start");
                    try {
                        //脚本执行正常时的输出信息
                        String inputResult = IoUtil.read(pid.getInputStream(), Charset.forName("GBK"));
                        //脚本执行异常时的输出信息
                        String errorResult = IoUtil.read(pid.getErrorStream(), Charset.forName("GBK"));
                        log.info("kill ie inputResult=" + inputResult + ",errorResult=" + errorResult);
                    } catch (Exception e) {
                        log.error("shell exec error." + e.getMessage(), e);
                    }
                    log.info("shell exec end");
                });
                pid.waitFor();
            } catch (Exception e) {
                log.error("kill IE浏览器异常." + e.getMessage(), e);
            }
        }
    }

    public static String getErrorMsg(List<RobotExecution> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        return list.stream().map(de -> de.getError()).distinct().collect(Collectors.joining(","));
    }

    public static List<String> paramKeyList(String paramKeys) {
        return Lists.newArrayList(paramKeys.split(","));
    }

    public static void parseJsonData(JSONObject json, List<String> paramKeyList, Map<String, Object> map) {
        for (String key : json.keySet()) {
            Object value = json.get(key);
            if (value == null || StringUtils.isBlank(value.toString())) {
                continue;
            }
            if (value instanceof JSONObject) {
                parseJsonData((JSONObject) value, paramKeyList, map);
            } else if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                if (array.size() == 0) {
                    continue;
                }
                for (int i = 0; i < array.size(); i++) {
                    Object obj = array.get(i);
                    if (obj instanceof JSONObject) {
                        parseJsonData((JSONObject) obj, paramKeyList, map);
                    } else {
                        if (paramKeyList.contains(key)) {
                            map.put(key, value);
                        }
                    }
                }
            } else {
                if (paramKeyList.contains(key)) {
                    map.put(key, value);
                }
            }
        }
    }


    public static Map<String, Object> parseRequestData(RequestPaused requestPaused, String paramKeys) {
        Map<String, Object> map = Maps.newHashMap();
        List<String> paramKeyList = paramKeyList(paramKeys);
        //1、请求头参数
        Headers headers = requestPaused.getRequest().getHeaders();
        for (String paramKey : paramKeyList) {
            for (String headerKey : headers.keySet()) {
                if (paramKey.toLowerCase().equals(headerKey.toLowerCase())) {
                    map.put(paramKey, headers.get(headerKey));
                }
            }
        }
        //2、请求链接参数
        UrlBuilder builder = UrlBuilder.ofHttp(requestPaused.getRequest().getUrl(), Charset.defaultCharset());
        Map<CharSequence, CharSequence> queryMap = builder.getQuery().getQueryMap();
        for (CharSequence key : queryMap.keySet()) {
            if (paramKeyList.contains(key.toString())) {
                map.put(key.toString(), queryMap.get(key));
            }
        }
        //3、请求参数
        Optional<String> postData = requestPaused.getRequest().getPostData();
        if (!postData.isPresent() || StringUtils.isBlank(postData.get())) {
            return map;
        }
        if (JSONUtil.isTypeJSON(postData.get())) {
            Convert.parseJsonData(JSON.parseObject(postData.get()), paramKeyList, map);
            return map;
        }
        String[] postDatas = postData.get().split("&");
        for (String data : postDatas) {
            if (StringUtils.isBlank(data)) {
                continue;
            }
            String[] keyValue = data.split("=");
            if (keyValue.length != 2) {
                continue;
            }
            String key = keyValue[0];
            String value = keyValue[1];
            if (paramKeyList.contains(key)) {
                map.put(key, value);
            }
        }
        return map;
    }

    public static Map<String, Object> parseResponseData(String responseUrl, String responseBody, String paramKeys) {
        Map<String, Object> map = Maps.newHashMap();
        List<String> paramKeyList = paramKeyList(paramKeys);
        UrlBuilder builder = UrlBuilder.ofHttp(responseUrl, Charset.defaultCharset());
        Map<CharSequence, CharSequence> queryMap = builder.getQuery().getQueryMap();
        for (CharSequence key : queryMap.keySet()) {
            if (paramKeyList.contains(key.toString())) {
                map.put(key.toString(), queryMap.get(key));
            }
        }
        if (JSONUtil.isTypeJSON(responseBody)) {
            JSONObject json = JSON.parseObject(responseBody);
            Convert.parseJsonData(json, paramKeyList, map);
            map.put("apiRespBody", json);
        } else {
            map.put("apiRespBody", responseBody);
        }
        return map;
    }

    public static String getText(WebElement element) {
        String text = "";
        try {
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
            log.info("text=" + text);
        } catch (Exception e) {
            log.error("读取文本异常." + e.getMessage(), e);
        }
        return StrUtil.trimToEmpty(text.trim());
    }

    public static Integer getPort(RobotTaskSessionKeep task) {
        if (task.getSharePort() != null) {
            return task.getSharePort();
        }
        return task.getPort();
    }

    public static List<String> read(InputStream input) {
        List<String> lines = Lists.newArrayList();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(input, Charset.forName("GBK")));
            String line = null;
            while ((line = reader.readLine()) != null) {
                lines.add(new String(line.getBytes(), "UTF8").trim());
            }
        } catch (Exception e) {
            log.error("read input stream error." + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(input);
        }
        return lines;
    }

    public static String getUserDataDir(String dirKey) {
        String userDataDir = RobotContext.workPath + "\\cache\\webDriver\\scoped_dir_" + dirKey + "\\" + UUIDGenerator.uuidStringWithoutLine() + "\\";;
        FileUtil.mkdir(userDataDir);
        return userDataDir;
    }

    public static void cleanUserDataDir(String dirKey){
        String userDataDir = RobotContext.workPath + "\\cache\\webDriver\\scoped_dir_" + dirKey + "\\";
        try {
            File dir = new File(userDataDir);
            if(dir.exists()){
                FileUtils.deleteDirectory(dir);
            }
        } catch (IOException e) {
            log.error("清理浏览器缓存异常." + e.getMessage(), e);
        }
    }
}
