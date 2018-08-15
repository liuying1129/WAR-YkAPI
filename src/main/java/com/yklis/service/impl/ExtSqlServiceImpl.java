package com.yklis.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.yklis.dao.ExtSqlDao;
import com.yklis.service.ExtSqlService;

@Service
public class ExtSqlServiceImpl implements ExtSqlService {

    //配置容器起动时候加载log4j配置文件
    //只要将log4j.properties放在classes下，tomcat启动的时候会自动加载log4j的配置信息，
    //在程式代码不再需要使用PropertyConfigurator.configure("log4j.properties")来加载，
    //如果用了它反而会出现上面的错误--Could not read configuration file [log4jj.properties]
    //PropertyConfigurator.configure("log4jj.properties");
    private final transient Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private ExtSqlDao extSqlDao;
    
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> selectSql(String extSqlNum) {
        
        String selectSql = null;
        try{
            selectSql = jdbcTemplate.queryForObject("select Reserve2 from CommCode where TypeName='对外接口' and Unid="+extSqlNum,String.class);
        }catch(Exception e){
            
            logger.error("jdbcTemplate.queryForObject失败:"+e.toString());
            return null;
        }

        return extSqlDao.selectSql(selectSql);
    }
    
    @Override
    public void execSql(String extSqlNum){
        
        String execSql = jdbcTemplate.queryForObject("select Reserve2 from CommCode where TypeName='对外接口' and Unid="+extSqlNum,String.class);

        if(execSql.indexOf("update ")>=0){
            extSqlDao.updateSql(execSql);
        }else{
            extSqlDao.insertSql(execSql);
        }
        
        

    }    

}
