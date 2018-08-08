package cwb.opendata.fileapi.web;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;

import cwb.opendata.fileapi.common.model.UserDao;
import cwb.opendata.fileapi.config.ResourceEnum;

@Component("accessLogInterceptor")
public class AccessLogInterceptor extends HandlerInterceptorAdapter {

	private static final Logger accessLogger = LogManager.getLogger(AccessLogInterceptor.class);
	
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;
	
	@Autowired
	@Qualifier("host")
	private String host;
	
	@Autowired
	@Qualifier("apiType")
	private String apiType;
	
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
            Object handler) throws Exception {
    	return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
            Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
            Object handler, Exception ex) throws Exception {
    	
    	ObjectMapper mapper = new ObjectMapper();
    	String userid;
    	Map<String, String> values = new HashMap<>();
//    	String[] splitUri = request.getRequestURI().split("/");
    	// Get authorization value in request parameter first. if it is not found, try to find in header.
    	String authorizationKey = ResourceEnum.REQ_PARAM_AUTHKEY.value();
    	String authorizationValue = Optional.ofNullable(request.getParameter(authorizationKey))
    			.orElse(Optional.ofNullable(request.getHeader(authorizationKey))
    					.orElse(""));
    	
    	String authorizationKeyOld = ResourceEnum.REQ_PARAM_AUTHKEY_OLD.value();
    	
    	authorizationValue = authorizationValue.equals("")?Optional.ofNullable(request.getParameter(authorizationKeyOld))
    			.orElse(Optional.ofNullable(request.getHeader(authorizationKeyOld))
    					.orElse("")):authorizationValue;
    			
    	values.put("api_type", apiType);
    	//values.put("dataid", splitUri[splitUri.length-1]);
    	if(response.getStatus() != 401 && 
    			Optional.ofNullable((userid = userDao.getUsernameByAuthorization(authorizationValue))).isPresent()) {
    		values.put("userid", userid);
    	}
    	values.put("uri", request.getRequestURI());
    	values.put("user_agent", request.getHeader("user-agent"));
    	values.put("datetime", Timestamp.valueOf(LocalDateTime.now().format(dtf)).toString());
    	values.put("status", String.valueOf(response.getStatus()));
    	values.put("host", host);
    	values.put("ip", request.getHeader("x-forwarded-for"));
    	values.put("size", "0");
    	String valueString = mapper.writeValueAsString(values);
    	accessLogger.info(valueString);
    	
    }

}