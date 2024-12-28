package com.seebon.rpa.actions.impl.word;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.DocTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.WordWriteType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;

import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2023-03-21 15:46:23
 */
@RobotAction(name = "更新Word", targetType = DocTarget.class, comment = "更新Word内容")
public class WriteWord extends AbstractAction {

    @ActionArgs(value = "更新类型", required = true, dict = WordWriteType.class)
    private String writeType;

    @ActionArgs(value = "替换信息", required = true)
    private Map<String, String> replaceInfo;

    @Override
    public void run() {
        XWPFDocument doc = getTarget();
        DocTarget docTarget = (DocTarget) this.getVariable("DocTarget");
        docTarget.setWrite(true);
        WordWriteType type = WordWriteType.valueOf(writeType);
        switch (type) {
            case replace: {
                List<XWPFParagraph> paragraphs = doc.getParagraphs();
                for (XWPFParagraph paragraph : paragraphs) {
                    List<XWPFRun> runs = paragraph.getRuns();
                    for (XWPFRun run : runs) {
                        // 替换文本内容
                        for (Map.Entry<String, String> entry : replaceInfo.entrySet()) {
                            if (entry.getKey() == null || entry.getValue() == null || !run.text().contains(entry.getKey())) {
                                continue;
                            }
                            String text = run.text();
                            text = text.replaceAll(entry.getKey(), entry.getValue());
                            run.setText(text, 0);
                        }
                    }
                }
                List<XWPFTable> tables = doc.getTables();
                if (CollectionUtils.isNotEmpty(tables)) {
                    for (XWPFTable table : tables) {
                        String text = table.getText();
                        if (StringUtils.isBlank(text)) {
                            continue;
                        }
                        changeTableMessage(replaceInfo, table, false, null);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    private void changeTableMessage(Map<String, String> params, XWPFTable table, boolean isBold, Integer fontSize) {
        int count = table.getNumberOfRows();//获取table的行数
        for (int i = 0; i < count; i++) {
            XWPFTableRow row = table.getRow(i);
            List<XWPFTableCell> cells = row.getTableCells();
            for (XWPFTableCell cell : cells) {//遍历每行的值并进行替换
                for (Map.Entry<String, String> e : params.entrySet()) {
                    if (cell.getText().contains(e.getKey()) && e.getValue() != null) {
                        String text = cell.getText();
                        text = text.replaceAll(e.getKey(), e.getValue());
                        XWPFParagraph newPara = new XWPFParagraph(cell.getCTTc().addNewP(), cell);
                        XWPFRun r1 = newPara.createRun();
                        r1.setBold(isBold);
                        if (fontSize != null) {
                            r1.setFontSize(fontSize);
                        }
                        r1.setText(text);
                        cell.removeParagraph(0);
                        cell.setParagraph(newPara);
                    }
                }
            }
        }
    }
}
