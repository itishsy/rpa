package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.common.config.MybatisMapper;
import com.seebon.rpa.entity.auth.po.SysResource;

import java.util.List;

public interface SysResourceDao extends MybatisMapper<SysResource> {

    List<SysResource> findByRole(String role);

}
