package com.yklis.util;

import com.yklis.service.CommonApiService;
import com.yklis.service.impl.ExecSQLCmdApiServiceImpl;

/**
 * 工厂模式
 * 工厂类
 * @author liuying
 *
 */
public class CommonApiFactory {

    //使用 getCommonApiService方法获取CommonApiService的对象
    public CommonApiService getCommonApiService(String methodNum){
      if(null == methodNum){
    	  
         return null;
      }
      if(methodNum.equalsIgnoreCase("AIF012")){
         return new ExecSQLCmdApiServiceImpl();
      };// else if(shapeType.equalsIgnoreCase("AIF013")){
         //return new ScalarSQLCmdApiService();
      
      /*map.put("rsa", RsaApiService.class);//rsa:生成rsa密钥对服务        
      map.put("login", LoginApiService.class);//login:登录服务       
      map.put("AIF012", ExecSQLCmdApiService.class);
      map.put("AIF013", ScalarSQLCmdApiService.class);
      map.put("AIF014", SelectDataSetSQLCmdApiService.class);
      map.put("AIF015", AppVisitApiService.class);
      map.put("AIF016", DesApiService.class);//DES加密服务*/

      return null;
    }
}
