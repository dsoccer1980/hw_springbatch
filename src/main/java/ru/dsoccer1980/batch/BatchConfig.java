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
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    JpaAuthorRepository jpaAuthorRepository;

    @Autowired
    JpaGenreRepository jpaGenreRepository;

    @Autowired
    JpaBookRepository jpaBookRepository;

    @Bean
    public ItemReader<Author> readerAuthor(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<Author>()
                .name("ItemReaderAuthor")
                .template(mongoTemplate)
                .jsonQuery("{}")
                .sorts(new HashMap<>())
                .targetType(Author.class)
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
    public ItemReader<Book> readerBook(MongoTemplate mongoTemplate) {
        return new MongoItemReaderBuilder<Book>()
                .name("ItemReaderBook")
                .template(mongoTemplate)
                .jsonQuery("{}")
                .sorts(new HashMap<>())
                .targetType(Book.class)
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
    public Job importUserJob(Step step1, Step step2, Step step3) {
        FlowJob job = (FlowJob) jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .next(step2)
                .next(step3)
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

    @Bean
    public Step step3(ItemWriter writerBook, ItemReader readerBook, ItemProcessor processorBook) {
        return stepBuilderFactory.get("step3")
                .chunk(5)
                .reader(readerBook)
                .processor(processorBook)
                .writer(writerBook)
                .build();
    }
}
