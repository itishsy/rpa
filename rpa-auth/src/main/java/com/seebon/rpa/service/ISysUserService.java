package com.seebon.rpa.service;

import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.auth.po.SysUser;
import com.seebon.rpa.entity.auth.vo.SysAdminUserVO;
import com.seebon.rpa.entity.auth.vo.SysUserDinDinVO;
import com.seebon.rpa.entity.auth.vo.SysUserVO;
import com.seebon.rpa.entity.auth.vo.UserVO;

import java.util.List;
import java.util.Map;

public interface ISysUserService {

    int saveUser(SysUserVO userVO);

    String validate(String validateValue, Integer id);

    int updateUserStatus(Integer userId, Integer status);

    int updateCustUserStatus(Integer custId, Integer status);

    Integer getCustActiveUserNumber(Integer custId);

    SysUser getSysUser(Integer userId);

    List<SysUserVO> getCustUserList(Integer custId, String keyword);

    List<SysUser> getAllSysUser();

    List<SysUserDinDinVO> getAllSysUserDinDinVO();

    void updateUserCustName(Integer custId, String custName);

    int saveAdminUser(SysUserVO userVO);

    IgGridDefaultPage<SysAdminUserVO> selectAdminUserPage(Map<String,Object> map);

    SysAdminUserVO getAdminUser(Integer userId);

    int restPassword(Integer userId, String oldPassword, String nowPassword);

    int restPasswordByPhoneNumber(String phoneNumber, String password);

    int restExpiresIn(Integer userId, Integer expiresIn);

    UserVO findUser(String username);

    List<String> getUserRole(Integer userId);

    int saveCustMainUser(SysUserVO userVO);

    void updateCustMenuPermission(Integer custId, List<String> moduleCodes);

    SysUser getMainUserByCustId(Integer custId);

    List<SysUser> getAllCustUser();

    List<SysUser> getOperatorUserByRoleIds(List<Integer> roleIds);

    SysUser getSysUserByPhoneNumber(String phoneNumber);

    int bindPhoneNumber(Integer userId, String phoneNumber);

    List<SysUser> getUserList(Integer userType);

}
