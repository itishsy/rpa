package com.seebon.rpa.actions.target.impl;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.actions.impl.tool.Keyboard;
import com.seebon.rpa.actions.target.AbstractTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.SessionKeep;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionTarget;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.BrowserType;
import com.seebon.rpa.context.enums.OpenMode;
import com.seebon.rpa.context.enums.StartOperation;
import com.seebon.rpa.runner.SyncInService;
import com.seebon.rpa.service.RobotTaskSessionKeepService;
import com.seebon.rpa.utils.Convert;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v114.fetch.Fetch;
import org.openqa.selenium.devtools.v114.fetch.model.RequestId;
import org.openqa.selenium.devtools.v114.fetch.model.RequestPaused;
import org.openqa.selenium.devtools.v114.network.Network;
import org.openqa.selenium.devtools.v114.network.model.ResourceType;
import org.openqa.selenium.devtools.v114.page.Page;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;
import java.util.*;

@Slf4j
@ActionTarget
public class DriverTarget extends AbstractTarget<WebDriver> {

    @Setter
    @Conditions({
            "chrome:optionMap,startOperation,blockedUrls,loadPlugsPath,openMode",
            "chrome360:optionMap,startOperation,blockedUrls,openMode",
            "ie:defaultUrl",
            "firefox:optionMap,startOperation,blockedUrls,openMode"
    })
    @ActionArgs(value = "浏览器", dict = BrowserType.class, comment = "当前浏览器")
    private String type;

    @Setter
    @ActionArgs("驱动参数")
    private Map<String, Object> optionMap;

    @Setter
    @ActionArgs(value = "默认url", comment = "要访问的页面")
    private String defaultUrl;

    @Setter
    @ActionArgs(value = "启动操作", dict = StartOperation.class, comment = "启动操作")
    private String startOperation;

    @Setter
    @ActionArgs(value = "打开模式", dict = OpenMode.class, comment = "打开模式")
    private String openMode;

    @Setter
    @ActionArgs(value = "屏蔽Urls", comment = "需要屏蔽Urls,逗号隔开")
    private String blockedUrls;

    @Setter
    @ActionArgs(value = "加载插件地址", comment = "加载插件地址")
    private String loadPlugsPath;

    @Value("${rpa.bin360:}")
    private String binary360;

    @Value("${rpa.binFirefox:}")
    private String binaryFirefox;

    @Setter
    private String driverPath;
    @Setter
    private String plugsPath;
    @Setter
    private String binaryPath;
    @Autowired
    private SyncInService syncInService;
    @Autowired
    private Keyboard keyboard;
    @Autowired
    private RobotTaskSessionKeepService taskSessionKeepService;
    @Autowired
    protected SessionKeep sessionKeep;

    private static final String STEALTH_JS = "plugins/stealth.min.js";
    private static final String DISABLE_DEVTOOL_JS = "plugins/disable.devtool.js";

    @Override
    public WebDriver fetchTarget() {
        WebDriver driver = null;
        if (!ctx.getVariables().containsKey(RobotConstant.DEFAULT_DRIVER_KEY)) {
            driver = this.newDriver();
        } else if (StringUtils.isBlank(type)) {
            driver = this.lastDriver();
        } else {
            switch (BrowserType.valueOf(type)) {
                case chrome:
                    if (syncInService.isDevEnv(null) || taskSessionKeepService.getSessionKeepPort() != null) {
                        driver = this.newDriver();
                    } else {
                        driver = ctx.getVariable(RobotConstant.DEFAULT_DRIVER_KEY);
                    }
                    break;
                default:
                    driver = ctx.getVariable(RobotConstant.DEFAULT_DRIVER_KEY);
                    break;
            }
            if (driver == null) {
                driver = this.newDriver();
            } else {
                ctx.setVariable(RobotConstant.DEFAULT_DRIVER_KEY, driver);
            }
        }
        return driver;
    }

    private WebDriver lastDriver() {
        Object o = ctx.getVariables().get(RobotConstant.DEFAULT_DRIVER_KEY);
        if (o instanceof List) {
            List<WebDriver> drivers = (List<WebDriver>) o;
            return drivers.get(drivers.size() - 1);
        } else {
            return (WebDriver) o;
        }
    }

    private WebDriver newDriver() {
        this.showDesktop();
        BrowserType driverType = StringUtils.isNotBlank(type) ? BrowserType.valueOf(type) : BrowserType.chrome;
        WebDriver driver;
        switch (driverType) {
            case ie:
                driver = ie();
                this.interceptor(driver);
                this.cacheAccountNumberDriver();
                break;
            case chrome360:
                driver = chrome360();
                this.interceptor(driver);
                this.cacheAccountNumberDriver();
                break;
            case firefox: {
                driver = fireFox();
                this.interceptor(driver);
                this.cacheAccountNumberDriver();
                break;
            }
            case edge: {
                driver = edge();
                this.interceptor(driver);
                this.clearBrowserFeatures(driver);
                this.cacheAccountNumberDriver();
                break;
            }
            case chrome:
            default:
                driver = chrome();
                this.interceptor(driver);
                this.clearBrowserFeatures(driver);
                this.cacheAccountNumberDriver();
                break;
        }
        ctx.setVariable(RobotConstant.DEFAULT_DRIVER_KEY, driver);
        return driver;
    }

    private void showDesktop() {
        try {
            keyboard.setKeyIn("VK_WINDOWS+VK_D");
            keyboard.run();
        } catch (Exception e) {
            log.error("启动浏览器前，显示桌面异常." + e.getMessage(), e);
        }
    }

    private void cacheAccountNumberDriver() {
        Map<String, String> driverMap = ctx.get(RobotConstant.ACCOUNTNUMBER_DRIVER);
        if (MapUtils.isEmpty(driverMap)) {
            driverMap = Maps.newHashMap();
        }
        driverMap.put(ctx.getAccountNumberDriverKey(), ctx.getAccountNumberDriverKey());

        ctx.setVariable(RobotConstant.ACCOUNTNUMBER_DRIVER, driverMap);
    }

    private WebDriver fireFox() {
        log.info("start a fireFox driver");
        System.setProperty("java.awt.headless", "false");
        System.setProperty("webdriver.gecko.driver", RobotContext.workPath + "\\driver\\geckodriver.exe");
        FirefoxProfile profile = new FirefoxProfile();
        if (MapUtils.isNotEmpty(optionMap)) {
            for (String key : optionMap.keySet()) {
                Object useDownloadDir = optionMap.get("browser.download.useDownloadDir");
                if (useDownloadDir != null && Boolean.parseBoolean(useDownloadDir.toString())) {
                    profile.setPreference("browser.download.dir", ctx.get(RobotConstant.DOWNLOAD_DEFAULT_PATH));
                }
                profile.setPreference(key, optionMap.get(key));
            }
        } else {
            profile.setPreference("browser.download.useDownloadDir", false);
        }
        FirefoxOptions options = new FirefoxOptions();
        if (StringUtils.isNotBlank(binaryFirefox)) {
            options.setBinary(binaryFirefox);
        }
        options.setProfile(profile);
        if (StringUtils.isNotBlank(openMode) && OpenMode.noTrace.equals(OpenMode.valueOf(openMode))) {
            options.addArguments("-private");//使用无痕模式
        }
        return new FirefoxDriver(options);
    }

    private WebDriver ie() {
        log.info("start a ie driver");
        System.setProperty("java.awt.headless", "false");
        System.setProperty("webdriver.ie.driver", RobotContext.workPath + "\\driver\\IEDriverServer.exe");
        InternetExplorerOptions options = new InternetExplorerOptions();
        options.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
        options.setCapability("nativeEvents", false);
        options.setCapability("ignoreProtectedModeSettings", true);
        options.setCapability("enablePersistentHover", true);
        if (StringUtils.isNotBlank(defaultUrl)) {
            options.withInitialBrowserUrl(defaultUrl);
        }
        //options.setCapability("unexpectedAlertBehaviour", "accept");
        //options.setCapability("disable-popup-blocking", true);
        if (optionMap != null && optionMap.size() > 0) {
            for (String key : optionMap.keySet()) {
                options.setCapability(key, optionMap.get(key));
            }
        }
        options.addCommandSwitches("--headless");
        return new InternetExplorerDriver(options);
    }

    private WebDriver edge() {
        log.info("start a edge driver");
        System.setProperty("java.awt.headless", "false");
        System.setProperty("webdriver.edge.driver", RobotContext.workPath + "\\driver\\msedgedriver.exe");
        EdgeOptions options = new EdgeOptions();
        // 设置下载路径
        Map<String, Object> prefs = Maps.newHashMap();
        prefs.put("download.default_directory", ctx.get(RobotConstant.DOWNLOAD_DEFAULT_PATH));
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        if (optionMap != null && optionMap.size() > 0) {
            for (String key : optionMap.keySet()) {
                options.setCapability(key, optionMap.get(key));
            }
        }
        return new EdgeDriver(options);
    }

    private WebDriver chrome() {
        log.info("start a chrome driver");
        System.setProperty("java.awt.headless", "false");
        if (StringUtils.isNotBlank(driverPath)) {
            System.setProperty("webdriver.chrome.driver", driverPath);
        } else {
            System.setProperty("webdriver.chrome.driver", RobotContext.workPath + "\\driver\\chromedriver.exe");
        }
        ChromeOptions options = new ChromeOptions();
        if (MapUtils.isNotEmpty(optionMap)) {
            for (String key : optionMap.keySet()) {
                Map<String, Object> optionValueMap = (Map<String, Object>) optionMap.get(key);
                optionValueMap.put("download.default_directory", ctx.get(RobotConstant.DOWNLOAD_DEFAULT_PATH));
                if ("prefs".equals(key)) {
                    optionValueMap.put("credentials_enable_service", false);
                    optionValueMap.put("profile.password_manager_enabled", false);
                    optionValueMap.put("dom.webdriver.enabled", false);
                }
                options.setExperimentalOption(key, optionValueMap);
            }
        } else {
            HashMap<String, Object> chromePrefs = Maps.newHashMap();
            chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put("download.prompt_for_download.popups", false);
            chromePrefs.put("download.default_directory", ctx.get(RobotConstant.DOWNLOAD_DEFAULT_PATH));
            chromePrefs.put("plugins.always_open_pdf_externally", true);
            chromePrefs.put("credentials_enable_service", false);
            chromePrefs.put("profile.password_manager_enabled", false);
            chromePrefs.put("dom.webdriver.enabled", false);
            chromePrefs.put("profile.default_content_setting_values.automatic_downloads", 1);
            options.setExperimentalOption("prefs", chromePrefs);
        }
        options.addArguments("--window-position=0,0");  // 将窗口定位到屏幕左上角
        options.addArguments("--disable-infobars");
        options.addArguments("--window-size=1366,2160"); //最大化
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-dev-shm-usage=true");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-cache");//不使用缓存 防止我们自定的js失效
        options.addArguments("--media-cache-size=0");//不使用缓存 防止我们自定的js失效
        options.addArguments("--disk-cache-size=0");//不使用缓存 防止我们自定的js失效
        String userDataDir = Convert.getUserDataDir(ctx.getAccountNumberDriverKey());
        options.addArguments("user-data-dir=" + userDataDir); // 设置用户数据目录
        options.addArguments("disk-cache-dir=" + userDataDir); // 设置缓存目录
        options.setExperimentalOption("w3c", true);
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        if (StringUtils.isNotBlank(plugsPath)) {
            ctx.setVariable(RobotConstant.OPEN_TOOL_BROWSER, "true");
            options.addArguments(String.format("--load-extension=%s", plugsPath));
        }
        if (StringUtils.isNotBlank(loadPlugsPath)) {
            options.addArguments(String.format("--load-extension=%s", loadPlugsPath));
        }
        if (StringUtils.isNotBlank(binaryPath)) {
            options.setBinary(binaryPath);
        } else {
            options.setBinary(RobotContext.workPath + "\\browser\\chrome\\chrome.exe");
        }
        if (StringUtils.isNotBlank(openMode) && OpenMode.noTrace.equals(OpenMode.valueOf(openMode))) {
            options.addArguments("--incognito");//使用无痕模式
        }
        if (syncInService.isDevEnv(null) || taskSessionKeepService.getSessionKeepPort() != null) {
            Integer port = this.getPort();
            Map<Integer, String> portMap = ctx.get(RobotConstant.REMOTE_DEBUG_PORT);
            if (MapUtils.isNotEmpty(portMap) && portMap.keySet().contains(port)) {
                return this.remoteWebDriver(null, "10019001");
            }
            ChromeDriver driver = this.remoteWebDriver(null, "10019001");
            if (driver == null) {
                options.addArguments("--remote-debugging-port=" + port);
                ChromeDriver chromeDriver = new ChromeDriver(options);
                driver = this.remoteWebDriver(chromeDriver, "10019001");
            }
            return driver;
        }
        WebDriver driver = null;
        try {
            driver = new ChromeDriver(options);
        } catch (SessionNotCreatedException e) {
            log.error("启动浏览器失败,重试一次." + e.getMessage(), e);
            try {
                if (driver != null) {
                    driver.quit();
                }
            } catch (Exception ex) {
                log.error("启动浏览器失败，退出浏览器异常." + e.getMessage(), e);
            }
            driver = new ChromeDriver(options);
        }
        return driver;
    }

    private WebDriver chrome360() {
        log.info("start a 360 driver");
        System.setProperty("webdriver.chrome.driver", RobotContext.workPath + "\\driver\\chromedriver360.exe");
        ChromeOptions options = new ChromeOptions();
        if (MapUtils.isNotEmpty(optionMap)) {
            for (String key : optionMap.keySet()) {
                Map<String, Object> optionValueMap = (Map<String, Object>) optionMap.get(key);
                optionValueMap.put("download.default_directory", ctx.get(RobotConstant.DOWNLOAD_DEFAULT_PATH));
                options.setExperimentalOption(key, optionMap.get(key));
            }
        } else {
            HashMap<String, Object> chromePrefs = Maps.newHashMap();
            chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put("download.prompt_for_download.popups", false);
            chromePrefs.put("download.default_directory", ctx.get(RobotConstant.DOWNLOAD_DEFAULT_PATH));
            chromePrefs.put("plugins.always_open_pdf_externally", true);
            options.setExperimentalOption("prefs", chromePrefs);
        }
        options.setBinary(binary360);
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.addArguments("--disable-infobars");
        options.addArguments("--window-size=1366,2160"); //最大化
        options.addArguments("--remote-allow-origins=*");
        String userDataDir = Convert.getUserDataDir(ctx.getAccountNumberDriverKey());
        options.addArguments("user-data-dir=" + userDataDir); // 设置用户数据目录
        options.addArguments("disk-cache-dir=" + userDataDir); // 设置缓存目录
        if (StringUtils.isNotBlank(openMode) && OpenMode.noTrace.equals(OpenMode.valueOf(openMode))) {
            options.addArguments("--incognito");//使用无痕模式
        }
        options.setExperimentalOption("w3c", true);
        options.addArguments("--remote-allow-origins=*");
        if (syncInService.isDevEnv(null) || taskSessionKeepService.getSessionKeepPort() != null) {
            Integer port = this.getPort();
            Map<Integer, String> portMap = ctx.get(RobotConstant.REMOTE_DEBUG_PORT);
            if (MapUtils.isNotEmpty(portMap) && portMap.keySet().contains(port)) {
                return this.remoteWebDriver(null, "10019004");
            }
            ChromeDriver driver = this.remoteWebDriver(null, "10019004");
            if (driver == null) {
                options.addArguments("--remote-debugging-port=" + port);
                ChromeDriver chromeDriver = new ChromeDriver(options);
                driver = this.remoteWebDriver(chromeDriver, "10019004");
            }
            return driver;
        }
        return new ChromeDriver(options);
    }

    public ChromeDriver remoteWebDriver(ChromeDriver targetDriver, String runSupport) {
        Integer port = this.getPort();
        String debuggerAddress = "127.0.0.1:" + port;
        boolean isAvailable = Convert.isAddressAvailable(debuggerAddress);
        if (!isAvailable) {
            this.log(port, "连接失败.");
            sessionKeep.removeDriver(port);
            return null;
        }
        ChromeDriver driver = sessionKeep.getRemoteDriver(port);
        if (driver != null) {
            return driver;
        }
        try {
            if ("10019004".equals(runSupport)) {
                System.setProperty("webdriver.chrome.driver", RobotContext.workPath + "\\driver\\chromedriver360.exe");
            } else {
                System.setProperty("webdriver.chrome.driver", RobotContext.workPath + "\\driver\\chromedriver.exe");
            }
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--remote-allow-origins=*");
            chromeOptions.setExperimentalOption("debuggerAddress", debuggerAddress);
            driver = new ChromeDriver(chromeOptions);
            if (syncInService.isDevEnv(null)) {
                this.cachePort(port);
            }
            sessionKeep.setRemoteDriver(port, driver);
            if (targetDriver != null) {
                sessionKeep.setTargetDriver(port, targetDriver);
            }
            this.log(port, "连接成功.");
        } catch (Exception e) {
            this.log(port, "连接失败.");
            sessionKeep.removeDriver(port);
            log.error("remoteWebDriver超时，端口：" + port, e);
        }
        return driver;
    }

    public Integer getPort() {
        if (syncInService.isDevEnv(null)) {
            Integer taskId = ctx.get("taskId");
            String tplTypeCode = ctx.get("tplTypeCode");
            return taskId + Integer.parseInt(tplTypeCode.replace("1000", ""));
        }
        return taskSessionKeepService.getSessionKeepPort();
    }

    private void cachePort(Integer port) {
        String taskCode = ctx.get("taskCode");
        Map<Integer, String> portMap = ctx.get(RobotConstant.REMOTE_DEBUG_PORT);
        if (MapUtils.isEmpty(portMap)) {
            portMap = Maps.newHashMap();
        }
        portMap.put(port, taskCode);
        ctx.setVariable(RobotConstant.REMOTE_DEBUG_PORT, portMap);
    }

    private void log(Integer port, String msg) {
        String taskCode = ctx.get("taskCode");
        String addrName = ctx.get("addrName");
        String accountNumber = ctx.getAccountNumber();
        log.info("taskCode=" + taskCode + ",地区：" + addrName + ",申报账户：" + accountNumber + ",remote debug port: " + port + " " + msg);
    }

    public void removePort(Integer port) {
        Map<Integer, String> portMap = ctx.get(RobotConstant.REMOTE_DEBUG_PORT);
        if (MapUtils.isEmpty(portMap)) {
            return;
        }
        portMap.remove(port);
        ctx.setVariable(RobotConstant.REMOTE_DEBUG_PORT, portMap);
    }

    /**
     * 去除模拟浏览器的特征
     *
     * @param driver
     */
    private void clearBrowserFeatures(WebDriver driver) {
        try {
            //((HasCdp) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", this.getSource(STEALTH_JS));
            if (StringUtils.isNotBlank(startOperation) && StartOperation.openInterceptAndDebug.equals(StartOperation.valueOf(startOperation))) {
                ((HasCdp) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", this.getSource(DISABLE_DEVTOOL_JS));
                Map<String, Object> map = Maps.newHashMap();
                String jsToInject = "() => {" + "  window.setInterval = function(){};" + "  DisableDevtool.isSuspend = true;" + "  DisableDevtool.interval = 9999999999;" + "  document.addEventListener('DOMContentLoaded', () => {" + "     window.setInterval = null;" + "     DisableDevtool.isSuspend = true;" + "     DisableDevtool.interval = 9999999999;" + "  });" + "}";
                map.put("source", jsToInject);
                ((HasCdp) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", map);
            }
        } catch (Exception e) {
            log.error("去除模拟浏览器的特征异常." + e.getMessage(), e);
        }
    }

    private Map<String, Object> getSource(String fileName) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
        String stealth = IoUtil.readUtf8(is);
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("source", stealth);
        return dataMap;
    }

    public void interceptor(WebDriver driver) {
        if (StringUtils.isBlank(startOperation)) {
            return;
        }
        log.info("已开启拦截：startOperation=" + startOperation);

        ctx.setVariable(RobotConstant.OPEN_INTERCEPT_API, startOperation);

        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();

        //屏蔽请求
        this.blockedUrl(devTools);

        //拦截请求
        this.requestInterceptor(devTools);

        //拦截响应
        this.responseInterceptor(devTools);

        //页面监听
        this.loadingFinished(devTools);
    }

    private void requestInterceptor(DevTools devTools) {
        try {
            devTools.send(Fetch.enable(Optional.empty(), Optional.empty()));
            devTools.addListener(Fetch.requestPaused(), requestPaused -> {
                RequestId requestId = requestPaused.getRequestId();
                String requestUrl = requestPaused.getRequest().getUrl();
                if (ResourceType.XHR == requestPaused.getResourceType()) {
                    this.parseRequestData(requestPaused);
                }
                // 继续原始请求
                devTools.send(Fetch.continueRequest(requestId, Optional.of(requestUrl), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
            });
        } catch (Exception e) {
            log.error("拦截请求异常." + e.getMessage(), e);
        }
    }

    private void responseInterceptor(DevTools devTools) {
        try {
            devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
            devTools.addListener(Network.responseReceived(), responseReceived -> {
                this.isSuspend("responseReceived");
                if (ResourceType.XHR != responseReceived.getType()) {
                    return;
                }
                try {
                    String responseUrl = responseReceived.getResponse().getUrl();
                    //拦截的接口地址
                    String urls = ctx.get(RobotConstant.INTERCEPTOR_URLS);
                    //需要获取的参数名称
                    String paramKeys = ctx.get(RobotConstant.INTERCEPTOR_PARAM_KEYS);
                    if (StringUtils.isBlank(urls) || !Convert.isContainUrl(urls, responseUrl) || StringUtils.isBlank(paramKeys)) {
                        return;
                    }
                    //获取响应数据
                    Network.GetResponseBodyResponse response = devTools.send(Network.getResponseBody(responseReceived.getRequestId()));
                    Map<String, Object> responseMap = Convert.parseResponseData(responseUrl, response.getBody(), paramKeys);
                    if (MapUtils.isEmpty(responseMap)) {
                        return;
                    }
                    log.info("已获取的响应参数：" + JSON.toJSONString(responseMap) + ",需要获取的响应参数：" + paramKeys + ",拦截响应接口地址：" + responseUrl);
                    for (String key : responseMap.keySet()) {
                        ctx.setVariable(key, responseMap.get(key));
                        log.info("拦截响应参数：" + key + "=" + responseMap.get(key));
                    }
                } catch (Exception e) {
                    log.info("拦截响应异常." + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            log.error("拦截响应异常." + e.getMessage(), e);
        }
    }

    private void loadingFinished(DevTools devTools) {
        try {
            devTools.addListener(Network.requestWillBeSent(), requestWillBeSent -> {
                this.isSuspend("requestWillBeSent");
            });
            devTools.addListener(Network.loadingFinished(), loadingFinished -> {
                this.isSuspend("loadingFinished");
            });
            devTools.send(Page.enable());
            devTools.addListener(Page.domContentEventFired(), domContentEventFired -> {
                this.isSuspend("domContentEventFired");
            });
            devTools.addListener(Page.frameNavigated(), domContentEventFired -> {
                this.isSuspend("frameNavigated");
            });
            devTools.addListener(Page.frameAttached(), domContentEventFired -> {
                this.isSuspend("frameNavigated");
            });
            devTools.addListener(Page.documentOpened(), domContentEventFired -> {
                this.isSuspend("documentOpened");
            });
            devTools.addListener(Page.loadEventFired(), domContentEventFired -> {
                this.isSuspend("loadEventFired");
            });
            devTools.addListener(Page.javascriptDialogClosed(), domContentEventFired -> {
                this.isSuspend("javascriptDialogClosed");
            });
            devTools.addListener(Page.javascriptDialogOpening(), domContentEventFired -> {
                this.isSuspend("javascriptDialogOpening");
            });
            devTools.addListener(Page.frameAttached(), domContentEventFired -> {
                this.isSuspend("frameAttached");
            });
            devTools.addListener(Page.frameStartedLoading(), domContentEventFired -> {
                this.isSuspend("frameStartedLoading");
            });
            devTools.addListener(Page.frameStoppedLoading(), domContentEventFired -> {
                this.isSuspend("frameStoppedLoading");
            });
        } catch (Exception e) {
            log.error("页面监听异常." + e.getMessage(), e);
        }
    }

    private void blockedUrl(DevTools devTools) {
        try {
            if (StringUtils.isBlank(blockedUrls)) {
                return;
            }
            String openInterceptApi = ctx.get(RobotConstant.OPEN_INTERCEPT_API);
            if (StringUtils.isNotBlank(openInterceptApi)) {
                devTools.send(Network.setBlockedURLs(Lists.newArrayList(blockedUrls.split(","))));
            }
        } catch (Exception e) {
            log.error("屏蔽请求异常." + e.getMessage(), e);
        }
    }

    private void isSuspend(String msg) {
        String openInterceptApi = ctx.get(RobotConstant.OPEN_INTERCEPT_API);
        if (StringUtils.isNotBlank(openInterceptApi) && StartOperation.openInterceptAndDebug.equals(StartOperation.valueOf(openInterceptApi))) {
            WebDriver driver = ctx.getDriver();
            try {
                ((JavascriptExecutor) driver).executeScript("window.setInterval = function(){}");
            } catch (Exception e) {
            }
            try {
                ((JavascriptExecutor) driver).executeScript("DisableDevtool.isSuspend = true");
            } catch (Exception e) {
            }
        }
    }

    private void parseRequestData(RequestPaused requestPaused) {
        String requestUrl = requestPaused.getRequest().getUrl();
        //拦截的接口地址
        String apiRespUrls = ctx.get(RobotConstant.INTERCEPTOR_URLS);
        //需要获取的参数名称
        String paramKeys = ctx.get(RobotConstant.INTERCEPTOR_PARAM_KEYS);
        if (StringUtils.isBlank(apiRespUrls) || !Convert.isContainUrl(apiRespUrls, requestUrl) || StringUtils.isBlank(paramKeys)) {
            return;
        }
        Map<String, Object> requestMap = Convert.parseRequestData(requestPaused, paramKeys);
        if (MapUtils.isEmpty(requestMap)) {
            return;
        }
        log.info("已获取的请求参数：" + JSON.toJSONString(requestMap) + ",需要获取的请求参数：" + paramKeys + "，拦截请求接口地址：" + requestUrl);
        for (String key : requestMap.keySet()) {
            ctx.setVariable(key, requestMap.get(key));
            log.info("拦截请求参数：" + key + "=" + requestMap.get(key));
        }
    }

    public void closeDevTools(WebDriver driver) {
        String openInterceptApi = ctx.get(RobotConstant.OPEN_INTERCEPT_API);
        if (StringUtils.isBlank(openInterceptApi)) {
            return;
        }
        try {
            if (driver instanceof ChromeDriver) {
                DevTools devTools = ((ChromeDriver) driver).getDevTools();
                devTools.clearListeners();
                devTools.close();
            }
        } catch (Exception e) {
            log.error("关闭 DevTool异常." + e.getMessage(), e);
        }
    }
}
