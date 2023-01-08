package com.adekunle.advancesearchusingredis;

import com.adekunle.advancesearchusingredis.model.Post;
import com.adekunle.advancesearchusingredis.repository.PostRepositoryImplementation;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.search.FieldName;
import redis.clients.jedis.search.IndexDefinition;
import redis.clients.jedis.search.IndexOptions;
import redis.clients.jedis.search.Schema;

import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class AdvanceSearchUsingRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvanceSearchUsingRedisApplication.class, args);
    }

    @Autowired
    private PostRepositoryImplementation postRepository;
    @Autowired
    private UnifiedJedis unifiedJedis;

    @Value("classpath:data.json")
    Resource resourceFile;   // to load the json file

    @Bean
    CommandLineRunner commandLineRunner() {
        return (args) -> {
            // deleting posts if exist
            postRepository.deletePost();

            // dropping index files if exist
            try {
                unifiedJedis.ftDropIndex("post-idx");
            } catch (Exception e) {
                log.info("index of not available ");
            }
            //retrieving the data from the json file
            String data = new String(resourceFile.getInputStream().readAllBytes());

            //converted the data to an object with the same properties and ignored the properties that can fail with the DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
            ObjectMapper objectMap = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            //converted all the object data to post
            Post[] posts = objectMap.readValue(data, Post[].class);

            //saving the data converted
            Arrays.stream(posts).forEach(postRepository::savePost);

            //creating the post schema

            //the database structure has content, title , tags(which is a set of tags) and views which is a numeric value
            Schema schema = new Schema()
                    .addField(new Schema.Field(FieldName.of("$.content").as("content"), Schema.FieldType.TEXT, true, false)) // creating schema for the content fields
                    .addField(new Schema.TextField((FieldName.of("$.title").as("title")))) //title
                    .addField(new Schema.Field(FieldName.of("$.tags[*]").as("tags"), Schema.FieldType.TAG)) // tags
                    .addField(new Schema.Field(FieldName.of("$.views").as("views"), Schema.FieldType.NUMERIC, false, true)); //views


            //creating index definition

            IndexDefinition indexDefinition =
                    new IndexDefinition(IndexDefinition.Type.JSON) // specific index definition either JSON OR HASH
                            .setPrefixes(new String[]{"post:"});

            unifiedJedis.ftCreate("post-idx", IndexOptions.defaultOptions().setDefinition(indexDefinition), schema);


        };
    }

}
