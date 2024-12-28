package com.seebon.rpa.service.impl;

import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.RobotAppCa;
import com.seebon.rpa.entity.robot.vo.RobotAppCaVO;
import com.seebon.rpa.entity.robot.vo.RobotAppVO;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mysql.RobotAppCaDao;
import com.seebon.rpa.service.CustomerCommandService;
import com.seebon.rpa.service.RobotAppCaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RobotAppCaImpl implements RobotAppCaService {
    @Autowired
    private RobotAppCaDao appCaDao;
    @Autowired
    private RpaSaasService rpaSaasService;
    @Autowired
    private CustomerCommandService customerCommandService;

    @Override
    public IgGridDefaultPage<RobotAppCaVO> list(Map<String, Object> map) {
        Integer start = (Integer) map.get("start");
        Integer size = (Integer) map.get("size");
        PageHelper.offsetPage(start, size);
        Page<RobotAppCaVO> page = (Page<RobotAppCaVO>) appCaDao.selectByParams(map);
        if (CollectionUtils.isNotEmpty(page.getResult())) {
            page.getResult().stream().forEach(it -> {
                RobotAppVO appVO = rpaSaasService.getRobotDeclareType(it.getDeclareSystem());
                if (appVO != null) {
                    it.setDeclareSystemName(appVO.getBusinessType());
                }
            });
        }
        return new IgGridDefaultPage<RobotAppCaVO>(page.getResult(), (int) page.getTotal());
    }

    @Override
    public Integer save(RobotAppCa appCa) {
        Session session = SecurityContext.currentUser();
        if (appCa.getId() == null) {
            this.exitAppCa(appCa.getAppCode(), appCa.getDeclareSystem());

            appCa.setSyncTime(null);
            appCa.setSyncMachineCode(null);
            appCa.setCreateId((int) session.getId());
            appCa.setCreateName(session.getName());
            appCa.setCreateTime(new Date());
            appCa.setUpdateTime(new Date());
            appCaDao.insertSelective(appCa);
        } else {
            RobotAppCa robotAppCa = appCaDao.selectByPrimaryKey(appCa.getId());
            if (!robotAppCa.getAppCode().equals(appCa.getAppCode()) || !robotAppCa.getDeclareSystem().equals(appCa.getDeclareSystem())) {
                this.exitAppCa(appCa.getAppCode(), appCa.getDeclareSystem());
            }

            appCa.setSyncTime(null);
            appCa.setSyncMachineCode(null);
            appCa.setUpdateTime(new Date());
            appCaDao.updateByPrimaryKey(appCa);
        }
        Dict dict = Dict.create().set("appCaId", appCa.getId());
        customerCommandService.addCustomerCommand("appCa", JSON.toJSONString(dict), "ca证书");
        return 1;
    }

    @Override
    public RobotAppCaVO getById(Integer id) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", id);
        List<RobotAppCaVO> list = appCaDao.selectByParams(map);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public void againSync(Integer id) {
        appCaDao.againSync(id);
        if (id != null) {
            Dict dict = Dict.create().set("appCaId", id);
            customerCommandService.addCustomerCommand("appCa", JSON.toJSONString(dict), "ca证书");
        } else {
            List<RobotAppCa> allList = appCaDao.selectAll();
            for (RobotAppCa robotAppCa : allList) {
                Dict dict = Dict.create().set("appCaId", robotAppCa.getId());
                customerCommandService.addCustomerCommand("appCa", JSON.toJSONString(dict), "ca证书");
            }
        }
    }

    @Override
    public void disabled(RobotAppCa appCa) {
        RobotAppCa update = new RobotAppCa();
        update.setId(appCa.getId());
        update.setDisabled(appCa.getDisabled());
        appCaDao.updateByPrimaryKeySelective(update);
        appCaDao.againSync(appCa.getId());
        Dict dict = Dict.create().set("appCaId", appCa.getId());
        customerCommandService.addCustomerCommand("appCa", JSON.toJSONString(dict), "ca证书");
    }

    private void exitAppCa(String appCode, String declareSystem) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("appCode", appCode);
        params.put("declareSystem", declareSystem);
        List<RobotAppCaVO> list = appCaDao.selectByParams(params);
        if (CollectionUtils.isNotEmpty(list)) {
            RobotAppCaVO appCaVO = list.get(0);
            RobotAppVO appVO = rpaSaasService.getRobotDeclareType(appCaVO.getDeclareSystem());
            throw new BusinessException(appCaVO.getAppName() + "-" + appVO.getBusinessType() + " 已存在");
        }
    }
}
