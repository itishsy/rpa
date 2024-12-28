package com.seebon.rpa.service.impl;

import com.google.common.collect.Maps;
import com.seebon.common.utils.CovertUtil;
import com.seebon.common.utils.JsonUtils;
import com.seebon.common.utils.UUIDGenerator;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.robot.CustomerCommand;
import com.seebon.rpa.entity.robot.vo.CustomerCommandVO;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.repository.mysql.CustomerCommandDao;
import com.seebon.rpa.repository.mysql.RobotClientAppDao;
import com.seebon.rpa.service.CustomerCommandService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CustomerCommandServiceImpl implements CustomerCommandService {
    @Autowired
    private CustomerCommandDao customerCommandDao;
    @Autowired
    private RobotClientAppDao robotClientAppDao;
    @Autowired
    private RpaSaasService rpaSaasService;

    @Override
    public List<CustomerCommandVO> listCustomerCommand(CustomerCommandVO commandVO) {
        Session session = SecurityContext.currentUser();
        Example example = new Example(CustomerCommand.class);
        example.orderBy("id").asc();
        example.createCriteria().andEqualTo("clientId", session.getCustId()).andEqualTo("status", 0);
        List<CustomerCommand> list = customerCommandDao.selectByExample(example);
        return CovertUtil.covertList(list, CustomerCommandVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void callbackCustomerCommand(List<CustomerCommandVO> commands) {
        if (CollectionUtils.isEmpty(commands)) {
            return;
        }
        for (CustomerCommandVO command : commands) {
            CustomerCommand update = new CustomerCommand();
            update.setId(command.getId());
            update.setStatus(command.getStatus());
            update.setRemark(command.getRemark());
            update.setFailReason(command.getFailReason());
            update.setSyncTime(new Date());
            update.setUpdateTime(new Date());
            customerCommandDao.updateByPrimaryKeySelective(update);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCustomerCommand(String command, String args, String remark) {

        if("component".equals(command)){
            // component 不同步到独立后台
            return;
        }

        Map<String,Object> params = Maps.newHashMap();
        if ("app".equals(command)) {
            params = (Map<String, Object>)JsonUtils.jsonToBean(args, Map.class);
        }
        List<Integer> custIds = robotClientAppDao.getCustIdsByParams(params);

        if (CollectionUtils.isNotEmpty(custIds)) {
            Session session = SecurityContext.currentUser();
            for (Integer custId : custIds) {

                CustomerBase customer = rpaSaasService.getCustomer(custId);
                if (customer == null || customer.getCategory()==null
                        || customer.getCategory() == 1) { // 如果客户为空或者客户类别不是独立部署的不需要做指令同步
                    continue;
                }

                CustomerCommand addCommand = new CustomerCommand();
                addCommand.setClientId(custId);
                addCommand.setUuid(UUIDGenerator.uuidStringWithoutLine());
                addCommand.setCommand(command);
                addCommand.setArgs(args);
                addCommand.setRemark(remark);
                addCommand.setStatus(0);
                addCommand.setCreateId((int) session.getId());
                addCommand.setCreateName(session.getName());
                addCommand.setCreateTime(new Date());
                addCommand.setUpdateTime(new Date());
                customerCommandDao.insertSelective(addCommand);
            }
        }
    }

    @Override
    public void addCustomerCommand(Integer custId, String command, String args, String remark) {

        if("component".equals(command)){
            // component 不同步到独立后台
            return;
        }

        Session session = SecurityContext.currentUser();
        CustomerCommand addCommand = new CustomerCommand();
        addCommand.setClientId(custId);
        addCommand.setUuid(UUIDGenerator.uuidStringWithoutLine());
        addCommand.setCommand(command);
        addCommand.setArgs(args);
        addCommand.setRemark(remark);
        addCommand.setStatus(0);
        addCommand.setCreateId((int) session.getId());
        addCommand.setCreateName(session.getName());
        addCommand.setCreateTime(new Date());
        addCommand.setUpdateTime(new Date());
        customerCommandDao.insertSelective(addCommand);
    }
}
