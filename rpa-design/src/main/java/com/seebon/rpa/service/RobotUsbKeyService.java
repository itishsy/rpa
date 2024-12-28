package com.seebon.rpa.service;

import cn.hutool.core.lang.Dict;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.robot.RobotUsbKey;
import com.seebon.rpa.entity.robot.device.response.UsbKeyResponse;
import com.seebon.rpa.entity.robot.vo.RobotUsbKeyVO;

import java.util.List;

public interface RobotUsbKeyService {

    IgGridDefaultPage<UsbKeyResponse> usbKeyPage(Dict dict);

    IgGridDefaultPage<RobotUsbKeyVO> usbAccountPage(Dict dict);

    List<RobotUsbKeyVO> getByUsbKeyId(Integer usbKeyId);

    Integer save(List<RobotUsbKey> usbKeys);

    RobotUsbKeyVO getById(Integer id);

    void deleteById(Integer id);

    UsbKeyResponse getUsbKey(RobotUsbKey usbKey);
}
