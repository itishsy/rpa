package com.seebon.rpa.rest.design;

import cn.hutool.core.lang.Dict;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.robot.RobotUsbKey;
import com.seebon.rpa.entity.robot.device.response.UsbKeyResponse;
import com.seebon.rpa.entity.robot.vo.RobotUsbKeyVO;
import com.seebon.rpa.service.RobotUsbKeyService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(value = "usb设备", tags = "usb设备")
@RestController
@RequestMapping("/usbKey")
public class RobotUsbKeyController {
    @Autowired
    private RobotUsbKeyService usbKeyService;

    @ApiOperation(value = "usb设备-分页")
    @PostMapping("/usbKeyPage")
    public IgGridDefaultPage<UsbKeyResponse> usbKeyPage(@RequestBody IgRequestObject reqObj) {
        Dict dict = RequestParamsUtil.toDict(reqObj);
        return usbKeyService.usbKeyPage(dict);
    }

    @ApiOperation(value = "usb账户-分页")
    @PostMapping("/usbAccountPage")
    public IgGridDefaultPage<RobotUsbKeyVO> usbAccountPage(@RequestBody IgRequestObject reqObj) {
        Dict dict = RequestParamsUtil.toDict(reqObj);
        return usbKeyService.usbAccountPage(dict);
    }

    @ApiOperation(value = "usb设备-查询")
    @GetMapping("/getByUsbKeyId")
    public List<RobotUsbKeyVO> getByUsbKeyId(@RequestParam("usbKeyId") Integer usbKeyId) {
        return usbKeyService.getByUsbKeyId(usbKeyId);
    }

    @ApiOperation(value = "usb设备-保存")
    @PostMapping("/save")
    public Integer save(@RequestBody List<RobotUsbKey> usbKeys) {
        return usbKeyService.save(usbKeys);
    }

    @ApiOperation(value = "usb设备-解绑")
    @PostMapping("/deleteById")
    public void deleteById(@RequestParam("id") Integer id) {
        usbKeyService.deleteById(id);
    }

    @ApiOperation(value = "usb设备-获取")
    @GetMapping("/getById")
    public RobotUsbKeyVO getById(@RequestParam("id") Integer id) {
        return usbKeyService.getById(id);
    }
}
