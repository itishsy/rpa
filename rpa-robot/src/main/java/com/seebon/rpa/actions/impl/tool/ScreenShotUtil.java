package com.seebon.rpa.actions.impl.tool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.FileTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.entity.robot.RobotExecutionFile;
import com.seebon.rpa.repository.mapper.RobotExecutionFileMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.Map;

/**
 * @Author: tanyong
 * @Description: 截屏
 * @Date: 2023/1/5 10:37
 * @Modified By:
 */
@Slf4j
@ActionUtils
@RobotAction(name = "截屏操作", targetType = FileTarget.class, comment = "截屏操作")
public class ScreenShotUtil extends AbstractAction {
    @Autowired
    private RobotExecutionFileMapper executionFileMapper;

    @Override
    public void run() {
        this.screenShot(flowCode, stepCode);
    }

    public void screenShot(String flowCode, String stepCode) {
        String executionCode = ctx.getVariable(RobotConstant.EXECUTION_CODE_KEY);
        if (StringUtils.isBlank(executionCode)) {
            log.info("执行code为空，跳过截图步骤");
            return;
        }
        try {
            //生成图片
            File file = this.getTarget();
            if (file == null) {
                file = new File(this.getFilePath());
            }
            this.screenShot(file);
            //保存记录
            this.saveExecutionFile(file, flowCode, stepCode);
        } catch (Exception e) {
            log.error("截屏失败--{}" + e.getMessage(), e);
        }
    }

    private void saveExecutionFile(File file, String flowCode, String stepCode) {
        if (StringUtils.isBlank(flowCode) || StringUtils.isBlank(stepCode)) {
            return;
        }
        RobotExecutionFile executionFile = new RobotExecutionFile();
        executionFile.setExecutionCode(ctx.getVariable(RobotConstant.EXECUTION_CODE_KEY));
        executionFile.setTaskCode(ctx.getVariable("taskCode"));
        executionFile.setFlowCode(flowCode);
        executionFile.setStepCode(stepCode);
        executionFile.setFileName(file.getName());
        executionFile.setFilePath(file.getPath());
        executionFile.setStatus(0);
        executionFile.setCreateTime(new Date());
        executionFile.setUpdateTime(new Date());
        executionFileMapper.insertSelective(executionFile);
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

    private String getFilePath() {
        String dataPath = ctx.getVariable("dataPath");
        if (StringUtils.isBlank(dataPath)) {
            dataPath = RobotContext.workPath + "\\cache\\images\\";
        } else {
            dataPath = dataPath + "\\images\\";
        }
        //如果文件夹不存在，则创建
        FileUtil.mkdir(dataPath);
        //文件名
        String fileName = UUIDGenerator.uuidStringWithoutLine() + ".png";

        return StrUtil.appendIfMissing(dataPath, "\\", "\\") + fileName;
    }

    public Map<String, Integer> getDimensionScreenSize() {
        // 获取屏幕分辨率
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Map<String, Integer> result = Maps.newHashMap();
        result.put("height", (int) screenSize.getHeight());
        result.put("width", (int) screenSize.getWidth());
        log.info("屏幕分辨率-height=", screenSize.getHeight() + ",Width" + screenSize.getWidth());
        return result;
    }

    public File screenShot(File file) {
        try {
            // 获取屏幕分辨率
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            log.info("屏幕分辨率--{}", screenSize);
            // 创建该分辨率的矩形对象
            Rectangle screenRect = new Rectangle(screenSize);
            // 根据这个矩形截图
            Robot robot = new Robot();
            //截图
            BufferedImage bufferedImage = robot.createScreenCapture(screenRect);
            //图片字节
            byte[] bytes = this.getBytes(bufferedImage);
            //生成图片
            if (file == null) {
                file = new File(this.getFilePath());
            }
            log.info("截屏文件路径--{}", file.getPath());
            //保存截图
            FileUtil.writeBytes(bytes, file);

            return file;
        } catch (Exception e) {
            log.error("截屏失败--{}" + e.getMessage(), e);
        }
        return null;
    }
}
