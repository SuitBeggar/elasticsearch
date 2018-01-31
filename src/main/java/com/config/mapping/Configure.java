package com.config.mapping;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configure {

    /**
     * 动态加载配置文件
     * @return
     */
    @Bean(name = "Configuration")
    @ConfigurationProperties(prefix="applications")
    @Qualifier("Configuration")
    public Applications configure(){
        return  new Applications();
    }
}
