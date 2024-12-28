package com.seebon.rpa.actions.impl.declare;

import com.alibaba.fastjson.JSON;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.entity.robot.RobotUsbKey;
import com.seebon.rpa.entity.robot.device.response.UsbKeyResponse;
import com.seebon.rpa.entity.robot.enums.UsbKeyStatusEnum;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.utils.UsbIpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@ActionUtils
@RobotAction(name = "挂载UsbKey", targetType = NoneTarget.class, comment = "挂载UsbKey")
public class AttachUsbKey extends AbstractAction {
    @Autowired
    private RpaDesignService rpaDesignService;

    @Override
    public void run() {
        String loginType = ctx.getLoginType();
        if (StringUtils.isBlank(loginType) || !"CA证书登录".equals(loginType)) {
            log.info("登录方式不是CA证书登录，无需激活,loginType=" + loginType);
            return;
        }
        RobotUsbKey usbKey = new RobotUsbKey();
        usbKey.setClientId(ctx.get("clientId"));
        usbKey.setAppCode(ctx.get("appCode"));
        usbKey.setTaskCode(ctx.get("taskCode"));
        usbKey.setDeclareSystem(ctx.get("tplTypeCode"));
        UsbKeyResponse usbKeyResponse = rpaDesignService.getUsbKey(usbKey);
        if (usbKeyResponse == null) {
            throw new RuntimeException("挂载UsbKey失败");
        }
        if (usbKeyResponse.getStatus() != 2) {
            throw new RuntimeException("挂载UsbKey失败,没有可用的UsbKey,UsbKey状态为：" + UsbKeyStatusEnum.getNameByCode(usbKeyResponse.getStatus()));
        }
        ctx.setVariable(RobotConstant.USB_KEY_IP_ADDRESS, usbKeyResponse.getHostIpAddress());
        ctx.setVariable(RobotConstant.USB_KEY_BUS_ID, usbKeyResponse.getBusId());
        log.info("开始挂载usbKey usbKeyResponse=" + JSON.toJSONString(usbKeyResponse));
        String result = UsbIpUtil.attach(usbKeyResponse.getHostIpAddress(), usbKeyResponse.getBusId(), this.getTimeout());
        if (StringUtils.isNotBlank(result) && result.contains("successfully")) {
            log.info("挂载usbKey成功.");
            RobotConstant.USB_KEY_FLAG = true;
            return;
        }
        throw new BusinessException("挂载usbKey失败.");
    }
}
