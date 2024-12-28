package com.seebon.rpa.entity.saas.vo.declare;

import com.seebon.rpa.entity.saas.po.declare.PaidInFieldBaseTpl;
import lombok.Data;

@Data
public class PaidInFieldBaseTplVO extends PaidInFieldBaseTpl {

    private String clientFileName;

    private String serverFilePath;

}
