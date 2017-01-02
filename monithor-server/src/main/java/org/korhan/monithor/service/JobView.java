package org.korhan.monithor.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Value;
import org.korhan.monithor.data.model.Job;

import java.util.List;

@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobView {
    private Links links;
    private List<Job> data;
}
