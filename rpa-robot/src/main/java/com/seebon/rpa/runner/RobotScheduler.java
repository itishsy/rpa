package com.seebon.rpa.runner;

import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.service.RobotExecutionFileService;
import com.seebon.rpa.service.handle.SessionKeepHandle;
import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 *
 * @author: xfz
 */
@Slf4j
@Component
public class RobotScheduler {
    @Autowired
    private RobotExecutionFileService executionFileService;
    @Autowired
    private SyncInService syncInService;
    @Autowired
    private SyncOutService syncOutService;
    @Autowired
    private RobotContext ctx;
    @Autowired
    private SessionKeepHandle sessionKeepHandle;

    @Value("${developer.auto.sync:on}")
    private String autoSync;

    @Value("${developer.auto.task:on}")
    private String autoTask;

    /**
     * 每1分钟自动【拉取】应用数据、任务数据
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void startSyncData() {
        if (this.autoSync()) {
            try {
                Integer result = syncInService.syncAppData();
                log.info("拉取应用数据。共" + result + "条");
            } catch (Exception e) {
                log.error("【应用数据】拉取发生了异常。" + e.getMessage(), e);
            }

            try {
                Integer result = syncInService.syncAppTaskData();
                log.info("拉取任务数据。共" + result + "条");
            } catch (Exception e) {
                log.error("【任务数据】拉取发生了异常。" + e.getMessage(), e);
            }

            try {
                Integer result = syncInService.syncTaskSessionKeep();
                log.info("拉取长session维持数据。共" + result + "条");
            } catch (Exception e) {
                log.error("【长session维持数据】拉取发生了异常。" + e.getMessage(), e);
            }

            try {
                Integer result = syncInService.syncAppCa();
                log.info("拉取应用CA数据。共" + result + "条");
            } catch (Exception e) {
                log.error("【应用CA】拉取发生了异常。" + e.getMessage(), e);
            }

            try {
                Integer result = syncInService.syncClient();
                log.info("拉取机器人客户端数据。共" + result + "条");
            } catch (Exception e) {
                log.error("【机器人客户端】拉取发生了异常。" + e.getMessage(), e);
            }

            try {
                Integer result = syncInService.syncConfig();
                log.info("拉取机器人配置数据。共" + result + "条");
            } catch (Exception e) {
                log.error("【机器人配置】拉取发生了异常。" + e.getMessage(), e);
            }
        }
    }

    /**
     * 每10秒自动【拉取】队列、数据
     */
    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 10 * 1000)
    public void startCmdData() {
        if (this.autoTask()) {
            try {
                Integer result = syncInService.syncTaskQueueData();
                log.info("拉取队列数据。共" + result + "条");
            } catch (Exception e) {
                log.error("【队列数据】拉取发生了异常。" + e.getMessage(), e);
            }

            try {
                Integer result = syncInService.syncCommandData();
                log.info("拉取指令数据。共" + result + "条");
            } catch (Exception e) {
                log.error("【指令数据】拉取发生了异常。" + e.getMessage(), e);
            }
        }
    }

    /**
     * 每8秒自动【回传】执行记录
     */
    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 8 * 1000)
    public void syncExecution() {
        if (this.autoSync()) {
            try {
                Integer result = syncOutService.syncExecution();
                log.info("回传执行记录。共" + result + "条");
            } catch (Exception e) {
                log.error("【执行记录】回传发生了异常。" + e.getMessage(), e);
            }
        }
    }

    /**
     * 每2分钟自动【回传】申报文件数据
     */
    @Scheduled(cron = "0 */2 * * * ?")
    public void syncExecutionFile() {
        if (this.autoSync()) {
            try {
                Integer result = executionFileService.syncExecutionFile();
                log.info("回传申报文件。共" + result + "条");
            } catch (Exception e) {
                log.error("【申报文件】回传发生了异常。" + e.getMessage(), e);
            }
        }
    }

    /**
     * 每5秒自动【回传】任务状态、队列状态、执行监控
     */
    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 5 * 1000)
    public void syncTaskStatusData() {
        if (this.autoSync()) {
            try {
                Integer result = syncOutService.syncTaskStatusData();
                log.info("回传任务状态。共" + result + "条");
            } catch (Exception e) {
                log.error("【任务状态】回传发生了异常。" + e.getMessage(), e);
            }

            try {
                Integer result = syncOutService.syncTaskQueueData();
                log.info("回传队列状态。共" + result + "条");
            } catch (Exception e) {
                log.error("【队列状态】回传发生了异常。" + e.getMessage(), e);
            }

            try {
                Integer result = syncOutService.syncMonitorData();
                log.info("回传执行监控。共" + result + "条");
            } catch (Exception e) {
                log.error("【执行监控】回传发生了异常。" + e.getMessage(), e);
            }
        }
    }

    /**
     * 每10分钟自动同步个人编号数据
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void syncEmpAccount() {
        if (this.autoSync()) {
            try {
                Integer count = syncOutService.syncEmpAccount();
                log.info("回传个人编号数据。共" + count + "条");
            } catch (Exception e) {
                log.error("【个人编号数据】回传发生了异常。" + e.getMessage(), e);
            }
        }
    }

    /**
     * 每1分钟检查驱动是否存活
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void checkDriver() {
        if (this.autoTask()) {
            if (ctx.isRun()) {
                log.info("盒子正在执行其他流程,等待下次检查驱动是否存活");
                return;
            }
            WebDriver driver = null;
            String openInterceptApi = ctx.get(RobotConstant.OPEN_INTERCEPT_API);
            try {
                if (!ctx.contains(RobotConstant.LOGIN_FLOW_FLAG)) {
                    return;
                }
                driver = ctx.getDriver();
                if (driver == null) {
                    return;
                }
                String windowHandle = driver.getWindowHandle();
                System.out.println("【检查驱动是否存活】 windowHandle=" + windowHandle);
            } catch (NoSuchWindowException e) {
                Convert.quit(driver, openInterceptApi);
                log.error("【检查驱动是否存活】浏览器已关闭,释放缓存数据。" + e.getMessage(), e);
            } catch (Exception e) {
                log.error("【检查驱动是否存活】发生了异常。" + e.getMessage(), e);
            }
        }
    }

    /**
     * 每2分钟检查驱动是否存活
     */
    @Scheduled(cron = "0 */2 * * * ?")
    public void refreshDriver() {
        if (syncInService.isDevEnv(null)) {
            sessionKeepHandle.refreshDevSessionDriver();
        } else {
            sessionKeepHandle.refreshProdSessionDriver();
        }
    }

    private boolean autoSync() {
        if ("on".equals(autoSync) || "true".equals(autoSync)) {
            return true;
        }
        return false;
    }

    private boolean autoTask() {
        if ("on".equals(autoTask) || "true".equals(autoTask)) {
            return true;
        }
        return false;
    }
}
