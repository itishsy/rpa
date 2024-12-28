package com.seebon.rpa.entity.saas.vo;

import com.seebon.rpa.entity.saas.vo.system.SysUserDeclareAccountVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * TODO
 *
 * @author zjf
 * @describe
 * @date 2023-07-19 17:00
 */
@ApiModel("查询在职人员vo")
@Data
public class JobRequestVo {
    private List<SysUserDeclareAccountVO > userDeclareAccountList;

    private Integer custId;
}
