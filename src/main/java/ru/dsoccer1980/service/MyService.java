package ru.dsoccer1980.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    private final JobLauncher jobLauncher;

    private final Job migrateBooksJob;

    public MyService(JobLauncher jobLauncher, Job migrateBooksJob)
    {
        this.jobLauncher = jobLauncher;
        this.migrateBooksJob = migrateBooksJob;
    }

    public void migration() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(migrateBooksJob, new JobParametersBuilder().toJobParameters());
    }
}
