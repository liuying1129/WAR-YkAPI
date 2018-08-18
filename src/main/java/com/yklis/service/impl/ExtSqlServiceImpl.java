package com.yklis.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yklis.dao.ExtSqlDao;
import com.yklis.service.ExtSqlService;

@Service
public class ExtSqlServiceImpl implements ExtSqlService {
   
    @Autowired
    private ExtSqlDao extSqlDao;                          
    
    @Override
    public List<Map<String, Object>> selectSql(String selectSql){
    	
    	return extSqlDao.selectSql(selectSql);
    }
        
    @Override
    public void updateSql(String updateSql){
    	
    	extSqlDao.updateSql(updateSql);
    }
    
    @Override
    public void insertSql(String insertSql){
    	
    	extSqlDao.insertSql(insertSql);
    }
}
