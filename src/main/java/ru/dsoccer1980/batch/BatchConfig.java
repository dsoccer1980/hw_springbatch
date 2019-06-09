package ru.dsoccer1980.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowJob;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.dsoccer1980.domain.Author;
import ru.dsoccer1980.domain.Genre;
import ru.dsoccer1980.domain.jpa.JpaAuthor;
import ru.dsoccer1980.domain.jpa.JpaGenre;
import ru.dsoccer1980.repository.JpaAuthorRepository;
import ru.dsoccer1980.repository.JpaGenreRepository;

import java.util.HashMap;
import java.util.List;

@EnableBatchProcessing
@Configuration
public class BatchConfig {
    private final Logger logger = LoggerFactory.getLogger("Batch");

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    JpaAuthorRepository jpaAuthorRepository;

    @Autowired
    JpaGenreRepository jpaGenreRepository;

    @Bean
    public ItemReader<Author> readerAuthor(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<Author>()
                .name("ItemReaderAuthor")
                .template(mongoTemplate)
                .jsonQuery("{}")
                .sorts(new HashMap<>())
                .targetType(Author.class)
                .name("mongo")
                .build();
    }

    @Bean
    public ItemReader<Genre> readerGenre(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<Genre>()
                .name("ItemReaderGenre")
                .template(mongoTemplate)
                .jsonQuery("{}")
                .sorts(new HashMap<>())
                .targetType(Genre.class)
                .build();
    }

    @Bean
    public ItemProcessor processorAuthor() {
        return (ItemProcessor<Author, JpaAuthor>) author -> new JpaAuthor(Long.parseLong(author.getId()), author.getName());
    }

    @Bean
    public ItemProcessor processorGenre() {
        return (ItemProcessor<Genre, JpaGenre>) genre -> new JpaGenre(Long.parseLong(genre.getId()), genre.getName());
    }

    @Bean
    public ItemWriter writerAuthor() {
        return new RepositoryItemWriterBuilder<JpaAuthor>()
                .repository(jpaAuthorRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public ItemWriter writerGenre() {
        return new RepositoryItemWriterBuilder<JpaGenre>()
                .repository(jpaGenreRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Job importUserJob(Step step1, Step step2) {
        FlowJob job = (FlowJob) jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .next(step2)
                .end()
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        logger.info("Начало job");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        logger.info("Конец job");
                    }
                })
                .build();
        job.setRestartable(true);
        return job;
    }

    @Bean
    public Step step1(ItemWriter writerAuthor, ItemReader readerAuthor, ItemProcessor processorAuthor) {
        return stepBuilderFactory.get("step1")
                .chunk(5)
                .reader(readerAuthor)
                .processor(processorAuthor)
                .writer(writerAuthor)
                .build();
    }

    @Bean
    public Step step2(ItemWriter writerGenre, ItemReader readerGenre, ItemProcessor processorGenre) {
        return stepBuilderFactory.get("step2")
                .chunk(5)
                .reader(readerGenre)
                .processor(processorGenre)
                .writer(writerGenre)
                .build();
    }
}
