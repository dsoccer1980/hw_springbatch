package ru.dsoccer1980.shell;

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
import ru.dsoccer1980.service.MyService;

@ShellComponent
public class AuthorCommands {

    private final MyService myService;
    private final JobLauncher jobLauncher;
    private final Job job;

    public AuthorCommands(MyService myService, JobLauncher jobLauncher, Job job) {
        this.myService = myService;
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    @ShellMethod("migrate")
    public void migrate() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters jobParameters =
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis()).toJobParameters();


        jobLauncher.run(job, jobParameters);

    }

}
