package com.seebon.rpa.response;

import com.alibaba.fastjson.JSON;
import com.seebon.rpa.BusinessException;
import com.seebon.rpa.DefinedBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


@Component
@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ResponseHandler implements ResponseBodyAdvice<Object> {

    /**
     * 业务异常处理
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public ResponseResult bizErrorHandler(BusinessException ex) {
        ResponseResult result = new ResponseResult();
        result.setCode(ResponseResult.ERROR);
        result.setMessage(ex.getMessage());
        result.setData(ex.getContent());
        return result;
    }

    /**
     * 自定义返回信息处理（返回成功状态）
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = DefinedBusinessException.class)
    public ResponseResult bizErrorHandler(DefinedBusinessException ex) {
        ResponseResult result = new ResponseResult();
        result.setCode(ResponseResult.SUCCESS);
        result.setMessage(ex.getMessage());
        result.setData(ex.getContent());
        return result;
    }

    /**
     * 其他异常捕捉处理
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseResult errorHandler(Exception ex) {
        ResponseResult result = new ResponseResult();
        result.setCode(ResponseResult.ERROR);
        if (StringUtils.isNotBlank(ex.getMessage()) && !ex.getMessage().equals("运行时发生异常")) {
            result.setMessage(ex.getMessage());
        } else {
            result.setMessage("运行时发生异常");
        }
        result.setData(ex.getMessage());
        ex.printStackTrace();
        return result;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }


    @Override
    public Object beforeBodyWrite(Object data, MethodParameter returnType, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (data instanceof ResponseResult) {
            return data;
        } else {
            ResponseResult result = new ResponseResult();
            result.setCode(ResponseResult.SUCCESS);
            result.setMessage(ResponseResult.SUCCESS_MESSAGE);
            result.setData(data);
            if (data instanceof String) {
                return JSON.toJSONString(result);
            }
            return result;
        }
    }
}
