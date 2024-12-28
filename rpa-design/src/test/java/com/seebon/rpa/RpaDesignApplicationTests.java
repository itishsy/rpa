package com.seebon.rpa;

import com.google.common.collect.Maps;
import com.seebon.common.utils.JsonUtils;
import com.seebon.rpa.remote.RpaSaasService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;

@SpringBootTest
class RpaDesignApplicationTests {
    @Autowired
    RpaSaasService rpaSaasService;

	@Test
	void contextLoads() {
	    try {
//            File file = new File("C:\\Users\\Administrator\\rpaScreen\\截图111.png");
//            File file = new File("C:\\Users\\Administrator\\rpaScreen\\d.txt");
//            FileInputStream fis = new FileInputStream(file);
//            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
//            byte[] b = new byte[1024];
//            int n;
//            while ((n = fis.read(b)) != -1) {
//                bos.write(b, 0, n);
//            }
//            fis.close();
//            byte[] bytes = bos.toByteArray();
//            bos.close();
//
//            Map<String, Object> params = Maps.newHashMap();
//            params.put("bytes", bytes);
//            params.put("fileName", "d.txt");
//            String s = JsonUtils.toJSon(params);

            // 创建一个robot对象
            System.setProperty("java.awt.headless", "false");
            Robot robot = new Robot();
            // 获取屏幕分辨率
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            System.out.println(screenSize);
            // 创建该分辨率的矩形对象
            Rectangle screenRect = new Rectangle(screenSize);
            // 根据这个矩形截图
            BufferedImage bufferedImage = robot.createScreenCapture(screenRect);

            // 保存截图
            String currentUserPath = System.getProperty("user.home");

//            String folderPath = (currentUserPath + File.separator + "rpaScreen" + File.separator + DateUtils.dateToStr(new Date(), DateUtils.NO_FORMAT_DATE) + File.separator);
            String folderPath = (currentUserPath + File.separator + "rpaScreen" + File.separator);
            File file = new File(folderPath + System.currentTimeMillis() + ".png");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String byteString = new String(byteArray, "ISO-8859-1");

            Map<String, Object> params = Maps.newHashMap();
            params.put("fileName", file.getName());
            params.put("byteString", byteString);
            Map<String, Object> resultMap = rpaSaasService.fileUploadForServer(JsonUtils.toJSon(params));
            System.out.println(JsonUtils.toJSon(resultMap));

        } catch (Exception e) {

        }
	}

}
