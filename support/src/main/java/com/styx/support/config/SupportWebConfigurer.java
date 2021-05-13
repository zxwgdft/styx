package com.styx.support.config;

import com.styx.common.spring.NotProduceCondition;
import com.styx.common.spring.web.DateFormatter;
import com.styx.support.core.SupportSecurityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Date;

@Slf4j
@Configuration
public class SupportWebConfigurer implements WebMvcConfigurer {

    @Value("${attachment.upload-folder}")
    private String filePath;

    @Autowired
    private Environment environment;

    @Autowired
    private SupportSecurityManager webSecurityManager;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (NotProduceCondition.isNotProduce(environment)) {
            registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldType(Date.class, new DateFormatter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 只拦截上传文件操作
        registry.addInterceptor(webSecurityManager)
                .addPathPatterns("/file/upload/**");
    }
}
