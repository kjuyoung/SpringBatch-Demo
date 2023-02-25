package com.ecsp.demo.batch.job;

import com.ecsp.demo.batch.step.Step1;
import com.ecsp.demo.common.config.JpaConfig;
import com.ecsp.demo.repository.storage.TestDefaultRepository;
import com.ecsp.demo.repository.validation.TestValidRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class TestJob {
    private final TestValidRepository testValidRepository;
    private final TestDefaultRepository testDefaultRepository;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlatformTransactionManager defaultTransactionManager;

    public TestJob(JobBuilderFactory jobBuilderFactory,
                   StepBuilderFactory stepBuilderFactory,
                   @Qualifier(JpaConfig.STORAGE_TRANSACTION_MANAGER) PlatformTransactionManager defaultTransactionManager,
                   TestDefaultRepository testDefaultRepository,
                   TestValidRepository testValidRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.defaultTransactionManager = defaultTransactionManager;
        this.testDefaultRepository = testDefaultRepository;
        this.testValidRepository = testValidRepository;
    }

    @Bean
    public Job ecspJob() {
        return jobBuilderFactory.get("ECSPJOB")
                .start(step1())
                    .on("COMPLETED")
                    .to(step2())
                .from(step1())
                    .on("FAILED")
                    .end()
                .end()
                .build();
    }

    @Bean
    @JobScope
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(new Step1(testDefaultRepository, testValidRepository))
                .transactionManager(defaultTransactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet((contribution, chunkContext) -> {log.info(">>>>> This is step2");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
