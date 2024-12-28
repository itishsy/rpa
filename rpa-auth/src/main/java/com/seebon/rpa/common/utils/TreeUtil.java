package com.seebon.rpa.common.utils;

import com.google.common.collect.Lists;
import com.seebon.rpa.entity.auth.dto.ResourceTreeDTO;

import java.util.Comparator;
import java.util.List;

public class TreeUtil {

    public static List<ResourceTreeDTO> convertTree(List<ResourceTreeDTO> list, Integer parentId) {
        List<ResourceTreeDTO> childList = Lists.newArrayList();
        for (ResourceTreeDTO tree : list) {
            if (null == tree) {
                continue;
            }
            Integer id = tree.getId();
            Integer pid = tree.getParentId();
            if (parentId.equals(pid)) {
                List<ResourceTreeDTO> childs = TreeUtil.convertTree(list, id);
                childs.sort(Comparator.comparing(ResourceTreeDTO::getSort));
                tree.setChildTrees(childs);
                childList.add(tree);
            }
        }
        return childList;
    }
}
