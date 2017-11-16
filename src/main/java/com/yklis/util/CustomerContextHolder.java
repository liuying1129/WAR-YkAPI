package com.yklis.util;

import java.util.Map;

/**
 * 动态切换数据源
 * @author ying07.liu
 *
 */
public abstract class CustomerContextHolder {
    
	//Java 7之前只能这样写：
	//private static final ThreadLocal<Map<String, Object>> contextHolder = new ThreadLocal<Map<String, Object>>()
	//Java 7支持类型推断（type inference）,让编译器推断出合适的类
    private static final ThreadLocal<Map<String, Object>> contextHolder = new ThreadLocal<>();  
    	
    public static void setCustomerType(Map<String, Object> customerType) {  
        contextHolder.set(customerType);  
    }  
      
    public static Map<String, Object> getCustomerType() {  
        return contextHolder.get();  
    }  
      
    public static void clearCustomerType() {  
        contextHolder.remove();  
    }
        
}
