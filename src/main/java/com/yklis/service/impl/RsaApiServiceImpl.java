package com.yklis.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.yklis.service.RsaApiService;
import com.yklis.util.MySingleton;
import com.yklis.util.RSAUtil;

/**
 * 该服务向“调用方”提供RSA加密的exponent、modulus
 * “调用方”通过exponent、modulus还原出公钥
 * 
 * RSA公钥用于“外部系统”的敏感信息加密
 * 
 * “调用方”其他请求的敏感信息（如登录请求的密码）用RSA加密后才传输到服务端
 * “API服务”收到敏感信息后用RSA私钥解密
 * 
 * “API服务”将生成的RSA密钥对保存在服务端的全局变量中
 * 
 * “外部系统”不保存RSA公钥，每次需要时去请求
 * 
 * @author ying07.liu
 *
 */
@Service
public class RsaApiServiceImpl implements RsaApiService {

    //配置容器起动时候加载log4j配置文件
    //只要将log4j.properties放在classes下，tomcat启动的时候会自动加载log4j的配置信息，
    //在程式代码不再需要使用PropertyConfigurator.configure("log4j.properties")来加载，
    //如果用了它反而会出现上面的错误--Could not read configuration file [log4jj.properties]
    //PropertyConfigurator.configure("log4jj.properties");
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        
        //生成密钥对服务，并将密钥对保存在服务器的单例类MySingleton中
        
        logger.info("访问RSA服务start");
                    
        MySingleton mySingleton = MySingleton.getInstance();
        KeyPair keyPair = mySingleton.getKeyPair();
        
        if(null == keyPair){
            try {
                keyPair = RSAUtil.generateKeyPair();
            } catch (Exception e) {         
                logger.error("生成密钥对失败:"+e.toString());
            }
            
            if(null == keyPair){
                PrintWriter printWriter = null;
                try {  
                    printWriter = response.getWriter();  
                    
                    Map<String, Object> mapResponse = new HashMap<>();
                    mapResponse.put("errorCode", -123);
                    mapResponse.put("errorMsg", "生成密钥对失败");
                    
                    Map<String, Object> map = new HashMap<>();
                    map.put("success", false);
                    map.put("response", mapResponse);

                    Gson gson = new Gson();
                    printWriter.print(gson.toJson(map));
                } catch (IOException e) {  
                    logger.error("response.getWriter失败:"+e.toString());
                } finally {  
                    if (printWriter != null) printWriter.close();
                }
                return;
            }
            mySingleton.setKeyPair(keyPair);    
        }           
                
        String exponent = RSAUtil.getExponent((RSAPublicKey) keyPair.getPublic());
        String modulus = RSAUtil.getModulus((RSAPublicKey) keyPair.getPublic());
        
        PrintWriter printWriter = null;
        try {  
            printWriter = response.getWriter();  
            
            Map<String, Object> mapResponse = new HashMap<>();
            mapResponse.put("exponent", exponent);
            mapResponse.put("modulus", modulus);
            
            Map<String, Object> map = new HashMap<>();
            map.put("success", true);
            map.put("response", mapResponse);
            
            Gson gson = new Gson();
            printWriter.print(gson.toJson(map));
        } catch (IOException e) {  
            logger.error("response.getWriter失败:"+e.toString());
        } finally {  
            if (printWriter != null) printWriter.close();
        }
        
        logger.info("访问RSA服务stop");

    }

}
