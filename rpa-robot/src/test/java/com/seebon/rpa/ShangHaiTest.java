package com.seebon.rpa;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.AttributeType;
import com.seebon.rpa.context.enums.ClickType;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class ShangHaiTest {
    @Autowired
    private RobotContext ctx;

    //超时时间
    private static final long timeout = 120;
    //证书密码
    private static final String password = "143a5f1b";
    //数据库连接
    private static final String db_url = "jdbc:mysql://192.168.0.81:3306/spfcore_test?characterEncoding=utf-8&rewriteBatchedStatements=true";
    //数据库用户名
    private static final String db_userName = "dev";
    //数据库密码
    private static final String db_password = "dev";

    @Test
    void testShangHai() throws Exception {
        WebDriver driver = this.getWebDriver();

        driver.get("https://www.shanghai.gov.cn/");

        //点击-登录
        WebElement dl = getElement(AttributeType.id, "dl");
        dl.click();

        //点击-法人登录
        WebElement but1 = getElement(AttributeType.xpath, "//a[contains(text(),'法人登录')]");
        but1.click();

        //点击-法人一证通登录
        WebElement button = getElement(AttributeType.xpath, "//span[text()='法人一证通登录']");
        button.click();

        //输入密码
        WebElement login = getElement(AttributeType.id, "login_showPwd");
        login.click();
        this.paste(password);
        this.sleep(2);

        //点击登录
        WebElement loginbtn = getElement(AttributeType.id, "loginbtn");
        this.click(loginbtn, ClickType.actionClick);
        this.sleep(2);

        //访问登记首页
        driver.get("https://zzjb.rsj.sh.gov.cn/portal-zzjb-shrs/ywtb-zl-index");
        this.sleep(12);

        //再次粘贴密码
        this.paste(password);

        //访问登记页面
        driver.get("https://zzjb.rsj.sh.gov.cn/affair-shrs/affair/case/create?affairCode=L0103&acceptType=2");
        this.sleep(12);

        JdbcTemplate jdbcTemplate = this.getDruidDataSource();
        List<Map<String, Object>> areaList = jdbcTemplate.queryForList("SELECT * FROM  t_social_shanghai_area_2023_03_15 WHERE level = 1");
        for (Map<String, Object> data : areaList) {
            String addr = (String) data.get("addr");
            String code = (String) data.get("code");
            Long id = (Long) data.get("id");
            if (StringUtils.isBlank(code)) {
                continue;
            }
            this.sleep(null);
            List<JSONObject> newList = this.loadData("https://zzjb.rsj.sh.gov.cn/ggjbxx-zzjb-shrs/0030/ggjbxx/gg007/queryRoadInfo?qxid=" + code);
            this.batchSave(jdbcTemplate, newList, 2, id.intValue());
            System.out.println("【" + addr + "】level=2, size=" + newList.size() + " 采集完成.");
        }
        System.out.println("所有 level=2 采集完成.");

        this.sleep(3);
        List<Map<String, Object>> areaList2 = jdbcTemplate.queryForList("SELECT  ar.* FROM t_social_shanghai_area_2023_03_15  ar WHERE  ar.level = 2  AND ar.id  NOT IN ( SELECT  DISTINCT arr.parent_id  FROM t_social_shanghai_area_2023_03_15  arr  WHERE arr.level = 3 )");
        for (Map<String, Object> data : areaList2) {
            String addr = (String) data.get("addr");
            String code = (String) data.get("code");
            Long id = (Long) data.get("id");
            if (StringUtils.isBlank(code)) {
                continue;
            }
            this.sleep(null);
            List<JSONObject> list = this.loadData("https://zzjb.rsj.sh.gov.cn/ggjbxx-zzjb-shrs/0030/ggjbxx/gg007/queryMlphInfo?lxdz=" + code);
            this.batchSave(jdbcTemplate, list, 3, id.intValue());
            System.out.println("【" + addr + "】level=3, size=" + list.size() + " 采集完成.");
        }
        System.out.println("所有 level=3 采集完成.");
        //退出浏览器
        driver.quit();
    }

    private WebElement getElement(AttributeType attrType, String attrValue) {
        try {
            this.sleep(2);

            WebDriver driver = ctx.getVariable(RobotConstant.DEFAULT_DRIVER_KEY);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            switch (attrType) {
                case id:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.id(attrValue)));
                case _name:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.name(attrValue)));
                case xpath:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(attrValue)));
                case className:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.className(attrValue)));
                case tagName:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName(attrValue)));
                case cssSelector:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(attrValue)));
                case linkText:
                    return wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(attrValue)));
                default:
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void click(WebElement element, ClickType clickType) {
        switch (clickType) {
            case click: {
                element.click();
                break;
            }
            case jsClick: {
                WebDriver driver = ctx.getVariable(RobotConstant.DEFAULT_DRIVER_KEY);
                JavascriptExecutor js = ((JavascriptExecutor) driver);
                js.executeScript("arguments[0].click();", element);
                break;
            }
            case actionClick: {
                WebDriver driver = ctx.getVariable(RobotConstant.DEFAULT_DRIVER_KEY);
                Actions actions = new Actions(driver);
                actions.click(element).perform();
                break;
            }
            default:
                break;
        }
    }

    private void paste(String content) {
        try {
            Robot robot = new Robot();
            robot.setAutoDelay(500);
            String[] cob = "VK_CONTROL+VK_V".split("\\+");
            int c0 = KeyEvent.class.getField(cob[0]).getInt(null);
            int c1 = KeyEvent.class.getField(cob[1]).getInt(null);
            if (c0 == KeyEvent.VK_CONTROL && c1 == KeyEvent.VK_V) {
                StringSelection stringSelection = new StringSelection(content);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            }
            robot.keyPress(c0);
            robot.keyPress(c1);
            robot.keyRelease(c1);
            robot.keyRelease(c0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<JSONObject> loadData(String url) {
        WebDriver driver = ctx.getVariable(RobotConstant.DEFAULT_DRIVER_KEY);
        driver.get(url);
        WebElement webElement = driver.findElement(By.tagName("body"));
        return JSON.parseArray(webElement.getText(), JSONObject.class);
    }

    private WebDriver getWebDriver() {
        System.setProperty("java.awt.headless", "false");
        System.setProperty("webdriver.chrome.driver", RobotContext.workPath + "\\driver\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> chromePrefs = Maps.newHashMap();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        options.setExperimentalOption("prefs", chromePrefs);
        options.addArguments("--disable-infobars");
        WebDriver driver = new ChromeDriver(options);

        ((JavascriptExecutor) driver).executeScript("window.focus();");
        driver.manage().timeouts().pageLoadTimeout(timeout, TimeUnit.SECONDS);//元素等待时间（隐式等待）
        driver.manage().window().maximize();//浏览器最大化
        ctx.setVariable(RobotConstant.DEFAULT_DRIVER_KEY, driver);
        return driver;
    }

    private void batchSave(JdbcTemplate jdbcTemplate, List<JSONObject> list, Integer level, Integer parentId) {
        final String sql = "insert into t_social_shanghai_area_2023_03_15 (level,code,addr,parent_id) VALUES(?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public int getBatchSize() {
                return list.size();
            }

            public void setValues(PreparedStatement ps, int i) throws SQLException {
                JSONObject json = list.get(i);
                ps.setInt(1, level);
                ps.setString(2, json.getString("value"));
                ps.setString(3, json.getString("text"));
                ps.setInt(4, parentId);
            }
        });
    }

    private JdbcTemplate getDruidDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(db_url);
        dataSource.setUsername(db_userName);
        dataSource.setPassword(db_password);
        return new JdbcTemplate(dataSource);
    }

    private void sleep(Integer time) {
        try {
            int rand;
            if (time != null && time > 0) {
                rand = time * 1000;
            } else {
                rand = (int) (Math.random() * 100);
                if (rand < 100) {
                    rand += 100;
                }
            }
            Thread.sleep(rand);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
