package com.seebon.rpa.actions.impl.web;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import com.seebon.common.utils.CovertUtil;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.ElementTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.utils.Convert;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

@Slf4j
@RobotAction(name = "页面元素截图", targetType = ElementTarget.class)
public class WebScreenshot extends AbstractAction {

    @Setter
    @ActionArgs(value = "文件地址", required = true, comment = "截图后图片保存地址")
    private String filePath;

    @Override
    public void run() {
        WebElement element = getTarget();
        File file = element.getScreenshotAs(OutputType.FILE);
        BufferedImage bufferedImage = ImgUtil.read(file);
        File saveFile = new File(filePath);
        byte[] bytes = Convert.getBytes(bufferedImage);
        //保存截图
        FileUtil.writeBytes(bytes, saveFile);
    }
}
