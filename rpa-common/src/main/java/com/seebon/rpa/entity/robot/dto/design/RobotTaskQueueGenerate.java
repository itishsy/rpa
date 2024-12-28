package com.seebon.rpa.entity.robot.dto.design;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seebon.rpa.entity.robot.vo.RobotFlowVO;
import com.seebon.rpa.entity.robot.vo.RobotTaskGenerateQueueVO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "robot_task_queue_generate")
public class RobotTaskQueueGenerate implements Serializable {

    @Id
    private String id;

    @Indexed
    private String taskCode;

    /**
     * 任务code
     */
    private RobotTaskGenerateQueueVO taskVO;

    /**
     * 队列服务项
     */
    private Integer queueItem;

    /**
     * 流程信息
     */
    private List<RobotFlowVO> flowVOList;

    /**
     * 当前额外处理参数
     */
    private Object otherData;

    /**
     * 原因
     */
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Long timeStamp;

}
