package com.seebon.rpa.service;

import com.seebon.rpa.entity.auth.dto.ResourceTreeDTO;
import com.seebon.rpa.entity.auth.po.SysButton;
import com.seebon.rpa.entity.auth.po.SysMenu;

import java.util.List;
import java.util.Map;

public interface IMenuService {

    Map<String, Object> queryMenuByUser(Integer type);

    List<ResourceTreeDTO> treeList(Integer menuType);

    String validateMenuName(String name, Integer menuType, Integer id);

    int saveAdd(SysMenu menu);

    int saveEdit(SysMenu menu);

    int deleteMenu(List<String> ids);

    List<SysButton> getButtonsByMenuId(Integer menuId);

    String validButtonCode(String code, Integer menuType);

    int addButton(SysButton button);

    int deleteButton(Integer buttonId);

    void reSort(ResourceTreeDTO dto, Integer type);

    int updateStatus(Integer menuId, Integer status);
}
