package com.seebon.rpa.utils;


import com.seebon.rpa.BusinessException;

import java.util.Collection;
import java.util.Map;

/**
 * 参数校验工具类
 *
 * @author xufz
 */
public final class Assert {

    /**
     * 判断包装数值类型是否不等于空并大于0，否则抛错
     *
     * @param obj
     * @param tips
     */
    public static void isGreaterThanZero(Object obj, String tips) {
        if (obj != null) {
            if ((obj instanceof Integer && ((Integer) obj) > 0) || (obj instanceof Long && ((Long) obj) > 0L) || (obj instanceof Double && ((Double) obj) > 0.0D)
                    || (obj instanceof Float && ((Float) obj) > 0.0F)) {
                return;
            }
        }
        throw new BusinessException(tips);
    }

    /**
     * obj等于null时，抛错
     *
     * @param obj
     * @param tips
     * @throws BusinessException
     */
    public static void isNotNull(Object obj, String tips) {
        if (obj == null) {
            throw new BusinessException(tips);
        }
    }

    /**
     * bool不等于true时，抛错
     *
     * @param bool
     * @param tips
     * @throws BusinessException
     */
    public static void isTrue(boolean bool, String tips) {
        if (!bool) {
            throw new BusinessException(tips);
        }
    }

    /**
     * 字符串s为null或trim后为空串时，抛错
     *
     * @param s
     * @param tips
     * @throws BusinessException
     */
    public static void isNotBlank(String s, String tips) {
        if (s == null || s.trim().length() == 0) {
            throw new BusinessException(tips);
        }
    }


    /**
     * 数组为null或空时，抛错
     *
     * @param arr
     * @param tips
     * @throws BusinessException
     */
    public static void isNotEmptyArrayOfInt(int[] arr, String tips) {
        if (arr == null || arr.length == 0) {
            throw new BusinessException(tips);
        }
    }

    /**
     * 集合为null或空时，抛错
     *
     * @param c
     * @param tips
     * @throws BusinessException
     */
    public static void isNotEmptyCollection(Collection<?> c, String tips) {
        if (c == null || c.size() == 0) {
            throw new BusinessException(tips);
        }
    }


    /**
     * map为null或空时，抛错
     *
     * @param c
     * @param tips
     * @throws BusinessException
     */
    public static void isNotEmptyMap(Map<?, ?> c, String tips) {
        if (c == null || c.size() == 0) {
            throw new BusinessException(tips);
        }
    }
}
