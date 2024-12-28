package com.seebon.rpa.service;

public interface RobotArgsDefineService {

    Boolean haveTask();

    void bing(String port);

    String usbData();

    void initUsbPort();

    String usbOpen(String port);

    String copyFlowStep(String flowCode);

    String copyFlow(String sourceFlowCode, String targetFlowCode);

    String copyApp(String sourceAppCode, String targetAppCode);
}
