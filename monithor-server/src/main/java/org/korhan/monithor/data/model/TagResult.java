package org.korhan.monithor.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = TagResult.TagResultBuilder.class)
public class TagResult {
  private String tag;
  private boolean success;
  private Long lastTimestamp;
  private List<Job> jobs;

  @JsonPOJOBuilder(withPrefix = "")
  public static final class TagResultBuilder {
  }
}
