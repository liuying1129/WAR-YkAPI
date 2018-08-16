package com.yklis.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.yklis.dao.ExtSqlDao;
import com.yklis.service.ExtSqlService;

@Service
public class ExtSqlServiceImpl implements ExtSqlService {

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ExtSqlDao extSqlDao;
        
    @Override
    public String runExtSql(String extSqlNum){
        
        String extSql;
        try{
            extSql = jdbcTemplate.queryForObject("select Reserve2 from CommCode where TypeName='对外接口' and Unid="+extSqlNum,String.class);
        }catch(Exception e){
            
            Map<String, Object> mapResponse = new HashMap<>();
            mapResponse.put("errorCode", -123);
            mapResponse.put("errorMsg", e.toString());
            
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("response", mapResponse);
            
            return JSON.toJSONString(map);
        }
        
        if(null==extSql){
            
            Map<String, Object> mapResponse = new HashMap<>();
            mapResponse.put("errorCode", -123);
            mapResponse.put("errorMsg", "未获取到待执行的SQL");
            
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("response", mapResponse);
            
            return JSON.toJSONString(map);
        }

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
        
    }

}
