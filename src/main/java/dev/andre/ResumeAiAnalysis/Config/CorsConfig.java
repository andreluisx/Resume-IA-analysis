package dev.andre.ResumeAiAnalysis.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // libera todas as rotas
                        .allowedOrigins("http://localhost:5173") // o front-end
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // métodos permitidos
                        .allowedHeaders("*") // libera todos os headers
                        .allowCredentials(true); // permite envio de cookies/autenticação se necessário
            }
        };
    }
}
