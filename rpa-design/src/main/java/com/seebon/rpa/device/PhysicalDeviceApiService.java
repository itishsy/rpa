package com.seebon.rpa.device;

import com.seebon.rpa.entity.robot.device.response.PhysicalDeviceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * "@FeignClient" 填当前服务名称
 */
@FeignClient(name = "device-manager", contextId = "physical-device-service", path = "/physicalDevice")
public interface PhysicalDeviceApiService {

    @GetMapping("/list")
    List<PhysicalDeviceResponse> list(@RequestParam(required = false) Integer companyId, @RequestParam(required = false) Integer type);
}
