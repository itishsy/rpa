package com.seebon.rpa.entity.auth.vo;

import com.seebon.rpa.entity.auth.po.SysMenu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author hao
 * @Date 2022/11/21 14:52
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMenuVO extends SysMenu {

    private List<UserMenuVO> childMenu;
}
