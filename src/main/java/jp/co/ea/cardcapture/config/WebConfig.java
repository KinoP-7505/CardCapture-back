package jp.co.ea.cardcapture.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebConfig {

	// application.properties から文字列として安全に受け取る
    @Value("${app.cors.allowed-origins}")
    private String rawAllowedOrigins;

    @Bean
    CorsFilter corsFilter() {
    	CorsConfiguration config = new CorsConfiguration();

        // カンマ区切りの文字列を分解し、前後の不要な空白を完全除去
        List<String> origins = Arrays.stream(rawAllowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .collect(Collectors.toList());

        // デバッグ用ログ出力（RenderのLOGSタブで確認可能）
        System.out.println("=== Loaded CORS Allowed Origins ===");
        origins.forEach(origin -> System.out.println("Allowed: [" + origin + "]"));

        // 1. 許可ドメインの設定
        config.setAllowedOrigins(origins);

        // 2. Cookie送受信（withCredentials: true）を許可
        config.setAllowCredentials(true);

        // 3. ヘッダー・メソッド・キャッシュ設定
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
