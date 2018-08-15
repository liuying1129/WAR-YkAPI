package com.yklis.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yklis.service.ExtSqlService;

@RestController
@RequestMapping("/extSqlNum/")
public class ExtSqlController {
    
    @Autowired
    private ExtSqlService extSqlService;

    @RequestMapping("{extSqlNum}")
    public List<Map<String, Object>> selectSql(@PathVariable(value="extSqlNum") String extSqlNum){
        
        return extSqlService.selectSql(extSqlNum);
    }

}
