package cwb.opendata.fileapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//@Configuration("restApidataPoolConfig")
//@PropertySource(value = "${jetty.config}", ignoreResourceNotFound = true)
@Configuration("restResourceConfig")
public class ResourceConfig {

//	public static String YAML_FILE_PATH;
	
	@Value("${api.fileDownload.pathPrefix}")
	private String filePathPrefix;
	
	@Value("${api.filter.authorized.flag}")
	private boolean authorizedFlag;
	
	@Value("${api.filter.accessLog.flag}")
	private boolean accessLogFlag;
	
	@Value("${api.service.host}")
	private String host;
	
	@Value("${api.service.api_type}")
	private String apiType;
	
	@Bean("authorizedFlag")
	public boolean getAuthorizedFlag() {
		return authorizedFlag;
	}
	
	@Bean("accessLogFlag")
	public boolean getAccessLogFlag() {
		return accessLogFlag;
	}

	@Bean("host")
	public String getHost() {
		return host;
	}
	
	@Bean("apiType")
	public String getApiType() {
		return apiType;
	}
	
	@Bean("filePathPrefix")
	public String getFilePathPrefix() {
		return filePathPrefix;
	}
	
	
//	@Bean
//	public static PropertySourcesPlaceholderConfigurer properties() {
//		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
//		YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
//		yaml.setResources(new FileSystemResource(YAML_FILE_PATH));
//		propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
//		return propertySourcesPlaceholderConfigurer;
//	}
	
}

