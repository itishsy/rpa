package com.seebon.rpa.rest;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.IgRequestObject;
import com.seebon.rpa.entity.auth.po.SysRole;
import com.seebon.rpa.entity.auth.vo.RoleResourceVO;
import com.seebon.rpa.service.ISysRoleService;
import com.seebon.rpa.utils.RequestParamsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 19:14:28
 */
@Slf4j
@RestController
@RequestMapping("/sys/role/")
@Api(value = "角色管理auth", tags = "角色管理auth")
public class SysRoleController {

    @Autowired
    private ISysRoleService sysRoleService;

    @ApiOperation(value = "角色管理-新增角色信息")
    @PostMapping(value = "/saveAdd")
    @ResponseBody
    @MyLog(moduleName = "角色管理", pageName = "角色管理-新增角色", operation = "新增角色")
    public int saveAdd(@ApiParam("保存的角色信息")@RequestBody SysRole role) {
        return sysRoleService.add(role);
    }

    @ApiOperation(value = "角色管理-修改角色信息")
    @PostMapping(value = "/updateRole")
    @ResponseBody
    @MyLog(moduleName = "角色管理", pageName = "角色管理-修改角色", operation = "修改角色")
    public int updateRole(@ApiParam("保存的角色信息")@RequestBody SysRole role) {
        return sysRoleService.update(role);
    }

    @ApiOperation(value = "角色管理-分页查询系统角色信息")
    @PostMapping(value = "/page")
    @ResponseBody
    public IgGridDefaultPage<SysRole> page(@RequestBody @ApiParam("分页查询对象")IgRequestObject reqObj) {
        Map<String,Object> params = RequestParamsUtil.toMap(reqObj);
        return sysRoleService.getRoleVOByPage(params);
    }

    @ApiOperation(value = "角色管理-获取全部角色")
    @PostMapping(value = "/getAll/{roleType}")
    @ResponseBody
    public List<SysRole> getAll(@ApiParam(name = "roleType", value = "角色类型：1管理后台角色，2客户端角色", required = true) @PathVariable("roleType")Integer roleType) {
        return sysRoleService.getAll(roleType);
    }

    @ApiOperation(value = "角色管理-获取系统角色菜单权限信息")
    @PostMapping(value = "/getRoleResource/{roleId}")
    @ResponseBody
    public List<RoleResourceVO> getRoleMenu(@ApiParam(name = "roleId", value = "角色id", required = true) @PathVariable("roleId")Integer roleId){
        return sysRoleService.getRoleResource(roleId);
    }

    @ApiOperation(value = "角色管理-保存角色菜单信息")
    @PostMapping(value = "/saveRoleResource")
    @ResponseBody
    @MyLog(moduleName = "角色管理", pageName = "角色管理-修改角色菜单信息", operation = "修改角色菜单信息")
    public void saveRoleMenu(@ApiParam(name = "roleId", value="角色id") Integer roleId,
                                 @ApiParam(name = "menuIds", value="分配的菜单ids，多个用英文逗号隔开")String resourceIds,
                                 @ApiParam(name = "buttonIds", value="分配的按钮ids，多个用英文逗号隔开")String buttonIds) throws Exception{
        sysRoleService.saveRoleResource(roleId, resourceIds, buttonIds);
    }

    @ApiOperation(value = "获取客户用户分配的角色")
    @PostMapping(value = "/getRolesByCustId/{custId}")
    public List<SysRole> getRolesByCustId(@PathVariable(value = "custId")Integer custId) {
        return sysRoleService.getRolesByCustId(custId);
    }

    @ApiOperation(value = "获取分配角色客户id")
    @PostMapping("/getCustIdsByRoleName")
    public List<Integer> getCustIdsByRoleName(@RequestParam("roleName") String roleName) {
        return sysRoleService.getCustIdsByRoleName(roleName);
    }

}
