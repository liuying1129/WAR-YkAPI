package com.yklis.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 公共接口interface
 * 
 */
public interface CommonApiService {
		
    /**
     * 接口操作
     * 
     * @param request
     * @param response
     */
    void handle(HttpServletRequest request, HttpServletResponse response);
	
}
