package com.seebon.rpa.service.impl;

import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.repository.mapper.RobotTaskSessionKeepMapper;
import com.seebon.rpa.service.RobotTaskSessionKeepService;
import com.seebon.rpa.service.handle.SessionKeepHandle;
import com.seebon.rpa.utils.FileStorage;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RobotTaskSessionKeepServiceImpl implements RobotTaskSessionKeepService {
    @Autowired
    private RobotTaskSessionKeepMapper taskSessionKeepMapper;
    @Autowired
    private SessionKeepHandle sessionKeepHandle;
    @Autowired
    private RobotContext ctx;

    @Override
    public Integer getSessionKeepPort() {
        RobotTaskSessionKeep sessionKeep = this.getSessionKeep();
        if (sessionKeep == null) {
            return null;
        }
        if (sessionKeep.getSharePort() != null) {
            return sessionKeep.getSharePort();
        }
        return sessionKeep.getPort();
    }

    @Override
    public RobotTaskSessionKeep getSessionKeep() {
        String appCode = ctx.get("appCode");
        String taskCode = ctx.get("taskCode");
        String tplTypeCode = ctx.get("tplTypeCode");
        return taskSessionKeepMapper.selectTaskCode(appCode, taskCode, tplTypeCode);
    }

    @Override
    public void saveSessionKeep(List<RobotTaskSessionKeep> list) {
        //删除数据
        list.stream().forEach(task -> {
            task.setMachineCode(FileStorage.loadMachineCodeFromDisk());
            task.setSyncMachineCode(null);
            task.setStartKeepTime(null);
            task.setEndKeepTime(null);
            task.setLoginStatus(1);
            if (task.getStatus() == null) {
                task.setStatus(1);
            }
            taskSessionKeepMapper.cleanByTaskCode(task.getAppCode(), task.getTaskCode(), task.getDeclareSystem());
        });
        list.stream().forEach(task -> {
            if (task.getDisabled() != null && task.getDisabled() == 1) {
                taskSessionKeepMapper.insert(task);
            } else {
                taskSessionKeepMapper.deleteByPrimaryKey(task.getId());
            }
        });
    }

    @Override
    public void syncSessionKeep(Integer loginStatus, String comment) {
        RobotTaskSessionKeep sessionKeep = this.getSessionKeep();
        if (sessionKeep == null) {
            return;
        }
        if (loginStatus == 1) {
            log.info("登录成功，开始保存会话信息.keepId=" + sessionKeep.getId());
            taskSessionKeepMapper.startKeep(sessionKeep.getId(), 3);
            if (sessionKeep.getSharePort() != null) {
                log.info("共享端口号：" + sessionKeep.getSharePort() + "登录成功，开始保存会话信息.keepId=" + sessionKeep.getId());
                taskSessionKeepMapper.startKeepSharePort(sessionKeep.getId(), 3, sessionKeep.getSharePort());
            }
        } else {
            log.info("登录失败，保存会话信息.keepId=" + sessionKeep.getId());
            Integer fileId = null;
            WebDriver webDriver = ctx.getDriver();
            if (webDriver != null) {
                fileId = sessionKeepHandle.fileUpload((ChromeDriver) webDriver, sessionKeep);
            }
            taskSessionKeepMapper.keepFail(sessionKeep.getId(), 2, fileId);
            if (sessionKeep.getSharePort() != null) {
                taskSessionKeepMapper.keepFailSharePort(sessionKeep.getSharePort(), 2, fileId);
            }
        }
        sessionKeepHandle.sysTaskSessionKeep(sessionKeep.getId());
    }
}



