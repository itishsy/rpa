package com.seebon.rpa.device;

import com.seebon.rpa.entity.robot.device.request.UsbKeyRequest;
import com.seebon.rpa.entity.robot.device.response.PageResponse;
import com.seebon.rpa.entity.robot.device.response.UsbKeyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * "@FeignClient" 填当前服务名称
 */
@FeignClient(name = "device-manager", contextId = "usb-key-service", path = "/usbKey")
public interface UsbKeyApiService {

    @PostMapping("/list")
    PageResponse<UsbKeyResponse> list(@RequestBody UsbKeyRequest request);

    @PostMapping("/getBusIdList")
    List<UsbKeyResponse> getBusIdList(@RequestBody UsbKeyRequest request);
}
