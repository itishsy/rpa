package com.seebon.rpa.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.entity.robot.RobotAppCa;
import com.seebon.rpa.entity.robot.RobotTaskArgs;
import com.seebon.rpa.entity.robot.enums.LoginTypeEnum;
import com.seebon.rpa.entity.robot.vo.RobotAppCaVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskQueueVO;
import com.seebon.rpa.repository.mapper.RobotAppCaMapper;
import com.seebon.rpa.repository.mapper.RobotTaskMapper;
import com.seebon.rpa.service.RobotAppCaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RobotAppCaServiceImpl implements RobotAppCaService {
    @Autowired
    private RobotAppCaMapper appCaMapper;
    @Autowired
    private RobotTaskMapper taskMapper;

    @Override
    public void saveAppCa(List<RobotAppCaVO> list) {
        list.stream().forEach(ca -> {
            ca.setSyncTime(null);
            ca.setSyncMachineCode(null);
            appCaMapper.cleanByAppCode(ca.getAppCode(), ca.getDeclareSystem());
        });
        list.stream().forEach(ca -> {
            RobotAppCa appCa = new RobotAppCa();
            BeanUtils.copyProperties(ca, appCa);
            if (appCa.getDisabled() != null && appCa.getDisabled() == 1) {
                appCaMapper.insert(appCa);
            } else {
                appCaMapper.deleteByPrimaryKey(appCa.getId());
            }
        });
    }

    @Override
    public void installAppCa(RobotTaskQueueVO queueVO) {
        RobotAppCa robotAppCa = this.getRobotAppCa(queueVO.getAppCode(), queueVO.getDeclareSystem(), queueVO.getDeclareAccount());
        if (robotAppCa == null) {
            return;
        }
        boolean loginType = this.checkLoginTypeCa(queueVO);
        if (!loginType) {
            return;
        }
        this.execShell(robotAppCa.getFileName(), "install");
    }

    @Override
    public void unInstallAppCa(RobotTaskQueueVO queueVO) {
        RobotAppCa robotAppCa = this.getRobotAppCa(queueVO.getAppCode(), queueVO.getDeclareSystem(), queueVO.getDeclareAccount());
        if (robotAppCa == null) {
            return;
        }
        boolean loginType = this.checkLoginTypeCa(queueVO);
        if (!loginType) {
            return;
        }
        this.execShell(robotAppCa.getFileName(), "uninstall");
    }

    @Override
    public void unInstallAppCa() {
        List<RobotAppCa> appCaList = appCaMapper.selectAll();
        if (CollectionUtils.isEmpty(appCaList)) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            for (RobotAppCa appCa : appCaList) {
                this.execShell(appCa.getFileName(), "uninstall");
            }
        });
    }

    @Override
    public RobotAppCa getRobotAppCa(String appCode, String declareSystem, String declareAccount) {
        Example example = new Example(RobotAppCa.class);
        example.createCriteria().andEqualTo("appCode", appCode).andEqualTo("declareSystem", declareSystem);
        List<RobotAppCa> list = appCaMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        if (list.size() == 1) {
            RobotAppCa appCa = list.get(0);
            if (StringUtils.isBlank(appCa.getDeclareAccount())) {
                return appCa;
            }
            if (Lists.newArrayList(appCa.getDeclareAccount().split(",")).contains(declareAccount)) {
                return appCa;
            }
            return null;
        }
        for (RobotAppCa appCa : list) {
            if (StringUtils.isBlank(appCa.getDeclareAccount())) {
                continue;
            }
            if (Lists.newArrayList(appCa.getDeclareAccount().split(",")).contains(declareAccount)) {
                return appCa;
            }
        }
        return null;
    }

    private boolean checkLoginTypeCa(RobotTaskQueueVO queueVO) {
        Map<String, Object> taskArgMap = this.getTaskArgMap(queueVO.getTaskCode());
        if (MapUtils.isEmpty(taskArgMap)) {
            return false;
        }
        Object loginType = taskArgMap.get(LoginTypeEnum.getValueByCode(queueVO.getDeclareSystem()));
        log.info("loginType=" + loginType);
        if (loginType != null && StringUtils.isNotBlank(loginType.toString()) && "CA证书登录".equals(loginType.toString())) {
            return true;
        }
        log.info("登录方式不是CA证书登录，无需激活,loginType=" + loginType);
        return false;
    }

    private void execShell(String execFileName, String execParam) {
        String execFilePath = RobotContext.workPath + "\\ca\\" + execFileName;
        if (!FileUtil.exist(execFilePath)) {
            log.info("执行文件" + execFilePath + " 不存在.");
            return;
        }
        List<String> commandsList = Lists.newArrayList();
        commandsList.add("CMD");
        commandsList.add("/C");
        commandsList.add(execFilePath);
        commandsList.add(execParam);
        String[] commands = new String[commandsList.size()];
        commands = commandsList.toArray(commands);
        try {
            Process pid = Runtime.getRuntime().exec(commands);
            CompletableFuture.runAsync(() -> {
                log.info("shell exec start");
                try {
                    //脚本执行正常时的输出信息
                    String inputResult = IoUtil.read(pid.getInputStream(), Charset.forName("UTF-8"));
                    //脚本执行异常时的输出信息
                    String errorResult = IoUtil.read(pid.getErrorStream(), Charset.forName("GBK"));
                    log.info("shell exec inputResult=" + inputResult + ",errorResult=" + errorResult);
                } catch (Exception e) {
                    log.error("shell exec error." + e.getMessage(), e);
                }
                log.info("shell exec end");
            });
            pid.waitFor(3, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("执行CA证书脚本异常." + e.getMessage(), e);
        }
    }

    private Map<String, Object> getTaskArgMap(String taskCode) {
        List<RobotTaskArgs> taskArgsList = taskMapper.findArgs(taskCode);
        if (CollectionUtils.isEmpty(taskArgsList)) {
            return Maps.newHashMap();
        }
        Map<String, Object> taskArgMap = Maps.newHashMap();
        for (RobotTaskArgs taskArgs : taskArgsList) {
            taskArgMap.put(taskArgs.getArgsKey(), taskArgs.getArgsValue());
        }
        return taskArgMap;
    }
}
