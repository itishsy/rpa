package com.seebon.rpa.utils.file;

import com.seebon.common.utils.UUIDGenerator;
import com.spire.xls.Worksheet;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author ZhenShijun
 * @dateTime 2021-09-24 15:43:47
 */
public class ImageUtil {


    public static BufferedImage[] getBufferedImages(Worksheet sheet, String workPath) throws IOException {
        int totalRow = sheet.getLastRow();

        int pageRow = 100;
        int s = totalRow % pageRow;
        int page = totalRow / pageRow + (s > 0 ? 1 : 0);
        BufferedImage[] bufferedImages = new BufferedImage[page];

        String uuid = UUIDGenerator.uuidStringWithoutLine();

        for (int i = 1; i <= page; i++) {
            int start = sheet.getFirstRow() + (i - 1) * pageRow;
            int end = i * pageRow > totalRow ? totalRow : i * pageRow;
            String filePath = workPath + uuid + i + ".png";
            sheet.saveToImage(filePath, start, sheet.getFirstColumn(), end, sheet.getLastColumn());
            FileInputStream fileInputStream = new FileInputStream(filePath);
            BufferedImage image = ImageIO.read(fileInputStream);
            bufferedImages[i - 1] = image;
            fileInputStream.close();
            File f = new File(filePath);
            if (f.exists() && f.isFile()) {
                f.delete();
            }
        }

        return bufferedImages;
    }

    /**
     * 合并图片
     *
     * @param imgs
     * @return
     * @throws IOException
     */
    public static BufferedImage mergeImage(BufferedImage... imgs) throws IOException {
        // 生成新图片
        BufferedImage destImage = null;
        // 计算新图片的长和高
        int allh = 0;
        int allwMax = 0;
        int allhMax = 0;
        // 获取总长、总宽、最长、最宽
        for (int i = 0; i < imgs.length; i++) {
            BufferedImage img = imgs[i];
            allh += img.getHeight();
            if (img.getWidth() > allwMax) {
                allwMax = img.getWidth();
            }
            if (img.getHeight() > allhMax) {
                allhMax = img.getHeight();
            }
        }

        //创建图片
        destImage = new BufferedImage(allwMax, allh, BufferedImage.TYPE_INT_RGB);
        Graphics2D dg = destImage.createGraphics();
        //设置背景色
        dg.setColor(Color.GRAY);
        //填充整个屏幕
        dg.fillRect(0, 0, allwMax, allh);
        dg.dispose();

        // 合并所有子图片到新图片
        int wy = 0;
        for (int i = 0; i < imgs.length; i++) {
            BufferedImage img = imgs[i];
            int w1 = img.getWidth();
            int h1 = img.getHeight();
            // 从图片中读取RGB
            int[] ImageArrayOne = new int[w1 * h1];
            // 逐行扫描图像中各个像素的RGB到数组中
            ImageArrayOne = img.getRGB(0, 0, w1, h1, ImageArrayOne, 0, w1);
            // 设置上半部分或左半部分的RGB
            destImage.setRGB(0, wy, w1, h1, ImageArrayOne, 0, w1);
            wy += h1;
        }
        return destImage;
    }

    public static void saveToImage(Worksheet sheet, String filePath) {
        int totalRow = sheet.getLastRow();
        int pageRow = 100;
        int s = totalRow % pageRow;
        int page = totalRow / pageRow + (s > 0 ? 1 : 0);
        for (int i = 1; i <= page; i++) {
            int start = sheet.getFirstRow() + (i - 1) * pageRow;
            int end = i * pageRow > totalRow ? totalRow : i * pageRow;
            sheet.saveToImage(filePath, start, sheet.getFirstColumn(), end, sheet.getLastColumn());
        }
    }
}
