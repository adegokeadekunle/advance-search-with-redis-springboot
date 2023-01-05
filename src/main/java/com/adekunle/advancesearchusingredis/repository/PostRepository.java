package com.adekunle.advancesearchusingredis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.UnifiedJedis;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    public final UnifiedJedis unifiedJedis;


}
