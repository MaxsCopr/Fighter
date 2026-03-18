package com.fighter.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jpa.autoconfigure.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfiguration {

    @Bean
    public SpringLiquibase liquibase(
            DataSource dataSource,
            @Value("${spring.liquibase.change-log}") String changeLog,
            @Value("${spring.liquibase.enabled:true}") boolean enabled) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setShouldRun(enabled);
        return liquibase;
    }

    @Configuration
    static class LiquibaseEntityManagerFactoryDependencyConfiguration
            extends EntityManagerFactoryDependsOnPostProcessor {

        LiquibaseEntityManagerFactoryDependencyConfiguration() {
            super("liquibase");
        }
    }
}
