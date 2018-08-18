package com.yklis.service;

import java.util.List;
import java.util.Map;

public interface ExtSqlService {
    
    List<Map<String, Object>> selectSql(String selectSql);
    
    void updateSql(String updateSql);
    
    void insertSql(String insertSql);
}
