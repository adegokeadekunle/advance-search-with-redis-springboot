package com.adekunle.advancesearchusingredis.repository;

import com.adekunle.advancesearchusingredis.model.Post;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.UnifiedJedis;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    public final UnifiedJedis unifiedJedis;

    public Post savePost(Post pt){
        if(pt.getPostId() == null){
            pt.setPostId(UUID.randomUUID().toString());
        }
        Gson gson = new Gson();
        String key = "post:"+pt.getPostId();
        unifiedJedis.jsonSet(key,gson.toJson(pt));
        unifiedJedis.sadd("post",key);
        return pt;
    }


}
