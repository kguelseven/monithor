package org.korhan.monithor.check;

import lombok.extern.log4j.Log4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.model.JobResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Log4j
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
      HttpResponse response = httpCall(job);
      String text = readText(response);
      version = extractor.extractVersion(job.getVersionMatch(), text);
      buildTimestamp = extractor.extractBuildTimestamp(job.getBuildTimestampMatch(), text);
      if (!containsSuccessMatch(job.getSuccessMatch(), text)) {
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

  private boolean containsSuccessMatch(String successMatch, String text) {
    boolean matches = Pattern.matches(".*" + successMatch + ".*", text);
    return matches;
  }

  private boolean hasExpectedDeployment(String version, String buildTimestamp) {
    if (buildTimestamp != null && version != null && version.contains("SNAPSHOT")) {
      LocalDate buildDate = LocalDate.parse(buildTimestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      return LocalDate.now().isEqual(buildDate);
    }
    return false;
  }

  private String readText(HttpResponse response) throws IOException {
    String text = EntityUtils.toString(response.getEntity(), "UTF-8");
    return text.replaceAll("\\r\\n|\\r|\\n", " ");
  }

  private HttpResponse httpCall(Job job) throws IOException {
    return client.execute(new HttpGet(job.getUrl()));
  }

  private String getError(Exception ex) {
    String error = ex.getClass().getSimpleName();
    if (ex.getMessage() != null) {
      error += ": " + ex.getMessage();
    }
    return error;
  }
}
