package com.seebon.rpa.entity.saas.vo.system.customer;

import com.seebon.rpa.entity.saas.po.declare.customer.CustomerInterflow;
import lombok.Data;

/**
 * @Author hao
 * @Date 2023/2/16 17:43
 * @Version 1.0
 **/
@Data
public class CustomerInterflowVO extends CustomerInterflow {
    private Integer searchTimes;
}
