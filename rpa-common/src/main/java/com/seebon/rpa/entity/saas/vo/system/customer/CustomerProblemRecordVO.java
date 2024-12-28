package com.seebon.rpa.entity.saas.vo.system.customer;

import com.seebon.rpa.entity.saas.po.declare.customer.CustomerInterflow;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerProblemRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Author hao
 * @Date 2022/6/23 15:48
 * @Version 1.0
 **/
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CustomerProblemRecordVO extends CustomerProblemRecord {

    CustomerInterflow customerInterflow;

}
