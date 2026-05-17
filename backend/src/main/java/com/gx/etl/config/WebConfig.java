package com.gx.etl.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Configuration
public class WebConfig {

    /**
     * CORS filter for frontend dev server
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * Strip .do suffix from requests for frontend compatibility
     */
    @Bean
    public FilterRegistrationBean<DoSuffixFilter> doSuffixFilter() {
        FilterRegistrationBean<DoSuffixFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new DoSuffixFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(1);
        return bean;
    }

    public static class DoSuffixFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) request;
            String uri = req.getRequestURI();
            if (uri.endsWith(".do")) {
                String newUri = uri.substring(0, uri.length() - 3);
                request.getRequestDispatcher(newUri).forward(request, response);
                return;
            }
            chain.doFilter(request, response);
        }
    }
}
