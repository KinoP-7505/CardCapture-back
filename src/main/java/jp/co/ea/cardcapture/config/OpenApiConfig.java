package jp.co.ea.cardcapture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
	
	@Bean
	public OpenAPI customOpenAPI() {
		var newInfo = new Info().title("Demo REST API")
				.version("1.0")
				.description("Spring Boot + Swagger UI サンプル");
		var openApi = new OpenAPI();
		openApi.info(newInfo);
		
		return openApi;
		
	}

}
