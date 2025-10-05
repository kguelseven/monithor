package org.korhan.monithor.check;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.model.JobResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Slf4j
@Component
public class HttpChecker implements Checker {

  private final HttpClient client;
  private final DataExtractor extractor;

  public HttpChecker(HttpClient client, DataExtractor extractor) {
    this.client = client;
    this.extractor = extractor;
  }

  @Override
  public JobResult check(Job job) {
    long startMs = System.currentTimeMillis();
    String version = null, buildTimestamp = null, error = null;
    try {
      String text = httpCall(job);
      version = extractor.extractVersion(job.getVersionMatch(), text);
      buildTimestamp = extractor.extractBuildTimestamp(job.getBuildTimestampMatch(), text);
      if (!containsMatch(job, text)) {
        error = "success match failed";
      } else if (job.isCheckDeployment()) {
        if (!hasExpectedDeployment(version, buildTimestamp)) {
          error = "deployment version check failed";
        }
      }
    } catch (Exception ex) {
      log.error("Error running check", ex);
      error = getError(ex);
    }
    return JobResult.builder()
                    .job(job)
                    .buildTimestamp(buildTimestamp)
                    .version(version)
                    .success(error == null)
                    .error(error)
                    .duration(System.currentTimeMillis() - startMs).build();
  }

  private boolean containsMatch(Job job, String text) {
    boolean matches = Pattern.matches(".*" + job.getMatch() + ".*", text);
    if (job.getMatchFailure() != null && job.getMatchFailure()) {
      return !matches;
    }
    return matches;
  }


  private boolean hasExpectedDeployment(String version, String buildTimestamp) {
    if (buildTimestamp != null && version != null && version.contains("SNAPSHOT")) {
      LocalDate buildDate = LocalDate.parse(buildTimestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      return LocalDate.now().isEqual(buildDate);
    }
    return false;
  }

  private String httpCall(Job job) throws IOException {
    return client.execute(new HttpGet(job.getUrl()), response -> {
      String text = EntityUtils.toString(response.getEntity(), "UTF-8");
      return text.replaceAll("\\r\\n|\\r|\\n", " ");
    });
  }

  private String getError(Exception ex) {
    String error = ex.getClass().getSimpleName();
    if (ex.getMessage() != null) {
      error += ": " + ex.getMessage();
    }
    return error;
  }
}
