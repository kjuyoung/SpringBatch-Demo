package com.ecsp.demo.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.*;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({JpaProperties.class, HibernateProperties.class})
public class JpaConfig {

    public static final String STORAGE_ENTITY_MANAGER_FACTORY = "storageEntityManagerFactory";
    public static final String VALIDATION_ENTITY_MANAGER_FACTORY = "validationEntityManagerFactory";
    public static final String STORAGE_TRANSACTION_MANAGER = "storageTransactionManager";
    public static final String VALIDATION_TRANSACTION_MANAGER = "validationTransactionManager";
    private final String STORAGE_DOMAIN_PACKAGE = "com.ecsp.demo.models.entity.storage";
    private final String VALIDATION_DOMAIN_PACKAGE = "com.ecsp.demo.models.entity.validation";
    public static final String STORAGE_REPOSITORY_PACKAGE = "com.ecsp.demo.repository.storage";
    public static final String VALIDATION_REPOSITORY_PACKAGE = "com.ecsp.demo.repository.validation";

    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;
    private final ObjectProvider<Collection<DataSourcePoolMetadataProvider>> metadataProviders;
    private final EntityManagerFactoryBuilder entityManagerFactoryBuilder;

    @Primary
    @Bean(name = STORAGE_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean storageEntityManagerFactory(@Qualifier(DatasourceConfig.STORAGE_DATASOURCE) DataSource dataSource)  {
        return EntityManagerFactoryCreator.builder()
                                        .properties(jpaProperties)
                                        .hibernateProperties(hibernateProperties)
                                        .metadataProviders(metadataProviders)
                                        .entityManagerFactoryBuilder(entityManagerFactoryBuilder)
                                        .dataSource(dataSource)
                                        .packages(STORAGE_DOMAIN_PACKAGE)
                                        .persistenceUnit("storageUnit")
                                        .build()
                                        .create();
    }

    @Bean(name = VALIDATION_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean validationEntityManagerFactory(@Qualifier(DatasourceConfig.VALIDATION_DATASOURCE) DataSource dataSource)  {
        Map<String, String> properties = new HashMap<>();
        properties.put( "hibernate.dialect", "org.hibernate.dialect.H2Dialect" );
        jpaProperties.setProperties(properties);
        return EntityManagerFactoryCreator.builder()
                                        .properties(jpaProperties)
                                        .hibernateProperties(hibernateProperties)
                                        .metadataProviders(metadataProviders)
                                        .entityManagerFactoryBuilder(entityManagerFactoryBuilder)
                                        .dataSource(dataSource)
                                        .packages(VALIDATION_DOMAIN_PACKAGE)
                                        .persistenceUnit("validationUnit")
                                        .build()
                                        .create();
    }

    @Primary
    @Bean(name = STORAGE_TRANSACTION_MANAGER)
    public PlatformTransactionManager storageTransactionManager(
            @Qualifier(STORAGE_ENTITY_MANAGER_FACTORY) LocalContainerEntityManagerFactoryBean emf) {
        return new JpaTransactionManager(Objects.requireNonNull(emf.getObject()));
    }

    @Bean(name = VALIDATION_TRANSACTION_MANAGER)
    public PlatformTransactionManager validationTransactionManager(
            @Qualifier(VALIDATION_ENTITY_MANAGER_FACTORY) LocalContainerEntityManagerFactoryBean emf) {
        return new JpaTransactionManager(Objects.requireNonNull(emf.getObject()));
    }

    @Configuration
    @EnableJpaRepositories(
            basePackages = STORAGE_REPOSITORY_PACKAGE
            ,entityManagerFactoryRef = JpaConfig.STORAGE_ENTITY_MANAGER_FACTORY
            ,transactionManagerRef = JpaConfig.STORAGE_TRANSACTION_MANAGER
    )
    public static class StorageDatasourceConfig{}

    @Configuration
    @EnableJpaRepositories(
            basePackages = VALIDATION_REPOSITORY_PACKAGE
            ,entityManagerFactoryRef = JpaConfig.VALIDATION_ENTITY_MANAGER_FACTORY
            ,transactionManagerRef = JpaConfig.VALIDATION_TRANSACTION_MANAGER
    )
    public static class ValidationDatasourceConfig{}
}
