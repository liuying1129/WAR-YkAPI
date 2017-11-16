package com.yklis.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class RestDispatcherServlet extends DispatcherServlet {
    
	private static final long serialVersionUID = 1L;
       
    public RestDispatcherServlet() {
        super();
    }
       
    public RestDispatcherServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
    }

    /**
     * 浏览器请求时有自定义headers，则会向服务器先发一个OPTIONS请求，问问服务器是否允许该请求.服务器允许后才发送真正的get、post请求。
     * 经测试，不带自定义header的请求不会进入doOptions方法
     */ 
	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	    super.doOptions(request, response);
	    
        logger.info("RestDispatcherServlet进入doOptions方法");
	    
        //解决跨域问题：允许所有域名访问
        response.setHeader("Access-Control-Allow-Origin", "*");
        //告诉请求域，你只能在请求中添加这些请求头
        //用逗号分隔各自定义请求头键
        response.setHeader("Access-Control-Allow-Headers", "X-Access-Token");
        
        //服务器已接受请求，但尚未处理
        response.setStatus(202);
	}
}
