package com.yklis.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;


/**
 * 使用 @ControllerAdvice + @ExceptionHandler 进行全局的 Controller 层异常处理，
 * 只要设计得当，就再也不用在 Controller 层进行 try-catch 了！
 * 而且，@Validated 校验器注解的异常，也可以一起处理，无需手动判断绑定校验结果 BindingResult/Errors 了！
 * 
 * 优点：将 Controller 层的异常和数据校验的异常进行统一处理，减少模板代码，减少编码量，提升扩展性和可维护性。
 * 缺点：只能处理 Controller 层未捕获（往外抛）的异常，对于 Interceptor（拦截器）层的异常，Spring 框架层的异常，就无能为力了。
 * 
 * @author liuying
 * 
 * 注:确保此类能被扫描、装载进Spring容器中
 *
 */

@ControllerAdvice//控制器增强
@ResponseBody
public class GlobalExceptionHandler {
	
    //配置容器起动时候加载log4j配置文件
    //只要将log4j.properties放在classes下，tomcat启动的时候会自动加载log4j的配置信息，
    //在程式代码不再需要使用PropertyConfigurator.configure("log4j.properties")来加载，
    //如果用了它反而会出现上面的错误--Could not read configuration file [log4jj.properties]
    //PropertyConfigurator.configure("log4jj.properties");
    private final Logger logger = Logger.getLogger(this.getClass());

	@ExceptionHandler()
	public String handleException(Exception e){
		
		logger.error("GlobalExceptionHandler.handleException捕获异常:"+e.toString());
		
        Map<String, Object> mapResponse = new HashMap<>();
        mapResponse.put("errorCode", -111);
        mapResponse.put("errorMsg", e.toString());
        
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("response", mapResponse);
        
        return JSON.toJSONString(map);
	}
}
