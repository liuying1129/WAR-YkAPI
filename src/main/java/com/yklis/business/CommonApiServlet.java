package com.yklis.business;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.google.gson.Gson;
import com.yklis.entity.ApiTokenEntity;
import com.yklis.service.ApiTokenService;
import com.yklis.service.AppVisitApiService;
import com.yklis.service.CheckUserSignService;
import com.yklis.service.CheckUserTokenService;
import com.yklis.service.ExecSQLCmdApiService;
import com.yklis.service.LoginApiService;
import com.yklis.service.RsaApiService;
import com.yklis.service.ScalarSQLCmdApiService;
import com.yklis.service.SelectDataSetSQLCmdApiService;
import com.yklis.service.CommonApiService;
import com.yklis.service.DesApiService;
import com.yklis.util.CommFunction;

/**
 * HttpServlet,拦截所有HTTP请求，RESTful请求除外
 * 
 * 实现了CheckUserTokenService接口的服务，会校验请求的token(令牌)及sign(签名)
 * 实现了CheckUserSignService接口的服务，会校验请求的sign(签名)
 * 
 * @author ying07.liu
 *
 */
public class CommonApiServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
    //配置容器起动时候加载log4j配置文件
    //只要将log4j.properties放在classes下，tomcat启动的时候会自动加载log4j的配置信息，
    //在程式代码不再需要使用PropertyConfigurator.configure("log4j.properties")来加载，
    //如果用了它反而会出现上面的错误--Could not read configuration file [log4jj.properties]
    //PropertyConfigurator.configure("log4jj.properties");
    private transient Logger logger = Logger.getLogger(this.getClass());
    
	@Autowired
	private ApiTokenService apiTokenService;
	
	/**
	 * init仅在 Servlet创建时被调用
	 * Servlet创建于用户第一次调用对应于该 Servlet的 URL，或指定该Servlet在服务器启动时被加载的情况
	 */
    @Override
	public void init(ServletConfig config) throws ServletException {
		
        super.init(config);
        
        logger.info("Servlet " + this.getServletName() + "的init()方法运行");

        //HttpServlet中自动装配spring定义的Bean
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,config.getServletContext());
        //执行上面这句后，就可以使用
    	//@Autowired
    	//private WorkerService workerService;        		
	}
    
    /**
     * 浏览器请求时有自定义headers，则会向服务器先发一个OPTIONS请求，问问服务器是否允许该请求.服务器允许后才发送真正的get、post请求。
     * 经测试，不带自定义header的请求不会进入doOptions方法
     */    
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //Gson gson = new Gson();
        //Map<String, String[]> inputParamMap = request.getParameterMap();
        //logger.info("进入doOptions方法:"+gson.toJson(inputParamMap));
        
        //解决跨域问题：允许所有域名访问
        response.setHeader("Access-Control-Allow-Origin", "*");
        //告诉请求域，你只能在请求中添加这些请求头
        //用逗号分隔各自定义请求头键
        response.setHeader("Access-Control-Allow-Headers", "X-Access-Token");
        
        //服务器已接受请求，但尚未处理
        response.setStatus(202);
    }
    
    /**
     * get、post均为客户端向服务器提交数据的方式
     * get提交的数据会加在URL上，如http://abc/def?key1=123&key2=456
     * post将表单的各个字段及其值放置在HEADER内一起提交
     * post方式安全性更高、能提交的数据量更大
     */

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
    	response.setHeader("Content-type", "text/html;charset=UTF-8");
    	//解决跨域问题：允许所有域名访问
    	response.setHeader("Access-Control-Allow-Origin", "*");
        
        //如果preflight请求暗示实际的请求可能包含用户认证，这个头的信息表示在缺乏用户认证的情况下，是否暴露响应
        //关于携带验证信息的请求(Credentialed Requests) 
        //如果希望把cookie信息、HTTP授权信息、客户端的SSL证书等发送到服务器端
        //客户端设置objXMLHTTP.withCredentials = true; //允许ajax请求携带cookie信息
        //response.setHeader("Access-Control-Allow-Credentials", "true");
    	response.setCharacterEncoding("UTF-8"); 
    	
    	//request.setCharacterEncoding("UTF-8");这个是只对post请求有用
    	//如果想对get请求有用，修改tomcat配置文件server.xml，元素Connector的属性URIEncoding="UTF-8" 
    	request.setCharacterEncoding("UTF-8");
    	
        String methodNum = request.getParameter("methodNum");
        if(null == methodNum||"".equals(methodNum)){
            logger.warn("无效的请求参数:methodNum为空");
			PrintWriter printWriter = null;
		    try {  
		    	printWriter = response.getWriter();  
	            
	            Map<String, Object> mapResponse = new HashMap<>();
                mapResponse.put("errorCode", -123);
                mapResponse.put("errorMsg", "无效的请求参数:methodNum为空");
                
                Map<String, Object> map = new HashMap<>();
                map.put("success", false);
                map.put("response", mapResponse);

		    	Gson gson = new Gson();
		    	printWriter.print(gson.toJson(map));
		    } catch (IOException e) {  
		    	logger.error("response.getWriter失败:"+e.toString());
		    } finally {  
		        if (printWriter != null) printWriter.close();
		    }
        	return;
        }
        
                	    	
        Map<String, Class<? extends CommonApiService>> apiMap = getApiServiceMap();
        if (null == apiMap || null == apiMap.keySet() || apiMap.keySet().isEmpty()){
            logger.info("系统中没有定义API");
        	return;
        }
                    
        if (!apiMap.keySet().contains(methodNum)){
            logger.warn("Request's method [" + methodNum + "] is not exists.");
    		PrintWriter printWriter11 = null;
    	    try {  
    	    	printWriter11 = response.getWriter(); 
    	    	
	            Map<String, Object> mapResponse = new HashMap<>();
                mapResponse.put("errorCode", -123);
                mapResponse.put("errorMsg", "Request's method [" + methodNum + "] is not exists");
                
                Map<String, Object> map = new HashMap<>();
                map.put("success", false);
                map.put("response", mapResponse);

    	    	Gson gson = new Gson();
    	    	printWriter11.print(gson.toJson(map));
    	    } catch (IOException e) {  
    	    	logger.error("response.getWriter失败:"+e.toString());
    	    } finally {  
    	        if (printWriter11 != null) printWriter11.close();
    	    }
            return;
        }
        
	    WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
	    
        CommonApiService commonApiService = webApplicationContext.getBean(apiMap.get(methodNum));
        
        //如果服务实现了CheckUserTokenService接口的情况
        if (commonApiService instanceof CheckUserTokenService){
            
            Map<String, String[]> inputParamMap = request.getParameterMap();
            
            String token1 = request.getHeader("X-Access-Token");
            //String token1 = request.getParameter("token");
            logger.info("访问服务["+methodNum+"]start,header's token:["+token1+"]");
            
            if(!CommFunction.signCheck(inputParamMap, token1)){
                
                response.setStatus(403);//HTTP STATUS CODE 403:Forbidden
                
                return;                
            }
            
            String msg = ifValidToken(token1,true,2);
            if((null != msg)&&(!"".equals(msg))){
                
              response.setStatus(403);//HTTP STATUS CODE 403:Forbidden
                                                    
              return;
            }
            
            //修改token的修改时间为当前时间
            apiTokenService.updateTokenModTime(token1);           
        }
        
        //如果服务实现了CheckUserSignService接口的情况
        if (commonApiService instanceof CheckUserSignService){
                       
            Map<String, String[]> inputParamMap = request.getParameterMap();
            
            if(!CommFunction.signCheck(inputParamMap, null)){
                
                response.setStatus(403);//HTTP STATUS CODE 403:Forbidden
                
                return;                
            }            
        }
        
	    try{
	        
	        commonApiService.handle(request, response);
	        
        }catch (Exception e) {  
	    	logger.error("执行HTTP API服务 ["+methodNum+"] 失败:"+e.toString());//token ["+token1+"] 
	    	
	    	//将错误信息传到前端，以便定位问题 start
			PrintWriter printWriter11 = null;
		    try {  
		    	printWriter11 = response.getWriter();  
		    	
	            Map<String, Object> mapResponse = new HashMap<>();
                mapResponse.put("errorCode", -123);
                mapResponse.put("errorMsg", "执行HTTP API服务 ["+methodNum+"] 失败:"+e.toString());
                
                Map<String, Object> map = new HashMap<>();
                map.put("success", false);
                map.put("response", mapResponse);

		    	Gson gson = new Gson();
		    	printWriter11.print(gson.toJson(map));
		    } catch (IOException e1) {  
		    	logger.error("response.getWriter失败:"+e1.toString());
		    } finally {  
		        if (printWriter11 != null) printWriter11.close();
		    }
	    	//将错误信息传到前端，以便定位问题 stop
        }   	    
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
		
	}
	
    @Override
    public void destroy() {

        //释放资源
        
    }
    
    /**
     * 
     * @param token
     * @param ifCheckExpire
     * @param expireTime 单位：分
     * @return
     */
    public String ifValidToken(String token,boolean ifCheckExpire,int expireTime){
    	
    	if((null == token)||("".equals(token))){
    		return "令牌为空";
    	}
    	
	    ApiTokenEntity apiTokenEntity = new ApiTokenEntity();
	    apiTokenEntity.setToken(token);
	    List<ApiTokenEntity> apiTokenList = apiTokenService.selectApiToken(apiTokenEntity);
    	
	    if((null == apiTokenList)||(apiTokenList.isEmpty())){
	    	return "无效的令牌";
	    }
	    
	    if(ifCheckExpire){
			for(ApiTokenEntity apiTokenEntity1 : apiTokenList){
				
		    	Date now = new Date();
		    	
				Date d1 = apiTokenEntity1.getCreateDateTime();
				if(null != apiTokenEntity1.getModDateTime()) d1 = apiTokenEntity1.getModDateTime();
				
		    	//毫秒ms
	            long diff = now.getTime() - d1.getTime();
	            //long diffSeconds = diff / 1000 % 60;
	            long diffMinutes = diff / (60 * 1000) ;
	            //long diffHours = diff / (60 * 60 * 1000) % 24;
	            //long diffDays = diff / (24 * 60 * 60 * 1000);
	            
				logger.info("token:["+token+"],上次访问时间:["+d1+"],间隔时间:["+diffMinutes+"]分钟");
				
	            if(diffMinutes>expireTime){
	            	return "令牌已过期";
	            }
			}
	    }
    	return null;
    }

    private Map<String, Class<? extends CommonApiService>> getApiServiceMap(){
        
        Map<String, Class<? extends CommonApiService>> map = new HashMap<>();
        
        map.put("rsa", RsaApiService.class);//rsa:生成rsa密钥对服务        
        map.put("login", LoginApiService.class);//login:登录服务       
        map.put("AIF012", ExecSQLCmdApiService.class);
        map.put("AIF013", ScalarSQLCmdApiService.class);
        map.put("AIF014", SelectDataSetSQLCmdApiService.class);
        map.put("AIF015", AppVisitApiService.class);
        map.put("AIF016", DesApiService.class);//DES加密服务
        
        return map;
    }
    
    static class RequestParameterComparator implements Comparator<Map.Entry<String,String[]>> {
        
        private Logger logger = Logger.getLogger(this.getClass());
        
        @Override
        public int compare(Entry<String, String[]> o1, Entry<String, String[]> o2) {

            try{
                
                return o1.getKey().compareTo(o2.getKey());
                                                    
            }catch(Exception e){
                logger.error("RequestParameterComparator出错:"+e.toString());
                return 0;
            }
        }
 
    }

}
