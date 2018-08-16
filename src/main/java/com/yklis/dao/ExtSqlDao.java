package com.yklis.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ExtSqlDao {
    
    /**
     * mybatis参数注解方式
     * @return
     */ 
    List<Map<String, Object>> selectSql(@Param(value="selectSql") String selectSql);
    
    void updateSql(@Param(value="updateSql") String updateSql);
    
    void insertSql(@Param(value="insertSql") String insertSql);

}
