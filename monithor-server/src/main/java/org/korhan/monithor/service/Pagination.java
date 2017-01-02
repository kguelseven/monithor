package org.korhan.monithor.service;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Pagination {
    private int total;
    private int per_page;
    private int current_page;
    private int last_page;
    private int from;
    private int to;
}