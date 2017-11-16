package com.yklis.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.yklis.service.CheckUserSignService;
import com.yklis.service.ExecSQLCmdApiService;

@Service
public class ExecSQLCmdApiServiceImpl implements ExecSQLCmdApiService,CheckUserSignService {

    //配置容器起动时候加载log4j配置文件
    //只要将log4j.properties放在classes下，tomcat启动的时候会自动加载log4j的配置信息，
    //在程式代码不再需要使用PropertyConfigurator.configure("log4j.properties")来加载，
    //如果用了它反而会出现上面的错误--Could not read configuration file [log4jj.properties]
    //PropertyConfigurator.configure("log4jj.properties");
    private Logger logger = Logger.getLogger(this.getClass());
    
    @Autowired
    JdbcTemplate jdbcTemplate;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) {
		
		String sql = request.getParameter("sql");
		
    	if((null == sql)||("".equals(sql))){
    		PrintWriter printWriter = null;
    	    try {  
    	    	printWriter = response.getWriter();  
                
                Map<String, Object> mapResponse = new HashMap<>();
                mapResponse.put("errorCode", -123);
                mapResponse.put("errorMsg", "sql为空!");
                
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
    	
    	try{
    		jdbcTemplate.update(sql);
    		
    		PrintWriter printWriter = null;
    	    try {  
    	    	printWriter = response.getWriter();  
                
                Map<String, Object> mapResponse = new HashMap<>();
                mapResponse.put("id", -1);
                mapResponse.put("msg", "sql执行成功");
                
                Map<String, Object> map = new HashMap<>();
                map.put("success", true);
                map.put("response", mapResponse);
                
    	    	Gson gson = new Gson();
    	    	printWriter.print(gson.toJson(map));
    	    } catch (IOException e1) {  
    	    	logger.error("response.getWriter失败:"+e1.toString());
    	    } finally {  
    	        if (printWriter != null) printWriter.close();
    	    }  

    	}catch(Exception e){
    		PrintWriter printWriter = null;
    	    try {  
    	    	printWriter = response.getWriter();  
                
                Map<String, Object> mapResponse = new HashMap<>();
                mapResponse.put("errorCode", -223);
                mapResponse.put("errorMsg", "sql执行出错:"+e.toString()+"。错误的SQL:"+sql);
                
                Map<String, Object> map = new HashMap<>();
                map.put("success", false);
                map.put("response", mapResponse);
                
    	    	Gson gson = new Gson();
    	    	printWriter.print(gson.toJson(map));
    	    	logger.info(gson.toJson(map));
    	    } catch (IOException e1) {  
    	    	logger.error("response.getWriter失败:"+e1.toString());
    	    } finally {  
    	        if (printWriter != null) printWriter.close();
    	    }  
    	}		
	}

}
