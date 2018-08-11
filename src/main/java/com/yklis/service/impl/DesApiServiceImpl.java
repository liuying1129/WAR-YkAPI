package com.yklis.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import com.google.gson.Gson;
import com.yklis.service.CheckUserTokenService;
import com.yklis.service.CommonApiService;
import com.yklis.util.CommFunction;

/**
 * 工厂模式
 * 实现类
 * @author liuying
 *
 */
public class DesApiServiceImpl implements CommonApiService,CheckUserTokenService {

    //配置容器起动时候加载log4j配置文件
    //只要将log4j.properties放在classes下，tomcat启动的时候会自动加载log4j的配置信息，
    //在程式代码不再需要使用PropertyConfigurator.configure("log4j.properties")来加载，
    //如果用了它反而会出现上面的错误--Could not read configuration file [log4jj.properties]
    //PropertyConfigurator.configure("log4jj.properties");
    private Logger logger = Logger.getLogger(this.getClass());
    
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) {
		
		String source = request.getParameter("source");
		String key = request.getParameter("key");
		
		String enCryptStr = CommFunction.enCryptStr(source, key);
		
        PrintWriter printWriter = null;
        try {  
            printWriter = response.getWriter();  
            
            Map<String, Object> mapResponse = new HashMap<>();
            mapResponse.put("enCryptStr", enCryptStr);
            
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
	}
}
