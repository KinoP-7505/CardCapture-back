package jp.co.ea.cardcapture.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

	// application.properties の app.cors.allowed-origins の値を配列として取得
	@Value("${app.cors.allowed-origins}")
	private String[] allowedOrigins;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins(allowedOrigins) // 環境変数から取得したドメインのみを許可
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true) // withCredentials: true に対応
				.maxAge(3600);
	}
}
