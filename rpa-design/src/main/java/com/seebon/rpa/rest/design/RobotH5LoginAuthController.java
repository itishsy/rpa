package com.seebon.rpa.rest.design;

import com.google.common.collect.Maps;
import com.seebon.rpa.entity.robot.enums.LoginAuthProcessStatusEnum;
import com.seebon.rpa.entity.robot.vo.RobotLoginAuthVO;
import com.seebon.rpa.response.ResponseResult;
import com.seebon.rpa.service.RobotLoginAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(value = "h5网页接口", tags = "h5网页")
@RestController
@RequestMapping("/h5")
public class RobotH5LoginAuthController {


    @Autowired
    private RobotLoginAuthService robotLoginAuthService;

    /**
     * 通过手机号，查待登录列表，最大限制一百条
     * @param code 加密手机号
     */
    @GetMapping("/queryLoginAuthByCode")
    @ApiOperation(value = "通过手机号，查待登录列表，最大限制一百条")
    public List<RobotLoginAuthVO> queryLoginAuthByCode(@ApiParam(name = "code", value = "手机号", required = true) @RequestParam(value = "code")String code) {
        HashMap<String , Object> params = Maps.newHashMap();
        params.put("phoneNumber",code);
        params.put("processStatus", LoginAuthProcessStatusEnum.NO_LOGIN.getStatus());
        params.put("start", 0);
        params.put("size", 100);
        return robotLoginAuthService.queryList(params);
    }

    /**
     * 启动流程
     */
    @GetMapping("/startLoginAuth/{id}")
    @ApiOperation(value = "启动流程")
    public Boolean startLoginAuth(@ApiParam(name = "id", value = "集中登录id", required = true) @PathVariable("id") Integer id) {
        return robotLoginAuthService.startLogin(id);
    }

    /**
     * 查询集中登录处理流程状态
     */
    @GetMapping("/checkLoginAuthInfo")
    @ApiOperation(value = "查询集中登录处理流程状态")
    public Map<String, Object> checkLoginAuthInfo(@ApiParam("申报系统") String declareSystem, @ApiParam("社保、公积金号") String accountNumber,
                                                  @ApiParam("账户名") String orgName, @ApiParam("手机号")String phoneNumber) {
        return robotLoginAuthService.checkLoginAuthInfo(declareSystem,accountNumber,orgName,phoneNumber);
    }


}
