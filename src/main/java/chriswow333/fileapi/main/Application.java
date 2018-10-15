package chriswow333.fileapi.main;

import java.util.Arrays;
import java.util.TimeZone;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"chriswow333.fileapi.**"})
//@PropertySource("file:/data/workspace/git_java/OpendataFileApiBoot/OpendataFileApiBoot/src/main/resources/application.properties")
public class Application{
	
	// switch to turn on/off printing bean when developing
	private boolean printBean = false;
	private boolean printSpecificBean = false;
	
	public static void main(String[] args) {		
		System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
		SpringApplication.run(Application.class, args);
		
	}
	
	
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			if(printSpecificBean) {
				System.out.println(ctx.getBean("globalExceptionHandler"));
			}
			
			if(printBean) {
				System.out.println("====Let's inspect the beans provided by Spring Boot===");
				
				String[] beanNames = ctx.getBeanDefinitionNames();
				Arrays.sort(beanNames);
				for(String beanName : beanNames) {
					System.out.println(beanName);
				}
			}
		};
	}
	
}
