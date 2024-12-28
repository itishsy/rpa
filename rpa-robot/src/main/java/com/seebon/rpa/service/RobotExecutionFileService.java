package com.seebon.rpa.service;

public interface RobotExecutionFileService {

    /**
     * 保存申报执行文件
     *
     * @return
     */
    void saveRobotExecutionFile(String flowCode, String stepCode);

    /**
     * 回传申报执行文件
     *
     * @return
     */
    Integer syncExecutionFile();
}
