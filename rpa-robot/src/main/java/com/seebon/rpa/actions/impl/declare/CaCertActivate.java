package com.seebon.rpa.actions.impl.declare;

import com.alibaba.fastjson.JSON;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.impl.flow.Wait;
import com.seebon.rpa.actions.impl.tool.UsbCtrl;
import com.seebon.rpa.actions.impl.win.App;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.enums.AppAction;
import com.seebon.rpa.context.enums.UsbType;
import com.seebon.rpa.entity.robot.RobotAppCa;
import com.seebon.rpa.entity.robot.RobotUsbKey;
import com.seebon.rpa.entity.robot.device.response.UsbKeyResponse;
import com.seebon.rpa.entity.robot.enums.UsbKeyStatusEnum;
import com.seebon.rpa.remote.RpaDesignService;
import com.seebon.rpa.service.RobotAppCaService;
import com.seebon.rpa.utils.UsbIpUtil;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@ActionUtils
@RobotAction(name = "CA证书激活", targetType = NoneTarget.class, comment = "CA证书激活")
public class CaCertActivate extends AbstractAction {
    @Autowired
    private UsbCtrl usbCtrl;
    @Autowired
    private App app;
    @Autowired
    private Wait wait;
    @Autowired
    private RobotAppCaService appCaService;
    @Autowired(required = false)
    private HikariDataSource dataSource;
    @Autowired
    private RpaDesignService rpaDesignService;

    @Value("${rpa.use-hub:0}")
    private String useHub;

    @Override
    public void run() {
        if (dataSource != null && dataSource.getJdbcUrl().contains("jdbc:mysql")) {
            log.info("已配置本地开发模式,取消CA证书操作.");
            return;
        }
        String loginType = ctx.getLoginType();
        if (StringUtils.isBlank(loginType) || !"CA证书登录".equals(loginType)) {
            log.info("登录方式为空或非CA证书登录，无需激活,loginType=" + loginType);
            return;
        }
        log.info("检测是否插入usb扩展设备.useHub=" + useHub);
        if (StringUtils.isNotBlank(useHub) && "2".equals(useHub)) {
            this.attachUsbKey();
        } else {
            String appCode = ctx.getVariable("appCode");
            String declareSystem = ctx.get("tplTypeCode");
            String accountNumber = ctx.getAccountNumber();

            RobotAppCa appCa = appCaService.getRobotAppCa(appCode, declareSystem, accountNumber);
            if (StringUtils.isBlank(useHub) || "0".equals(useHub)) {
                if (appCa == null) {
                    log.info("非集线器，未找到CA证书自动安装卸载配置，需要先关闭所有证书.");
                    app.setFlowCode(flowCode);
                    app.setActionType(AppAction.close.name());
                    app.run();
                } else {
                    log.info("非集线器，已找到CA证书自动安装卸载配置，不需要再关闭所有证书.");
                }
            }

            //激活
            usbCtrl.setActionType(UsbType.active.name());
            usbCtrl.run();

            //等待
            wait.setCondition("${USBStatus==0}");
            wait.setTimeout(60);
            wait.run();

            Integer USBStatus = ctx.get("USBStatus");
            if (USBStatus != 0) {
                throw new RuntimeException("激活USB失败");
            }

            if (StringUtils.isBlank(useHub) || "0".equals(useHub)) {
                if (appCa == null) {
                    log.info("非集线器，未找到CA证书自动安装卸载配置，需要打开证书应用");
                    app.setFlowCode(flowCode);
                    app.setActionType(AppAction.open.name());
                    app.run();
                } else {
                    log.info("非集线器，已找到CA证书自动安装卸载配置，不需要打开证书应用.");
                }
            }
        }
    }

    private void attachUsbKey() {
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
        log.info("挂载usbKey result =" + result);
        if (StringUtils.isNotBlank(result) && result.contains("succesfully")) {
            log.info("挂载usbKey成功.");
            RobotConstant.USB_KEY_FLAG = true;
            return;
        }
        throw new BusinessException("挂载usbKey失败.");
    }
}
