package com.seebon.rpa.actions.impl.win;

import cn.hutool.core.io.FileUtil;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.OcrDetailType;
import com.seebon.rpa.context.enums.OcrOriginType;
import com.seebon.rpa.entity.robot.vo.OcrReqVO;
import com.seebon.rpa.remote.RpaDesignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Slf4j
@RobotAction(name = "OCR图片识别", targetType = NoneTarget.class)
public class OcrIdentify extends AbstractAction {
    @Autowired
    private RpaDesignService designService;

    @ActionArgs(value = "OCR识别源", dict = OcrOriginType.class ,comment = "默认【dddc_OCR】")
    private OcrOriginType originType;

    @ActionArgs(value = "图片地址", required = true, comment = "被识别图片的地址")
    private String imgPath;

    @ActionArgs(value = "OCR识别程度", dict = OcrDetailType.class, comment = "不选默认仅文本")
    private String ocrDetailType;

    @ActionArgs(value = "结果变量", required = true)
    private String dataKey;

    @Override
    public void run() {
        OcrReqVO reqVO = new OcrReqVO();
        reqVO.setBytes(FileUtil.readBytes(imgPath));
        String text = "";
        if (originType == null || OcrOriginType.SELF_DDDC_OCR.equals(originType)) {
            text = designService.ocrText(reqVO);
        } else if (OcrOriginType.SELF_BAIDU_OCR.equals(originType)){
            text = OcrOriginType.SELF_BAIDU_OCR.toString() + Optional.ofNullable(designService.ocrTextBySelfBaidu(reqVO)).orElse("");
        }
        log.info("OCR图片识别 返回：" + text);
        ctx.setVariable(dataKey, StringUtils.isEmpty(text) ? "" : text);
    }
}
