package com.styx.support.config;

import com.styx.common.service.ServiceSupportManager;
import com.styx.common.service.mybatis.CommonSqlInjector;
import com.styx.common.spring.SpringBeanHelper;
import com.styx.support.core.file.FileStoreService;
import com.styx.support.core.file.LocalFileStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author TontoZhou
 * @since 2019/12/10
 */
@Slf4j
@Configuration
public class SupportConfiguration {


    /**
     * 文件存储服务
     *
     * @return
     */
    @Bean
    public FileStoreService getFileStoreService(Environment env) {
        String folder = env.getProperty("attachment.upload-folder");
        String visitUrl = env.getProperty("attachment.visit-base-url");

        FileStoreService fileStoreService = new LocalFileStoreService(folder) {

//            @Override
//            public String getStoreType() {
//                return "local";
//            }

            @Override
            public String getFileUrl(String relativePath) {
                return visitUrl + relativePath;
            }
        };

        return fileStoreService;
    }

    //---------------------------------------
    //
    // 以下注入为系统通用实例，与具体业务和项目无关
    //
    //---------------------------------------

    /**
     * spring bean 获取帮助类
     */
    @Bean
    public SpringBeanHelper springBeanHolder() {
        return new SpringBeanHelper();
    }

    /**
     * 基于mybatis plus和业务封装的支持类管理启用
     */
    @Bean
    public ServiceSupportManager getServiceSupportManager() {
        return new ServiceSupportManager();
    }

    /**
     * 扩展mybatis plus 通用方法
     */
    @Bean
    public CommonSqlInjector getCommonSqlInjector() {
        return new CommonSqlInjector();
    }

}
