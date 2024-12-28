package com.seebon.rpa.device;

import com.seebon.rpa.entity.robot.device.request.ReadUsbContentRequest;
import com.seebon.rpa.entity.robot.device.response.PageResponse;
import com.seebon.rpa.entity.robot.device.response.ReadUsbContentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * "@FeignClient" 填当前服务名称
 */
@FeignClient(name = "device-manager", contextId = "read-usb-content-service", path = "/readUsbContent")
public interface ReadUsbContentApiService {

    @PostMapping("/list")
    PageResponse<ReadUsbContentResponse> list(@RequestBody ReadUsbContentRequest request);

    @PostMapping("/save")
    void save(@RequestBody ReadUsbContentRequest request);

    @PostMapping("/deleteById")
    void deleteById(@RequestParam Integer id);
}