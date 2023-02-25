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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.*;

@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(
        basePackages = ValidationDatasourceConfig.VALIDATION_REPOSITORY_PACKAGE,
        entityManagerFactoryRef = ValidationDatasourceConfig.VALIDATION_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = ValidationDatasourceConfig.VALIDATION_TRANSACTION_MANAGER
)
public class ValidationDatasourceConfig {

    private final String VALIDATION_PROPERTIES = "spring.datasource.validation.hikari";
    public final String VALIDATION_DATASOURCE = "validationDataSource";
    private final String VALIDATION_DOMAIN_PACKAGE = "com.ecsp.demo.models.entity.validation";
    public static final String VALIDATION_ENTITY_MANAGER_FACTORY = "validationEntityManagerFactory";
    public static final String VALIDATION_TRANSACTION_MANAGER = "validationTransactionManager";
    public static final String VALIDATION_REPOSITORY_PACKAGE = "com.ecsp.demo.repository.validation";

    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;

    @Bean
    @ConfigurationProperties(prefix = VALIDATION_PROPERTIES)
    public HikariConfig validationHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = VALIDATION_DATASOURCE)
    public DataSource validationDataSource() {
        return new LazyConnectionDataSourceProxy(new HikariDataSource(validationHikariConfig()));
    }

    @Bean(name = VALIDATION_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean validationEntityManagerFactory(
            @Qualifier(VALIDATION_DATASOURCE) DataSource dataSource,
            EntityManagerFactoryBuilder builder)  {

        Map<String, String> properties = new HashMap<>();
        properties.put( "hibernate.dialect", "org.hibernate.dialect.H2Dialect" );
        jpaProperties.setProperties(properties);

        return builder.dataSource(dataSource)
                    .packages(VALIDATION_DOMAIN_PACKAGE)
                    .properties(new LinkedHashMap<>(hibernateProperties.determineHibernateProperties(
                                jpaProperties.getProperties(), new HibernateSettings())))
                    .persistenceUnit("validationUnit")
                    .build();
    }

    @Bean(name = VALIDATION_TRANSACTION_MANAGER)
    public PlatformTransactionManager validationTransactionManager(
            @Qualifier(VALIDATION_ENTITY_MANAGER_FACTORY) LocalContainerEntityManagerFactoryBean emf) {

        return new JpaTransactionManager(Objects.requireNonNull(emf.getObject()));
    }
}
