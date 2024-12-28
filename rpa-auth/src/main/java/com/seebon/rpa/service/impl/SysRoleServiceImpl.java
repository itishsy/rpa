package com.seebon.rpa.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.entity.IgGridDefaultPage;
import com.seebon.rpa.entity.auth.po.*;
import com.seebon.rpa.entity.system.User;
import com.seebon.rpa.entity.auth.vo.RoleResourceVO;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.service.ISysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-22 19:17:47
 */
@Slf4j
@Service
public class SysRoleServiceImpl implements ISysRoleService {

    @Autowired
    private SysRoleDao sysRoleDao;

    @Autowired
    private SysMenuDao sysMenuDao;

    @Autowired
    private SysButtonDao sysButtonDao;

    @Autowired
    private SysRoleMenuDao sysRoleMenuDao;

    @Autowired
    private SysRoleButtonDao sysRoleButtonDao;

    @Autowired
    private SysPrivilegeDao sysPrivilegeDao;

    @Override
    public int add(SysRole role) {

        //保存数据库
        User user = SecurityContext.currentUser();
        Integer userType = user.getUserType();

        //角色名称唯一
        String name = role.getName();
        Example example = new Example(SysRole.class);

        if (userType != null && userType == 1) {
            example.createCriteria().andEqualTo("name", name).andEqualTo("roleType", userType);
        } else if (userType != null && userType == 2) {
            Integer custId = user.getCustId();
            example.createCriteria().andEqualTo("name", name).andEqualTo("custId", custId).andEqualTo("roleType", userType);
        }

        List<SysRole> roleList = sysRoleDao.selectByExample(example);
        if (CollectionUtils.isNotEmpty(roleList)) {
            throw new BusinessException("角色名称已存在");
        }

        role.setRoleType(userType);
        if (userType != null && userType==1) {
            role.setCode("manager");
        } else {
            role.setCode("client");
        }
        role.setCreateId((int)user.getId());
        role.setCreateTime(new Date());
        sysRoleDao.insert(role);
        log.info("save role info:", role);

        SysPrivilege sp = new SysPrivilege();
        sp.setRoleId(role.getId());
        sp.setResourceId(1);
        sysPrivilegeDao.insert(sp);

        return role.getId();
    }

    @Override
    public int update(SysRole role) {
        //更新到数据库
        User user = SecurityContext.currentUser();
        if (role == null || role.getId() == null) {
            throw new BusinessException("参数不合法");
        }
        //角色是否存在系统
        SysRole roleFromDB = sysRoleDao.selectByPrimaryKey(role.getId());
        if (roleFromDB == null) {
            throw new BusinessException("角色不存在系统");
        }
        if (!roleFromDB.getName().equals(role.getName())) {
            //角色名称是否唯一
            Example example = new Example(SysRole.class);
            Integer userType = user.getUserType();
            if (userType != null && userType == 1) {
                example.createCriteria().andEqualTo("name", role.getName()).andEqualTo("roleType", user.getUserType());
            } else if (userType != null && userType == 2) {
                Integer custId = user.getCustId();
                example.createCriteria().andEqualTo("name", role.getName()).andEqualTo("custId", custId).andEqualTo("roleType", user.getUserType());
            }
            /*if (custId!=null) {
                example.createCriteria().andEqualTo("name", role.getName()).andEqualTo("custId", custId).andEqualTo("roleType", user.getUserType());
            } else {
                example.createCriteria().andEqualTo("name", role.getName()).andEqualTo("roleType", user.getUserType());
            }*/
            List<SysRole> roleList = sysRoleDao.selectByExample(example);
            if (CollectionUtils.isNotEmpty(roleList)) {
                throw new BusinessException("角色名称已存在");
            }
        }
        role.setUpdateId((int)user.getId());
        role.setUpdateTime(new Date());
        log.info("update role info:", role);
        return sysRoleDao.updateByPrimaryKeySelective(role);
    }

    @Override
    public IgGridDefaultPage<SysRole> getRoleVOByPage(Map<String, Object> params) {
        User user = SecurityContext.currentUser();
        Example example = new Example(SysRole.class);
        String searchText = (String)params.get("searchText");
        if (StringUtils.isNotBlank(searchText)) {
            searchText = searchText.replaceAll("%", "\\\\%");
            example.createCriteria().andEqualTo("roleType", user.getUserType()).
                    andCondition(String.format(" (name like '%s' or comment like '%s')", "%".concat(searchText).concat("%"), "%".concat(searchText).concat("%")));
        } else {
            example.createCriteria().andEqualTo("roleType", user.getUserType());
        }
        int count = sysRoleDao.selectCountByExample(example);
        Integer start = (Integer)params.get("start");
        Integer size = (Integer)params.get("size");
        example.setOrderByClause("id limit " + start + "," + size);
        List<SysRole> sysRoles = sysRoleDao.selectByExample(example);
        return new IgGridDefaultPage<>(sysRoles, count);
    }

    @Override
    public List<RoleResourceVO> getRoleResource(Integer roleId) {
        if (roleId == null) {
            throw new BusinessException("参数不合法");
        }
        //角色是否存在系统
        SysRole roleFromDB = sysRoleDao.selectByPrimaryKey(roleId);
        if (roleFromDB == null) {
            throw new BusinessException("角色不存在系统");
        }

        Map<String,Object> params = Maps.newHashMap();
        params.put("roleId", roleId);

        List<RoleResourceVO> roleResourceVOS = sysMenuDao.getRoleResources(params);

        List<RoleResourceVO> firstLevels = roleResourceVOS.stream().filter(item -> item.getParentId() == 0L).collect(Collectors.toList());
        firstLevels.sort((o1,o2) -> o1.getSort() - o2.getSort());
        firstLevels.stream().forEach(item ->{
            getSubResources(roleResourceVOS, item);
        });

        return firstLevels;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveRoleResource(Integer roleId, String resourceIds, String buttonIds) {
        if (roleId == null) {
            throw new BusinessException("参数不合法");
        }
        //角色是否存在系统
        SysRole roleFromDB = sysRoleDao.selectByPrimaryKey(roleId);
        if (roleFromDB == null) {
            throw new BusinessException("角色不存在系统");
        }
        List<String> menus = Lists.newArrayList();
        if (StringUtils.isNotBlank(resourceIds)) {
            menus = Lists.newArrayList(resourceIds.split(","));
            List<String> ids = Lists.newArrayList();
            for (String id : menus) {
                SysMenu menu = sysMenuDao.selectByPrimaryKey(id);
                if (menu == null) {
                    ids.add(id);
                }
            }
            if (CollectionUtils.isNotEmpty(ids)) {
                throw new BusinessException("id为：["+StringUtils.join(ids,"，")+"]的菜单在系统不存在，请核查");
            }
        }

        List<String> buttons = Lists.newArrayList();
        if (StringUtils.isNotBlank(buttonIds)) {
            buttons = Lists.newArrayList(buttonIds.split(","));
            List<String> ids = Lists.newArrayList();
            for (String id : buttons) {
                SysButton button = sysButtonDao.selectByPrimaryKey(id);
                if (button == null) {
                    ids.add(id);
                }
            }
            if (CollectionUtils.isNotEmpty(ids)) {
                throw new BusinessException("id为：["+StringUtils.join(ids,"，")+"]的按钮在系统不存在，请核查");
            }
        }

        Example exa = new Example(SysRoleMenu.class);
        exa.createCriteria().andEqualTo("roleId",roleId);
        sysRoleMenuDao.deleteByExample(exa);

        if (CollectionUtils.isNotEmpty(menus)) {
            List<SysRoleMenu> sysRoleMenus = menus.stream().map(menuId -> {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId.intValue());
                rm.setMenuId(Integer.valueOf(menuId));
                return rm;
            }).collect(Collectors.toList());
            sysRoleMenuDao.insertList(sysRoleMenus);
        }

        exa.clear();
        exa = new Example(SysRoleButton.class);
        exa.createCriteria().andEqualTo("roleId",roleId);
        sysRoleButtonDao.deleteByExample(exa);

        if (CollectionUtils.isNotEmpty(buttons)) {
            List<SysRoleButton> sysRoleButtons = buttons.stream().map(buttonId -> {
                SysRoleButton rb = new SysRoleButton();
                rb.setRoleId(roleId.intValue());
                rb.setButtonId(Integer.valueOf(buttonId));
                return rb;
            }).collect(Collectors.toList());
            sysRoleButtonDao.insertList(sysRoleButtons);
        }
    }

    @Override
    public List<SysRole> getAll(Integer roleType) {
        Example example = new Example(SysRole.class);
        example.createCriteria().andEqualTo("roleType", roleType);
        return sysRoleDao.selectByExample(example);
    }

    @Override
    public List<SysRole> getRolesByCustId(Integer custId) {
        return sysRoleDao.getRolesByCustId(custId);
    }

    @Override
    public List<Integer> getCustIdsByRoleName(String roleName) {
        return sysRoleDao.getCustIdsByRoleName(roleName);
    }

    private void getSubResources(List<RoleResourceVO> roleResourceVOS, RoleResourceVO roleResourceVO) {
        if (roleResourceVO.getPerType() == 1) {
            List<RoleResourceVO> subResources = roleResourceVOS.stream().filter(item -> item.getParentId() != null
                    && item.getParentId().equals(roleResourceVO.getId())).collect(Collectors.toList());
            subResources.sort((o1,o2) -> o1.getSort() - o2.getSort());
            if (CollectionUtils.isNotEmpty(subResources)) {
                subResources.stream().forEach(item ->{
                    getSubResources(roleResourceVOS, item);
                });
                roleResourceVO.setSubRoleResourceVOS(subResources);
            }
        }
    }
}
