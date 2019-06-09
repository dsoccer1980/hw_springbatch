package ru.dsoccer1980;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.dsoccer1980.repository.JpaAuthorRepository;
import ru.dsoccer1980.service.MyService;

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class);
        MyService myService = context.getBean(MyService.class);
        myService.migration();
        // JpaAuthorRepository bean = context.getBean(JpaAuthorRepository.class);
//        JobLauncher jobLauncher = context.getBean(JobLauncher.class);
//        Job job = context.getBean(Job.class);
//        jobLauncher.run(job, new JobParametersBuilder().toJobParameters());
    }


}
