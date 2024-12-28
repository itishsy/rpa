package com.seebon.rpa.actions.impl.win;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.seebon.rpa.actions.impl.AbstractAction;
import com.seebon.rpa.actions.target.impl.NoneTarget;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.annotation.RobotAction;
import com.seebon.rpa.context.enums.AppAction;
import com.seebon.rpa.entity.robot.RobotAppEnv;
import com.seebon.rpa.repository.mapper.RobotAppEnvMapper;
import com.seebon.rpa.runner.SyncInService;
import com.seebon.rpa.utils.ProcessUtil;
import com.seebon.rpa.utils.python.PythonUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RobotAction(name = "win应用", targetType = NoneTarget.class)
public class App extends AbstractAction {
    @Setter
    @ActionArgs(value = "应用操作", required = true, dict = AppAction.class)
    private String actionType;

    @ActionArgs(value = "操作参数")
    private String actionArgs;

    @Autowired
    private RobotAppEnvMapper appEnvMapper;
    @Autowired
    private SyncInService syncInService;

    @Override
    public void run() {
        String appCode = ctx.getVariable("appCode");
        switch (AppAction.valueOf(actionType)) {
            case start: {
                List<String> commands = Lists.newArrayList();
                commands.add(RobotContext.pythonPath);
                commands.add(RobotContext.workPath + "\\python\\Start.py");
                if (StringUtils.isNotBlank(actionArgs)) {
                    commands.add(actionArgs);
                } else {
                    List<String> exePaths = this.getExePath(appCode, flowCode, 0);
                    if (CollectionUtils.isEmpty(exePaths)) {
                        break;
                    }
                    commands.add(exePaths.get(0));
                }
                commands.add(String.valueOf(this.getTimeout()));

                PythonUtil.runCommand("win应用启动", null, this.getTimeout(), commands);
                ctx.addVariable("appType", "start");
                ctx.addVariable("appPath", actionArgs);
                break;
            }
            case open: {
                List<String> commands = Lists.newArrayList();
                commands.add(RobotContext.pythonPath);
                commands.add(RobotContext.workPath + "\\python\\Open.py");
                if (StringUtils.isNotBlank(actionArgs)) {
                    commands.add(actionArgs);
                } else {
                    if (syncInService.isDevEnv(null)) {
                        log.info("已配置本地开发模式,取消证书的关闭操作.");
                        break;
                    }
                    List<String> exePaths = this.getExePath(appCode, flowCode, 0);
                    if (CollectionUtils.isEmpty(exePaths)) {
                        log.error("打开应用路径未配置且应用环境未找需启动的路径。");
                        return;
                    }
                    //启动服务
                    List<String> serviceNames = this.getServiceNameList(exePaths);
                    for (String serviceName : serviceNames) {
                        PythonUtil.runCommand("win服务启动", null, this.getTimeout(), Lists.newArrayList("net start " + serviceName));
                    }
                    commands.add(exePaths.stream().collect(Collectors.joining(",")));
                }
                commands.add(String.valueOf(this.getTimeout()));

                //打开exe
                PythonUtil.runCommand("win应用打开", null, this.getTimeout(), commands);

                ctx.addVariable("appType", "open");
                ctx.addVariable("appPath", actionArgs);
                break;
            }
            case close: {
                List<String> commands = Lists.newArrayList();
                commands.add(RobotContext.pythonPath);
                commands.add(RobotContext.workPath + "\\python\\Close.py");
                if (StringUtils.isNotBlank(actionArgs)) {
                    if (actionArgs.contains("\\")) {
                        List<String> args = Lists.newArrayList(actionArgs.split(","));
                        commands.add(this.getExeName(args));
                    } else {
                        commands.add(actionArgs);
                    }
                } else {
                    if (syncInService.isDevEnv(null)) {
                        log.info("已配置本地开发模式,取消证书的关闭操作.");
                        break;
                    }
                    List<String> exePaths = this.getExePath(null, null, null);
                    if (CollectionUtils.isEmpty(exePaths)) {
                        break;
                    }
                    commands.add(this.getExeName(exePaths));
                }
                PythonUtil.runCommand("win应用关闭", null, this.getTimeout(), commands);
                break;
            }
            default:
                break;
        }
    }

    private List<String> getExePath(String appCode, String flowCode, Integer type) {
        log.info("获取应用路径:appCode={},flowCode={},type={}", appCode, flowCode, type);
        Example example = new Example(RobotAppEnv.class);
        Example.Criteria ca = example.createCriteria();
        if (type != null) {
            ca.andEqualTo("type", type);
        }
        if (StringUtils.isNotBlank(appCode)) {
            ca.andEqualTo("appCode", appCode);
        }
        List<RobotAppEnv> appEnvs = appEnvMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(appEnvs)) {
            return null;
        }
        log.info("获取应用路径:appEnvs={}", JSON.toJSONString(appEnvs));
        appEnvs = this.getAppEnvs(appEnvs, flowCode, type);
        return appEnvs.stream().map(a -> a.getPath()).distinct().collect(Collectors.toList());
    }

    private String getExeName(List<String> paths) {
        return paths.stream().map(path -> StringUtils.substringAfterLast(path, "\\")).collect(Collectors.joining(","));
    }

    private List<String> getServiceNameList(List<String> paths) {
        List<Map<String, String>> serviceNames = ProcessUtil.getServiceName();
        List<String> nameList = Lists.newArrayList();
        for (String path : paths) {
            for (Map<String, String> dataMap : serviceNames) {
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    if (path.toLowerCase().contains(entry.getValue().toLowerCase())) {
                        nameList.add(entry.getKey());
                    }
                }
            }
        }
        return nameList;
    }

    private List<RobotAppEnv> getAppEnvs(List<RobotAppEnv> appEnvs, String flowCode, Integer type) {
        if (type == null) {
            return appEnvs;
        }
        List<RobotAppEnv> list = Lists.newArrayList();
        for (RobotAppEnv env : appEnvs) {
            if (StringUtils.isBlank(env.getFlowCode())) {
                list.add(env);
                continue;
            }
            if (StringUtils.isNotBlank(flowCode) && flowCode.equals(env.getFlowCode())) {
                list.add(env);
            }
        }
        return list;
    }
}
