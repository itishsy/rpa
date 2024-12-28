package com.seebon.rpa.entity.agent.dto.declare;

import com.seebon.rpa.entity.agent.dto.openApi.EmployeeDeclareInfoDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * 批量导入校验结果缓存表
 *
 * @Author xumin
 * @Date 2023/2/22 14:05
 */
@Getter
@Setter
@Document(collection = "employee_declare_result")
public class EmployeeDeclareResult extends EmployeeDeclareInfoDTO implements Serializable {
    private static final long serialVersionUID = 2297778768956995918L;

    @Transient
    private List<DeclareInfoDTO> declareInfoDTOS;

    @ApiModelProperty("参保地")
    private Integer addrId;

    @ApiModelProperty("aiId")
    private String aiId;
}
