package com.ecsp.demo.common.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class DatasourceConfig {

    private static final String STORAGE_PROPERTIES = "spring.datasource.storage.hikari";
    private static final String VALIDATION_PROPERTIES = "spring.datasource.validation.hikari";
    public static final String STORAGE_DATASOURCE = "storageDataSource";
    public static final String VALIDATION_DATASOURCE = "validationDataSource";

    @Bean
    @ConfigurationProperties(prefix = STORAGE_PROPERTIES)
    public HikariConfig storageHikariConfig() {
        return new HikariConfig();
    }

    @Primary
    @Bean(STORAGE_DATASOURCE)
    public DataSource storageDataSource() {
        return new LazyConnectionDataSourceProxy(new HikariDataSource(storageHikariConfig()));
    }

    @Bean
    @ConfigurationProperties(prefix = VALIDATION_PROPERTIES)
    public HikariConfig validationHikariConfig() {
        return new HikariConfig();
    }

    @Bean(VALIDATION_DATASOURCE)
    public DataSource validationDataSource() {
        return new LazyConnectionDataSourceProxy(new HikariDataSource(validationHikariConfig()));
    }
}
