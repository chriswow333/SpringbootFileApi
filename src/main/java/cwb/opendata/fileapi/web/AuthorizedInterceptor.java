package cwb.opendata.fileapi.web;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cwb.opendata.fileapi.common.model.UserDao;
import cwb.opendata.fileapi.config.ResourceEnum;

@Component("authorizedInterceptor")
public class AuthorizedInterceptor extends HandlerInterceptorAdapter  {

	@Autowired
	@Qualifier("userDao")
	private UserDao userDao;
	
	private static final String REQUEST_FORBIDDEN_MSG = "{\"message\": \"Authorization key is not correct.\"}";
	
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
            Object handler) throws Exception {
    	
    	// Get authorization value in request parameter first. if it is not found, try to find in header.
    	String authorizationKey = ResourceEnum.REQ_PARAM_AUTHKEY.value();
    	
    	String authorizationValue = Optional.ofNullable(request.getParameter(authorizationKey))
    			.orElse(Optional.ofNullable(request.getHeader(authorizationKey))
    					.orElse(""));
    	
    	String authorizationKeyOld = ResourceEnum.REQ_PARAM_AUTHKEY_OLD.value();
    	
    	authorizationValue = authorizationValue.equals("")?Optional.ofNullable(request.getParameter(authorizationKeyOld))
    			.orElse(Optional.ofNullable(request.getHeader(authorizationKeyOld))
    					.orElse("")):authorizationValue;
    	
    	if(!authorizationValue.equals("") 
    			&& userDao.checkAuthorizationExist(authorizationValue)) {
    		return true;
    	}else {
    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    		
    		ServletOutputStream out;
    		try {
    			out = response.getOutputStream();
    			response.setCharacterEncoding("UTF-8");
    			response.setContentType("application/json;charset=UTF-8 "); 
    			out.write(REQUEST_FORBIDDEN_MSG.getBytes(),0,REQUEST_FORBIDDEN_MSG.length());
    			out.flush();
    		} catch (IOException e) {
//    			e.printStackTrace();
    		}
    		
    		return false;
    	}
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
            Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
            Object handler, Exception ex) throws Exception {
    }

}
