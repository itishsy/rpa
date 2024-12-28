package com.seebon.rpa.actions.impl.excel;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.TargetsTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.utils.file.ImageUtil;
import com.spire.xls.ExcelPicture;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@RobotAction(name = "生成长春公积金文件", targetType = TargetsTarget.class, comment = "生成长春公积金文件")
public class GenerateZhongShan extends AbstractAction {

    @ActionArgs(value = "证明文件路径", required = true)
    private String filePath;

    @ActionArgs(value = "公章路径")
    private String sealPath;

    @Override
    public void run() {
        List<Map<String, Object>> declareList = ctx.get(RobotConstant.DECLARE_LIST);
        String declarePath = ctx.get(RobotConstant.FILE_PATH);
        try {
            InputStream input = new FileInputStream(declarePath);
            HSSFWorkbook workbook = new HSSFWorkbook(input);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] bytes = out.toByteArray();
            Workbook wb = new Workbook();
            wb.loadFromStream(new ByteArrayInputStream(bytes));

            //获取工作表
            Worksheet sheet = wb.getWorksheets().get(0);
            int topRow = declareList.size() - 1;
            if (StringUtils.isNotBlank(sealPath)) {
                //添加图片到工作表的指定位置
                ExcelPicture pic = sheet.getPictures().add(topRow == 0 ? 1 : topRow, 5, sealPath);
                //设置图片的宽度和高度
                pic.setWidth(160);
                pic.setHeight(160);
            }
            wb.getConverterSetting().setXDpi(500);
            wb.getConverterSetting().setYDpi(500);
            ImageUtil.saveToImage(sheet, filePath);
        } catch (Exception e) {
            log.error("生成长春公积金文件失败", e);
        }
    }
}
