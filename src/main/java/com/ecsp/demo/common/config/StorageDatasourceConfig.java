package com.ecsp.demo.common.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Objects;


@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(
        basePackages = StorageDatasourceConfig.STORAGE_REPOSITORY_PACKAGE,
        entityManagerFactoryRef = StorageDatasourceConfig.STORAGE_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = StorageDatasourceConfig.STORAGE_TRANSACTION_MANAGER
)
public class StorageDatasourceConfig {

    private final String STORAGE_PROPERTIES = "spring.datasource.storage.hikari";
    public final String STORAGE_DATASOURCE = "storageDataSource";
    private final String STORAGE_DOMAIN_PACKAGE = "com.ecsp.demo.models.entity.storage";
    public static final String STORAGE_ENTITY_MANAGER_FACTORY = "storageEntityManagerFactory";
    public static final String STORAGE_TRANSACTION_MANAGER = "storageTransactionManager";
    public static final String STORAGE_REPOSITORY_PACKAGE = "com.ecsp.demo.repository.storage";

    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;

    @Bean
    @ConfigurationProperties(prefix = STORAGE_PROPERTIES)
    public HikariConfig storageHikariConfig() {
        return new HikariConfig();
    }

    @Primary
    @Bean(name = STORAGE_DATASOURCE)
    public DataSource storageDataSource() {
        return new LazyConnectionDataSourceProxy(new HikariDataSource(storageHikariConfig()));
    }

    @Primary
    @Bean(name = STORAGE_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean storageEntityManagerFactory(
            @Qualifier(STORAGE_DATASOURCE) DataSource dataSource,
            EntityManagerFactoryBuilder builder) {

        return builder.dataSource(dataSource)
                    .packages(STORAGE_DOMAIN_PACKAGE)
                    .properties(new LinkedHashMap<>(hibernateProperties.determineHibernateProperties(
                                jpaProperties.getProperties(), new HibernateSettings())))
                    .persistenceUnit("storageUnit")
                    .build();
    }

    @Primary
    @Bean(name = STORAGE_TRANSACTION_MANAGER)
    public PlatformTransactionManager storageTransactionManager(
            @Qualifier(STORAGE_ENTITY_MANAGER_FACTORY) LocalContainerEntityManagerFactoryBean emf) {

        return new JpaTransactionManager(Objects.requireNonNull(emf.getObject()));
    }
}
