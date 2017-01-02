package org.korhan.monithor.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = JobResult.JobResultBuilder.class)
public class JobResult {
    private Job job;
    private boolean success;
    private long duration;
    private String version;
    private String error;
}
