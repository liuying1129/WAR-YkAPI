package com.yklis.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.yklis.dao.ExtSqlDao;
import com.yklis.service.ExtSqlService;
import com.yklis.util.CustomerContextHolder;

@Service
public class ExtSqlServiceImpl implements ExtSqlService {

    //配置容器起动时候加载log4j配置文件
    //只要将log4j.properties放在classes下，tomcat启动的时候会自动加载log4j的配置信息，
    //在程式代码不再需要使用PropertyConfigurator.configure("log4j.properties")来加载，
    //如果用了它反而会出现上面的错误--Could not read configuration file [log4jj.properties]
    //PropertyConfigurator.configure("log4jj.properties");
    private transient Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ExtSqlDao extSqlDao;
        
    @Override
    public String runExtSql(String extSqlNum){
                    
        Map<String, Object> mapExtSql;
        try{
            mapExtSql = jdbcTemplate.queryForMap("select * from CommCode where Unid="+extSqlNum+" and TypeName='对外接口' ");
        }catch(EmptyResultDataAccessException e){
            
            Map<String, Object> mapResponse = new HashMap<>();
            mapResponse.put("errorCode", -123);
            mapResponse.put("errorMsg", "无效的SQL编码:"+extSqlNum);
            
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("response", mapResponse);
            
            return JSON.toJSONString(map);
        }catch(Exception e){
            
            Map<String, Object> mapResponse = new HashMap<>();
            mapResponse.put("errorCode", -123);
            mapResponse.put("errorMsg", e.toString());
            
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("response", mapResponse);
            
            return JSON.toJSONString(map);
        }
        
        String extSql = null==mapExtSql.get("Reserve2")?"":mapExtSql.get("Reserve2").toString();
        int jdbcUnid = null==mapExtSql.get("Reserve5")?-1:Integer.parseInt(mapExtSql.get("Reserve5").toString());
                      
            
        //切换数据源的变量准备工作start
        Map<String, Object> customerTypeMap = null;
        try{
            customerTypeMap = jdbcTemplate.queryForMap("select Reserve as driverClass,Reserve2 as url,Reserve3 as 'user',Reserve4 as password from CommCode where Unid="+jdbcUnid+" and TypeName='JDBC连接字符串' ");
        }catch(EmptyResultDataAccessException e){
            //取不数据是正常业务逻辑,有可能不需要切换数据源
        }catch(Exception e){
            
            Map<String, Object> mapResponse = new HashMap<>();
            mapResponse.put("errorCode", -123);
            mapResponse.put("errorMsg", "获取数据源连接信息失败:"+e.toString());
            
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("response", mapResponse);
            
            return JSON.toJSONString(map);
        }
        //切换数据源的变量准备工作stop
        
        logger.info("切换连接信息:"+JSON.toJSONString(customerTypeMap));

        try{   
            
            //if((null!=customerTypeMap)&&(!customerTypeMap.isEmpty())){
                
                logger.info("切换DB");
                CustomerContextHolder.setCustomerType(customerTypeMap);
            //}
            
            
            if(extSql.toLowerCase().indexOf("update ")>=0){
                
                try{
                    
                    extSqlDao.updateSql(extSql);
                }catch(Exception e){
                    
                    Map<String, Object> mapResponse = new HashMap<>();
                    mapResponse.put("errorCode", -123);
                    mapResponse.put("errorMsg", e.toString());
                    
                    Map<String, Object> map = new HashMap<>();
                    map.put("success", false);
                    map.put("response", mapResponse);
                    
                    return JSON.toJSONString(map);
                }
                
                Map<String, Object> map = new HashMap<>();
                map.put("success", true);
                map.put("response", "update执行成功");
                
                return JSON.toJSONString(map);

            }else if(extSql.toLowerCase().indexOf("insert ")>=0){
                           
                try{
                    
                    extSqlDao.insertSql(extSql);
                }catch(Exception e){
                    
                    Map<String, Object> mapResponse = new HashMap<>();
                    mapResponse.put("errorCode", -123);
                    mapResponse.put("errorMsg", e.toString());
                    
                    Map<String, Object> map = new HashMap<>();
                    map.put("success", false);
                    map.put("response", mapResponse);
                    
                    return JSON.toJSONString(map);
                }
                
                Map<String, Object> map = new HashMap<>();
                map.put("success", true);
                map.put("response", "insert执行成功");
                
                return JSON.toJSONString(map);
                
            }else{

                List<Map<String, Object>> list;
                try{
                    
                    list = extSqlDao.selectSql(extSql);
                }catch(Exception e){
                    
                    Map<String, Object> mapResponse = new HashMap<>();
                    mapResponse.put("errorCode", -123);
                    mapResponse.put("errorMsg", e.toString());
                    
                    Map<String, Object> map = new HashMap<>();
                    map.put("success", false);
                    map.put("response", mapResponse);
                    
                    return JSON.toJSONString(map);
                }
                
                Map<String, Object> map = new HashMap<>();
                map.put("success", true);
                map.put("response", list);
                
                return JSON.toJSONStringWithDateFormat(map, "yyyy-MM-dd HH:mm:ss");
            }            
            
        }catch(Exception e){
            
            Map<String, Object> mapResponse = new HashMap<>();
            mapResponse.put("errorCode", -123);
            mapResponse.put("errorMsg", "切换数据源，执行出错:"+e.toString());
            
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("response", mapResponse);
            
            return JSON.toJSONString(map);
        }finally{
            CustomerContextHolder.clearCustomerType();
        }        

    }

}
