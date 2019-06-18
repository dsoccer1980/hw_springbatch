package ru.dsoccer1980.shell;

import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.dsoccer1980.service.MigrationService;

@ShellComponent
public class MigrateCommands {

    private final MigrationService migrationService;
    private final JobLauncher jobLauncher;
    private final Job job;

    public MigrateCommands(MigrationService migrationService, JobLauncher jobLauncher, Job job) {
        this.migrationService = migrationService;
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    @ShellMethod("migrate")
    @SneakyThrows
    public void migrate() {
        JobParameters jobParameters =
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis()).toJobParameters();

        jobLauncher.run(job, jobParameters);
    }

}
