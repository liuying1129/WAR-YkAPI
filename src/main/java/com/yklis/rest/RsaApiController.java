package com.yklis.rest;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yklis.util.MySingleton;
import com.yklis.util.RSAUtil;

//传统MVC控制器与RESTful web service控制器的一个关键区别在于：
//HTTP响应体的创建方式。
//前者采用视图层技术（view technology）实现把服务器端的数据渲染为HTML
//后者则返回一个对象。对象数据将会直接以JSON格式写到Response对象的body数据区
@Controller
@RequestMapping("/")
public class RsaApiController {
    
    //配置容器起动时候加载log4j配置文件
    //只要将log4j.properties放在classes下，tomcat启动的时候会自动加载log4j的配置信息，
    //在程式代码不再需要使用PropertyConfigurator.configure("log4j.properties")来加载，
    //如果用了它反而会出现上面的错误--Could not read configuration file [log4jj.properties]
    //PropertyConfigurator.configure("log4jj.properties");
    private Logger logger = Logger.getLogger(this.getClass());

    @RequestMapping("rsa")
    @ResponseBody
    //@ResponseBody表示该方法的返回结果直接写入Response对象的body数据区
    //一般的使用时机：返回的数据不是html标签的页面，而是其他某种格式的数据时（如json、xml等）使用
    public Map<String, Object> rsa(HttpServletResponse response){
        
        //生成密钥对服务，并将密钥对保存在服务器的单例类MySingleton中        
        
        MySingleton mySingleton = MySingleton.getInstance();
        KeyPair keyPair = mySingleton.getKeyPair();
        
        if(null == keyPair){
            try {
                keyPair = RSAUtil.generateKeyPair();
            } catch (Exception e) {         
                logger.error("生成密钥对失败:"+e.toString());
            }
            
            if(null == keyPair){
                Map<String, Object> mapResponse = new HashMap<>();
                mapResponse.put("errorCode", -123);
                mapResponse.put("errorMsg", "生成密钥对失败");
                
                Map<String, Object> map = new HashMap<>();
                map.put("success", false);
                map.put("response", mapResponse);

                return map;
            }
            mySingleton.setKeyPair(keyPair);    
        }           
                
        //“外部系统”通过exponent、modulus还原出公钥
        String exponent = RSAUtil.getExponent((RSAPublicKey) keyPair.getPublic());
        String modulus = RSAUtil.getModulus((RSAPublicKey) keyPair.getPublic());
         
        Map<String, Object> mapResponse = new HashMap<>();
        mapResponse.put("exponent", exponent);
        mapResponse.put("modulus", modulus);
        
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("response", mapResponse);
        
        return map;
    }
}
