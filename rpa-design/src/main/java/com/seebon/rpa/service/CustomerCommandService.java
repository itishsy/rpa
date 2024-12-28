package com.seebon.rpa.service;

import com.seebon.rpa.entity.robot.vo.CustomerCommandVO;

import java.util.List;

public interface CustomerCommandService {

    List<CustomerCommandVO> listCustomerCommand(CustomerCommandVO commandVO);

    void callbackCustomerCommand(List<CustomerCommandVO> commands);

    void addCustomerCommand(String command, String args, String remark);

    void addCustomerCommand(Integer custId, String command, String args, String remark);
}
