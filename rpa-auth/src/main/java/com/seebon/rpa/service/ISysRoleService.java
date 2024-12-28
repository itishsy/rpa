package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.auth.po.SysRole;
import com.seebon.rpa.entity.auth.vo.RoleResourceVO;

import java.util.List;
import java.util.Map;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 19:17:30
 */
public interface ISysRoleService {

    int add(SysRole role);

    int update(SysRole role);

    IgGridDefaultPage<SysRole> getRoleVOByPage(Map<String, Object> params);

    List<RoleResourceVO> getRoleResource(Integer roleId);

    void saveRoleResource(Integer roleId, String resourceIds, String buttonIds);

    List<SysRole> getAll(Integer roleType);

    List<SysRole> getRolesByCustId(Integer custId);

    List<Integer> getCustIdsByRoleName(String roleName);
}
