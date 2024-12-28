package com.seebon.rpa.rest;

import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.entity.auth.dto.ResourceTreeDTO;
import com.seebon.rpa.entity.auth.po.SysButton;
import com.seebon.rpa.entity.auth.po.SysMenu;
import com.seebon.rpa.service.IMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 20:26:30
 */
@Slf4j
@RestController
@RequestMapping("/sys/menu/")
@Api(value = "菜单管理auth", tags = "菜单管理auth")
public class SysMenuController {

    @Autowired
    private IMenuService menuService;

    @ApiOperation(value = "菜单管理-获取系统菜单")
    @PostMapping(value = "/getAll/{menuType}")
    public List<ResourceTreeDTO> getAll(@ApiParam(name = "menuType", value = "菜单类型，1：运营后台菜单，2：客户端菜单", required = true)@PathVariable("menuType")Integer menuType) {
        return menuService.treeList(menuType);
    }


    @ApiOperation(value = "菜单管理-校验菜单名称是否唯一")
    @PostMapping(value = "/validateMenuName")
    public String validateMenuName(@ApiParam(name="name", value = "菜单名称", required = true) String name,
                                   @ApiParam(name="menuType", value = "菜单类型，1：运营后台菜单，2：客户端菜单", required = true) Integer menuType,
                                   @ApiParam(name = "id", value = "菜单id", required = false)Integer id) {
        return menuService.validateMenuName(name, menuType, id);
    }

    @ApiOperation(value = "菜单管理-新增菜单")
    @PostMapping(value = "/saveAdd")
    @MyLog(moduleName = "菜单管理", pageName = "菜单管理-新增菜单", operation = "新增菜单")
    public int saveAdd(@RequestBody @ApiParam("菜单信息") SysMenu menu) {
        return menuService.saveAdd(menu);
    }

    @ApiOperation(value = "菜单管理-修改菜单")
    @PostMapping(value = "/saveEdit")
    @MyLog(moduleName = "菜单管理", pageName = "菜单管理-修改菜单", operation = "修改菜单")
    public int saveEdit(@RequestBody @ApiParam("菜单信息") SysMenu menu) {
       return menuService.saveEdit(menu);
    }

    @ApiOperation(value = "菜单管理-删除菜单")
    @PostMapping(value = "/delete")
    @MyLog(moduleName = "菜单管理", pageName = "菜单管理-删除菜单", operation = "删除菜单")
    public int delete(@ApiParam(name = "ids", value = "菜单ids", required = true) @RequestParam(value = "ids") List<String> ids) {
        return menuService.deleteMenu(ids);
    }

    @ApiOperation(value = "菜单管理-调整菜单状态")
    @PostMapping(value = "/updateStatus/{menuId}/{status}")
    @MyLog(moduleName = "菜单管理", pageName = "菜单管理-调整菜单状态", operation = "调整菜单状态")
    public int updateStatus(@ApiParam(name = "menuId", value = "菜单id", required = true) @PathVariable(value = "menuId") Integer menuId,
                            @ApiParam(name = "status", value = "菜单状态，1：启用，0：停用", required = true) @PathVariable(value = "status") Integer status) {
        return menuService.updateStatus(menuId, status);
    }

    @ApiOperation(value = "菜单管理-根据菜单id获取菜单按钮")
    @PostMapping(value = "/buttonsByMenuId")
    public List<SysButton> getButtonsByMenuId(@ApiParam(name = "menuId", value = "菜单id", required = true)Integer menuId) {
        return menuService.getButtonsByMenuId(menuId);
    }

    @ApiOperation(value = "菜单管理-校验按钮编码是否已存在")
    @PostMapping(value = "/validButtonCode/{menuType}")
    public String validButtonCode(@ApiParam(name="code", value = "按钮编码", required = true) String code,
                                  @ApiParam(name = "menuType", value = "菜单类型，1：运营后台菜单，2：客户端菜单", required = true)
                                  @PathVariable("menuType") Integer menuType) {
        return menuService.validButtonCode(code, menuType);
    }

    @ApiOperation(value = "菜单管理-新增按钮")
    @PostMapping(value = "/addButton")
    @MyLog(moduleName = "菜单管理", pageName = "菜单管理-新增按钮", operation = "新增按钮")
    public int deleteButton(@RequestBody @ApiParam("按钮信息") SysButton button) {
        return menuService.addButton(button);
    }

    @ApiOperation(value = "菜单管理-删除按钮")
    @PostMapping(value = "/deleteButton")
    @MyLog(moduleName = "菜单管理", pageName = "菜单管理-删除按钮", operation = "删除按钮")
    public int deleteButton(@ApiParam(name = "buttonId", value = "按钮id", required = true)Integer buttonId) {
        return menuService.deleteButton(buttonId);
    }

    @ApiOperation(value = "菜单管理-调整菜单顺序")
    @PostMapping(value = "/reSort/{type}")
    @MyLog(moduleName = "菜单管理", pageName = "菜单管理-调整菜单顺序", operation = "调整菜单顺序")
    public void reSort(@ApiParam(name = "dto", value = "被调整的菜单", required = true)@RequestBody ResourceTreeDTO dto,
                           @ApiParam(name = "type", value = "顺序调整类型，1：下调，-1：上调", required = true)@PathVariable("type") Integer type){
        menuService.reSort(dto, type);
    }

}
