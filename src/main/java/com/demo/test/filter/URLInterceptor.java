package com.demo.test.filter;

import com.alibaba.fastjson.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * URL拦截，做对应处理
 *
 */
@Component
public class URLInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(URLInterceptor.class);




    /**
     * URL 白名单
     */
    public List<String> fullMatchURL = new ArrayList<String>(){{
        add("/public/getInfo/**");//登录
//        add("/client/payment/wechat/notify/**");//支付回调
//        add("/client/public");//公共请求
    }} ;


    /**
     * 请求前置处理（后置处理同理）
     *
     * @param request
     * @param response
     * @param handler
     * @return boolean
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getServletPath();
        logger.info(path);
//        String token = request.getHeader(Cons.JWT_TOKEN);
//        if(token != null){
//            Claims claims =  JwtUtils.checkAdminJWT(token);
//            if(claims != null){
//                Integer userId = (Integer) claims.get(Cons.JWT_USER_ID);
//                User  user = userService.getById(userId);
//                if(user == null){
//                    sendJsonMessage(response, Response.buildError(Cons.ERR_CODE_LOGIN,Cons.MSG_LOGIN));
//                    return false;
//                }
//                UserContext.set(user);
//                return true;
//            }
//        }
//        sendJsonMessage(response, Response.buildError(Cons.ERR_CODE_LOGIN,Cons.MSG_LOGIN));
//        return false;
        return true;
    }

    /**
     * 响应数据给前端
     * @param response
     * @param obj
     */
    public static void sendJsonMessage(HttpServletResponse response, Object obj) throws IOException {

        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(JSONObject.toJSON(obj));
        writer.close();
        response.flushBuffer();

    }



    /**
     * 在整个请求结束之后被调用（主要是用于进行资源清理工作）
     * 一定要在请求结束后调用remove清除当前线程的副本变量值，否则会造成内存泄漏
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception ex)
            throws Exception {
//        UserContext.remove();
    }
}
