package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.auth.po.SysRoleButton;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 20:22:16
 */
public interface SysRoleButtonDao extends Mapper<SysRoleButton>, MySqlMapper<SysRoleButton> {

    void saveRoleButton(@Param("roleId") Integer roleId, @Param("moduleCodes") List<String> moduleCodes);

    void removeCustOtherButton(@Param("roleIds")List<Integer> roleIds, @Param("buttonIds")List<Integer> buttonIds);
}
