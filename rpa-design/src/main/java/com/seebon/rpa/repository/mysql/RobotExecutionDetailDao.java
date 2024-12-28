package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.robot.RobotExecutionDetail;
import com.seebon.rpa.entity.robot.dto.design.RobotExecutionFileInfo;
import tk.mybatis.mapper.common.Mapper;

public interface RobotExecutionDetailDao extends Mapper<RobotExecutionDetail> {

    /**
     * 保存执行明细文件信息
     *
     * @param robotFileInfo
     */
    void saveRobotFileInfo(RobotExecutionFileInfo robotFileInfo);
}
