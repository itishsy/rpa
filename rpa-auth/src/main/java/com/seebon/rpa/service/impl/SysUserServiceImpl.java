package com.seebon.rpa.service.impl;

import com.google.common.collect.Lists;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.auth.vo.SysUserDinDinVO;
import com.seebon.rpa.utils.EncryptUtil;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.auth.enums.UserTypeEnum;
import com.seebon.rpa.entity.auth.po.*;
import com.seebon.rpa.entity.auth.vo.SysAdminUserVO;
import com.seebon.rpa.entity.auth.vo.SysUserVO;
import com.seebon.rpa.entity.auth.vo.UserVO;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.repository.redis.UserSessionDao;
import com.seebon.rpa.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysUserServiceImpl implements ISysUserService {

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysButtonDao sysButtonDao;

    @Autowired
    private SysUserRoleDao sysUserRoleDao;

    @Autowired
    private SysRoleDao sysRoleDao;

    @Autowired
    private SysRoleMenuDao sysRoleMenuDao;

    @Autowired
    private SysRoleButtonDao sysRoleButtonDao;

    @Autowired
    private SysPrivilegeDao sysPrivilegeDao;

    @Autowired
    private UserSessionDao userSessionDao;

    @Override
    public void updateCustMenuPermission(Integer custId, List<String> moduleCodes) {
        Session session = SecurityContext.currentUser();
        Example example = new Example(SysRole.class);
        example.createCriteria().andEqualTo("custId", custId).andEqualTo("mainRole", 1);
        SysRole sysRole = sysRoleDao.selectOneByExample(example);
        if (sysRole == null) {
            sysRole = new SysRole();
            sysRole.setRoleType(2);
            sysRole.setMainRole(1);
            sysRole.setCode("client");
            sysRole.setCustId(custId);
            sysRole.setName("系统管理员");
            sysRole.setStatus(1);
            sysRole.setCreateId((int)session.getId());
            sysRole.setCreateTime(new Date());
            sysRoleDao.insert(sysRole);

            SysPrivilege sp = new SysPrivilege();
            sp.setRoleId(sysRole.getId());
            sp.setResourceId(1);
            sysPrivilegeDao.insert(sp);
        } else {
            Example example1 = new Example(SysRoleMenu.class);
            example1.createCriteria().andEqualTo("roleId", sysRole.getId());
            sysRoleMenuDao.deleteByExample(example1);

            example1.clear();
            example1 = new Example(SysRoleButton.class);
            example1.createCriteria().andEqualTo("roleId", sysRole.getId());
            sysRoleButtonDao.deleteByExample(example1);
        }

        // 保存客户角色菜单权限 默认有首页跟系统管理
        if (CollectionUtils.isEmpty(moduleCodes)) {
            moduleCodes = Lists.newArrayList("10024004","10024005");
        } else {
            moduleCodes.addAll(Lists.newArrayList("10024004","10024005"));
        }
        //菜单权限
        sysRoleMenuDao.saveRoleMenu(sysRole.getId(), moduleCodes);
        //按钮权限
        sysRoleButtonDao.saveRoleButton(sysRole.getId(), moduleCodes);

        Example e = new Example(SysRole.class);
        e.createCriteria().andEqualTo("custId", custId).andNotEqualTo("mainRole", 1);
        List<SysRole> sysRoles = sysRoleDao.selectByExample(e);
        if (CollectionUtils.isNotEmpty(sysRoles)) {
            List<Integer> roleIds = sysRoles.stream().map(it -> it.getId()).collect(Collectors.toList());
            // 删除客户其他角色不在赋权范围的菜单和按钮权限数据
            sysRoleMenuDao.removeCustOtherMunu(roleIds, moduleCodes);

            List<Integer> buttonIds = sysButtonDao.selectButtonIdsByNotModuleCodes(moduleCodes);
            if (CollectionUtils.isNotEmpty(buttonIds)) {
                sysRoleButtonDao.removeCustOtherButton(roleIds, buttonIds);
            }
        }
    }

    @Override
    public SysUser getMainUserByCustId(Integer custId) {
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("custId", custId).andEqualTo("userType", 2).andEqualTo("mainUser", 1);
        example.setOrderByClause(" id desc limit 1");
        SysUser sysUser = sysUserDao.selectOneByExample(example);
        if (sysUser != null) {
            sysUser.setPassword(null);
        }
        return sysUser;
    }

    @Override
    public List<SysUser> getAllCustUser() {
        return getUserList(2);
    }

    @Override
    public List<SysUser> getUserList(Integer userType) {
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("userType", userType).andEqualTo("userCategory", 1);
        return sysUserDao.selectByExample(example);
    }

    @Override
    public List<SysUser> getOperatorUserByRoleIds(List<Integer> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Lists.newArrayList();
        }
        List<SysUser> users = sysUserDao.getOperatorUserByRoleIds(roleIds);
        return users;
    }

    @Override
    public SysUser getSysUserByPhoneNumber(String phoneNumber) {
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("phoneNumber", phoneNumber).
                andEqualTo("userType", UserTypeEnum.ClientUser.getCode());
        try {
            SysUser sysUser = sysUserDao.selectOneByExample(example);
            return sysUser;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int bindPhoneNumber(Integer userId, String phoneNumber) {

        SysUser sysUser = sysUserDao.selectByPrimaryKey(userId);
        if (sysUser == null) {
            throw new BusinessException("未找到账户信息！");
        }

        String s = validateUserPhoneNumber(phoneNumber, userId, sysUser.getUserType());
        if (StringUtils.isNotBlank(s)) {
            throw new BusinessException(s);
        }
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPhoneNumber(phoneNumber);
        return sysUserDao.updateByPrimaryKeySelective(user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveCustMainUser(SysUserVO userVO) {
        Session session = SecurityContext.currentUser();
        String userValidate = validate(userVO.getUsername(),  null);
        if (StringUtils.isNotBlank(userValidate)) {
            throw new BusinessException(userValidate);
        }
        SysUser sysUser = new SysUser();
        sysUser.setClientId("rpa");
        sysUser.setName(StringUtils.isBlank(userVO.getName())?userVO.getUsername():userVO.getName());
        sysUser.setUsername(userVO.getUsername());
        sysUser.setCustId(userVO.getCustId());
        sysUser.setCustName(userVO.getCustName());
        sysUser.setUserType(UserTypeEnum.ClientUser.getCode());
        sysUser.setStatus(1);
        sysUser.setMainUser(1);
        sysUser.setPhoneNumber(userVO.getPhoneNumber());
        sysUser.setEmail(userVO.getEmailsStr());
        sysUser.setComment(userVO.getComment());
        sysUser.setUserCategory(userVO.getUserCategory()==null?1:userVO.getUserCategory());
        if (userVO.getExpiresIn() != null) {
            sysUser.setExpiresIn(userVO.getExpiresIn());
        }
        sysUser.setCreateId((int)session.getId());
        sysUser.setCreateTime(new Date());
        try {
            String password = EncryptUtil.aesDecrypt(userVO.getPassword());
            sysUser.setPassword(new BCryptPasswordEncoder().encode(password));
        }catch (Exception e){
            log.error("==========密码解密出错：{}", e.getMessage(), e);
            throw new BusinessException("保存出错，请联系管理员处理！");
        }
        sysUserDao.insert(sysUser);

        SysUser sysUserRobot = new SysUser();
        sysUserRobot.setUsername(userVO.getUsername().concat("robot"));
        sysUserRobot.setName("robot");
        sysUserRobot.setCustId(userVO.getCustId());
        sysUserRobot.setCustName(userVO.getCustName());
        String pw = null;
        try {
            pw = EncryptUtil.aesEncrypt("robot996");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常");
        }
        sysUserRobot.setPassword(pw);
        sysUserRobot.setUserType(2);
        sysUserRobot.setUserCategory(2);
        sysUserRobot.setExpiresIn(null);
        sysUserRobot.setComment("机器人盒子登录账户");
        sysUserRobot.setCreateId((int)session.getId());
        sysUserRobot.setCreateTime(new Date());
        try {
            sysUserDao.insert(sysUserRobot);
        }catch (Exception e) {
            log.error("======新增用户异常信息：{}", e.getMessage(), e);
            if (e instanceof BusinessException) {
                throw e;
            } else {
                throw new BusinessException("系统异常");
            }
        }

        // 创建客户管理员角色
        SysRole sysRole = new SysRole();
        sysRole.setRoleType(2);
        sysRole.setMainRole(1);
        sysRole.setCode("client");
        sysRole.setCustId(userVO.getCustId());
        sysRole.setName("系统管理员");
        sysRole.setStatus(1);
        sysRole.setCreateId((int)session.getId());
        sysRole.setCreateTime(new Date());
        sysRoleDao.insert(sysRole);

        SysPrivilege sp = new SysPrivilege();
        sp.setRoleId(sysRole.getId());
        sp.setResourceId(1);
        sysPrivilegeDao.insert(sp);

        List<String> moduleCodes = userVO.getModuleCodes();
        // 保存客户角色菜单权限 默认有首页跟系统管理
        if (CollectionUtils.isEmpty(moduleCodes)) {
            moduleCodes = Lists.newArrayList("10024004","10024005");
        } else {
            moduleCodes.addAll(Lists.newArrayList("10024004","10024005"));
        }
        //菜单权限
        sysRoleMenuDao.saveRoleMenu(sysRole.getId(), moduleCodes);
        //按钮权限
        sysRoleButtonDao.saveRoleButton(sysRole.getId(), moduleCodes);

        // 保存用户角色信息
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(sysUser.getId());
        userRole.setRoleId(sysRole.getId());
        sysUserRoleDao.insert(userRole);

        return sysUser.getId();
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveUser(SysUserVO userVO) {
        Integer userId = userVO.getId();
        String userValidate = validate(userVO.getUsername(),  userId);
        if (StringUtils.isNotBlank(userValidate)) {
            throw new BusinessException(userValidate);
        }
        Session session = SecurityContext.currentUser();
        if (userId == null) { // 新增用户账号
            String s = validateUserPhoneNumber(userVO.getPhoneNumber(), userId, UserTypeEnum.ClientUser.getCode());
            // 没异常继续校验邮箱
            s = StringUtils.isNotBlank(s) ? s : validateUserEmail(userVO.getEmails(),session.getCustId(),userId);
            if (StringUtils.isNotBlank(s)) {
                throw new BusinessException(s);
            }
            SysUser sysUser = new SysUser();
            sysUser.setClientId("rpa");
            sysUser.setName(StringUtils.isBlank(userVO.getName())?userVO.getUsername():userVO.getName());
            sysUser.setUsername(userVO.getUsername());
            sysUser.setCustId(session.getCustId());
            sysUser.setCustName(session.getCustName());
            sysUser.setUserType(UserTypeEnum.ClientUser.getCode());
            sysUser.setStatus(1);
            sysUser.setMainUser(0);
            sysUser.setPhoneNumber(userVO.getPhoneNumber());
            sysUser.setEmail(userVO.getEmailsStr());
            sysUser.setComment(userVO.getComment());
            sysUser.setUserCategory(userVO.getUserCategory()==null?1:userVO.getUserCategory());
            if (userVO.getExpiresIn() != null) {
                sysUser.setExpiresIn(userVO.getExpiresIn());
            }
            sysUser.setCreateId((int)session.getId());
            sysUser.setCreateTime(new Date());
            try {
                String password = EncryptUtil.aesDecrypt(userVO.getPassword());
                sysUser.setPassword(new BCryptPasswordEncoder().encode(password));
            }catch (Exception e){
                log.error("==========密码解密出错：{}", e.getMessage(), e);
                throw new BusinessException("保存出错，请联系管理员处理！");
            }
            sysUserDao.insert(sysUser);
            userId = sysUser.getId();
        } else { // 编辑用户账号

            Example e = new Example(SysUser.class);
            e.createCriteria().andEqualTo("custId", session.getCustId()).andEqualTo("id", userId);
            SysUser sysUserEdit = sysUserDao.selectOneByExample(e);

            if (sysUserEdit == null) {
                throw new BusinessException("未能找到需要修改的账户信息");
            }
            if (StringUtils.isNotBlank(userVO.getPhoneNumber())) {
                if (StringUtils.isBlank(sysUserEdit.getPhoneNumber()) || !sysUserEdit.getPhoneNumber().equals(userVO.getPhoneNumber())) {
                    String s = validateUserPhoneNumber(userVO.getPhoneNumber(), userId, UserTypeEnum.ClientUser.getCode());
                    if (StringUtils.isNotBlank(s)) {
                        throw new BusinessException(s);
                    }
                }
                sysUserEdit.setPhoneNumber(userVO.getPhoneNumber());
            }
            // 校验邮箱
            String s = validateUserEmail(userVO.getEmails(),session.getCustId(),userId);
            if (StringUtils.isNotBlank(s)) {
                throw new BusinessException(s);
            }
            sysUserEdit.setUsername(userVO.getUsername());
            sysUserEdit.setName(userVO.getName());
            String password = userVO.getPassword();
            if (StringUtils.isNotBlank(password)) {
                try {
                    password = EncryptUtil.aesDecrypt(password);
                    sysUserEdit.setPassword(new BCryptPasswordEncoder().encode(password));
                }catch (Exception ex){
                    log.error("==========密码解密出错：{}", ex.getMessage(), ex);
                    throw new BusinessException("保存出错，请联系管理员处理！");
                }
//                sysUserEdit.setPassword(new BCryptPasswordEncoder().encode(userVO.getPassword()));
            }
            sysUserEdit.setComment(userVO.getComment());
//            sysUserEdit.setStatus(userVO.getStatus());

            sysUserEdit.setEmail(userVO.getEmailsStr());
            sysUserEdit.setUpdateId((int)session.getId());
            sysUserEdit.setUpdateTime(new Date());
            sysUserDao.updateByPrimaryKey(sysUserEdit);
        }
        Example example = new Example(SysUserRole.class);
        example.createCriteria().andEqualTo("userId", userId);
        sysUserRoleDao.deleteByExample(example);

        example.clear();
        example = new Example(SysRole.class);
        example.createCriteria().andEqualTo("custId", session.getCustId()).andEqualTo("mainRole", 1);
        SysRole sysRole = sysRoleDao.selectOneByExample(example);
        if (sysRole != null) {
            SysUserRole userRole = new SysUserRole();
            userRole.setRoleId(sysRole.getId());
            userRole.setUserId(userId);
            sysUserRoleDao.insert(userRole);
        }
        if (userVO.getId()!=null && StringUtils.isNotBlank(userVO.getPassword())) {
            userSessionDao.removeUser(userVO.getUsername());
        }
        return userId;
    }

    private String validateUserEmail(List<String> emails, Integer custId, Integer userId) {
        if (CollectionUtils.isNotEmpty(emails)) {
            // 判断本身多邮箱地址是否重复
            if(emails.parallelStream().distinct().count() != Integer.valueOf(emails.size()).longValue()){
                throw new BusinessException("电子邮箱存在重复");
            }
            List<String> existsEmail = Lists.newArrayList();
            for (String email : emails) {
                Boolean b = sysUserDao.existsEmail(custId,email,userId);
                if (b) {
                    existsEmail.add(email);
                }
            }
            if (CollectionUtils.isNotEmpty(existsEmail)) {
                throw new BusinessException("电子邮箱" + existsEmail.toString() + "已被其他用户绑定，请修改电子邮箱");
            }
        }
        return null;
    }

    private String validateUserPhoneNumber(String phoneNumber, Integer userId, Integer userType) {
        if (StringUtils.isBlank(phoneNumber)) {
            throw new BusinessException("参数错误：phoneNumber必填");
        }
        String validateResult = "";
        Example example = new Example(SysUser.class);
        if (userId == null) {
            example.createCriteria().andEqualTo("phoneNumber", phoneNumber).andEqualTo("userType", userType);
        } else {
            example.createCriteria().andEqualTo("phoneNumber", phoneNumber).andEqualTo("userType", userType).andNotEqualTo("id", userId);
        }
        int count = sysUserDao.selectCountByExample(example);
        if (count > 0) {
            validateResult = String.format("手机号码%s已被其他用户绑定，请修改手机号码", phoneNumber);
        }
        return validateResult;
    }

    @Override
    public int saveAdminUser(SysUserVO userVO) {
        Integer userId = userVO.getId();
        String userValidate = validate(userVO.getUsername(),  userId);
        if (StringUtils.isNotBlank(userValidate)) {
            throw new BusinessException(userValidate);
        }
        Session session = SecurityContext.currentUser();
        if (userId == null) { // 新增用户账号
            String s = validateUserPhoneNumber(userVO.getPhoneNumber(), userId, UserTypeEnum.AdminUser.getCode());
            if (StringUtils.isNotBlank(s)) {
                throw new BusinessException(s);
            }

            SysUser sysUser = new SysUser();
            sysUser.setClientId("rpa");
            sysUser.setName(StringUtils.isBlank(userVO.getName())?userVO.getUsername():userVO.getName());
            sysUser.setUsername(userVO.getUsername());
            sysUser.setUserType(UserTypeEnum.AdminUser.getCode());
            sysUser.setStatus(userVO.getStatus());
            sysUser.setCustId(0);
            sysUser.setPhoneNumber(userVO.getPhoneNumber());
            sysUser.setEmail(userVO.getEmailsStr());
            sysUser.setComment(userVO.getComment());
            String password = userVO.getPassword();
            sysUser.setCreateId((int)session.getId());
            sysUser.setUserCategory(1);
            sysUser.setCreateTime(new Date());
            try {
                password = EncryptUtil.aesDecrypt(password);
                sysUser.setPassword(new BCryptPasswordEncoder().encode(password));
            }catch (Exception ex){
                log.error("==========密码解密出错：{}", ex.getMessage(), ex);
                throw new BusinessException("保存出错，请联系管理员处理！");
            }
            sysUserDao.insert(sysUser);
            userId = sysUser.getId();
        } else { // 编辑用户账号
            SysUser sysUserEdit = sysUserDao.selectByPrimaryKey(userId);
            if (sysUserEdit == null) {
                throw new BusinessException("未能找到需要修改的账户信息");
            }
            if (StringUtils.isNotBlank(userVO.getPhoneNumber())) {
                if (StringUtils.isBlank(sysUserEdit.getPhoneNumber()) || !sysUserEdit.getPhoneNumber().equals(userVO.getPhoneNumber())) {
                    String s = validateUserPhoneNumber(userVO.getPhoneNumber(), userId, UserTypeEnum.AdminUser.getCode());
                    if (StringUtils.isNotBlank(s)) {
                        throw new BusinessException(s);
                    }
                }
                sysUserEdit.setPhoneNumber(userVO.getPhoneNumber());
            }
            sysUserEdit.setUsername(userVO.getUsername());
            sysUserEdit.setName(userVO.getName());
            String password = userVO.getPassword();
            if (StringUtils.isNotBlank(password)) {
                try {
                    password = EncryptUtil.aesDecrypt(password);
                    sysUserEdit.setPassword(new BCryptPasswordEncoder().encode(password));
                }catch (Exception ex){
                    log.error("==========密码解密出错：{}", ex.getMessage(), ex);
                    throw new BusinessException("保存出错，请联系管理员处理！");
                }
            }
            sysUserEdit.setComment(userVO.getComment());
            sysUserEdit.setEmail(userVO.getEmailsStr());
            sysUserEdit.setStatus(userVO.getStatus());
            sysUserEdit.setUpdateId((int)session.getId());
            sysUserEdit.setUpdateTime(new Date());
            sysUserDao.updateByPrimaryKey(sysUserEdit);
            if (userVO.getStatus() == 0) { //注销
                userSessionDao.removeUser(sysUserEdit.getUsername());
            }
        }
        Example example = new Example(SysUserRole.class);
        example.createCriteria().andEqualTo("userId", userId);
        sysUserRoleDao.deleteByExample(example);
        List<SysRole> roles = userVO.getRoles();
        if (CollectionUtils.isNotEmpty(roles)) {
            final int fUserId = userId;
            List<SysUserRole> sysUserRoles = roles.stream().map(role -> {
                SysUserRole userRole = new SysUserRole();
                userRole.setRoleId(role.getId());
                userRole.setUserId(fUserId);
                return userRole;
            }).collect(Collectors.toList());
            sysUserRoleDao.insertList(sysUserRoles);
        }
        return userId;
    }

    @Override
    public IgGridDefaultPage<SysAdminUserVO> selectAdminUserPage(Map<String, Object> map) {
        map.put("userType", 1);
        String keyword = (String)map.get("keyword");
        if (StringUtils.isNotBlank(keyword)) {
            keyword = keyword.replaceAll("%", "/%");
            map.put("keyword", keyword);
        }
        int count = sysUserDao.selectCountByParams(map);
        List<SysAdminUserVO> list = Lists.newArrayList();
        if (count > 0) {
            list = sysUserDao.selectAdminUserPage(map);
        }
        return new IgGridDefaultPage<>(list, count);
    }

    @Override
    public SysAdminUserVO getAdminUser(Integer userId) {
        return sysUserDao.getAdminUser(userId);
    }

    @Override
    public int restPassword(Integer userId, String oldPassword, String nowPassword) {

        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(nowPassword)) {
            throw new BusinessException("请输入旧密码和新密码！");
        }

        SysUser sysUser = sysUserDao.selectByPrimaryKey(userId);
        if (sysUser == null) {
            throw new BusinessException("用户不存在，请核对！");
        }
        try {
            oldPassword = EncryptUtil.aesDecrypt(oldPassword);
        }catch (Exception ex){
            log.error("==========密码解密出错：{}", ex.getMessage(), ex);
            throw new BusinessException("保存出错，请联系管理员处理！");
        }
        if (!new BCryptPasswordEncoder().matches(oldPassword, sysUser.getPassword())) {
            throw new BusinessException("旧密码不正确！");
        }

        try {
            nowPassword = EncryptUtil.aesDecrypt(nowPassword);
        }catch (Exception ex){
            log.error("==========密码解密出错：{}", ex.getMessage(), ex);
            throw new BusinessException("保存出错，请联系管理员处理！");
        }
        if (oldPassword.equals(nowPassword)) {
            throw new BusinessException("新密码不能与旧密码相同！");
        }

        SysUser user = new SysUser();
        user.setId(userId);
        nowPassword = new BCryptPasswordEncoder().encode(nowPassword);
        user.setPassword(nowPassword);
        Session session = SecurityContext.currentUser();
        user.setUpdateId((int)session.getId());
        user.setUpdateTime(new Date());
        int count = sysUserDao.updateByPrimaryKeySelective(user);
        userSessionDao.removeUser(sysUser.getUsername());
        return count;
    }

    @Override
    public int restPasswordByPhoneNumber(String phoneNumber, String password) {
        if (StringUtils.isBlank(password)) {
            throw new BusinessException("请输入密码！");
        }
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("phoneNumber", phoneNumber);
        List<SysUser> users = sysUserDao.selectByExample(example);
        if (CollectionUtils.isEmpty(users)) {
            throw new BusinessException(String.format("%s该手机号码未绑定账户，请核实！"));
        }
        if (users.size() > 1) {
            throw new BusinessException(String.format("%s该手机号码绑定了多个账户，请联系管理处理！"));
        }

        String oldPassword = users.get(0).getPassword();

        try {
            password = EncryptUtil.aesDecrypt(password);
        }catch (Exception ex){
            log.error("==========密码解密出错：{}", ex.getMessage(), ex);
            throw new BusinessException("保存出错，请联系管理员处理！");
        }

        if (new BCryptPasswordEncoder().matches(password, oldPassword)) {
            throw new BusinessException("新密码不能与旧密码相同！");
        }

        SysUser user = new SysUser();
        password = new BCryptPasswordEncoder().encode(password);
        user.setPassword(password);
        user.setUpdateId(users.get(0).getId());
        user.setUpdateTime(new Date());
        int count = sysUserDao.updateByExampleSelective(user, example);
        userSessionDao.removeUser(users.get(0).getUsername());
        return count;
    }

    @Override
    public int restExpiresIn(Integer userId, Integer expiresIn) {
        SysUser sysUser = sysUserDao.selectByPrimaryKey(userId);
        if (sysUser == null) {
            throw new BusinessException("用户不存在，请核对！");
        }
        SysUser user = new SysUser();
        user.setId(userId);
        user.setExpiresIn(expiresIn);
        Session session = SecurityContext.currentUser();
        user.setUpdateId((int)session.getId());
        user.setUpdateTime(new Date());
        int i = sysUserDao.updateByPrimaryKeySelective(user);
        userSessionDao.refresh(session.getUsername(), session.getSessionId(), expiresIn * 60 * 60l);
        return i;
    }

    @Override
    public UserVO findUser(String username) {
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("username", username);
        SysUser sysUser = sysUserDao.selectOneByExample(example);
        UserVO userVO = new UserVO();
        if (sysUser != null) {
            sysUser.setPassword(null);
            if (sysUser.getCustName() == null) {
                sysUser.setCustName("");
            }
            userVO.setUsername(sysUser.getUsername());
            userVO.setName(sysUser.getName());
            userVO.setCustId(sysUser.getCustId());
            userVO.setCustName(sysUser.getCustName());
            userVO.setEmail(sysUser.getEmail());
            userVO.setEmails(userVO.getEmails());
            userVO.setPhoneNumber(sysUser.getPhoneNumber());
            userVO.setMainUser(sysUser.getMainUser());
            userVO.setUserType(sysUser.getUserType());
            Integer expiresIn = sysUser.getExpiresIn();
            if (expiresIn == null && sysUser.getUserType()!=null && sysUser.getUserType()==2) {
                expiresIn = 2;
            }
            userVO.setExpiresIn(expiresIn);
            userVO.setCreateTime(sysUser.getCreateTime());
            userVO.setId(Long.valueOf(sysUser.getId()));
            userVO.setComment(sysUser.getComment());
        }
        List<String> roles = sysRoleDao.findCodeByUser(username);
        userVO.setRoles(roles);

        return userVO;
    }

    @Override
    public List<String> getUserRole(Integer userId) {
        return sysUserRoleDao.getUserRole(userId);
    }

    @Override
    public String validate(String validateValue, Integer id) {
        if (StringUtils.isBlank(validateValue)) {
            throw new BusinessException("参数错误：validateValue必填");
        }
        String validateResult = "";
        Example example = new Example(SysUser.class);
        if (id == null) {
            example.createCriteria().andEqualTo("username", validateValue);
        } else {
            example.createCriteria().andEqualTo("username", validateValue).andNotEqualTo("id", id);
        }
        int count = sysUserDao.selectCountByExample(example);
        if (count > 0) {
            validateResult = "该账户名已存在，请勿重复添加";
        }
        return validateResult;
    }

    @Override
    public int updateUserStatus(Integer userId, Integer status) {

        Session session = SecurityContext.currentUser();
        Integer userType = session.getUserType();
        SysUser sysUser = null;
        if (userType!=null && userType == 1) { //运营后台操作用户
            sysUser = sysUserDao.selectByPrimaryKey(userId);
        }
        if (userType!=null && userType == 2) { // 客户端操作用户
            Example e = new Example(SysUser.class);
            e.createCriteria().andEqualTo("custId", session.getCustId()).andEqualTo("id", userId);
            sysUser = sysUserDao.selectOneByExample(e);
        }

        if (sysUser == null) {
            throw new BusinessException(String.format("未能找到用户账户信息，请核查", userId));
        }
        Integer userStatus = sysUser.getStatus();
        if (userStatus != null && userStatus.equals(status)) {
            throw new BusinessException(String.format("用户账户已是[%s]状态，无需%s", userStatus.equals(1)?"激活":"注销", userStatus.equals(1)?"激活":"注销"));
        }

        SysUser user = new SysUser();
        user.setId(userId);
        user.setStatus(status);
        int i = sysUserDao.updateByPrimaryKeySelective(user);
        if (status == 0) { //注销
            userSessionDao.removeUser(sysUser.getUsername());
        }
        return i;
    }

    @Override
    public int updateCustUserStatus(Integer custId, Integer status) {
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("custId", custId).andEqualTo("userType", UserTypeEnum.ClientUser.getCode())
                .andNotEqualTo("status", status);
        List<SysUser> users = sysUserDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(users)) {
            for (SysUser user : users) {
                try {
                    updateUserStatus(user.getId(), status);
                } catch (Exception e) {

                }
            }
        }
        return users.size();
    }

    @Override
    public List<SysUserVO> getCustUserList(Integer custId, String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            keyword = keyword.replaceAll("%", "\\\\%");
        }
        return sysUserDao.getCustUserList(custId,keyword);
    }

    @Override
    public Integer getCustActiveUserNumber(Integer custId) {
        Example example = new Example(SysUser.class);
        example.createCriteria().andEqualTo("custId", custId).andEqualTo("userType", 2).andEqualTo("userCategory", 1)
                .andEqualTo("status", 1).andEqualTo("mainUser", 0);
        return sysUserDao.selectCountByExample(example);
    }

    @Override
    public SysUser getSysUser(Integer userId) {
        return sysUserDao.selectByPrimaryKey(userId);
    }

    @Override
    public List<SysUser> getAllSysUser() {
        return sysUserDao.selectAll();
    }

    @Override
    public List<SysUserDinDinVO> getAllSysUserDinDinVO() {
        return sysUserDao.selectSysUserDinDinVOAll();
    }

    @Override
    public void updateUserCustName(Integer custId, String custName) {
        if (StringUtils.isNotBlank(custName)) {
            Example example = new Example(SysUser.class);
            example.createCriteria().andEqualTo("custId", custId);
            SysUser user = new SysUser();
            user.setCustName(custName);
            sysUserDao.updateByExampleSelective(user, example);
        }
    }


}
