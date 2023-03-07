package com.ecsp.demo.batch.scheduler;

import com.ecsp.demo.batch.job.TestJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Date;


@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
@EnableScheduling
public class Scheduler {

    private final Logger logger = LoggerFactory.getLogger(Scheduler.class);
    private final JobLauncher jobLauncher;
    private final TestJob testJob;
    
    @Scheduled(cron = "10 * * * * *")
    public void testScheduler() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        logger.info("============================================================================================");
        logger.info("Test Scheduler STARTED AT: " + LocalDateTime.now());
        Date date = new Date();
        jobLauncher.run(testJob.ecspJob(),
                new JobParametersBuilder().addDate("launchedAt", date).toJobParameters());
        logger.info("Test Scheduler 완료 FINISHED AT: " + LocalDateTime.now());
        logger.info("============================================================================================");
    }
}
