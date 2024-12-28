package com.seebon.rpa.actions.target;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.seebon.rpa.context.RobotContext;
import com.seebon.rpa.context.annotation.ActionArgs;
import com.seebon.rpa.context.constant.RobotConstant;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Slf4j
public abstract class AbstractTarget<T> implements Target {
    @Setter
    @Autowired
    protected RobotContext ctx;

    @Override
    public Map<String, Object> fetch() {
        Map<String, Object> result = new HashMap<>();
        T t = fetchTarget();
        if (t instanceof List) {
            List<Object> aList = Lists.newArrayList();
            if (t instanceof JSONArray) {
                aList = JSONObject.parseObject(JSONObject.toJSONString(t), new TypeReference<ArrayList<Object>>() {
                });
            } else {
                aList = (ArrayList<Object>) t;
            }
            boolean a = aList.stream().anyMatch(m -> (m instanceof Target));
            if (a) {
                List<Target> targets = (ArrayList<Target>) t;
                for (Target target : targets) {
                    Map<String, Object> map = target.fetch();
                    for (String key : map.keySet()) {
                        if (result.containsKey(key)) {
                            Object o = result.get(key);
                            if (o.equals(map.get(key))) {
                                continue;
                            }
                            List<Object> list;
                            if (o instanceof List) {
                                list = (List) o;
                            } else {
                                list = new ArrayList<>();
                                list.add(o);
                            }
                            list.add(map.get(key));
                            result.put(key, list);
                        } else {
                            result.put(key, map.get(key));
                        }
                    }
                }
            } else {
                String key = this.getClass().getSimpleName().replace(RobotConstant.TARGET_BEAN_SUFFIX, "");
                result.put((key.substring(0, 1).toLowerCase() + key.substring(1)), t);
            }
        } else {
            String key = this.getClass().getSimpleName().replace(RobotConstant.TARGET_BEAN_SUFFIX, "");
            result.put((key.substring(0, 1).toLowerCase() + key.substring(1)), t);
        }
        return result;
    }

    protected abstract T fetchTarget();

    @Override
    public void release() {
        Arrays.stream(this.getClass().getDeclaredFields()).filter(f -> f.isAnnotationPresent(ActionArgs.class)).forEach(field -> {
            field.setAccessible(true);
            try {
                field.set(this, null);
            } catch (IllegalAccessException e) {
                log.error("【Exception】", e);
            }
        });
    }
}
