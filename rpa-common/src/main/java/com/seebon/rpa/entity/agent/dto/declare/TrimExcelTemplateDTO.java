package com.seebon.rpa.entity.agent.dto.declare;

import com.seebon.rpa.entity.agent.po.declare.employee.EmployeeSocialDeclareChangeSuppDetail;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author hao
 * @Date 2022/5/18 14:03
 * @Version 1.0
 **/
@Data
@Table(name = "employee_social_declare_change")
public class TrimExcelTemplateDTO implements Serializable {

    private static final long serialVersionUID = -5866018697507113166L;
    @Id
    @Column
    private Integer id;

    @Column
    private String employeeName;

    @Column
    private String idCard;


    private List<EmployeeSocialDeclareChangeSuppDetail> socialSuppDetails;


    private Map<String, Object> socialSuppMap = new HashMap<>();

    private Map<String, Object> fundSupMap = new HashMap<>();
}
