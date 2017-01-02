package org.korhan.monithor.service;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Links {
    private Pagination pagination;
}