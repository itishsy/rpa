package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.common.config.MybatisMapper;
import com.seebon.rpa.entity.auth.po.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleDao extends MybatisMapper<SysRole> {

    List<SysRole> findAll();

    List<SysRole> findByUser(String username);

    List<String> findCodeByUser(String username);

    List<SysRole> getRolesByCustId(@Param("custId") Integer custId);

    List<Integer> getCustIdsByRoleName(@Param("roleName")String roleName);

}
