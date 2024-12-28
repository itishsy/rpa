package com.seebon.rpa.rest;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.auth.po.SysUser;
import com.seebon.rpa.entity.auth.vo.SysAdminUserVO;
import com.seebon.rpa.entity.auth.vo.SysUserDinDinVO;
import com.seebon.rpa.entity.auth.vo.SysUserVO;
import com.seebon.rpa.service.IMenuService;
import com.seebon.rpa.service.ISysUserService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/user")
@Api(value = "用户管理auth", tags = "用户管理auth")
public class SysUserController{

    @Autowired
    private IMenuService menuService;

    @Autowired
    private ISysUserService sysUserService;

    @ApiOperation(value = "用户管理-登录用户菜单权限")
    @PostMapping("/menu/listMenuByUser/{type}")
    public Map<String, Object> listMenuByUser(@ApiParam(name = "type", value = "入口类型1：运营后台，2：客户端", required = true)@PathVariable("type") Integer type){
        return menuService.queryMenuByUser(type);
    }

    @ApiOperation(value = "用户管理-校验用户唯一性")
    @PostMapping("/validate/{validateValue}")
    public String validateCustInfo(@ApiParam(name = "validateValue", value = "校验的值", required = true) @PathVariable("validateValue") String validateValue,
                                   @ApiParam(name = "id", value = "用户id，编辑时必填", required = false) @RequestParam("id")Integer id) {
        return sysUserService.validate(validateValue, id);
    }

    @ApiOperation(value = "客户端用户管理-保存账号信息")
    @PostMapping("/saveUser")
    @MyLog(moduleName = "客户端用户管理", pageName = "客户端用户管理", operation = "保存账号信息")
    public int saveUser(@ApiParam(name = "userVO", value = "用户信息", required = true)
            @RequestBody SysUserVO userVO) {
        return sysUserService.saveUser(userVO);
    }

    @PostMapping("/saveCustMainUser")
    public int saveCustMainUser(@ApiParam(name = "userVO", value = "用户信息", required = true)@RequestBody SysUserVO userVO){
        return sysUserService.saveCustMainUser(userVO);
    }

    @PostMapping("/updateCustMenuPermission/{custId}")
    public void updateCustMenuPermission(@ApiParam(name = "custId", value = "客户id", required = true)@PathVariable(value = "custId") Integer custId,
                                         @ApiParam(name = "moduleCodes", value = "服务模块code", required = true)@RequestBody List<String> moduleCodes) {
        sysUserService.updateCustMenuPermission(custId, moduleCodes);
    }

    @ApiOperation(value = "运营后端用户管理-保存账号信息")
    @PostMapping("/saveAdminUser")
    @MyLog(moduleName = "运营后端用户管理", pageName = "运营后端用户管理", operation = "保存账号信息")
    public int saveAdminUser(@ApiParam(name = "userVO", value = "用户信息", required = true)
                        @RequestBody SysUserVO userVO) {
        return sysUserService.saveAdminUser(userVO);
    }

    @ApiOperation(value = "客户端用户管理-重置用户密码")
    @PostMapping("/restPassword/{userId}")
    @MyLog(moduleName = "客户端用户管理", pageName = "客户端用户管理", operation = "重置用户密码")
    public int restPassword(@ApiParam(name = "userId", value = "用户id", required = true)
                            @PathVariable(value = "userId") Integer userId,
                            @ApiParam(name = "oldPassword", value = "旧密码", required = true)
                            @RequestParam(value = "oldPassword", required = true)String oldPassword,
                            @ApiParam(name = "nowPassword", value = "新密码", required = true)
                            @RequestParam(value = "nowPassword", required = true)String nowPassword) {
        return sysUserService.restPassword(userId, oldPassword, nowPassword);
    }

    @ApiOperation(value = "客户端用户管理-根据手机号码重置用户密码")
    @PostMapping("/restPasswordByPhoneNumber/{phoneNumber}")
    @MyLog(moduleName = "客户端用户管理", pageName = "客户端用户管理", operation = "重置用户密码")
    public int restPasswordByPhoneNumber(@ApiParam(name = "phoneNumber", value = "手机号码", required = true)
                            @PathVariable(value = "phoneNumber") String phoneNumber,
                            @ApiParam(name = "password", value = "新密码", required = true)
                            @RequestParam(value = "password", required = true)String password) {
        return sysUserService.restPasswordByPhoneNumber(phoneNumber, password);
    }

    @ApiOperation(value = "客户端用户管理-重置用户session维持时间")
    @PostMapping("/restExpiresIn/{userId}/{expiresIn}")
    @MyLog(moduleName = "客户端用户管理", pageName = "客户端用户管理", operation = "重置用户session维持时间")
    public int restExpiresIn(@ApiParam(name = "userId", value = "用户id", required = true)
                            @PathVariable(value = "userId") Integer userId,
                             @ApiParam(name = "expiresIn", value = "登录维持时间", required = true)
                             @PathVariable(value = "expiresIn") Integer expiresIn
                            ) {
        return sysUserService.restExpiresIn(userId, expiresIn);
    }

    @ApiOperation(value = "用户管理-账号列表查询")
    @PostMapping("/getCustUserList/{custId}")
    public List<SysUserVO> getCustUserList(@ApiParam(name = "custId", value = "客户id", required = true)@PathVariable(value = "custId") Integer custId,
                                           @ApiParam(name = "keyword", value = "关键字", required = false)@RequestParam("keyword")String keyword) {
        return sysUserService.getCustUserList(custId,keyword);
    }

    @ApiOperation(value = "运营后端用户管理-用户列表查询")
    @PostMapping("/selectAdminUserPage")
    public IgGridDefaultPage<SysAdminUserVO> selectAdminUserPage(@RequestBody IgRequestObject reqObj) {
        Map<String, Object> map = RequestParamsUtil.toMap(reqObj);
        return sysUserService.selectAdminUserPage(map);
    }

    @ApiOperation(value = "运营后端用户管理-获取用户信息")
    @PostMapping("/getAdminUser/{userId}")
    public SysAdminUserVO getAdminUser(@ApiParam(name = "userId", value = "用户id", required = true)@PathVariable(value = "userId") Integer userId) {
        return sysUserService.getAdminUser(userId);
    }

    @ApiOperation(value = "客户端用户管理-获取客户激活的子账户数")
    @PostMapping("/getCustActiveUserNumber/{custId}")
    public Integer getCustActiveUserNumber(@ApiParam(name = "custId", value = "客户id", required = true)@PathVariable(value = "custId") Integer custId) {
        return sysUserService.getCustActiveUserNumber(custId);
    }

    @ApiOperation(value = "用户管理-修改用户状态")
    @PostMapping("/updateUserStatus/{userId}/{status}")
    @MyLog(moduleName = "用户管理", pageName = "用户管理", operation = "修改用户状态")
    public int updateUserStatus(@ApiParam(name = "userId", value = "用户id", required = true)@PathVariable(value = "userId") Integer userId,
                                @ApiParam(name = "status", value = "状态，1：激活，0：注销", required = true)@PathVariable(value = "status") Integer status) {
        return sysUserService.updateUserStatus(userId, status);
    }

    @ApiOperation(value = "用户管理-修改用户状态")
    @PostMapping("/updateCustUserStatus/{custId}/{status}")
    @MyLog(moduleName = "用户管理", pageName = "用户管理", operation = "修改用户状态")
    public int updateCustUserStatus(@ApiParam(name = "custId", value = "客户id", required = true)@PathVariable(value = "custId") Integer custId,
                                @ApiParam(name = "status", value = "状态，1：激活，0：注销", required = true)@PathVariable(value = "status") Integer status) {
        return sysUserService.updateCustUserStatus(custId, status);
    }

    @ApiOperation(value = "获取用户信息")
    @PostMapping("/getSysUser/{userId}")
    public SysUser getSysUser(@ApiParam(name = "userId", value = "用户id", required = true)@PathVariable(value = "userId") Integer userId) {
        return sysUserService.getSysUser(userId);
    }

    @ApiOperation(value = "修改用户客户名称")
    @PostMapping("/updateUserCustName/{custId}")
    public void updateUserCustName(@ApiParam(name = "custId", value = "客户id", required = true)@PathVariable(value = "custId") Integer custId,
                                      @ApiParam(name = "custName", value = "客户名称", required = false)@RequestParam("custName")String custName) {
        sysUserService.updateUserCustName(custId, custName);
    }

    @ApiOperation(value = "获取系统所有用户信息")
    @PostMapping("/getAllSysUser")
    public List<SysUser> getAllSysUser() {
        return sysUserService.getAllSysUser();
    }

    @ApiOperation(value = "获取系统所有用户信息和钉钉手机号")
    @PostMapping("/getAllSysUserDinDinVO")
    public List<SysUserDinDinVO> getAllSysUserDinDinVO() {
        return sysUserService.getAllSysUserDinDinVO();
    }

    @ApiOperation(value = "获取用户拥有的角色")
    @PostMapping("/getUserRole/{userId}")
    public List<String> getUserRole(@ApiParam(name = "userId", value = "用户id", required = true)@PathVariable(value = "userId") Integer userId) {
        return sysUserService.getUserRole(userId);
    }

    @ApiOperation(value = "获取客户管理员账户信息")
    @PostMapping("/getMainUserByCustId/{custId}")
    public SysUser getMainUserByCustId(@PathVariable("custId") Integer custId) {
        return sysUserService.getMainUserByCustId(custId);
    }

    @ApiOperation(value = "绑定手机号码")
    @PostMapping("/bindPhoneNumber/{userId}/{phoneNumber}")
    public int bindPhoneNumber(@ApiParam(name = "userId", value = "用户id", required = true)@PathVariable("userId") Integer userId,
                               @ApiParam(name = "phoneNumber", value = "phoneNumber", required = true)@PathVariable("phoneNumber") String phoneNumber) {
        return sysUserService.bindPhoneNumber(userId, phoneNumber);
    }

    @PostMapping("/getAllCustUser")
    public List<SysUser> getAllCustUser() {
        return sysUserService.getAllCustUser();
    }

    @PostMapping("/getUserList/{userType}")
    public List<SysUser> getUserList(@ApiParam(name = "userType", value = "用户类型，1：运营用户，2：客户用户", required = true)
                                         @PathVariable(value = "userType") Integer userType) {
        return sysUserService.getUserList(userType);
    }

    @PostMapping("/getOperatorUserByRoleIds")
    public List<SysUser> getOperatorUserByRoleIds(@RequestBody List<Integer> roleIds) {
        return sysUserService.getOperatorUserByRoleIds(roleIds);
    }

    @PostMapping("/getSysUserByPhoneNumber/{phoneNumber}")
    public SysUser getSysUserByPhoneNumber(@PathVariable("phoneNumber") String phoneNumber) {
        return sysUserService.getSysUserByPhoneNumber(phoneNumber);
    }

}
