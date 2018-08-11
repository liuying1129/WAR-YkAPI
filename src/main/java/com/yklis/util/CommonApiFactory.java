package com.yklis.util;

import com.yklis.service.CommonApiService;
import com.yklis.service.impl.AppVisitApiServiceImpl;
import com.yklis.service.impl.DesApiServiceImpl;
import com.yklis.service.impl.ExecSQLCmdApiServiceImpl;
import com.yklis.service.impl.LoginApiServiceImpl;
import com.yklis.service.impl.RsaApiServiceImpl;
import com.yklis.service.impl.ScalarSQLCmdApiServiceImpl;
import com.yklis.service.impl.SelectDataSetSQLCmdApiServiceImpl;

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
      if(methodNum.equalsIgnoreCase("rsa")){
    	  return new RsaApiServiceImpl();//rsa:生成rsa密钥对服务
      } else if(methodNum.equalsIgnoreCase("login")){
    	  return new LoginApiServiceImpl();//login:登录服务
      } else if(methodNum.equalsIgnoreCase("AIF012")){
    	  return new ExecSQLCmdApiServiceImpl();
      } else if(methodNum.equalsIgnoreCase("AIF013")){
          return new ScalarSQLCmdApiServiceImpl();
      } else if(methodNum.equalsIgnoreCase("AIF014")){
          return new SelectDataSetSQLCmdApiServiceImpl();
      } else if(methodNum.equalsIgnoreCase("AIF015")){
    	  return new AppVisitApiServiceImpl();
      } else if(methodNum.equalsIgnoreCase("AIF016")){
    	  return new DesApiServiceImpl();//DES加密服务
      }      

      return null;
    }
}
