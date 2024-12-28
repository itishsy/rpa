package com.seebon.rpa.service.impl;

import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.common.utils.DateUtil;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.*;
import com.seebon.rpa.entity.robot.dto.RobotStatisticsDTO;
import com.seebon.rpa.entity.robot.dto.design.RobotClientComponentVO;
import com.seebon.rpa.entity.robot.dto.design.RobotClientFlowVO;
import com.seebon.rpa.entity.robot.dto.design.RobotComponentVO;
import com.seebon.rpa.entity.robot.dto.design.RobotVersionVO;
import com.seebon.rpa.entity.robot.enums.RobotClientStatus;
import com.seebon.rpa.entity.robot.vo.RobotClientLogVO;
import com.seebon.rpa.entity.robot.vo.RobotClientVO;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.entity.system.User;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.service.RobotClientService;
import com.seebon.rpa.utils.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotClientServiceImpl implements RobotClientService {
    @Autowired
    private RobotClientDao robotClientDao;
    @Autowired
    private RobotClientTrackDao robotClientTrackDao;
    @Autowired
    private RobotComponentDao robotComponentDao;
    @Autowired
    private RobotClientComponentDao robotClientComponentDao;
    @Autowired
    private RobotClientLogDao robotClientLogDao;
    @Autowired
    private RobotTaskDao robotTaskDao;
    @Autowired
    private RpaSaasService rpaSaasService;

    @Override
    public int add(RobotClient client) {
        if (StringUtils.isBlank(client.getMachineAmount())){
            client.setMachineAmount(null);
        }
        Assert.isNotBlank(client.getMachineCode(), "机器标识码不能为空");
        User user = SecurityContext.currentUser();
        if (client.getId() == null) {
            this.checkMachineCode(client.getMachineCode());
            //记录日志
            this.saveRobotClientLog(client.getMachineCode(), RobotClientStatus.Unused.getIndex());

            client.setStatus(RobotClientStatus.Unused.getIndex());
            client.setUserName(user.getUsername());
            client.setCreateTime(new Date());
            client.setLoginTime(new Date());
            client.setHeartbeatTime(new Date());
            client.setLastMaintenanceDate(new Date());
            return robotClientDao.insertSelective(client);
        }
        RobotClient robotClient = robotClientDao.selectByPrimaryKey(client.getId());
        if (!robotClient.getMachineCode().equals(client.getMachineCode())) {
            this.checkMachineCode(client.getMachineCode());
        }
        client.setUserName(user.getUsername());
        client.setLoginTime(new Date());
        client.setHeartbeatTime(new Date());
        client.setLastMaintenanceDate(new Date());
        client.setStatus(client.getStatus());
        return robotClientDao.updateByPrimaryKeySelective(client);
    }

    @Override
    public int check(String machineCode) {
        Assert.isNotBlank(machineCode, "机器标识码不能为空");

        RobotClient robotClient = robotClientDao.selectClient(machineCode);
        if (robotClient == null) {
            throw new RuntimeException("机器码不存在");
        }
        Integer custId = SecurityContext.currentUser().getCustId();
        if (!robotClient.getClientId().equals(custId)) {
            throw new RuntimeException("机器码与当前客户不一致。custId=" + custId + ",machineCode=" + machineCode);
        }

        RobotClient client = new RobotClient();
        client.setId(robotClient.getId());
        client.setToken(SecurityContext.currentToken());
        client.setStatus(1);
        client.setLoginTime(new Date());
        client.setHeartbeatTime(new Date());
        client.setLastMaintenanceDate(new Date());
        return robotClientDao.updateByPrimaryKeySelective(client);
    }

    @Override
    public String heartbeat(String machineCode, int status) {
        Example example = new Example(RobotClient.class);
        example.createCriteria().andEqualTo("machineCode", machineCode);
        RobotClient robotClient = robotClientDao.selectOneByExample(example);
        if (robotClient == null) {
            return "0";
        }
        //机器状态不一样，则记录日志
        if (robotClient.getStatus().intValue() != status) {
            RobotClientTrack clientTrack = robotClientTrackDao.selectLatest(machineCode);
            if(clientTrack != null){
                clientTrack.setEndTime(new Date());
                robotClientTrackDao.updateByPrimaryKey(clientTrack);
            }

            RobotClientTrack track = new RobotClientTrack();
            track.setClientId(SecurityContext.currentUser().getCustId());
            track.setMachineCode(machineCode);
            track.setStatus(status);
            track.setCreateTime(new Date());
            robotClientTrackDao.insertSelective(track);
        }
        //修改心跳时间
        String cmd = robotClient.getCmd();
        RobotClient updateRobotClient = new RobotClient();
        updateRobotClient.setStatus(status);
        updateRobotClient.setHeartbeatTime(new Date());
        updateRobotClient.setCmd("0");
        int i = robotClientDao.updateByExampleSelective(updateRobotClient, example);
        if (StringUtils.isNotBlank(cmd)) {
            return cmd;
        }
        return i + "";
    }

    @Override
    public int registerComponent(RobotClientComponent clientComponent) {
        RobotClientComponent component = robotClientComponentDao.selectClientComponentByRobotCode(clientComponent.getComponent(), clientComponent.getComponent());
        RobotClientComponent component1 = new RobotClientComponent();
        if (component != null) {//存在
            BeanUtils.copyProperties(clientComponent, component1);
            return robotClientComponentDao.updateClientComponent(component1);
        }
        return robotClientComponentDao.insert(clientComponent);
    }

    @Override
    public RobotVersionVO checkComponent(String component, String version) {
        //检查组件名称bundleName的最新版本是否与currentVersion一致。
        // 是，返回null；
        // 否，返回最新的VersionVO
        RobotComponentVO robotComponentVO = robotComponentDao.selectVersion(component);
        if (robotComponentVO != null) {
            if (version.equals(robotComponentVO.getVersion())) {
                return null;
            } else {
                RobotVersionVO versionVO = new RobotVersionVO();
                versionVO.setNewVersion(robotComponentVO.getVersion());
                StringBuffer sb = new StringBuffer();
                String path = sb.append(component).append("/").append(versionVO.getNewVersion()).append("/")
                        .append(robotComponentVO.getFileName()).toString();
                //http://172.172.4.8:9527/api/download/release/rpa-console-web/abc.jar
                String url = "/download/jar/" + path;
                versionVO.setDownloaderUrl(url);
                return versionVO;
            }
        }
        return null;
    }


    @Override
    public RobotComponentVO checkComponentVersion(String component) {
        return robotComponentDao.selectVersion(component);
    }


    @Override
    public IgGridDefaultPage<RobotClient> monitorListPage(Map<String, Object> map) {
        if (!map.containsKey("isPage")) {
            throw new BusinessException("非查询参数");
        }
        //分页查询流程设计信息
        List<RobotClient> consoleAppList = robotClientDao.findByIgQuery(map);
        //查询总记录数
        int records = robotClientDao.findByRecords(map);
        return new IgGridDefaultPage<>(consoleAppList, records);
    }

    @Override
    public List<RobotClientComponentVO> componentList(String appCode) {
        return robotClientDao.componentList(appCode);
    }

    @Override
    public List<RobotClientFlowVO> appList(String appCode) {
        return robotClientDao.appList(appCode);
    }

    @Override
    public IgGridDefaultPage<RobotClientVO> listRobotClient(Map<String, Object> params) {
        Integer start = Integer.parseInt(params.get("start").toString());
        Integer size = Integer.parseInt(params.get("size").toString());
        if (params.get("status") != null && StrUtil.isNotBlank(params.get("status").toString())) {
            List<Integer> statusList = new ArrayList<>();
            String status = params.get("status").toString();
            if (status.contains(",")) {
                String[] split = status.split(",");
                for (String s : split) {
                    statusList.add(Integer.parseInt(s));
                }
            } else {
                statusList.add(Integer.parseInt(status));
            }
            params.put("status", statusList);
        } else {
            params.put("status", null);
        }
        PageHelper.offsetPage(start, size);
        Page<RobotClientVO> page = (Page<RobotClientVO>) robotClientDao.selectRobotClients(params);
        List<CustomerBase> customers = rpaSaasService.listCustomer(false, "");
        Map<Integer, CustomerBase> customerMap = customers.stream().collect(Collectors.toMap(k -> k.getId(), v -> v, (x, y) -> x));
        for (RobotClientVO clientVO : page.getResult()) {
            CustomerBase base = customerMap.get(clientVO.getClientId());
            if (base == null) {
                continue;
            }
            clientVO.setStatusName(RobotClientStatus.getByIndex(clientVO.getStatus()));
            clientVO.setClientName(base.getName());
        }
        return new IgGridDefaultPage<RobotClientVO>(page.getResult(), (int) page.getTotal());
    }

    @Override
    public List<RobotClientVO> getByClientId(Integer clientId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("clientId", clientId);
        return robotClientDao.selectRobotClients(params);
    }

    @Override
    public String generateCode() {
        String machineCode = this.getCode();
        log.info("随机机器码:" + machineCode);
        RobotClient robotClient = robotClientDao.selectClient(machineCode);
        if (robotClient != null) {
            return this.generateCode();
        }
        return machineCode;
    }

    private void checkMachineCode(String machineCode) {
        RobotClient robotClient = robotClientDao.selectClient(machineCode);
        if (robotClient != null) {
            throw new BusinessException("机器码已存在.");
        }
    }

    private String getCode() {
        String prefix = "SR10-" + DateUtil.formatDate(new Date(), "yyMM") + "-";
        String randomCode = RandomUtil.randomString(4).toUpperCase();
        Boolean num = ReUtil.contains(PatternPool.NUMBERS, randomCode);
        Boolean word = ReUtil.contains(PatternPool.WORD, randomCode);
        if (!(num && word)) {
            return getCode();
        }
        return prefix + randomCode;
    }

    private void saveRobotClientLog(String machineCode, Integer status) {
        User user = SecurityContext.currentUser();
        RobotClientLog log = new RobotClientLog();
        log.setMachineCode(machineCode);
        log.setUserName(user.getUsername());
        log.setStatus(status);
        log.setRemark(RobotClientStatus.getValueByIndex(status));
        log.setCreateTime(new Date());
        //robotClientLogDao.insertSelective(log);//先不记录日志
    }

    @Override
    public List<RobotClientLogVO> listRobotClientLog(Map<String, Object> params) {
        String machineCode = params.get("machineCode").toString();
        Example example = new Example(RobotClientLog.class);
        example.orderBy("id").desc();
        example.createCriteria().andEqualTo("machineCode", machineCode).andGreaterThanOrEqualTo("createTime", DateUtil.addDays(new Date(), -2));
        List<RobotClientLog> list = robotClientLogDao.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return list.stream().map(log -> {
            RobotClientLogVO logVO = new RobotClientLogVO();
            BeanUtils.copyProperties(log, logVO);
            logVO.setStatusName(RobotClientStatus.getByIndex(log.getStatus()));
            return logVO;
        }).collect(Collectors.toList());
    }

    @Override
    public Integer saveRobotClientLog(RobotClientLogVO logVO) {
        User user = SecurityContext.currentUser();
        RobotClientLog log = new RobotClientLog();
        log.setMachineCode(logVO.getMachineCode());
        log.setUserName(user.getUsername());
        log.setStatus(logVO.getStatus());
        log.setRemark(logVO.getRemark());
        log.setCreateTime(new Date());
        return robotClientLogDao.insertSelective(log);
    }

    @Override
    public RobotStatisticsDTO robotStatistics() {
        RobotStatisticsDTO result = new RobotStatisticsDTO();
        RobotStatisticsDTO dto = robotClientDao.selectStatistics();
        if (dto != null) {
            result.setMachineTotal(dto.getMachineTotal());
            result.setExecMachines(dto.getExecMachines());
            result.setAbnormalMachines(dto.getAbnormalMachines());
        }
        RobotStatisticsDTO dto1 = robotTaskDao.selectStatistics();
        if (dto1 != null) {
            result.setAccountTotal(dto1.getAccountTotal());
            result.setRunningAccounts(dto1.getRunningAccounts());
            result.setStopAccounts(dto1.getStopAccounts());
        }
        return result;
    }

    @Override
    public void updateById(RobotClientVO clientVO) {
        RobotClient client = new RobotClient();
        client.setId(clientVO.getId());
        client.setMaxKeepNum(clientVO.getMaxKeepNum());
        client.setSync(0);
        robotClientDao.updateByPrimaryKeySelective(client);
    }
}
