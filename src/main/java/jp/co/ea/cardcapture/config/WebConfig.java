package jp.co.ea.cardcapture.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	// application.properties の app.cors.allowed-origins の値を配列として取得
	@Value("${app.cors.allowed-origins}")
	private String[] allowedOrigins;

    @Bean
    CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. 環境変数から読み込んだドメイン（Vercel, localhost）を許可
        config.setAllowedOrigins(Arrays.asList(allowedOrigins));

        // 2. Cookie送受信（withCredentials: true）を許可
        config.setAllowCredentials(true);

        // 3. 全てのリクエストヘッダーおよびレスポンスヘッダーの参照を許可
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        // 4. 許可するHTTPメソッド
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 5. プリフライト結果のキャッシュ時間（1時間）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // アプリ内の全パス（springdoc/openapi を含む）に適用
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
