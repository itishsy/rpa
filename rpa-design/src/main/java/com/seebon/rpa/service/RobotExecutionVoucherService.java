package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.RobotExecutionVoucher;
import com.seebon.rpa.entity.robot.vo.RobotExecutionVoucherVO;

import java.util.List;

public interface RobotExecutionVoucherService {

    List<RobotExecutionVoucher> listVoucher(RobotExecutionVoucherVO voucher);
}
