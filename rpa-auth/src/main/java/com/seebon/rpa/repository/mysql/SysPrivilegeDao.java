package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.auth.po.SysPrivilege;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-29 10:40:26
 */
public interface SysPrivilegeDao extends Mapper<SysPrivilege>, MySqlMapper<SysPrivilege> {
}
