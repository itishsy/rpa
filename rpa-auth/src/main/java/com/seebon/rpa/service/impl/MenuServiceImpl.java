package com.seebon.rpa.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.common.utils.TreeUtil;
import com.seebon.rpa.entity.auth.dto.ResourceTreeDTO;
import com.seebon.rpa.entity.auth.po.SysButton;
import com.seebon.rpa.entity.auth.po.SysMenu;
import com.seebon.rpa.entity.auth.po.SysRoleButton;
import com.seebon.rpa.entity.auth.po.SysRoleMenu;
import com.seebon.rpa.entity.system.User;
import com.seebon.rpa.entity.auth.vo.UserMenuVO;
import com.seebon.rpa.repository.mysql.*;
import com.seebon.rpa.repository.redis.UserSessionDao;
import com.seebon.rpa.service.IMenuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Author hao
 * @Date 2022/11/14 15:45
 * @Version 1.0
 **/
@Service
@Slf4j
public class MenuServiceImpl implements IMenuService {

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysMenuDao sysMenuDao;

    @Autowired
    private SysButtonDao sysButtonDao;

    @Autowired
    private SysRoleMenuDao sysRoleMenuDao;

    @Autowired
    private SysRoleButtonDao sysRoleButtonDao;

    @Autowired
    private UserSessionDao userSessionDao;

    /**
     * 查询当前用户菜单
     * @return
     */
    @Override
    public Map<String, Object> queryMenuByUser(Integer type) {

        Map<String, Object> result = Maps.newHashMap();

        Session session = SecurityContext.currentUser();
        Integer userType = session.getUserType();

        if (!userType.equals(type)) {
            userSessionDao.remove(session.getUsername(), session.getSessionId());
            throw new BusinessException("权限不足，请联系管理员处理！");
        }

        List<UserMenuVO> userMenuVOS = sysUserDao.selectMenuByUser((int) session.getId(), userType);

        if (userType != null && userType==2) {
            Integer mainUser = session.getMainUser();
            if (mainUser==null || mainUser==0) {
                userMenuVOS = userMenuVOS.stream().filter(it -> StringUtils.isBlank(it.getModuleCode())
                        || !"10024005".equals(it.getModuleCode())).collect(Collectors.toList());
            }
        }

        /*List<UserMenuVO> menuVOS = userMenuVOS.stream()
                .filter(distinctByVariable(SysMenu::getId))
                .collect(Collectors.toList());*/
        //父层级
        List<UserMenuVO> parentMenus = userMenuVOS.stream()
                .filter(s -> s.getParentId()==0)
                .collect(Collectors.toList());
        //子层级
        List<UserMenuVO> childMenus = userMenuVOS.stream()
                .filter(s -> s.getParentId() != 0)
                .collect(Collectors.toList());
        for (UserMenuVO parentMenu : parentMenus) {
            setChildMenu(parentMenu,childMenus);
        }

        // 获取当前用户所有按钮信息
        List<SysButton> buttons = sysUserDao.selectButtonsByUserId((int)session.getId(), userType);

        result.put("menus", parentMenus);
        result.put("buttons", buttons);
        result.put("tabs", getTabs(parentMenus));

        return result;
    }

    private Map<String, List<Map<String, String>>> getTabs(List<UserMenuVO> resourceList) {
        Map<String, List<Map<String, String>>> tabMaps = Maps.newHashMap();
        resourceList.stream().forEach(item -> {
            String name = item.getName();
            String url = item.getUrl();

            Map<String, String> map = Maps.newHashMap();
            map.put("name", name);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(url)) {
                map.put("path", "/" + url);
            }
            List list = Lists.newArrayList();
            list.add(map);

            List<UserMenuVO> subResources = item.getChildMenu();
            if (!org.springframework.util.CollectionUtils.isEmpty(subResources)) {
                getSubMap(tabMaps, list, subResources);
            } else {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(url)) {
                    tabMaps.put("/" + url, list);
                }
            }
        });
        return tabMaps;
    }

    private void getSubMap (Map<String, List<Map<String, String>>> tabMaps,List<Map<String, String>> pMaps, List<UserMenuVO> subResources) {
        subResources.stream().forEach(item -> {
            ArrayList<Map<String, String>> subList = Lists.newArrayList();
            org.apache.commons.collections.CollectionUtils.addAll(subList, new Object[pMaps.size()]);
            Collections.copy(subList, pMaps);

            String name = item.getName();
            String url = item.getUrl();
            List<UserMenuVO> subResources1 = item.getChildMenu();
            Map<String, String> map = Maps.newHashMap();
            map.put("name", name);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(url)) {
                map.put("path", "/" + url);
            }
            subList.add(map);
            if (!org.springframework.util.CollectionUtils.isEmpty(subResources1)) {
                getSubMap(tabMaps, subList, subResources1);
            } else {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(url)) {
                    tabMaps.put("/" + url, subList);
                }
            }
        });
    }

    @Override
    public List<ResourceTreeDTO> treeList(Integer menuType) {
        Example example = new Example(SysMenu.class);
        example.createCriteria().andEqualTo("menuType", menuType);
        example.orderBy("sort").asc();
//        example.createCriteria().andEqualTo("disabled", 0);
        List<SysMenu> list = sysMenuDao.selectByExample(example);
        List<ResourceTreeDTO> treeDTOs = list.stream().map(menu -> {
            ResourceTreeDTO dto = new ResourceTreeDTO();
            BeanUtils.copyProperties(menu, dto);
            return dto;
        }).collect(Collectors.toList());
        return TreeUtil.convertTree(treeDTOs, 0);
    }

    @Override
    public String validateMenuName(String name, Integer menuType, Integer id) {
        Example example = new Example(SysMenu.class);
        if (id == null) { // 新增
            example.createCriteria().andEqualTo("name", name.trim()).andEqualTo("menuType", menuType);
        } else { // 编辑
            example.createCriteria().andEqualTo("name", name.trim()).andEqualTo("menuType", menuType).andNotEqualTo("id", id);
        }
        int count = sysMenuDao.selectCountByExample(example);
        return count>0?"菜单名称已存在":"";
    }

    @Override
    public int saveAdd(SysMenu menu) {
        String validateStr = validateMenuName(menu.getName(), menu.getMenuType(), null);
        if (StringUtils.isNotBlank(validateStr)) {
            throw new BusinessException(validateStr);
        }
        User user = SecurityContext.currentUser();
        menu.setCreateId((int)user.getId());
        menu.setCreateTime(new Date());
        Integer parentId = menu.getParentId();
        Integer sort = sysMenuDao.getMaxSortNumber(parentId);
        if (sort == null) {
            sort = 0;
        }
        menu.setSort(sort + 1);
        sysMenuDao.insert(menu);

        // 如果新增的是客户端的菜单，那么有对应模块的客户角色都得授权新加的菜单权限
        if (menu.getMenuType() == 2) {
            String moduleCode = menu.getModuleCode();
            if (StringUtils.isNotBlank(moduleCode)) {
                List<Integer> roleIds = sysRoleMenuDao.selectCustRoleIds(moduleCode);
                if (CollectionUtils.isNotEmpty(roleIds)) {
                    List<SysRoleMenu> roleMenus = roleIds.stream().map(it -> {
                        SysRoleMenu roleMenu = new SysRoleMenu();
                        roleMenu.setRoleId(it);
                        roleMenu.setMenuId(menu.getId());
                        return roleMenu;
                    }).collect(Collectors.toList());
                    sysRoleMenuDao.insertList(roleMenus);
                }
            }
        }
        return menu.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int saveEdit(SysMenu menu) {
        String validateStr = validateMenuName(menu.getName(), menu.getMenuType(), menu.getId());
        if (StringUtils.isNotBlank(validateStr)) {
            throw new BusinessException(validateStr);
        }
        User user = SecurityContext.currentUser();
        menu.setUpdateId((int)user.getId());
        menu.setUpdateTime(new Date());
        SysMenu sysMenu1 = sysMenuDao.selectByPrimaryKey(menu.getId());
        if (menu.getMenuType() == 2) { // 客户端菜单
            SysMenu sysMenu = new SysMenu();
            sysMenu.setModuleCode(menu.getModuleCode());
            sysMenu.setModuleName(menu.getModuleName());
            Example example = new Example(SysMenu.class);
            example.createCriteria().andEqualTo("parentId", menu.getId());
            sysMenuDao.updateByExampleSelective(sysMenu, example);

            sysMenu1.setModuleCode(menu.getModuleCode());
            sysMenu1.setModuleName(menu.getModuleName());
        }
        sysMenu1.setStatus(menu.getStatus());
        sysMenu1.setName(menu.getName());
        sysMenu1.setUrl(menu.getUrl());
        sysMenu1.setComment(menu.getComment());
        sysMenu1.setIconClass(menu.getIconClass());

        return sysMenuDao.updateByPrimaryKey(sysMenu1);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteMenu(List<String> ids) {
        // 删除菜单
        Example example = new Example(SysMenu.class);
        example.createCriteria().andIn("id", ids);
        int count = sysMenuDao.deleteByExample(example);

        // 删除菜单下的按钮信息
        example.clear();
        example = new Example(SysButton.class);
        example.createCriteria().andIn("menuId",ids);
        List<SysButton> buttons = sysButtonDao.selectByExample(example);

        sysButtonDao.deleteByExample(example);


        // 删除系统角色中的对应菜单权限信息
        example.clear();
        example = new Example(SysRoleMenu.class);
        example.createCriteria().andIn("menuId", ids);
        sysRoleMenuDao.deleteByExample(example);

        List<Integer> buttonIds = buttons.stream().map(item -> item.getId()).collect(Collectors.toList());
        // 删除系统角色中的对应按钮权限信息
        if (CollectionUtils.isNotEmpty(buttonIds)) {
            example.clear();
            example = new Example(SysRoleButton.class);
            example.createCriteria().andIn("buttonId",ids);
            sysRoleButtonDao.deleteByExample(example);
        }
        return count;
    }

    @Override
    public int updateStatus(Integer menuId, Integer status) {
        SysMenu sysMenu = sysMenuDao.selectByPrimaryKey(menuId);
        if (sysMenu == null) {
            throw new BusinessException("未能找到对应菜单信息，请核查传参信息！");
        }
        if (sysMenu.getStatus() != null && sysMenu.getStatus().equals(status)) {
            throw new BusinessException(String.format("菜单当前已处于{%s}状态，无需%s", status==1?"启用":"停用", status==1?"启用":"停用"));
        }
        SysMenu menu = new SysMenu();
        menu.setId(menuId);
        menu.setStatus(status);
        return sysMenuDao.updateByPrimaryKeySelective(menu);
    }

    @Override
    public List<SysButton> getButtonsByMenuId(Integer menuId) {
        Example example = new Example(SysButton.class);
        example.createCriteria().andEqualTo("menuId", menuId);
        example.setOrderByClause("id");
        return sysButtonDao.selectByExample(example);
    }

    @Override
    public String validButtonCode(String code, Integer menuType) {
        /*Example example = new Example(SysButton.class);
        example.createCriteria().andEqualTo("code", code.trim());
        int count = sysButtonDao.selectCountByExample(example);*/
        Map<String, Object> params = Maps.newHashMap();
        params.put("code", code);
        params.put("menuType", menuType);
        int count = sysButtonDao.selectCountByParams(params);
        return count>0?"按钮编码已存在":"";
    }

    @Override
    public int addButton(SysButton button) {

        Integer menuId = button.getMenuId();
        if (menuId == null) {
            throw  new BusinessException("未找到按钮对应的菜单信息，请核查！");
        }
        SysMenu sysMenu = sysMenuDao.selectByPrimaryKey(menuId);
        if (sysMenu == null) {
            throw  new BusinessException("未找到按钮对应的菜单信息，请核查！");
        }

        String msg = validButtonCode(button.getCode(), sysMenu.getMenuType());
        if (StringUtils.isNotBlank(msg)) {
            throw new BusinessException(msg);
        }

        sysButtonDao.insert(button);

        // 如果新增的是客户端的按钮，那么有对应模块的客户角色都得授权新加的菜单按钮权限
        if (sysMenu.getMenuType() == 2) {
            String moduleCode = sysMenu.getModuleCode();
            if (StringUtils.isNotBlank(moduleCode)) {
                List<Integer> roleIds = sysRoleMenuDao.selectCustRoleIds(moduleCode);
                if (CollectionUtils.isNotEmpty(roleIds)) {
                    List<SysRoleButton> roleButtons = roleIds.stream().map(it -> {
                        SysRoleButton roleButton = new SysRoleButton();
                        roleButton.setRoleId(it);
                        roleButton.setButtonId(button.getId());
                        return roleButton;
                    }).collect(Collectors.toList());
                    sysRoleButtonDao.insertList(roleButtons);
                }
            }
        }
        return button.getId();
    }

    @Override
    public int deleteButton(Integer buttonId) {
        // 删除按钮
        int count = sysButtonDao.deleteByPrimaryKey(buttonId);

        // 清除已分配该按钮的角色的按钮权限
        Example example = new Example(SysRoleButton.class);
        example.createCriteria().andEqualTo("buttonId",buttonId);
        sysRoleButtonDao.deleteByExample(example);
        return count;
    }

    @Override
    public void reSort(ResourceTreeDTO dto, Integer type) {
        Integer parentId = dto.getParentId();
        Integer sort = dto.getSort();
        Integer sort1 = 0;
        Integer menuType = dto.getMenuType();
        List<Integer> resourceIds = Lists.newArrayList();
        if (type == 1) {
            // 下调
            Example example = new Example(SysMenu.class);
            example.createCriteria().andEqualTo("parentId", parentId).andEqualTo("menuType", menuType).andGreaterThan("sort", sort);
            example.setOrderByClause("sort asc");
            List<SysMenu> resources = sysMenuDao.selectByExample(example);
            if (CollectionUtils.isEmpty(resources)) {
                throw new BusinessException("已在最末位，无需调整顺序");
            } else {
                sort1 = resources.get(0).getSort();
                Integer sort2 = resources.get(0).getSort();
                resourceIds = resources.stream().filter(item -> item.getSort().equals(sort2)).map(item -> item.getId()).collect(Collectors.toList());
            }
        } else {
            // 上调
            Example example = new Example(SysMenu.class);
            example.createCriteria().andEqualTo("parentId", parentId).andEqualTo("menuType", menuType).andLessThan("sort", sort);
            example.setOrderByClause("sort desc");
            List<SysMenu> resources = sysMenuDao.selectByExample(example);
            if (CollectionUtils.isEmpty(resources)) {
                throw new BusinessException("已在最首位，无需调整顺序");
            } else {
                sort1 = resources.get(0).getSort();
                Integer sort2 = resources.get(0).getSort();
                resourceIds = resources.stream().filter(item -> item.getSort().equals(sort2)).map(item -> item.getId()).collect(Collectors.toList());
            }
        }
        Example example = new Example(SysMenu.class);
        example.createCriteria().andIn("id", resourceIds);
        SysMenu r = new SysMenu();
        r.setSort(sort);
        sysMenuDao.updateByExampleSelective(r, example);

        r = new SysMenu();
        r.setId(dto.getId());
        r.setSort(sort1);
        sysMenuDao.updateByPrimaryKeySelective(r);
    }

    private static void setChildMenu(UserMenuVO parent,List<UserMenuVO> list){
        List<UserMenuVO> childList = new ArrayList<>();
        for(Iterator<UserMenuVO> iterator = list.iterator();iterator.hasNext();){
            UserMenuVO next = iterator.next();
            if(parent.getId().equals(next.getParentId())){
                childList.add(next);
                iterator.remove();
            }
        }
        parent.setChildMenu(childList);

        if (CollectionUtils.isNotEmpty(list)){
            for (UserMenuVO userMenuVO : childList) {
                setChildMenu(userMenuVO,list);
            }
        }
    }

    private static <T> Predicate<T> distinctByVariable(Function<? super T, ?> keyExtractor) {
        HashMap<Object, Boolean> map = new HashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
