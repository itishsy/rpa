package com.seebon.rpa.repository.mysql;

import com.seebon.rpa.entity.auth.po.SysButton;
import com.seebon.rpa.entity.auth.po.SysUser;
import com.seebon.rpa.entity.auth.vo.*;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

public interface SysUserDao extends Mapper<SysUser>, MySqlMapper<SysUser> {

    UserVO findUser(String username);

    List<String> findRoles(String username);

    List<SysButton> selectButtonsByUserId(@Param("userId") Integer userId, @Param("menuType")Integer menuType);

    List<UserMenuVO> selectMenuByUser(@Param("userId") Integer userId, @Param("menuType")Integer menuType);

    List<SysUserVO> getCustUserList(@Param("custId") Integer custId, @Param("keyword") String keyword);

    int selectCountByParams(Map<String,Object> map);

    List<SysAdminUserVO> selectAdminUserPage(Map<String,Object> map);

    SysAdminUserVO getAdminUser(@Param("userId") Integer userId);

    List<SysUser> getOperatorUserByRoleIds(@Param("roleIds") List<Integer> roleIds);

    Boolean existsEmail(@Param("custId") Integer custId, @Param("email") String email, @Param("eliminateUserId") Integer eliminateUserId);

    List<SysUserDinDinVO> selectSysUserDinDinVOAll();

}
