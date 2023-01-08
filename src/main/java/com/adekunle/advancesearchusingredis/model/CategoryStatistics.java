package com.adekunle.advancesearchusingredis.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryStatistics {
    private String tags;
    private Long totalPosts;
    private String averageViews;
}
