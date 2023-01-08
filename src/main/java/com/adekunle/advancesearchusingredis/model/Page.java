package com.adekunle.advancesearchusingredis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Page {
    private List<Post> posts;
    private Integer totalPages;
    private Integer currentPage;
    private Long total;
}
