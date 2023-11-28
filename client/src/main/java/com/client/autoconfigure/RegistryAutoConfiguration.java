package com.client.autoconfigure;

import com.client.registry.core.GraceRegistryApplicationRunner;
import com.client.properties.GraceProperties;
import com.client.properties.GraceRegistryProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 注册中心自动配置类
 *
 * @author youzhengjie
 * @date 2023/10/24 11:20:33
 */
@Configuration
@EnableConfigurationProperties({
        GraceProperties.class,
        GraceRegistryProperties.class
})
public class RegistryAutoConfiguration {

    @Bean
    public GraceRegistryApplicationRunner graceRegistryApplicationRunner(){
        return new GraceRegistryApplicationRunner();
    }

}
