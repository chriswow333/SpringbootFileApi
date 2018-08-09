package cwb.opendata.fileapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cwb.opendata.fileapi.web.AccessLogInterceptor;
import cwb.opendata.fileapi.web.AuthorizedInterceptor;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer  {
	
	
	@Autowired
	@Qualifier("authorizedInterceptor")
	private AuthorizedInterceptor authorizedInterceptor;
	
	@Autowired
	@Qualifier("accessLogInterceptor")
	private AccessLogInterceptor logInterceptor;

	
	@Autowired
	@Qualifier("authorizedFlag")
	private boolean authorizedFlag;
	
	@Autowired
	@Qualifier("accessLogFlag")
	boolean accessLogFlag;
	
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

		//set path extension to false
		configurer.favorPathExtension(false).
	    //request parameter ("format" by default) should be used to determine the requested media type
	    favorParameter(false).
	    //the favour parameter is set to "mediaType" instead of default "format"
//	    parameterName("format").
	    //ignore the accept headers
	    ignoreAcceptHeader(true).
	    //dont use Java Activation Framework since we are manually specifying the mediatypes required below
	    useRegisteredExtensionsOnly(true).
	    defaultContentType(MediaType.APPLICATION_JSON);
//	    mediaType("xml", MediaType.APPLICATION_XML).
//	    mediaType("json", MediaType.APPLICATION_JSON);
	  }
	
	@Override
    public void addInterceptors (InterceptorRegistry registry) {

		// bind second tier interceptor: log interceptor
		if(accessLogFlag) {
			registry.addInterceptor(logInterceptor).addPathPatterns("/fileapi/v1/opendataapi/**");
			registry.addInterceptor(logInterceptor).addPathPatterns("/fileapi/opendataapi/**");
			registry.addInterceptor(logInterceptor).addPathPatterns("/opendataapi");
			registry.addInterceptor(logInterceptor).addPathPatterns("/govdownload");
			registry.addInterceptor(logInterceptor).addPathPatterns("/opendata/**");
		}
		
		// bind first tier interceptor: authorized authorizationkey 
		if(authorizedFlag) {
			registry.addInterceptor(authorizedInterceptor).addPathPatterns("/fileapi/v1/opendataapi/**");
			registry.addInterceptor(authorizedInterceptor).addPathPatterns("/opendataapi");
			registry.addInterceptor(authorizedInterceptor).addPathPatterns("/govdownload");
		}
		
		
    }
}
