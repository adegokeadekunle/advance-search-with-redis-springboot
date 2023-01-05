package com.adekunle.advancesearchusingredis;

import com.adekunle.advancesearchusingredis.model.Post;
import com.adekunle.advancesearchusingredis.repository.PostRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import redis.clients.jedis.UnifiedJedis;

@SpringBootApplication
public class AdvanceSearchUsingRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvanceSearchUsingRedisApplication.class, args);
    }

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UnifiedJedis unifiedJedis;

    @Value("classpath:data.json")
    Resource resourceFile;   // to load the json file

    @Bean
    CommandLineRunner commandLineRunner(){
        return (args) -> {
            String data = new String(resourceFile.getInputStream().readAllBytes());
            ObjectMapper objectMap =new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

            Post[] posts = objectMap.readValue(data,Post[].class);

        };
    }
}
