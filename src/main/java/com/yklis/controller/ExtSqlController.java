package com.yklis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yklis.service.ExtSqlService;

/**
 * 通过配置SQL向外提供HTTP API
 * @author ying07.liu
 *
 */

@RestController
//@RestController = @Controller + @ResponseBody
@RequestMapping("/extSqlNum/")
public class ExtSqlController {
        
    @Autowired
    private ExtSqlService extSqlService;

    @RequestMapping("{extSqlNum}")
    public String runExtSql(@PathVariable(value="extSqlNum") String extSqlNum){
       
        return extSqlService.runExtSql(extSqlNum);
    }
}
