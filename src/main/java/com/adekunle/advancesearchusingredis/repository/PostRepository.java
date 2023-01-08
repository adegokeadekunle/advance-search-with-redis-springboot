package com.adekunle.advancesearchusingredis.repository;

import com.adekunle.advancesearchusingredis.model.Page;
import com.adekunle.advancesearchusingredis.model.Post;

import java.util.Set;

public interface PostRepository {
    Post savePost(Post pt);

    Page searchPost(String content, Set<String> tags, Integer page);

    void deletePost();

    void getTotalPostByCategory();
}
