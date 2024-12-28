package com.seebon.rpa.context;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.seebon.rpa.actions.Action;
import com.seebon.rpa.context.annotation.ActionUtils;
import com.seebon.rpa.context.constant.RobotConstant;
import com.seebon.rpa.context.runtime.RobotConfigException;
import com.seebon.rpa.context.runtime.RobotInterruptException;
import com.seebon.rpa.entity.agent.enums.TplTypeEnum;
import com.seebon.rpa.entity.robot.RobotFlowStep;
import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.entity.robot.enums.LoginTypeEnum;
import com.seebon.rpa.repository.mapper.RobotTaskSessionKeepMapper;
import com.seebon.rpa.runner.SyncInService;
import com.seebon.rpa.service.RobotTaskSessionKeepService;
import com.seebon.rpa.utils.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RobotContext implements ApplicationContextAware {
    @Autowired
    private SyncInService syncInService;
    @Autowired
    private RobotTaskSessionKeepService taskSessionKeepService;

    @Autowired
    private RobotTaskSessionKeepMapper taskSessionKeepMapper;

    public static String workPath;

    @Value("${rpa.work-path}")
    private void setWorkPath(String workPath) {
        RobotContext.workPath = workPath;
    }

    public static String rpaServer;

    @Value("${rpa.server}")
    private void setRpaServer(String rpaServer) {
        RobotContext.rpaServer = rpaServer;
    }

    public static String pythonPath;

    @Value("${rpa.python-path}")
    private void setPythonPath(String pythonPath) {
        RobotContext.pythonPath = pythonPath;
    }

    private Map<String, Object> beans = Maps.newHashMap();

    private Action action;

    private Map<String, Object> variables = Maps.newHashMap();

    private Map<String, Integer> counter = Maps.newHashMap();

    public <T> T getVariable(String key) {
        if (this.get().containsKey(key)) {
            Object o = this.get().get(key);
            if (o instanceof List) {
                if (CollectionUtils.isEmpty((List) o)) {
                    return (T) ((List) o);
                }
                return (T) ((List) o).get(0);
            } else {
                return (T) o;
            }
        }
        return null;
    }

    public <T> T getVariableToList(String key) {
        if (this.get().containsKey(key)) {
            Object o = this.get().get(key);
            return (T) o;
        }
        return null;
    }

    public <T> T get(String key) {
        return (T) this.get().get(key);
    }

    public Map<String, Object> getVariables() {
        return this.get();
    }

    public void remove(String key) {
        Object o = this.get().get(key);
        if (o instanceof List) {
            List list = (List) o;
            if (CollectionUtils.isNotEmpty(list)) {
                list.remove(0);
            }
            if (list.size() == 1) {
                this.get().put(key, list.get(0));
            }
        } else {
            this.get().remove(key);
        }
    }

    public boolean contains(String key) {
        return this.get().containsKey(key);
    }

    public boolean contains(String key, Object val) {
        Object o = this.get().get(key);
        if (o == null) {
            return false;
        }
        if (o instanceof List) {
            List list = (List) o;
            return list.contains(val);
        }
        return false;
    }

    public void setVariable(String key, Object val) {
        this.get().put(key, val);
    }

    public void addVariable(String key, Object val) {
        if (this.get().containsKey(key)) {
            Object o = this.get().get(key);
            if (o.equals(val)) {
                return;
            }
            List list;
            if (o instanceof List) {
                list = (List) o;
            } else {
                list = new ArrayList();
                list.add(o);
            }
            if (!list.contains(val)) {
                if (val instanceof List) {
                    list.addAll((List) val);
                } else {
                    list.add(val);
                }
            }
            this.get().put(key, list);
        } else {
            this.get().put(key, val);
        }
    }

    public void init() {
        setVariable("workPath", RobotContext.workPath);
        setVariable("rpaServer", RobotContext.rpaServer);
        this.get().putAll(beans);
    }

    private Map<String, Object> get() {
        return variables;
    }

    public void release() {
        if (syncInService.isDevEnv(null) || this.contains(RobotConstant.LOGIN_FLOW_FLAG)) {
            this.cacheData();
        }
    }

    public void sessionKeep() {
        Integer sessionKeep = this.getVariable("sessionKeep");
        log.info("sessionKeep=" + sessionKeep);
        if (null != sessionKeep && 1 == sessionKeep) {
            Integer flowNum = this.getVariable(RobotConstant.FLOW_NUM_KEY);
            log.info("flowNum=" + flowNum);
            if (flowNum != null && flowNum > 0) {
                this.cacheData();
                this.get().put(RobotConstant.FLOW_NUM_KEY, flowNum);
            } else {
                this.quit();
            }
        } else {
            this.quit();
        }
    }

    private void cacheData() {
        WebDriver driver = this.getVariable(RobotConstant.DEFAULT_DRIVER_KEY);
        String downloadDefaultPath = this.getVariable(RobotConstant.DOWNLOAD_DEFAULT_PATH);
        String dataPath = this.getVariable(RobotConstant.DATA_PATH);
        String openInterceptApi = this.getVariable(RobotConstant.OPEN_INTERCEPT_API);
        String interceptorUrls = this.getVariable(RobotConstant.INTERCEPTOR_URLS);
        String interceptorParamKeys = this.getVariable(RobotConstant.INTERCEPTOR_PARAM_KEYS);
        Map<String, String> driverMap = this.getVariable(RobotConstant.ACCOUNTNUMBER_DRIVER);
        String openToolBrowser = this.getVariable(RobotConstant.OPEN_TOOL_BROWSER);
        String loginFlowFlag = this.getVariable(RobotConstant.LOGIN_FLOW_FLAG);
        Map<Integer, String> remoteDebugPort = this.getVariable(RobotConstant.REMOTE_DEBUG_PORT);

        this.get().clear();

        if (driver != null) {
            this.get().put(RobotConstant.DEFAULT_DRIVER_KEY, driver);
        }
        if (MapUtils.isNotEmpty(driverMap)) {
            this.get().put(RobotConstant.ACCOUNTNUMBER_DRIVER, driverMap);
        }
        if (StringUtils.isNotBlank(downloadDefaultPath)) {
            this.get().put(RobotConstant.DOWNLOAD_DEFAULT_PATH, downloadDefaultPath);
        }
        if (StringUtils.isNotBlank(dataPath)) {
            this.get().put(RobotConstant.DATA_PATH, dataPath);
        }
        if (StringUtils.isNotBlank(openInterceptApi)) {
            this.get().put(RobotConstant.OPEN_INTERCEPT_API, openInterceptApi);
        }
        if (StringUtils.isNotBlank(interceptorUrls)) {
            this.get().put(RobotConstant.INTERCEPTOR_URLS, interceptorUrls);
        }
        if (StringUtils.isNotBlank(interceptorParamKeys)) {
            this.get().put(RobotConstant.INTERCEPTOR_PARAM_KEYS, interceptorParamKeys);
        }
        if (openToolBrowser != null) {
            this.get().put(RobotConstant.OPEN_TOOL_BROWSER, openToolBrowser);
        }
        if (StringUtils.isNotBlank(loginFlowFlag)) {
            this.get().put(RobotConstant.LOGIN_FLOW_FLAG, loginFlowFlag);
        }
        if (MapUtils.isNotEmpty(remoteDebugPort)) {
            this.get().put(RobotConstant.REMOTE_DEBUG_PORT, remoteDebugPort);
        }
    }

    public void quit() {
        this.quitDriver();
        this.clear();
    }

    public void clear() {
        this.get().clear();
        this.counter.clear();
    }

    public void quitDriver() {
        WebDriver driver = this.getVariable(RobotConstant.DEFAULT_DRIVER_KEY);
        String appCode = this.getVariable("appCode");
        String taskCode = this.getVariable("taskCode");
        String tplTypeCode = this.getVariable("tplTypeCode");
        if (driver == null) {
            log.info("appCode：" + appCode + ",taskCode：" + taskCode + ",tplTypeCode：" + TplTypeEnum.getNameByCode(tplTypeCode) + ",浏览器已关闭或浏览器未打开.");
            return;
        }
        RobotTaskSessionKeep sessionKeep = taskSessionKeepService.getSessionKeep();
        if (sessionKeep != null && sessionKeep.getLoginStatus() == 1) {
            log.info("appCode：" + appCode + ",taskCode：" + taskCode + ",tplTypeCode：" + TplTypeEnum.getNameByCode(tplTypeCode) + ",机器人维持端口port：" + (sessionKeep.getSharePort() != null ? sessionKeep.getSharePort() : sessionKeep.getPort()) + ",不关闭浏览器.");
            return;
        }

        if (sessionKeep != null && sessionKeep.getSharePort() != null) {
            RobotTaskSessionKeep sharePortKeep = taskSessionKeepMapper.selectSharePortKeep(sessionKeep.getSharePort());
            if (sharePortKeep != null) {
                log.info("appCode：" + appCode + ",taskCode：" + taskCode + ",tplTypeCode：" + TplTypeEnum.getNameByCode(tplTypeCode) + ",共享端口port：" + (sessionKeep.getSharePort() != null ? sessionKeep.getSharePort() : sessionKeep.getPort()) + ",不关闭浏览器.");
                return;
            }
        }

        if (this.contains(RobotConstant.LOGIN_FLOW_FLAG)) {
            log.info("单独执行登录流程,不关闭浏览器.");
            return;
        }
        if (StringUtils.isBlank(appCode) && StringUtils.isBlank(taskCode) && StringUtils.isBlank(tplTypeCode)) {
            log.info("appCode：" + appCode + ",taskCode：" + taskCode + ",tplTypeCode：" + TplTypeEnum.getNameByCode(tplTypeCode) + ",参数为空,不关闭浏览器.");
            return;
        }
        String openInterceptApi = this.getVariable(RobotConstant.OPEN_INTERCEPT_API);
        log.info("appCode：" + appCode + ",taskCode：" + taskCode + ",tplTypeCode：" + TplTypeEnum.getNameByCode(tplTypeCode) + ",不需要维持,关闭浏览器.");
        while (driver != null) {
            Convert.quit(driver, openInterceptApi);
            Convert.cleanUserDataDir(getAccountNumberDriverKey());
            remove(RobotConstant.DEFAULT_DRIVER_KEY);
            driver = this.getVariable(RobotConstant.DEFAULT_DRIVER_KEY);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] utils = applicationContext.getBeanNamesForAnnotation(ActionUtils.class);
        for (String key : utils) {
            beans.put(key, applicationContext.getBean(key));
        }
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public boolean isRun() {
        if (MapUtils.isNotEmpty(RobotConstant.taskRunMap)) {
            log.info("盒子正在执行任务 taskCodes=" + JSON.toJSONString(RobotConstant.taskRunMap));
            return true;
        }
        return false;
    }

    public String getDriverKey() {
        Map<String, String> driverMap = this.getVariable(RobotConstant.ACCOUNTNUMBER_DRIVER);
        //log.info("driverMap=" + driverMap);
        if (MapUtils.isNotEmpty(driverMap)) {
            return driverMap.get(this.getAccountNumberDriverKey());
        }
        return null;
    }

    public String getAccountNumberDriverKey() {
        Integer clientId = this.get("clientId");
        String addrName = this.get("addrName");
        String tplTypeCode = this.get("tplTypeCode");
        String accountNumber = this.getAccountNumber();
        return clientId + "_" + addrName + "_" + accountNumber + "_" + tplTypeCode;
    }

    public WebDriver getDriver() {
        return this.getVariable(RobotConstant.DEFAULT_DRIVER_KEY);
    }

    public WebDriver getWebDriver() {
        WebDriver driver = getDriver();
        if (driver == null) {
            throw new RobotConfigException("请先打开浏览器");
        }
        return driver;
    }

    public String getAccountNumber() {
        String businessType = this.getVariable("businessType");
        if ("1001001".equals(businessType)) {//社保
            return this.getVariable("socialNumber");
        } else if ("1001002".equals(businessType)) {//公积金
            return this.getVariable("accfundNumber");
        }
        return null;
    }

    public String getLoginType() {
        String tplTypeCode = this.get("tplTypeCode");
        String loginType = this.get(LoginTypeEnum.getValueByCode(tplTypeCode));
        log.info("tplTypeCode=" + tplTypeCode + ",loginType=" + loginType);
        return loginType;
    }

    public String getTplType() {
        return this.get("tplTypeCode");
    }

    public Map<String, Object> getBeans() {
        return beans;
    }

    public void checkCounter(RobotFlowStep step) {
        Integer maxExecuteNum = step.getMaxExecuteNum();
        if (maxExecuteNum == null) {
            Object maxNum = this.get("maxExecuteNum");
            if (maxNum != null) {
                maxExecuteNum = Integer.valueOf(maxNum.toString());
            }
        }
        if (maxExecuteNum == null) {
            return;
        }
        String key = step.getFlowCode() + "_" + step.getStepCode();

        Integer count = this.counter.getOrDefault(key, 0);
        if (count >= maxExecuteNum) {
            throw new RobotInterruptException("执行步骤：" + step.getStepName() + "(" + step.getActionCode() + ") 已执行次数：" + count + ",超过最大执行次数：" + maxExecuteNum + ",停止执行.");
        }
        this.counter.put(key, count + 1);
    }
}
