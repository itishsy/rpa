package com.seebon.rpa.rest.design;

import cn.hutool.core.lang.Dict;
import com.seebon.rpa.device.PhysicalDeviceApiService;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.device.response.PhysicalDeviceResponse;
import com.seebon.rpa.entity.saas.po.declare.customer.CustomerBase;
import com.seebon.rpa.remote.RpaSaasService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Api(value = "物理设备", tags = "物理设备")
@RestController
@RequestMapping("/device")
public class RobotDeviceController {
    @Autowired
    private PhysicalDeviceApiService physicalDeviceApiService;
    @Autowired
    private RpaSaasService rpaSaasService;

    @ApiOperation(value = "物理设备-查询")
    @PostMapping("/list")
    public IgGridDefaultPage<PhysicalDeviceResponse> list(@RequestBody IgRequestObject reqObj) {
        Dict dict = RequestParamsUtil.toDict(reqObj);
        Integer companyId = dict.getInt("companyId");
        Integer type = dict.getInt("type");
        List<PhysicalDeviceResponse> list = physicalDeviceApiService.list(companyId, type);
        List<CustomerBase> customerList = rpaSaasService.listCustomer(false, "");
        Map<Integer, String> customerMap = customerList.stream().collect(Collectors.toMap(k -> k.getId(), v -> v.getName(), (x, y) -> x));
        list.stream().forEach(it -> {
            it.setCompanyName(customerMap.get(it.getCompanyId()));
        });
        return new IgGridDefaultPage<PhysicalDeviceResponse>(list, (int) list.size());
    }
}
