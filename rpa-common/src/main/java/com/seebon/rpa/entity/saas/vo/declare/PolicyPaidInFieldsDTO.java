package com.seebon.rpa.entity.saas.vo.declare;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class PolicyPaidInFieldsDTO implements Serializable {

    private PolicyPaidInSituationVO paidInSituationVO;

    private Map<String, Object> detailOne;

    private Integer mysqlId;

}
