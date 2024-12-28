package com.seebon.rpa.entity.saas.vo.declare.callBack;

import com.seebon.rpa.entity.saas.po.declare.callBack.CustomerCallBackServiceInfo;
import com.seebon.rpa.entity.saas.po.declare.callBack.CustomerDeclareChangeCallBackList;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@ApiModel("")
@Data
public class CustomerCallBackServiceInfoVO extends CustomerCallBackServiceInfo {

    private List<CustomerDeclareChangeCallBackList> callBackList;

}
