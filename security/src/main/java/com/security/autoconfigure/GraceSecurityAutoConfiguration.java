package com.security.autoconfigure;

import com.security.permission.PermissionService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.security")
@MapperScan(basePackages = "com.security.mapper")
public class GraceSecurityAutoConfiguration {

    @Bean("pms")
    public PermissionService permissionService(){
        return new PermissionService();
    }

}
