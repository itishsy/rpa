package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.common.config.MybatisMapper;
import com.seebon.rpa.entity.auth.po.SysMenu;
import com.seebon.rpa.entity.auth.vo.RoleResourceVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface SysMenuDao extends MybatisMapper<SysMenu> {

    /**
     * 查询当前用户的菜单
     */
    List<SysMenu> getMenu(@Param("id") long id);

    List<RoleResourceVO> getRoleResources(Map<String,Object> params);

    Integer getMaxSortNumber(@Param("parentId") Integer parentId);
}
