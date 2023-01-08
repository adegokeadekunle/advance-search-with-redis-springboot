package com.adekunle.advancesearchusingredis.repository;

import com.adekunle.advancesearchusingredis.model.CategoryStatistics;
import com.adekunle.advancesearchusingredis.model.Page;
import com.adekunle.advancesearchusingredis.model.Post;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.SearchResult;
import redis.clients.jedis.search.aggr.AggregationBuilder;
import redis.clients.jedis.search.aggr.AggregationResult;
import redis.clients.jedis.search.aggr.Reducers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;


@Repository
@RequiredArgsConstructor
public class PostRepositoryImplementation implements PostRepository {

    public final UnifiedJedis unifiedJedis;

    private static final Integer PAGE_SIZE = 5;

    @Override
    public Post savePost(Post pt) {
        if (pt.getPostId() == null) {
            pt.setPostId(UUID.randomUUID().toString());
        }
        Gson gson = new Gson();
        String key = "post:" + pt.getPostId();
        unifiedJedis.jsonSet(key, gson.toJson(pt));
        unifiedJedis.sadd("post", key);
        return pt;
    }

    @Override
    public Page searchPost(String content, Set<String> tags, Integer page) {

        long totalResult = 0L;

        StringBuilder queryBuilder = new StringBuilder();

        if (content != null && !content.isEmpty()) {
            queryBuilder.append("@content:" + content);

        }
        if (tags != null && !tags.isEmpty()) {
            queryBuilder.append(" @tags:{" + tags.stream().collect(Collectors.joining("|")) + "}");

        }
        String queryCriteria = queryBuilder.toString();

        Query query = null;
        if (queryCriteria.isEmpty()) {
            query = new Query();
        } else {
            query = new Query(queryCriteria);
        }

        query.limit(PAGE_SIZE * (page - 1), PAGE_SIZE); // this will  get the number of pages that will be returned from the query
        SearchResult searchResult =
                unifiedJedis.ftSearch("post-idx", query);

        totalResult = searchResult.getTotalResults();
        int numOfPages = (int) Math.ceil((double) totalResult / PAGE_SIZE);

        List<Post> postList = searchResult.getDocuments().stream().map(this::convertDocumentToPost).collect(Collectors.toList());

        return Page.builder()
                .posts(postList)
                .total(totalResult)
                .totalPages(numOfPages)
                .currentPage(page)
                .build();
    }

    private Post convertDocumentToPost(Document document) { //
        Gson gson = new Gson();
        String jsonDocument = document.getProperties()
                .iterator()
                .next()
                .getValue()
                .toString();
        return gson.fromJson(jsonDocument, Post.class);
    }

    @Override
    public void deletePost() {
        Set<String> keys = unifiedJedis.smembers("post");

        if (!keys.isEmpty()) {
            keys.forEach(unifiedJedis::jsonDel);
        }
        unifiedJedis.del("post");
    }

    @Override
    public void getTotalPostByCategory() {
        AggregationBuilder aggregateBuilder = new AggregationBuilder();

        aggregateBuilder.groupBy("tags",
                Reducers.count().as("NO_OF_POSTS")
                Reducers.avg("@views").as("AVERAGE_VIEW"));

        AggregationResult aggregationResult = unifiedJedis.ftAggregate("post-idx",aggregateBuilder);

        List<CategoryStatistics> categoriesList = new ArrayList<>();


//        LongStream.range(0,aggregationResult.totalResults)
//                .mapToObj(index -> aggregationResult.getRow((int)index))
//                .forEach(row ->)
    }
}
