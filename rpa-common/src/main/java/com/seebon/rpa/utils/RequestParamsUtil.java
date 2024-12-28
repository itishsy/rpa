package com.seebon.rpa.utils;

import cn.hutool.core.lang.Dict;
import com.seebon.rpa.entity.IgQuery;
import com.seebon.rpa.entity.IgRequestObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParamsUtil {

    public static Dict toDict(IgRequestObject reqObj) {
        return new Dict(toMap(reqObj));
    }

    /**
     * 构造查询参数
     *
     * @param reqObj
     * @return
     */
    public static Map<String, Object> toMap(IgRequestObject reqObj) {
        Map<String, Object> params = new HashMap<>();
        List<IgQuery> querys = reqObj.getQuery();
        //构造查询参数
        if (null != querys && !querys.isEmpty()) {
            for (IgQuery query : querys) {
                String property = query.getProperty();
                Object value = query.getValue();
                if (StringUtils.isNotBlank(property)) {
                    params.put(property, value);
                }
            }
        }
        Sort sort = null;
        //如果存在需要排序内容
        if (StringUtils.isNotBlank(reqObj.getSidx())) {
            if (reqObj.getSort().equals("asc")) {
                sort = Sort.by(Sort.Direction.ASC, reqObj.getSidx());
            } else {
                sort = Sort.by(Sort.Direction.DESC, reqObj.getSidx());
            }
        }
        PageRequest pageRequest = null;

        if (sort != null) {
            pageRequest = PageRequest.of(reqObj.getPage(), reqObj.getSize(), sort);
        } else {
            pageRequest = PageRequest.of(reqObj.getPage(), reqObj.getSize());
        }
        if (pageRequest != null) {
            params.put("isPage", true);
            params.put("page", pageRequest.getPageNumber());
            params.put("start", (pageRequest.getPageNumber() - 1) * pageRequest.getPageSize());
            params.put("size", pageRequest.getPageSize());
            if (pageRequest.getSort() != null && pageRequest.getSort().iterator().hasNext()) {
                Sort.Order order = pageRequest.getSort().iterator().next();
                params.put("sidx", order.getProperty());
                params.put("sort", order.getDirection() == Sort.Direction.DESC ? "desc" : "asc");
            }
        }
        return params;
    }
}
