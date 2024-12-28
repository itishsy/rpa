package com.seebon.rpa.context;

import com.google.common.collect.Maps;
import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SessionKeep {
    @Autowired
    private RobotContext ctx;

    /**
     * 长session维持的远程ChromeDriver缓存
     */
    private Map<String, ChromeDriver> sessionKeepRemoteDriverMap = Maps.newHashMap();
    /**
     * 长session维持的被远程ChromeDriver缓存
     */
    private Map<String, ChromeDriver> sessionKeepTargetDriverMap = Maps.newHashMap();

    public ChromeDriver getRemoteDriver(Integer port) {
        return sessionKeepRemoteDriverMap.get(this.getSessionKeepKey(port));
    }

    public ChromeDriver getRemoteDriver(RobotTaskSessionKeep task) {
        return sessionKeepRemoteDriverMap.get(this.getSessionKeepKey(task));
    }

    public void setRemoteDriver(Integer port, ChromeDriver driver) {
        sessionKeepRemoteDriverMap.put(this.getSessionKeepKey(port), driver);
    }

    public void setRemoteDriver(RobotTaskSessionKeep task, ChromeDriver driver) {
        sessionKeepRemoteDriverMap.put(this.getSessionKeepKey(task), driver);
    }

    public ChromeDriver getTargetDriver(RobotTaskSessionKeep task) {
        return sessionKeepTargetDriverMap.get(this.getSessionKeepKey(task));
    }

    public void setTargetDriver(Integer port, ChromeDriver driver) {
        sessionKeepTargetDriverMap.put(this.getSessionKeepKey(port), driver);
    }

    public void removeDriver(RobotTaskSessionKeep task) {
        sessionKeepRemoteDriverMap.remove(this.getSessionKeepKey(task));
        sessionKeepTargetDriverMap.remove(this.getSessionKeepKey(task));
    }

    public void removeDriver(Integer port) {
        sessionKeepRemoteDriverMap.remove(this.getSessionKeepKey(port));
        sessionKeepTargetDriverMap.remove(this.getSessionKeepKey(port));
    }

    private String getSessionKeepKey(Integer port) {
        String taskCode = ctx.get("taskCode");
        String tplTypeCode = ctx.get("tplTypeCode");
        return taskCode + "_" + tplTypeCode + "_" + port;
    }

    private String getSessionKeepKey(RobotTaskSessionKeep task) {
        return task.getTaskCode() + "_" + task.getDeclareSystem() + "_" + Convert.getPort(task);
    }
}
