package com.seebon.rpa.actions.impl.web;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ElementTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.runtime.RobotConfigException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.List;

@RobotAction(name = "上传文件", targetType = ElementTarget.class, comment = "上传文件")
public class UploadFile extends AbstractAction {

    @ActionArgs(value = "文件路径")
    private String filePath;

    @Override
    public void run() {
        WebDriver driver = ctx.getWebDriver();
        WebElement element = getTarget();
        if (element == null) {
            List<WebElement> elements = driver.findElements(By.xpath("//input[@type='file']"));
            if (elements == null || elements.size() > 1) {
                throw new RobotConfigException("上传文件配置错误，未找到file控件或存在多个");
            } else {
                element = elements.get(0);
            }
        }

        if (!element.isDisplayed()) {
            String attrs = "class,style";
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for (String attr : attrs.split(",")) {
                js.executeScript("arguments[0].removeAttribute(arguments[1])", element, attr);
            }
            js.executeScript("arguments[0].style.cssText=arguments[1]", element, "display: block!important;");
        }

        File file = new File(filePath);
        if (!file.isFile() || !file.exists()) {
            throw new RobotConfigException("上传文件配置错误。上传的文件不存在");
        }

        element.clear();
        element.sendKeys((CharSequence) filePath);
    }
}
