package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.auth.po.SysUserRole;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 19:02:36
 */
public interface SysUserRoleDao extends Mapper<SysUserRole>, MySqlMapper<SysUserRole> {

    List<String> getUserRole(@Param("userId") Integer userId);

}
