package com.seebon.rpa.common.security.server;

import com.seebon.rpa.response.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

/**
 * @author ZhenShijun
 * @dateTime 2022-11-24 15:54:53
 */
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

    @Override
    public ResponseEntity<ResponseResult> translate(Exception e) throws Exception {
        ResponseResult response = resolveException(e);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 构建返回异常
     * @param e exception
     * @return
     */
    private ResponseResult resolveException(Exception e) {
        // 初始值 500
        int code = ResponseResult.ERROR;
        String errorMsg = "客户端认证失败";
        int httpStatus = HttpStatus.UNAUTHORIZED.value();
        //不支持的认证方式
        if(e instanceof UnsupportedGrantTypeException){
            errorMsg = "不支持的认证模式";
            //用户名或密码异常
        }else if(e instanceof InvalidGrantException){
            errorMsg = "用户名或密码错误";
        } else if (e instanceof RuntimeException) {
            errorMsg = StringUtils.isBlank(e.getMessage())?"认证失败":e.getMessage();
        }
        ResponseResult result = new ResponseResult();
        result.setCode(code);
        result.setMessage(errorMsg);
        return result;
    }
}
