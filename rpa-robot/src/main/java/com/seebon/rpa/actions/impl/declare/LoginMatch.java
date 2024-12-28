package com.seebon.rpa.actions.impl.declare;

import com.google.common.collect.Maps;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.DriverTarget;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.BrowserType;
import com.seebon.rpa.context.enums.LoginMatchType;
import com.seebon.rpa.context.runtime.RobotInterruptException;
import com.seebon.rpa.context.runtime.RuntimeSkipTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@ActionUtils
@RobotAction(name = "登录判断", targetType = NoneTarget.class, comment = "判断是否已登录，是返回True，否则执行登录的初始化操作，如启动浏览器、激活CA等")
public class LoginMatch extends AbstractAction {

    @ActionArgs(value = "登录判断类型", dict = LoginMatchType.class)
    @Conditions({
            "elementExists:url,xpath,retrySize",
            "elementTextContain:url,xpath,matchValue,retrySize",
            "elementNoExists:url,xpath,retrySize",
            "respExists:url,respKey,matchValue,retrySize",
            "respContain:url,respKey,matchValue,retrySize",
    })
    private String matchType;

    @ActionArgs(value = "账户信息URL", style = DynamicFieldType.text)
    private String url;

    @ActionArgs(value = "元素Xpath", style = DynamicFieldType.text)
    private String xpath;

    @ActionArgs(value = "响应参数", style = DynamicFieldType.text)
    private String respKey;

    @ActionArgs(value = "匹配值", style = DynamicFieldType.text)
    private String matchValue;

    @Autowired
    private DriverTarget driverTarget;

    @Override
    public void run() {
        if (isLogin()) {
            throw new RuntimeSkipTo(skipTo);
        } else {
            preLogin();
            if (StringUtils.isNotBlank(failedSkipTo)) {
                throw new RuntimeSkipTo(failedSkipTo);
            }
            if (StringUtils.isNotBlank(falseSkipTo)) {
                throw new RuntimeSkipTo(falseSkipTo);
            }
            throw new RuntimeSkipTo(RobotConstant.NEXT_STEP);
        }
    }

    private boolean isLogin() {
        if (StringUtils.isBlank(matchType)) {
            log.warn("未配置判断类型");
            return false;
        }
        if (ctx.contains("driver")) {
            try {
                WebDriver driver = ctx.getWebDriver();
                if (driver.getWindowHandles().size() == 0) {
                    return false;
                }
                long timeout = getTimeout() / 2;
                if (StringUtils.isNotBlank(url)) {
                    driver.manage().timeouts().pageLoadTimeout(timeout, TimeUnit.SECONDS);//元素等待时间（隐式等待）
                    driver.get(url);
                }
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
                WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
                switch (LoginMatchType.valueOf(matchType)) {
                    case elementExists:
                        return element != null;
                    case elementNoExists:
                        return element == null;
                    case elementTextContain:
                        String tagName = element.getTagName();
                        String text = element.getText();
                        if ("span".equals(tagName) || "label".equals(tagName)) {
                            text = element.getAttribute("innerHTML");
                        } else if ("textarea".equals(tagName) || "input".equals(tagName)) {
                            text = element.getAttribute("value");
                        }
                        return (text != null && text.contains(matchValue));
                    default:
                        break;
                }
            } catch (NoSuchWindowException ne){
                log.info("窗口被关闭");
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                log.info("判断登录失败");
            }
        }
        return false;
    }

    /**
     * 登录准备
     */
    private void preLogin() {
        if (!ctx.contains("driver")) {
            String runSupport = ctx.getVariable("runSupport");
            BrowserType browserType = BrowserType.chrome;
            if (runSupport.equals("10019002"))
                browserType = BrowserType.ie;
            else if (runSupport.equals("10019003")) {
                browserType = BrowserType.firefox;
            } else if (runSupport.equals("10019004")) {
                browserType = BrowserType.chrome360;
            }
            driverTarget.setType(browserType.name());
            if (browserType.equals(BrowserType.chrome) || browserType.equals(BrowserType.chrome360)) {
                Map<String, Object> optionMap = Maps.newHashMap();
                Map<String, Object> map = Maps.newHashMap();
                map.put("profile.default_content_settings.popups", 0);
                map.put("download.prompt_for_download", false);
                optionMap.put("prefs", map);
                driverTarget.setOptionMap(optionMap);
            }
            driverTarget.fetchTarget();
        }
    }
}
