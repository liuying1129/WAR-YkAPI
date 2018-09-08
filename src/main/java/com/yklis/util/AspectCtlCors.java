package com.yklis.util;

import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * rest包的Controller方法执行前处理跨域请求问题及其他设置
 * @author ying07.liu
 *
 */

//通过@AspectJ将类标识为切面
@Aspect
public class AspectCtlCors {

    //配置容器起动时候加载log4j配置文件
    //只要将log4j.properties放在classes下，tomcat启动的时候会自动加载log4j的配置信息，
    //在程式代码不再需要使用PropertyConfigurator.configure("log4j.properties")来加载，
    //如果用了它反而会出现上面的错误--Could not read configuration file [log4jj.properties]
    //PropertyConfigurator.configure("log4jj.properties");
    //private final Logger logger = Logger.getLogger(this.getClass());
    
    //Spring支持的5种增强：
    //前置增强:表示在目标方法执行前实施增强
    //后置增强:表示在目标方法执行后实施增强
    //环绕增强:表示在目标方法执行前后实施增强.可替代前、后置增强
    //抛出异常增强:表示在目标方法抛出异常后实施增强
    //引介增强:表示在目标类中添加一些新的方法和属性
    
    //定义增强类型:Before,前置增强；切点：由切点表达式确定
    @Before("execution(* com.yklis.controller.*.*(..))")
    //增强的横切逻辑
    public void doBefore(JoinPoint joinPoint) {  
        
        //logger.info("执行切面方法");

        //SpringMVC中获取response对象
        HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
        
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setCharacterEncoding("UTF-8"); 
    }  
}
