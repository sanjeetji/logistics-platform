package com.logistics.dispatch.config;

import com.logistics.dispatch.mapper.DispatchMapper;
import com.logistics.dispatch.mapper.DispatchMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public DispatchMapper dispatchMapper() {
        return new DispatchMapperImpl();
    }
}
