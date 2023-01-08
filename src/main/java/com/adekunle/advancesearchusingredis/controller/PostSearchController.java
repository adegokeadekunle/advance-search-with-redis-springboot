package com.adekunle.advancesearchusingredis.controller;

import com.adekunle.advancesearchusingredis.model.CategoryStatistics;
import com.adekunle.advancesearchusingredis.model.Page;
import com.adekunle.advancesearchusingredis.repository.PostRepository;
import com.adekunle.advancesearchusingredis.repository.PostRepositoryImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class PostSearchController {
    private final PostRepository postSearch;

    @GetMapping("/search")
    public Page searchPost(@RequestParam(name = "content", required = false) String content,
                           @RequestParam(name = "tags", required = false) Set<String> tags,
                           @RequestParam(name = "page", defaultValue = "1") Integer page) {

        return postSearch.searchPost(content, tags, page);

    }

    @GetMapping("/categoryStat")
    public List<CategoryStatistics> getTotalPostByCategory() {
        return postSearch.getTotalPostByCategory();
    }
}
