package com.seebon.rpa.entity.agent.po.declare.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author xumin
 * @Date 2023/2/21 14:12
 */
@Document(collection = "employee_declare_base")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDeclareBase implements Serializable {
    private static final long serialVersionUID = 3967325181800516298L;

    @ApiModelProperty("文件id")
    private Integer fileId;

    @ApiModelProperty("参保地名称")
    private Integer address;

    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ApiModelProperty("申报类型")
    private Integer declareType;

    @ApiModelProperty("员工申报信息")
    private List<Map<String, String>> declareInfo;
}
