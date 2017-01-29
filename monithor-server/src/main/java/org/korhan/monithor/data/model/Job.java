package org.korhan.monithor.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @NotNull
  private String name;
  @NotNull
  @URL
  private String url;
  @NotNull
  private String successMatch;
  private String versionMatch;
  private String buildTimestampMatch;
  private boolean checkDeployment;
  @NotNull
  private Integer intervalSecs = 300;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "TAGS", joinColumns = @JoinColumn(name = "job_id"))
  @Column(name = "tag")
  private Set<String> tags = new HashSet<>();
  private Long lastTimestamp = 0l;
  private Boolean lastResult = false;
  private String lastMessage = "";
  private String lastVersion = "";
  private String lastBuildTimestamp = "";
  private Long lastDuration = 0l;

  public boolean isDue() {
    if (lastTimestamp == null) {
      return true;
    }
    return (System.currentTimeMillis() - lastTimestamp) > (1000 * intervalSecs);
  }

  public void populateFromResult(JobResult result) {
    setLastResult(result.isSuccess());
    setLastTimestamp(System.currentTimeMillis());
    setLastMessage(result.getError());
    setLastDuration(result.getDuration());
    setLastVersion(result.getVersion());
    setLastBuildTimestamp(result.getBuildTimestamp());
  }
}
