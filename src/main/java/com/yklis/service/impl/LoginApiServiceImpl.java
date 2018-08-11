package com.yklis.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import com.google.gson.Gson;
import com.yklis.entity.ApiTokenEntity;
import com.yklis.entity.WorkerEntity;
import com.yklis.service.ApiTokenService;
import com.yklis.service.CheckUserSignService;
import com.yklis.service.CommonApiService;
import com.yklis.service.WorkerService;
import com.yklis.util.MySingleton;
import com.yklis.util.RSAUtil;

/**
 * 工厂模式
 * 实现类
 * 
 * 登录服务
 * 每次登录成功则生成新的token
 * 
 * token即令牌，用于校验“受限制服务的请求”的合法性
 * 
 * token=userId+时间戳，进行MD5编码
 * 
 * 服务端将token保存在数据库中，
 * userId为主键，每个userId对应一个token
 * 
 * “调用方”将token保存在本地
 * 
 * “调用方”请求时收到“令牌无效”，则需要做重新登录的处理
 * 
 * token可设置有效期，每次校验token有效后，会重置有效期
 * 
 * 响应头信息中返回这个的信息，目的是把这个 token 存储到浏览器的本地存储中
 * 
 * @author ying07.liu
 *
 */
public class LoginApiServiceImpl implements CommonApiService,CheckUserSignService {

    //配置容器起动时候加载log4j配置文件
    //只要将log4j.properties放在classes下，tomcat启动的时候会自动加载log4j的配置信息，
    //在程式代码不再需要使用PropertyConfigurator.configure("log4j.properties")来加载，
    //如果用了它反而会出现上面的错误--Could not read configuration file [log4jj.properties]
    //PropertyConfigurator.configure("log4jj.properties");
    private Logger logger = Logger.getLogger(this.getClass());
    
    //to-do:不能自动注入
    @Autowired
    private WorkerService workerService;
    
    @Autowired
    private ApiTokenService apiTokenService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) {
       
        //登录服务
        
        String userId = request.getParameter("userId");
        
        logger.info("访问login服务start,userId:["+userId+"]");
        
        String rsaPassWord = request.getParameter("passWord");//如果没有这个参数，则返回值为null
        if(null == rsaPassWord) rsaPassWord = "";
        
        //对“外部系统”传到服务端的密码进行解密 start
        MySingleton mySingleton1 = MySingleton.getInstance();
        KeyPair keyPair1 = mySingleton1.getKeyPair();
        if(null == keyPair1){
            PrintWriter printWriter1 = null;
            try {  
                printWriter1 = response.getWriter();  
                
                Map<String, Object> mapResponse = new HashMap<>();
                mapResponse.put("errorCode", -123);
                mapResponse.put("errorMsg", "密钥对为空");
                
                Map<String, Object> map = new HashMap<>();
                map.put("success", false);
                map.put("response", mapResponse);

                Gson gson = new Gson();
                printWriter1.print(gson.toJson(map));
            } catch (IOException e) {  
                logger.error("response.getWriter失败:"+e.toString());
            } finally {  
                if (printWriter1 != null) printWriter1.close();
            }
            return;
        }
        
        String passWord = null;
        try {
            passWord = RSAUtil.decryptDataFromJs(keyPair1.getPrivate(), rsaPassWord);
        } catch (Exception e) {         
            logger.error("解密出错:"+e.toString());
        }
        if(null == passWord){
            PrintWriter printWriter1 = null;
            try {  
                printWriter1 = response.getWriter();  
                
                Map<String, Object> mapResponse = new HashMap<>();
                mapResponse.put("errorCode", -123);
                mapResponse.put("errorMsg", "解密出错");
                
                Map<String, Object> map = new HashMap<>();
                map.put("success", false);
                map.put("response", mapResponse);

                Gson gson = new Gson();
                printWriter1.print(gson.toJson(map));
            } catch (IOException e) {  
                logger.error("response.getWriter失败:"+e.toString());
            } finally {  
                if (printWriter1 != null) printWriter1.close();
            }
            return;
        }
        //对“外部系统”传到服务端的密码进行解密 stop
                        
        if((null == userId)||("".equals(userId))){
            PrintWriter printWriter1 = null;
            try {  
                printWriter1 = response.getWriter();  
                
                Map<String, Object> mapResponse = new HashMap<>();
                mapResponse.put("errorCode", -123);
                mapResponse.put("errorMsg", "用户不能为空");
                
                Map<String, Object> map = new HashMap<>();
                map.put("success", false);
                map.put("response", mapResponse);

                Gson gson = new Gson();
                printWriter1.print(gson.toJson(map));
            } catch (IOException e) {  
                logger.error("response.getWriter失败:"+e.toString());
            } finally {  
                if (printWriter1 != null) printWriter1.close();
            }
            return;
        }       
                    
        //passWord为null时Mybatis并不会作为空字符串""处理
        List<WorkerEntity> workerList = workerService.ifCanLogin(userId, passWord);
        
        if((workerList == null)||(workerList.isEmpty())){
            
            PrintWriter printWriter1 = null;
            try {  
                printWriter1 = response.getWriter();  
                
                Map<String, Object> mapResponse = new HashMap<>();
                mapResponse.put("errorCode", -123);
                mapResponse.put("errorMsg", "用户或密码错误");
                
                Map<String, Object> map = new HashMap<>();
                map.put("success", false);
                map.put("response", mapResponse);

                Gson gson = new Gson();
                printWriter1.print(gson.toJson(map));
                //printWriter.print("{\"msg\":\"用户或密码错误\"}");
            } catch (IOException e) {  
                logger.error("response.getWriter失败:"+e.toString());
            } finally {  
                if (printWriter1 != null) printWriter1.close();
            }
            return;
        }           
        
        //生成token并保存 start          
        DateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmssSSS");//SSS表示毫秒
        String timeStamp = dateFormat.format(new Date());//时间戳
        
        //token令牌
        String expressToken = userId+timeStamp;
        String token = DigestUtils.md5DigestAsHex(expressToken.getBytes());
        
        PrintWriter printWriter1 = null;
        try {  
            printWriter1 = response.getWriter();  
            //JSON的要求
            //key:必须要用双引号
            //value:数值类型可不用双引号，字符串类型必须要用双引号
            //否则前端(angularJS $http)调用时会返回error
            //printWriter.print("{\"token\":\""+token+"\"}");
            
            Map<String, Object> mapResponse = new HashMap<>();
            mapResponse.put("token", token);
            
            Map<String, Object> map = new HashMap<>();
            map.put("success", true);
            map.put("response", mapResponse);

            Gson gson = new Gson();
            printWriter1.print(gson.toJson(map));
        } catch (IOException e) {  
            logger.error("response.getWriter失败:"+e.toString());
        } finally {  
            if (printWriter1 != null) printWriter1.close();
        } 
        
        //保存token start
        ApiTokenEntity apiTokenEntity = new ApiTokenEntity();
        apiTokenEntity.setUserId(userId);
        List<ApiTokenEntity> apiTokenList = apiTokenService.selectApiToken(apiTokenEntity);
                    
        ApiTokenEntity apiTokenEntityEdit = new ApiTokenEntity();
        apiTokenEntityEdit.setUserId(userId);
        apiTokenEntityEdit.setToken(token);       
        apiTokenEntityEdit.setModDateTime(new Date());
        
        if((null == apiTokenList)||(apiTokenList.isEmpty())){
            apiTokenService.insertApiToken(apiTokenEntityEdit);
        }else{                  
            apiTokenService.updateApiToken(apiTokenEntityEdit);
        }
        //保存token stop
        //生成token并保存 stop       

        logger.info("访问login服务stop,userId:["+userId+"],token:["+token+"]");


    }

}
