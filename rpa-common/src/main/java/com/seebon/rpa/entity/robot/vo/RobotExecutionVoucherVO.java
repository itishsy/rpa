package com.seebon.rpa.entity.robot.vo;

import com.seebon.rpa.entity.robot.RobotExecutionVoucher;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 客户机器人执行凭证
 */
@Data
public class RobotExecutionVoucherVO extends RobotExecutionVoucher {
    @ApiModelProperty(value = "申报类型 1：增员，2：减员，3：调基，5：补缴")
    private List<Integer> declareTypes;

    private String startMonth;

    private String endMonth;
}
