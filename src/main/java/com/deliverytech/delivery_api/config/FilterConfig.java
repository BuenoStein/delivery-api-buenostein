package com.deliverytech.delivery_api.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ResponseCaptureFilter> responseCaptureFilter() {
        FilterRegistrationBean<ResponseCaptureFilter> registrationBean = new FilterRegistrationBean<>();
        
        registrationBean.setFilter(new ResponseCaptureFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(1);
        
        return registrationBean;
    }
}
