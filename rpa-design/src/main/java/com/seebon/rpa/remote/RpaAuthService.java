package com.seebon.rpa.remote;

import com.seebon.rpa.entity.auth.po.SysUser;
import com.seebon.rpa.http.HttpService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@HttpService("${rpa.auth.server}")
public interface RpaAuthService {

    @PostMapping("/sys/user/getSysUser/{userId}")
    SysUser getSysUser(@PathVariable(value = "userId") Integer userId);

    @PostMapping("/sys/user/getOperatorUserByRoleIds")
    List<SysUser> getOperatorUserByRoleIds(@RequestBody List<Integer> roleIds);

    @PostMapping("/sys/user/getAllCustUser")
    List<SysUser> getAllCustUser();

}
