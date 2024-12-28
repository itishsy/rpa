package com.seebon.rpa.service.impl;

import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.robot.RobotExecutionVoucher;
import com.seebon.rpa.entity.robot.vo.RobotExecutionVoucherVO;
import com.seebon.rpa.repository.mysql.RobotExecutionVoucherDao;
import com.seebon.rpa.service.RobotExecutionVoucherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Slf4j
@Service
public class RobotExecutionVoucherImpl implements RobotExecutionVoucherService {
    @Autowired
    private RobotExecutionVoucherDao executionVoucherDao;

    @Override
    public List<RobotExecutionVoucher> listVoucher(RobotExecutionVoucherVO voucher) {
        Session session = SecurityContext.currentUser();
        if (voucher.getClientId() == null) {
            voucher.setClientId(session.getCustId());
        }
        Example example = new Example(RobotExecutionVoucher.class);
        Example.Criteria ca = example.createCriteria();
        ca.andEqualTo("clientId", voucher.getClientId());
        ca.andEqualTo("addrName", voucher.getAddrName());
        ca.andEqualTo("businessType", voucher.getBusinessType());
        ca.andEqualTo("accountNumber", voucher.getAccountNumber());
        if (CollectionUtils.isNotEmpty(voucher.getDeclareTypes())) {
            ca.andIn("declareType", voucher.getDeclareTypes());
        }
        if (StringUtils.isNotBlank(voucher.getStartMonth())) {
            ca.andGreaterThanOrEqualTo("declareMonth", voucher.getStartMonth().substring(0, 7));
        }
        if (StringUtils.isNotBlank(voucher.getEndMonth())) {
            ca.andLessThanOrEqualTo("declareMonth", voucher.getEndMonth().substring(0, 7));
        }
        return executionVoucherDao.selectByExample(example);
    }
}
