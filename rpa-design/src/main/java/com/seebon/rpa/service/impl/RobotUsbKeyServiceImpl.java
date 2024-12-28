package com.seebon.rpa.service.impl;

import cn.hutool.core.lang.Dict;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.device.UsbKeyApiService;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.RobotTaskSessionKeep;
import com.seebon.rpa.entity.robot.RobotUsbKey;
import com.seebon.rpa.entity.robot.device.request.UsbKeyRequest;
import com.seebon.rpa.entity.robot.device.response.PageResponse;
import com.seebon.rpa.entity.robot.device.response.UsbKeyResponse;
import com.seebon.rpa.entity.robot.vo.RobotAppVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskSessionKeepVO;
import com.seebon.rpa.entity.robot.vo.RobotUsbKeyVO;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.entity.saas.po.system.SysDataDict;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mysql.RobotTaskSessionKeepDao;
import com.seebon.rpa.repository.mysql.RobotUsbKeyDao;
import com.seebon.rpa.service.RobotUsbKeyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RobotUsbKeyServiceImpl implements RobotUsbKeyService {
    @Autowired
    private UsbKeyApiService usbKeyApiService;
    @Autowired
    private RobotUsbKeyDao usbKeyDao;
    @Autowired
    private RpaSaasService rpaSaasService;

    @Override
    public IgGridDefaultPage<UsbKeyResponse> usbKeyPage(Dict dict) {
        UsbKeyRequest request = new UsbKeyRequest();
        request.setIdVendor(dict.getInt("idVendor"));
        request.setIdProduct(dict.getInt("idProduct"));
        request.setStatus(dict.getInt("status"));
        request.setCompanyId(dict.getInt("clientId"));
        request.setKeyword(dict.getStr("keyword"));
        request.setPageStart(dict.getInt("start"));
        request.setPageSize(dict.getInt("size"));
        PageResponse<UsbKeyResponse> page = usbKeyApiService.list(request);
        if (CollectionUtils.isEmpty(page.getRows())) {
            return new IgGridDefaultPage<>();
        }
        List<CustomerBase> customerList = rpaSaasService.listCustomer(false, "");
        Map<Integer, String> customerMap = customerList.stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getName(), (x, y) -> x));
        List<Integer> usbKeyIds = page.getRows().stream().map(vo -> vo.getId()).collect(Collectors.toList());
        Map<Integer, List<RobotUsbKeyVO>> usbKeyMap = this.getUsbKey(usbKeyIds);
        page.getRows().stream().forEach(it -> {
            it.setCompanyName(customerMap.get(it.getCompanyId()));
            List<RobotUsbKeyVO> usbKeyList = usbKeyMap.getOrDefault(it.getId(), Lists.newArrayList());
            List<String> declareAccounts = Lists.newArrayList();
            for (RobotUsbKeyVO usbKey : usbKeyList) {
                RobotAppVO appVO = rpaSaasService.getRobotDeclareType(usbKey.getDeclareSystem());
                declareAccounts.add(usbKey.getAppName() + ":" + usbKey.getCompanyName() + "-" + usbKey.getDeclareAccount() + "【" + appVO.getBusinessType() + "】");
            }
            it.setBingNum(usbKeyList.size());
            it.setDeclareAccounts(declareAccounts.stream().collect(Collectors.joining(";")));
            it.setIdVendorStr(Integer.toHexString(this.unsignedInt(it.getIdVendor().shortValue())));
            it.setIdProductStr(Integer.toHexString(this.unsignedInt(it.getIdProduct().shortValue())));
        });
        return new IgGridDefaultPage<UsbKeyResponse>(page.getRows(), page.getRecords());
    }

    private int unsignedInt(short s) {
        return 0x0000ffff & s;
    }

    @Override
    public IgGridDefaultPage<RobotUsbKeyVO> usbAccountPage(Dict dict) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("clientId", dict.getInt("clientId"));
        params.put("declareSystem", dict.getStr("declareSystem"));
        params.put("keyword", dict.getStr("keyword"));
        Integer pageNum = dict.getInt("page");
        Integer size = dict.getInt("size");
        PageHelper.startPage(pageNum, size);
        Page<RobotUsbKeyVO> page = (Page<RobotUsbKeyVO>) usbKeyDao.selectByParams(params);
        if (CollectionUtils.isEmpty(page)) {
            return new IgGridDefaultPage<>();
        }
        List<CustomerBase> customerList = rpaSaasService.listCustomer(false, "");
        Map<Integer, String> customerMap = customerList.stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getName(), (x, y) -> x));
        Map<Integer, UsbKeyResponse> usbKeyMap = this.getUsbKeyMap(page.getResult());
        page.stream().forEach(it -> {
            it.setCustomerName(customerMap.get(it.getClientId()));
            RobotAppVO appVO = rpaSaasService.getRobotDeclareType(it.getDeclareSystem());
            if (appVO != null) {
                it.setDeclareSystemName(appVO.getBusinessType());
            }
            UsbKeyResponse usbKeyResponse = usbKeyMap.get(it.getUsbKeyId());
            if (usbKeyResponse != null) {
                it.setBaseHash(usbKeyResponse.getBaseHash());
                it.setContextHash(usbKeyResponse.getContextHash());
                it.setHostMacAddress(usbKeyResponse.getHostMacAddress());
                it.setStatus(usbKeyResponse.getStatus());
                it.setMountClient(usbKeyResponse.getMountClient());
            }
        });
        return new IgGridDefaultPage<RobotUsbKeyVO>(page.getResult(), (int) page.getTotal());
    }

    @Override
    public List<RobotUsbKeyVO> getByUsbKeyId(Integer usbKeyId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("usbKeyId", usbKeyId);
        List<RobotUsbKeyVO> list = usbKeyDao.selectByParams(params);
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        list.stream().forEach(it -> {
            RobotAppVO appVO = rpaSaasService.getRobotDeclareType(it.getDeclareSystem());
            if (appVO != null) {
                it.setDeclareSystemName(appVO.getBusinessType());
            }
        });
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer save(List<RobotUsbKey> usbKeys) {
        Session session = SecurityContext.currentUser();
        usbKeyDao.deleteByUsbKeyId(usbKeys.get(0).getUsbKeyId());
        for (RobotUsbKey usbKey : usbKeys) {
            this.checkUsbKey(usbKey);

            usbKey.setCreateId((int) session.getId());
            usbKey.setCreateName(session.getName());
            usbKeyDao.insertUsbKey(usbKey);
        }
        return 1;
    }

    @Override
    public RobotUsbKeyVO getById(Integer id) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", id);
        List<RobotUsbKeyVO> list = usbKeyDao.selectByParams(map);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        usbKeyDao.deleteByPrimaryKey(id);
    }

    @Override
    public UsbKeyResponse getUsbKey(RobotUsbKey usbKey) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("clientId", usbKey.getClientId());
        params.put("appCode", usbKey.getAppCode());
        params.put("taskCode", usbKey.getTaskCode());
        params.put("declareSystem", usbKey.getDeclareSystem());
        List<RobotUsbKeyVO> list = usbKeyDao.selectByParams(params);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        RobotUsbKeyVO usbKeyVO = list.get(0);
        UsbKeyRequest request = new UsbKeyRequest();
        request.setIds(Lists.newArrayList(usbKeyVO.getUsbKeyId()));
        PageResponse<UsbKeyResponse> response = usbKeyApiService.list(request);
        if (CollectionUtils.isEmpty(response.getRows())) {
            return null;
        }
        return response.getRows().get(0);
    }

    private Map<Integer, List<RobotUsbKeyVO>> getUsbKey(List<Integer> usbKeyIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("usbKeyIds", usbKeyIds);
        List<RobotUsbKeyVO> list = usbKeyDao.selectByParams(params);
        if (CollectionUtils.isEmpty(list)) {
            return Maps.newHashMap();
        }
        return list.stream().collect(Collectors.groupingBy(vo -> vo.getUsbKeyId()));
    }

    private void checkUsbKey(RobotUsbKey usbKey) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("clientId", usbKey.getClientId());
        params.put("appCode", usbKey.getAppCode());
        params.put("taskCode", usbKey.getTaskCode());
        params.put("declareSystem", usbKey.getDeclareSystem());
        List<RobotUsbKeyVO> list = usbKeyDao.selectByParams(params);
        if (CollectionUtils.isNotEmpty(list)) {
            RobotUsbKeyVO usbKeyVO = list.get(0);
            RobotAppVO appVO = rpaSaasService.getRobotDeclareType(usbKey.getDeclareSystem());
            throw new RuntimeException(usbKeyVO.getAppName() + " " + usbKey.getCompanyName() + "-" + usbKey.getDeclareAccount() + " " + appVO.getBusinessType() + " 已绑定.");
        }
    }

    private Map<Integer, UsbKeyResponse> getUsbKeyMap(List<RobotUsbKeyVO> list) {
        List<Integer> usbKeyIds = list.stream().map(vo -> vo.getUsbKeyId()).distinct().collect(Collectors.toList());
        UsbKeyRequest request = new UsbKeyRequest();
        request.setIds(usbKeyIds);
        PageResponse<UsbKeyResponse> response = usbKeyApiService.list(request);
        if (CollectionUtils.isNotEmpty(response.getRows())) {
            return response.getRows().stream().collect(Collectors.toMap(k -> k.getId(), v -> v, (x, y) -> x));
        }
        return Maps.newHashMap();
    }
}
