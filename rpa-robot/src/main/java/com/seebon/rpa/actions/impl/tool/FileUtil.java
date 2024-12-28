package com.seebon.rpa.actions.impl.tool;

import cn.hutool.core.util.ZipUtil;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.excel.utils.DocUtils;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.FileTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.Conditions;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.FileMethod;
import com.seebon.rpa.context.runtime.RobotConfigException;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.file.ImageUtil;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.documents.ImageType;
import com.spire.doc.documents.TextWrappingStyle;
import com.spire.doc.fields.DocPicture;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.graphics.PdfImageType;
import com.spire.xls.ExcelPicture;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jumpmind.symmetric.csv.CsvReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Slf4j
@RobotAction(name = "文件操作",
        targetType = FileTarget.class,
        comment = "文件操作")
public class FileUtil extends AbstractAction {

    //CSV常用分隔符，如需动态扩展设置成配置项
    private static final char[] DELIMITERS = {
            ',',
            ';',
            '\001',
            ' ',
            '\t',
            '|',
            '#',
            '&'
    };

    @Conditions({
            "copy:copyTo",
            "get:varKey",
            "find:keyword,varKey",
            "rename:newName",
            "createExcel:rows",
            "csv2Excel:newName,charsetName",
            "excel2Img:newName",
            "pdf2Img:newName",
            "word2Img:newName,updates",
            "wordToImg:newName",
            "excelInsertPicture:sheetIndex,picturePath,pictureInfo",
            "WordInsertPicture:picturePath,pictureInfo",
            "unzip:upzipFileName",
            "zip:zipPackagePath,zipFileName",
            "downloadNewFile:varKey",
            "base642File:base64"
    })

    @Setter
    @ActionArgs(value = "操作类型", required = true, dict = FileMethod.class)
    private String actionType;

    @ActionArgs(value = "复制到")
    private String copyTo;

    @ActionArgs(value = "初始化行")
    private String rows;

    @Setter
    @ActionArgs(value = "新文件名")
    private String newName;

    @ActionArgs(value = "修改信息", required = true, comment = "Json")
    private Map<String, Object> updates;

    @ActionArgs(value = "文件名关键字")
    private String keyword;

    @Setter
    @ActionArgs(value = "原始文件编码", comment = "不填默认utf-8")
    private String charsetName;

    @ActionArgs(value = "结果变量")
    @Setter
    private String varKey;

    @ActionArgs(value = "表格索引", comment = "表格索引，默认0")
    private Integer sheetIndex;

    @ActionArgs(value = "图片地址", comment = "插入图片地址", required = true)
    private String picturePath;

    @ActionArgs(value = "图片信息", comment = "插入图片在文件中的位置和大小", required = true)
    private Map<String, Integer> pictureInfo;

    @ActionArgs(value = "减压后的文件名称", comment = "减压后的文件名称", required = true)
    private String upzipFileName;

    @ActionArgs(value = "压缩文件夹路径", comment = "压缩文件夹路径", required = true)
    private String zipPackagePath;

    @ActionArgs(value = "压缩后的文件名称", comment = "压缩后的文件名称", required = true)
    private String zipFileName;

    @ActionArgs(value = "base64", comment = "base64文本", required = true)
    private String base64;

    @Override
    public void run() {
        FileOutputStream out = null;
        File src = getTarget();
        try {
            switch (FileMethod.valueOf(actionType)) {
                case copy: {
                    if (!src.exists()) {
                        throw new RobotConfigException("File not exist: " + src);
                    }
                    Path dest = (Path) Paths.get(copyTo);
                    Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                    break;
                }
                case get: {
                    if (src != null && src.exists()) {
                        ctx.setVariable(varKey, src);
                    } else {
                        ctx.setVariable(varKey, null);
                    }
                    break;
                }
                case find: {
                    File[] files = find(src, keyword);
                    if (files != null && files.length > 0) {
                        ctx.setVariable(varKey, files);
                    } else {
                        ctx.setVariable(varKey, null);
                    }
                    break;
                }
                case createPath: {
                    if (!src.exists()) {
                        src.mkdirs();
                    }
                    break;
                }
                case createFile: {
                    if (!src.exists()) {
                        src.createNewFile();
                    }
                    break;
                }
                case base642File: {
                    if (!src.exists()) {
                        src.createNewFile();
                    }
                    byte[] decode = Base64.getDecoder().decode(base64);
                    try(FileOutputStream fos = new FileOutputStream(src)) {
                        fos.write(decode);
                        fos.flush();
                    }
                }
                case createExcel: {
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet sheet = workbook.createSheet();
                    String[] split = rows.split(",");
                    for (String s : split) {
                        sheet.createRow(Integer.parseInt(s));
                    }
                    out = new FileOutputStream(src.getAbsolutePath());
                    workbook.write(out);
                    break;
                }
                case rename: {
                    System.gc();
                    cn.hutool.core.io.FileUtil.rename(src, newName, true);
                    break;
                }
                case delete: {
                    System.gc();
                    if (src.exists()) {
                        boolean del = cn.hutool.core.io.FileUtil.del(src);
                        if (!del) {
                            src.deleteOnExit();
                        }
                    }
                    break;
                }
                case csv2Excel: {
                    this.csvToExcel(src);
                    break;
                }
                case word2Img: {
                    try (ByteArrayOutputStream outFile = new ByteArrayOutputStream();) {
                        InputStream input = new FileInputStream(src.getPath());
                        DocUtils.replaceDocToStream(input, updates, outFile);
                        byte[] bytes = outFile.toByteArray();

                        //创建Document对象
                        Document doc = new Document();
                        doc.loadFromStream(new ByteArrayInputStream(bytes), FileFormat.Doc);
                        BufferedImage image = doc.saveToImages(0, ImageType.Bitmap);

                        //将图片数据保存为PNG格式文档
                        File file = new File(newName);
                        ImageIO.write(image, "PNG", file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case wordToImg: {
                    //创建Document实例
                    Document doc = new Document();
                    //加载Word文档
                    doc.loadFromFile(src.getPath());
                    //转换到图片并设置图片的分辨率
                    BufferedImage image = doc.saveToImages(0, ImageType.Bitmap);
                    File file = new File(newName);
                    ImageIO.write(image, "PNG", file);
                    break;
                }
                case excel2Img: {
                    Workbook wb = null;
                    Worksheet sheet = null;
                    try {
                        wb = new Workbook();
                    } catch (Exception e) {

                    }
                    try {
                        wb.loadFromFile(src.getPath());
                    } catch (Exception e) {

                    }
                    //获取工作表
                    sheet = wb.getWorksheets().get(0);
                    try {
                        //获取工作表
                        sheet.saveToImage(newName);
                    } catch (Exception e) {
                    }
                    break;
                }
                case pdf2Img: {
                    //实例化PdfDocument类的对象
                    PdfDocument pdf = new PdfDocument();

                    //加载PDF文档
                    pdf.loadFromFile(src.getPath());

                    BufferedImage[] bufferedImages = new BufferedImage[pdf.getPages().getCount()];
                    //遍历PDF每一页，保存为图片
                    for (int i = 0; i < pdf.getPages().getCount(); i++) {
                        //将页面保存为图片，并设置DPI分辨率
                        BufferedImage image = pdf.saveAsImage(i, PdfImageType.Bitmap, 300, 300);
                        bufferedImages[i] = image;
                        //将图片保存为png格式
                        //File file = new File( String.format(("ToImage-img-%d.png"), i));
                        //ImageIO.write(image, "PNG", file);
                    }
                    BufferedImage mergeImage = null;
                    if (pdf.getPages().getCount() > 1) {
                        mergeImage = ImageUtil.mergeImage(bufferedImages);
                    } else {
                        mergeImage = bufferedImages[0];
                    }
                    try {
                        File file = new File(newName);
                        ImageIO.write(mergeImage, newName.substring(newName.lastIndexOf(".") + 1, newName.length()).toUpperCase(), file);
                    } catch (Exception e) {
                    }
                    pdf.close();
                    break;
                }
                case excelInsertPicture: {
                    Workbook workbook = new Workbook();
                    //加载Excel文档
                    workbook.loadFromFile(src.getPath());
                    //获取第一张工作表
                    Worksheet sheet = workbook.getWorksheets().get(sheetIndex == null ? 0 : sheetIndex);
                    //添加图片到工作表的指定位置
                    ExcelPicture pic = sheet.getPictures().add(pictureInfo.get("topRow"), pictureInfo.get("leftColumn"), picturePath);
                    //设置图片的宽度和高度
                    pic.setWidth(pictureInfo.get("width"));
                    pic.setHeight(pictureInfo.get("height"));
                    //保存文档
                    workbook.saveToFile(src.getPath());
                    break;
                }
                case WordInsertPicture: {
                    //创建 Document 类的对象
                    Document doc = new Document();
                    DocPicture picture = null;
                    try {
                        //从磁盘载入 Word 文件
                        doc.loadFromFile(src.getPath());
                        //创建 DocPicture 类的对象
                        picture = new DocPicture(doc);
                        //从磁盘加载图片
                        picture.loadImage(picturePath);
                        //设置图片大小
                        picture.setWidth(pictureInfo.get("width"));
                        picture.setHeight(pictureInfo.get("height"));
                        picture.setHorizontalPosition(pictureInfo.get("horizontalPosition"));
                        //将图片文本环绕方式设置为四周环绕
                        picture.setTextWrappingStyle(TextWrappingStyle.In_Front_Of_Text);
                        //将图片插入到第二段
                        doc.getSections().get(pictureInfo.get("sectionIndex")).getParagraphs().get(pictureInfo.get("paragraphIndex")).getChildObjects().insert(pictureInfo.get("insertIndex"), picture);
                        //保存文档
                        doc.saveToFile(src.getPath());
                    } finally {
                        System.gc();
                        doc.close();
                    }
                    break;
                }
                case unzip: {
                    String zipFilePath = src.getPath();
                    File resourceFile = new File(zipFilePath);
                    if (!resourceFile.exists()) {
                        System.out.println("源目标路径：[" + src.getPath() + "] 不存在...");
                        break;
                    }
                    if (resourceFile.getName().endsWith("zip")) {
                        // 开始解压
                        ZipFile zipFile = null;
                        try {
                            zipFile = new ZipFile(resourceFile, Charset.forName("GBK"));
                            Enumeration<?> entries = zipFile.entries();
                            int index = 0;
                            while (entries.hasMoreElements()) {
                                ZipEntry entry = (ZipEntry) entries.nextElement();
                                // 如果是文件夹，就创建个文件夹
                                if (!entry.isDirectory()) {
                                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                                    File targetFile = new File(upzipFileName + "_" + index + entry.getName().substring(entry.getName().lastIndexOf("."), entry.getName().length()));
                                    // 保证这个文件的父文件夹必须要存在
                                    if (!targetFile.getParentFile().exists()) {
                                        targetFile.getParentFile().mkdirs();
                                    }
                                    targetFile.createNewFile();
                                    // 将压缩文件内容写入到这个文件中
                                    InputStream is = zipFile.getInputStream(entry);
                                    FileOutputStream fos = new FileOutputStream(targetFile);
                                    int len;
                                    int BUFFER_SIZE = 1024;
                                    byte[] buf = new byte[BUFFER_SIZE];
                                    while ((len = is.read(buf)) != -1) {
                                        fos.write(buf, 0, len);
                                    }
                                    // 关流顺序，先打开的后关闭
                                    fos.close();
                                    is.close();
                                    index++;
                                }
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("unzip error from ZipUtils", e);
                        } finally {
                            if (zipFile != null) {
                                try {
                                    zipFile.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    break;
                }
                case zip: {
                    if (zipPackagePath.contains(",")) {
                        String dataPath = ctx.get(RobotConstant.DATA_PATH);
                        String fileDir = Convert.appendPath(dataPath, UUIDGenerator.uuidStringWithoutLine());
                        cn.hutool.core.io.FileUtil.mkdir(fileDir);
                        String[] zipPackagePaths = zipPackagePath.split(",");
                        for (int i = 0; i < zipPackagePaths.length; i++) {
                            File srcFile = new File(zipPackagePaths[i]);
                            File destFile = new File(fileDir);
                            cn.hutool.core.io.FileUtil.copyFile(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING);
                        }
                        ZipUtil.zip(fileDir, zipFileName);
                        cn.hutool.core.io.FileUtil.del(fileDir);
                    } else {
                        ZipUtil.zip(zipPackagePath, zipFileName);
                    }
                    break;
                }
                case open: {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(src);
                    }
                    break;
                }
                case downloadNewFile: {
                    String downloadPath = ctx.getVariable(RobotConstant.DOWNLOAD_DEFAULT_PATH);
                    if (StringUtils.isBlank(downloadPath)) {
                        throw new RuntimeException("浏览器不支持自动下载,请先配置文件路径.");
                    }
                    //缓存文件列表
                    List<String> fileNames = ctx.get(RobotConstant.CACHE_DOWNLOAD_FILE_NAME);
                    //新的文件路径
                    String newFilePath = Convert.getNewPath(downloadPath, fileNames);
                    if (newFilePath.contains(".crdownload")) {//下载中的文件名
                        Convert.sleep(5);//休眠5秒
                        newFilePath = Convert.getNewPath(downloadPath, fileNames);
                    }
                    ctx.setVariable(varKey, newFilePath);
                    //用完后，清空文件名称
                    ctx.setVariable(RobotConstant.CACHE_DOWNLOAD_FILE_NAME, Lists.newArrayList());
                    break;
                }
                default:
                    break;
            }
        } catch (IOException e) {
            log.error("【Exception】", e);
            throw new RobotConfigException("文件操作异常");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    public void csvToExcel(File src){
        FileOutputStream fileOutputStream = null;
        String charsetName1 = "UTF-8";
        if (StringUtils.isNotBlank(charsetName)) {
            charsetName1 = charsetName;
        }
        try {
            char delimiter = getDelimiter(src.getPath(), charsetName1);
            String version = newName.substring(newName.lastIndexOf("."), newName.length());
            if ("xlsx".equals(version) || ".xlsx".equals(version)) {
                toXSSF(src.getPath(), newName, delimiter, fileOutputStream, charsetName1);
            } else if ("xls".equals(version) || ".xls".equals(version)) {
                toHSSF(src.getPath(), newName, delimiter, fileOutputStream, charsetName1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String csvToExcel(String srcFilePath) {
        if (!StringUtils.endsWithIgnoreCase(srcFilePath, ".csv")) {
            return srcFilePath;
        }
        String batchCode = UUIDGenerator.uuidStringWithoutLine();
        String newFilePath = StringUtils.replaceIgnoreCase(srcFilePath, ".csv", "_" + batchCode + ".xlsx");
        File srcFile = new File(srcFilePath);
        this.setActionType(FileMethod.csv2Excel.toString());
        this.setNewName(newFilePath);
        this.csvToExcel(srcFile);
        return newFilePath;
    }

    public File[] find(File dir, String keyword) {
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains(keyword);
            }
        });
        return files;
    }

    private static void toXSSF(String csvFilePath, String newName, char delimiter, FileOutputStream fileOutputStream, String charsetName) throws Exception {
        XSSFWorkbook workBook = new XSSFWorkbook();
        XSSFSheet sheet = workBook.createSheet("sheet1");
        int RowNum = -1;
        CsvReader csvReader = new CsvReader(csvFilePath, delimiter, Charset.forName(charsetName));
        while (csvReader.readRecord()) {
            RowNum++;
            XSSFRow currentRow = sheet.createRow(RowNum);
            for (int i = 0; i < csvReader.getColumnCount(); i++) {
                String s = csvReader.get(i);
                s = handleText(s);
                currentRow.createCell(i).setCellValue(s);
            }
        }
        fileOutputStream = new FileOutputStream(newName);
        workBook.write(fileOutputStream);
    }

    private static void toHSSF(String csvFilePath, String newName, char delimiter, FileOutputStream fileOutputStream, String charsetName) throws Exception {
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet sheet = workBook.createSheet("sheet1");
        int RowNum = -1;
        CsvReader csvReader = new CsvReader(csvFilePath, delimiter, Charset.forName(charsetName));
        while (csvReader.readRecord()) {
            RowNum++;
            HSSFRow currentRow = sheet.createRow(RowNum);
            for (int i = 0; i < csvReader.getColumnCount(); i++) {
                String s = csvReader.get(i);
                s = handleText(s);
                currentRow.createCell(i).setCellValue(s);
            }
        }
        fileOutputStream = new FileOutputStream(newName);
        workBook.write(fileOutputStream);
    }

    /**
     * 字符串转输入流
     * 把CSV文件第一行数据转成输入流
     *
     * @param sInputString
     * @return
     */
    private static InputStream getStringStream(String sInputString) {
        if (sInputString != null && !"".equals(sInputString)) {
            try {
                ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
                return tInputStringStream;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 常用CSV分隔符数组遍历资源第一行，分隔的字段数多的为资源分隔符
     * 异常情况下默认用’,‘作为分隔符
     *
     * @param path 资源路径
     * @return
     */
    private static char getDelimiter(String path, String charsetName) {
        BufferedReader br = null;
        char delimiter = ',';
        try {
            br = new BufferedReader(new FileReader(path));
            String line = br.readLine();
            CsvReader csvReader;
            int columCount = 0;
            for (char delimiterTest : DELIMITERS) {
                csvReader = new CsvReader(getStringStream(line), delimiterTest, Charset.defaultCharset().forName(charsetName));
                if (csvReader.readRecord()) {
                    int newColumnCount = csvReader.getColumnCount();
                    if (newColumnCount > columCount) {
                        columCount = newColumnCount;
                        delimiter = delimiterTest;
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                br.close();
            } catch (IOException e) {
            }
        }
        return delimiter;
    }

    private static String handleText(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        text = text.replace("\uFEFF", "");
        text = text.replace("\t", "");
        if (text.startsWith("=")) {
            text = text.substring(1, text.length());
        }
        if (text.startsWith("\"")) {
            text = text.substring(1, text.length());
        }
        if (text.endsWith("\"")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }
}
