package com.yklis.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.yklis.dao.AppVisitDao;
import com.yklis.entity.AppVisitEntity;
import com.yklis.service.AppVisitApiService;
import com.yklis.service.CheckUserSignService;

@Service
public class AppVisitApiServiceImpl implements AppVisitApiService,CheckUserSignService {

    //配置容器起动时候加载log4j配置文件
    //只要将log4j.properties放在classes下，tomcat启动的时候会自动加载log4j的配置信息，
    //在程式代码不再需要使用PropertyConfigurator.configure("log4j.properties")来加载，
    //如果用了它反而会出现上面的错误--Could not read configuration file [log4jj.properties]
    //PropertyConfigurator.configure("log4jj.properties");
    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private AppVisitDao appVisitDao;
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        String sysName = request.getParameter("sysName");
        String pageName = request.getParameter("pageName");
        String ip = request.getParameter("ip");
        String customer = request.getParameter("customer");
        String userName = request.getParameter("userName");
        String actionName = request.getParameter("actionName");
        String actionTime = request.getParameter("actionTime");
        Date dateActionTime;
        try {
            dateActionTime = dateFormat.parse(actionTime);
        } catch (Exception e) {
            logger.error("参数actionTime为无效的日期时间格式:"+e.toString());
            return;
        }
        
        AppVisitEntity appVisitEntity = new AppVisitEntity();
        appVisitEntity.setSysName(sysName);
        appVisitEntity.setPageName(pageName);
        appVisitEntity.setIp(ip);
        appVisitEntity.setCustomer(customer);
        appVisitEntity.setUserName(userName);
        appVisitEntity.setActionName(actionName);
        appVisitEntity.setActionTime(dateActionTime);
        
        try{
            appVisitDao.insertAppVisit(appVisitEntity);
            
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
            logger.error("方法insertAppVisit执行报错:"+e.toString());
            
            PrintWriter printWriter = null;
            try {  
                printWriter = response.getWriter();  
                
                Map<String, Object> mapResponse = new HashMap<>();
                mapResponse.put("errorCode", -223);
                mapResponse.put("errorMsg", "方法insertAppVisit执行出错:"+e.toString());
                
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
