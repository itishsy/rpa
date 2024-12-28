package com.seebon.rpa.rest;

import com.seebon.rpa.entity.auth.po.SysMenu;
import com.seebon.rpa.entity.auth.po.SysUser;
import com.seebon.rpa.entity.auth.vo.SysUserVO;
import com.seebon.rpa.entity.auth.vo.UserVO;
import com.seebon.rpa.service.ISysUserService;
import com.seebon.rpa.service.ResourceService;
import com.seebon.rpa.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Api(value = "登录用户", tags = "系统管理")
@Slf4j
public class UserController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private UserService userService;

    @Autowired
    private ISysUserService sysUserService;

    @ApiOperation(value = "当前用户")
    @RequestMapping(value = "/current", method = RequestMethod.POST)
    public UserVO user(Principal principal) {
        if(principal == null || principal.getName() ==null){
            throw new RuntimeException("用户未登录");
        }
        return sysUserService.findUser(principal.getName());
    }

    @ApiOperation(value = "新建用户")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public int addUser(@RequestParam("username") String username,
                    @RequestParam("password") String password,
                    @RequestParam("clientId") String clientId,
                    @RequestParam("name") String name) {
        log.info("接收创建用户参数：username=" + username + ",password=" +  password + ",clientId=" +  clientId + ",name=" +  name);
        return userService.create(clientId,username,password,name);
    }

    @ApiOperation(value = "修改用户密码")
    @RequestMapping(value = "/changePwd", method = RequestMethod.POST)
    public int resetPassword(@RequestParam("username") String username,
                       @RequestParam("password") String password) {
        return userService.updatePassword(username,password);
    }

    @GetMapping("/menu")
    @ApiOperation(value = "当前用户的菜单")
    public List<SysMenu> getMenu() {
        return userService.getMenu();
    }

    @GetMapping("/logout")
    @ApiOperation(value = "用户登出")
    public int logout() {
        return userService.logout();
    }

}
