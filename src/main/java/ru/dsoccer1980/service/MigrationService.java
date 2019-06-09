package ru.dsoccer1980.service;

import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

@Service

public class MigrationService {

    private final JobLauncher jobLauncher;

    private final Job migrateBooksJob;

    public MigrationService(JobLauncher jobLauncher, Job migrateBooksJob) {
        this.jobLauncher = jobLauncher;
        this.migrateBooksJob = migrateBooksJob;
    }

    @SneakyThrows
    public void migration() {
        jobLauncher.run(migrateBooksJob, new JobParametersBuilder().toJobParameters());
    }
}
