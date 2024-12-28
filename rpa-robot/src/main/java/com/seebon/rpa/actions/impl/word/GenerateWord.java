package com.seebon.rpa.actions.impl.word;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.DocTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@RobotAction(name = "生成Word", targetType = DocTarget.class, comment = "生成Word内容")
public class GenerateWord extends AbstractAction {

    @ActionArgs(value = "数据源")
    private List<Map<String, Object>> mapList;

    @ActionArgs(value = "插入起始行")
    private Integer insertIndex;

    @ActionArgs(value = "表头列", comment = "如个人账号,姓名,转出单位账号")
    private String headerCols;

    @ActionArgs(value = "输出地址", required = true)
    private String outPath;

    @Override
    public void run() {
        XWPFDocument doc = getTarget();
        DocTarget docTarget = (DocTarget) this.getVariable("DocTarget");
        docTarget.setWrite(true);
        String[] keys = headerCols.split(",");
        List<XWPFTable> tables = doc.getTables();
        for (XWPFTable table : tables) {
            int count = table.getNumberOfRows();
            int needAddCount = mapList.size() - (count - insertIndex);
            if (needAddCount > 0) {
                for (int i = 0; i < needAddCount; i++) {
                    table.addRow(table.getRow(table.getRows().size() - 1));
                }
            }
            int dataIndex = 0;
            for (int i = insertIndex; i < count && dataIndex < mapList.size(); i++) {
                XWPFTableRow row = table.getRow(i);
                Map<String, Object> dataMap = mapList.get(dataIndex);
                List<XWPFTableCell> cells = row.getTableCells();
                for (int j = 0; j < cells.size(); j++) {
                    XWPFTableCell cell = cells.get(j);
                    cell.removeParagraph(0);
                    cell.addParagraph();
                    cell.setText(String.valueOf(dataMap.get(keys[j])));
                }
                dataIndex++;
            }
            OutputStream out = null;
            try {
                out = new FileOutputStream(outPath);
                doc.write(out);
            } catch (IOException e) {
                log.error("生成Word失败", e);
            } finally {
                IOUtils.closeQuietly(out);
            }
        }
    }
}
