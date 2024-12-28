package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.auth.po.SysRoleMenu;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 20:18:21
 */
public interface SysRoleMenuDao extends Mapper<SysRoleMenu>, MySqlMapper<SysRoleMenu> {

    void saveRoleMenu(@Param("roleId")Integer roleId, @Param("moduleCodes")List<String> moduleCodes);

    void removeCustOtherMunu(@Param("roleIds")List<Integer> roleIds, @Param("moduleCodes")List<String> moduleCodes);

    List<Integer> selectCustRoleIds(@Param("moduleCode")String moduleCode);

}
