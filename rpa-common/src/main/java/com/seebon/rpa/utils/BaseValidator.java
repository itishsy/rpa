package com.seebon.rpa.utils;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 用于DTO上面的增加了javax.validation.constraints注解的进行校验，例如@NotNull,@Min等
 *
 * @author 作者 :邹俊飞
 * @modify liuhongchang
 * @since 创建时间：2017/3/1
 */
public class BaseValidator {
    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static String nullMessage ="校验对象为空";

    /**
     * 用于DTO上面的增加了javax.validation.constraints注解的进行校验，例如@NotNull,@Min等
     * @param t 需要校验的DTO
     * @param <T>
     * @throws RuntimeException 将注解上的message信息
     */
    public static <T> void validate(T t) throws RuntimeException {
        if (null == t) {
            throw new RuntimeException(nullMessage);
        }
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);

        List<String> messageList = new ArrayList<>();
        for (ConstraintViolation<T> constraintViolation : constraintViolations) {
            messageList.add(constraintViolation.getMessage());
        }
        if (messageList.size() > 0) {
            throw new RuntimeException(StringUtils.join(messageList, "|"));
        }
    }

    /**
     * 用于DTO上面的增加了javax.validation.constraints注解的进行校验，例如@NotNull,@Min等
     * @param t 需要校验的DTO
     * @param <T>
     * @return 返回注解上的message信息
     */
    public static <T> List<String> validateAndReturn(T t) {
        List<String> messageList = new ArrayList<>();
        if (null == t) {
            messageList.add(nullMessage);
            return messageList;
        }
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);

        for (ConstraintViolation<T> constraintViolation : constraintViolations) {
            messageList.add(constraintViolation.getMessage());
        }
        return messageList;
    }
}
