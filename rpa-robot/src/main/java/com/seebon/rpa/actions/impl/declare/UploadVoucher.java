package com.seebon.rpa.actions.impl.declare;

import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.constant.DynamicFieldType;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.entity.robot.RobotExecutionVoucher;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.service.RobotCommonService;
import com.seebon.rpa.utils.Convert;
import com.seebon.rpa.utils.FileStorage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

@Slf4j
@ActionUtils
@RobotAction(name = "上传凭证", targetType = NoneTarget.class, comment = "上传凭证")
public class UploadVoucher extends AbstractAction {

    @Setter
    @ActionArgs(value = "文件地址", style = DynamicFieldType.text, required = true)
    private String filePath;

    @Autowired
    private RobotCommonService robotCommonService;
    @Autowired
    private RpaDesignService rpaDesignService;

    @Override
    public void run() {
        String fileId = robotCommonService.fileUpload(new File(filePath));
        RobotExecutionVoucher executionVoucher = new RobotExecutionVoucher();
        String machineCode = null;
        try {machineCode = FileStorage.loadMachineCodeFromDisk();} catch (Exception ignored) {}
        executionVoucher.setMachineCode(machineCode);
        executionVoucher.setExecutionCode(ctx.get(RobotConstant.EXECUTION_CODE_KEY));
        executionVoucher.setAddrName(ctx.get("addrName"));
        executionVoucher.setBusinessType(Convert.getBusinessType(ctx.get("businessType")));
        executionVoucher.setDeclareType(ctx.get("declareType"));
        executionVoucher.setAccountNumber(ctx.getAccountNumber());
        executionVoucher.setDeclareMonth(new DateTime().toString("yyyy-MM"));
        executionVoucher.setTplTypeCode(ctx.get("tplTypeCode"));
        executionVoucher.setFileId(Integer.parseInt(fileId));
        log.info("上传凭证-请求参数：{}",executionVoucher.toString());
        rpaDesignService.saveExecutionVoucher(executionVoucher);
    }
}
