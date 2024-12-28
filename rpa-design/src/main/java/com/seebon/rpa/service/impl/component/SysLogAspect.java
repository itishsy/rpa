package com.seebon.rpa.service.impl.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.seebon.rpa.annotation.MyLog;
import com.seebon.rpa.auth.SecurityContext;
import com.seebon.rpa.auth.Session;
import com.seebon.rpa.entity.SysLog;
import com.seebon.rpa.repository.mongodb.SysLogRepository;
import com.seebon.rpa.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZhenShijun
 * @dateTime 2021-04-07 17:28:06
 */
@Slf4j
@Aspect
@Component
public class SysLogAspect {

    @Autowired
    private SysLogRepository sysLogRepository;

    @Before("@annotation(com.seebon.rpa.annotation.MyLog)")
    public void runBefore() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        long beginNaoTime = System.nanoTime();
        request.setAttribute("beginNaoTime", beginNaoTime);
    }

    /**
     * 切面 配置通知
     * 用于记录有正常返回的操作
     * @param joinPoint
     * @param resultVO
     */
    @AfterReturning(returning="resultVO", pointcut = "@annotation(com.seebon.rpa.annotation.MyLog)")
    public void saveSysLog(JoinPoint joinPoint, Object resultVO) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        long endNaoTime = System.nanoTime();
        long beginNaoTime = (Long)request.getAttribute("beginNaoTime");

        BigDecimal time = new BigDecimal(endNaoTime).subtract(new BigDecimal(beginNaoTime)).divide(new BigDecimal("1000000"), 0);

        // 保存操作日志信息
        SysLog sysLog = new SysLog();
        sysLog.setState(1);//返回正常结果
        sysLog.setTime(time.longValue());
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();
        MyLog myLog = method.getAnnotation(MyLog.class);
        sysLog.setResult(resultVO);
        saveLog(joinPoint, sysLog);
    }

    /**
     * 切面 配置通知
     * 用于记录异常的操作
     * @param joinPoint
     * @param error
     */
    @AfterThrowing(throwing="error", pointcut = "@annotation(com.seebon.rpa.annotation.MyLog)")
    public void saveThrowingLog(JoinPoint joinPoint, Throwable error) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        long endNaoTime = System.nanoTime();
        long beginNaoTime = (Long)request.getAttribute("beginNaoTime");

        BigDecimal time = new BigDecimal(endNaoTime).subtract(new BigDecimal(beginNaoTime)).divide(new BigDecimal("1000000"), 0);
        SysLog sysLog = new SysLog();
        sysLog.setTime(time.longValue());
        sysLog.setState(2);// 调用有抛异常
        sysLog.setResult(JSON.toJSONString(error));
        saveLog(joinPoint, sysLog);
    }

    private void saveLog(JoinPoint joinPoint, SysLog sysLog) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();
        //获取操作
        MyLog myLog = method.getAnnotation(MyLog.class);

        // 获取系统当前的操作用户进行塞值
        try{
            Session session = SecurityContext.currentUser();
            sysLog.setCustId(session.getCustId());
            sysLog.setCompany(session.getCustName());
            sysLog.setUserId((int)session.getId());
            sysLog.setUserName(session.getUsername());
            sysLog.setRealName(session.getName());
        }catch (Exception e) {
            e.printStackTrace();
        }
        sysLog.setCreateTime(new Date());
        if (myLog != null) {
            String moduleName = myLog.moduleName();
            sysLog.setModuleName(moduleName);

            String pageName = myLog.pageName();
            sysLog.setPageName(pageName);

            String operation = myLog.operation();
            sysLog.setOperation(operation);

            String prestatus = myLog.prestatus();
            sysLog.setPrestatus(prestatus);

            String poststatus = myLog.poststatus();
            sysLog.setPoststatus(poststatus);

            String callType = myLog.callType();
            sysLog.setCallType(callType);
        }

        //获取请求的类名
        String className = joinPoint.getTarget().getClass().getName();
        //获取请求的方法名
        String methodName = method.getName();
        sysLog.setMethod(className + "." + methodName);

        //请求的参数
        List<Object> args = Lists.newArrayList(joinPoint.getArgs());
        args = args.stream().filter(item -> !(item instanceof HttpServletRequest || item instanceof HttpServletResponse)).collect(Collectors.toList());
        //将参数所在的数组转换成json
        String params = JSON.toJSONString(args,SerializerFeature.IgnoreNonFieldGetter);
        sysLog.setParams(params);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = IpUtils.getIpAddr2(request) ;
        sysLog.setIp(ip);
        String requestURI = request.getRequestURI();
        sysLog.setUrl(requestURI);
        StringBuffer requestURL = request.getRequestURL();

        log.info("uri ----------> {},  url ------------> {}", requestURI, requestURL);

        // 保存日志信息入库
        sysLogRepository.inset(sysLog);
    }

    public String getOpenIdByLoginUuid(String loginUuid) {
        return loginUuid.split(":")[0];
    }

    public String getPhoneByLoginUuid(String loginUuid) {
        return loginUuid.split(":")[1];
    }
}
