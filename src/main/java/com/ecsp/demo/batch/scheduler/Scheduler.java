package com.ecsp.demo.batch.scheduler;

import com.ecsp.demo.batch.job.TestJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
@EnableScheduling
public class Scheduler {

    private final Logger logger = LoggerFactory.getLogger(Scheduler.class);
    private final JobLauncher jobLauncher;
    private final TestJob testJobConfig;

    @Scheduled(cron = "10 * * * * *")
    public void testScheduler() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Map<String, JobParameter> jobParametersMap = new HashMap<>();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date time = new Date();
        String time1 = format1.format(time);
        jobParametersMap.put("date",new JobParameter(time1));
        JobParameters parameters = new JobParameters(jobParametersMap);

        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        jobLauncher.run(testJobConfig.ecspJob(), parameters);
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }
    
    @Scheduled(cron = "30 * * * * *")
    public void runTestScheduler() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        log.info("============================================================================================");
        log.info("Test Scheduler STARTED AT: " + LocalDateTime.now());
        Date date = new Date();
        jobLauncher.run(jobEvseTest.testJob(),
                new JobParametersBuilder().addDate("launchedAt", date).toJobParameters());
        log.info("Test Scheduler 완료 FINISHED AT: " + LocalDateTime.now());
        log.info("============================================================================================");
    }
}
