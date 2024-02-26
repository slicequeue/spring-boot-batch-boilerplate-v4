package com.slicequeue.springboot.batch.job;

import com.slicequeue.springboot.batch.job.validator.ParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

public class HelloWorldJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public CompositeJobParametersValidator validator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();

        DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator(
                new String[]{"fileName"},
                new String[]{"name", "currentDate"}
        );
        defaultJobParametersValidator.afterPropertiesSet();

        validator.setValidators(Arrays.asList(new ParameterValidator(), defaultJobParametersValidator));
        return validator;
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("basicJob")
                .start(step1())
                .build();
    }

    @Bean
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
                .tasklet((stepContribution, chunkContext) -> {
                    String name = (String) chunkContext.getStepContext().getJobParameters().get("name");
                    System.out.printf("Hello %s!\n", name);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
