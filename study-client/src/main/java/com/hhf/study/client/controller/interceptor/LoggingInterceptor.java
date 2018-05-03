package com.hhf.study.client.controller.interceptor;

import com.hhf.study.client.entity.LoggingEntity;
import com.hhf.study.client.service.LoggingService;
import com.hhf.study.client.utils.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author huanghongfei
 * @Description
 * @Date Create in 14:57 2018/3/26
 */

@Component
public class LoggingInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log= LoggerFactory.getLogger(LoggingInterceptor.class);

    @Autowired
    private LoggingService loggingService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info(">>> {}",request.getRequestURI());
        loggingService.add(new LoggingEntity(request.getSession().getId(),request.getRequestURI(), RequestUtils.getIpAddress(request),request.getHeader("user-agent"),request.getQueryString(),response.getStatus(),null));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoggingEntity loggingEntity=loggingService.getBySessionIdAndRequestURIAndCreateTimeMax(request.getSession().getId(),request.getRequestURI());
        loggingEntity.setStatus(response.getStatus());
        if(ex!=null){
            loggingEntity.setException(ex.getMessage());
        }
        loggingService.update(loggingEntity);
    }
}
