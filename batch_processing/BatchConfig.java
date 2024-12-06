package com.springbatch.batch_processing;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.core.configuration.annotation.JobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.configuration.annotation;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@EnableBatchProcessing
public class BatchConfig {


//    @Autowired
//    private JobBuilderFactory jobBuilderFactory;
//
//    @Autowired
//    private JobRepository jobRepository;

//    @Autowired
//    private StepBuilderFactory stepBuilderFactory;
    @Bean
    public JpaPagingItemReader<User> itemReader(EntityManagerFactory entityManagerFactory) {
        JpaPagingItemReader<User> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT u FROM User u");
        reader.setPageSize(10);
        return reader;
    }

    @Bean
    public ItemProcessor<User,User> processor(){
        return user -> {
            user.setName(user.getName().toUpperCase());
            return user;
        };
    }

    @Bean
    public JdbcBatchItemWriter<User> writer(){
        JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("UPDATE user SET name = :name, email = :email WHERE id = :id");
        return writer;

    }

//    @Bean
//    public Tasklet myTasklet() {
//        return new Tasklet();
//    }
//
//   @Bean
//    public Step step1(StepBuilderFactory stepBuilderFactory, JpaPagingItemReader<User> reader,
//                      ItemProcessor<User, User> processor, JdbcBatchItemWriter<User> writer) {
//        return stepBuilderFactory.get("step1")
//                .<User, User>chunk(10)
//                .reader(reader)
//                .processor(processor)
//                .writer(writer)
//                .build();
//    }




    @Bean
    public Step myStep(JobRepository jobRepository, Tasklet myTasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(myTasklet, transactionManager) // or .chunk(chunkSize, transactionManager)
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step1) {

        return new JobBuilder("job", jobRepository)
                .start(step1)
                .build();

    }


}
