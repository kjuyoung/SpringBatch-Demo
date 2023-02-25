package com.ecsp.demo.batch.step;

import com.ecsp.demo.models.entity.storage.TestDefaultEntity;
import com.ecsp.demo.models.entity.validation.TestValidEntity;
import com.ecsp.demo.repository.storage.TestDefaultRepository;
import com.ecsp.demo.repository.validation.TestValidRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

@RequiredArgsConstructor
public class Step1 implements Tasklet, StepExecutionListener {

    private final Logger logger = LoggerFactory.getLogger(Step1.class);
    private final TestDefaultRepository testDefaultRepository;
    private final TestValidRepository testValidRepository;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        testDefaultRepository.save(TestDefaultEntity.builder()
                                                    .trace("10000")
                                                    .code("9999")
                                                    .build());
        testValidRepository.save(TestValidEntity.builder()
                                                    .trace("50000")
                                                    .code("3333")
                                                    .build());
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<TestDefaultEntity> defaultEntity = testDefaultRepository.findAll();
        List<TestValidEntity> validationEntity = testValidRepository.findAll();
        logger.info("Default entity = " + defaultEntity);
        logger.info("Validation entity = " + validationEntity);

        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}
