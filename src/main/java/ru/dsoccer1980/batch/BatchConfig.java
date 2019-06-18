package ru.dsoccer1980.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowJob;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.dsoccer1980.domain.Author;
import ru.dsoccer1980.domain.Book;
import ru.dsoccer1980.domain.Genre;
import ru.dsoccer1980.domain.jpa.JpaAuthor;
import ru.dsoccer1980.domain.jpa.JpaBook;
import ru.dsoccer1980.domain.jpa.JpaGenre;
import ru.dsoccer1980.repository.JpaAuthorRepository;
import ru.dsoccer1980.repository.JpaBookRepository;
import ru.dsoccer1980.repository.JpaGenreRepository;

import java.util.HashMap;

@EnableBatchProcessing
@Configuration
public class BatchConfig {
    private final Logger logger = LoggerFactory.getLogger("Batch");

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JpaAuthorRepository jpaAuthorRepository;
    private final JpaGenreRepository jpaGenreRepository;
    private final JpaBookRepository jpaBookRepository;

    public BatchConfig(JobBuilderFactory jobBuilderFactory,
                       StepBuilderFactory stepBuilderFactory,
                       JpaAuthorRepository jpaAuthorRepository,
                       JpaGenreRepository jpaGenreRepository,
                       JpaBookRepository jpaBookRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jpaAuthorRepository = jpaAuthorRepository;
        this.jpaGenreRepository = jpaGenreRepository;
        this.jpaBookRepository = jpaBookRepository;
    }


    @Bean
    public ItemReader<Author> readerAuthor(MongoTemplate mongoTemplate) {
        return getBuild(mongoTemplate, Author.class, "ItemReaderAuthor");
    }

    @Bean
    public ItemReader<Genre> readerGenre(MongoTemplate mongoTemplate) {
        return getBuild(mongoTemplate, Genre.class, "ItemReaderGenre");
    }

    @Bean
    public ItemReader<Book> readerBook(MongoTemplate mongoTemplate) {
        return getBuild(mongoTemplate, Book.class, "ItemReaderBook");
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
    public ItemProcessor processorBook() {
        return (ItemProcessor<Book, JpaBook>) book ->
                new JpaBook(Long.parseLong(book.getId()), book.getName(),
                        new JpaAuthor(Long.parseLong(book.getAuthor().getId()), book.getAuthor().getName()),
                        new JpaGenre(Long.parseLong(book.getGenre().getId()), book.getGenre().getName()));
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
    public ItemWriter writerBook() {
        return new RepositoryItemWriterBuilder<JpaBook>()
                .repository(jpaBookRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Job importUserJob(Step stepMigrateAuthors, Step stepMigrateGenres, Step stepMigrateBooks) {
        FlowJob job = (FlowJob) jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(stepMigrateAuthors)
                .next(stepMigrateGenres)
                .next(stepMigrateBooks)
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
    public Step stepMigrateAuthors(ItemWriter writerAuthor, ItemReader readerAuthor, ItemProcessor processorAuthor) {
        return getStep(writerAuthor, readerAuthor, processorAuthor, "stepMigrateAuthors");
    }

    @Bean
    public Step stepMigrateGenres(ItemWriter writerGenre, ItemReader readerGenre, ItemProcessor processorGenre) {
        return getStep(writerGenre, readerGenre, processorGenre, "stepMigrateGenres");
    }

    @Bean
    public Step stepMigrateBooks(ItemWriter writerBook, ItemReader readerBook, ItemProcessor processorBook) {
        return getStep(writerBook, readerBook, processorBook, "stepMigrateBooks");
    }

    private <T> MongoItemReader<T> getBuild(MongoTemplate mongoTemplate, Class<T> clazz, String name) {
        return new MongoItemReaderBuilder<T>()
                .name(name)
                .template(mongoTemplate)
                .jsonQuery("{}")
                .sorts(new HashMap<>())
                .targetType(clazz)
                .build();
    }

    private <S, D> Step getStep(ItemWriter<D> writer, ItemReader<S> reader, ItemProcessor<S, D> processor, String name) {
        return stepBuilderFactory.get(name)
                .<S, D>chunk(5)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
