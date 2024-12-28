package com.seebon.rpa.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * EL表达式，类似java8 Stream 部分API方法
 */
public class ELStream {

    private List<Map<String, Object>> maps;

    private ELStream() {
    }

    public static ELStream of(List<Map<String, Object>> list) {
        if (list == null) {
            throw new RuntimeException();
        }
        ELStream stream = new ELStream();
        stream.maps = list;
        return stream;
    }

    /**
     * 过滤
     */
    public ELStream filter(String expression, Map<String, Object> ctx) {
        List<Map<String, Object>> filter = Lists.newLinkedList();
        Map<String, Object> elCtx = Maps.newHashMap();
        elCtx.putAll(ctx);
        for (Map<String, Object> map : maps) {
            elCtx.put("map", map);
            if (ELParser.parse(expression, elCtx, Boolean.class)) {
                filter.add(map);
            }
        }
        maps = filter;
        return this;
    }

    public boolean contains(String expression, Map<String, Object> ctx) {
        Map<String, Object> elCtx = Maps.newHashMap();
        elCtx.putAll(ctx);
        if (maps != null && maps.size() > 0) {
            for (Map<String, Object> map : maps) {
                elCtx.put("map", map);
                if (ELParser.parse(expression, elCtx, Boolean.class)) {
                    return true;
                }
            }
        } else {
            return ELParser.parse(expression, elCtx, Boolean.class);
        }
        return false;
    }

    public String sum(String key) {
        BigDecimal total = BigDecimal.ZERO;
        if (CollectionUtils.isNotEmpty(maps)) {
            for (Map<String, Object> map : maps) {
                Object value = map.get(key);
                if (value == null || StringUtils.isBlank(value.toString())) {
                    continue;
                }
                BigDecimal val = new BigDecimal(value.toString().trim());
                total = total.add(val);
            }
        }
        return total.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    public String sumToString(String key) {
        BigDecimal total = new BigDecimal("0");
        if (maps != null && maps.size() > 0) {
            for (Map<String, Object> map : maps) {
                Object value = map.get(key);
                if (value == null || StringUtils.isBlank(value.toString())) {
                    continue;
                }
                //有些表格金额字段在excel中是格式化了带有英文逗号  如 100,000.23
                BigDecimal val = new BigDecimal(value.toString().trim().replace(",", ""));
                total = total.add(val);
            }
        }
        return total.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 投影
     */
//    public ELStream projector(String expression, Class desiredResultType) {
//        List<K> list = Lists.newLinkedList();
//        for (Map<String,Object> map : maps) {
//            list.add(ELParser.getValue(expression, desiredResultType));
//        }
//        return SpringELStreamSimulater.of(list);
//    }

    /**
     * 总数
     */
    public long count() {
        return maps.size();
    }

    public boolean exists() {
        return maps.size() > 0;
    }

    public List<Map<String, Object>> list() {
        return maps;
    }


	/*public static void main(String[] args) {
		List<Map<String, Object>> list = new ArrayList<>();
		Double total = 0.0;
		for (int i = 0; i < 10; i++) {
			Map<String, Object> map = Maps.newHashMap();
			Double val = (i + 1) * 10 + (i + 1) * 0.1;
			map.put("total", val);
			total = Double.sum(total,val);
			list.add(map);
		}
		Double result = ELStream.of(list).sum("total");
		System.out.println("total=" + total);
		System.out.println("result=" + result);
	}*/
}
