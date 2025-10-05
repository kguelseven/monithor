package org.korhan.monithor.check;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.model.JobResult;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HttpCheckerTest {

  private HttpChecker testee;
  private DataExtractor extractor = new DataExtractor();

  @Mock
  private HttpClient client;

  @BeforeEach
  public void setup() throws IOException {
    this.testee = new HttpChecker(client, extractor);
    lenient().when(client.execute(any(HttpGet.class), any(HttpClientResponseHandler.class)))
      .thenReturn("YfooU 12 x13 ver = 1.20<br> a");
  }

  @Test
  public void testCheckSimpleString() {
    JobResult result = testee.check(newJob("foo"));
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getError()).isNull();
  }

  @Test
  public void testCheckSimpleStringFailure() {
    Job foo = newJob("foo");
    foo.setMatchFailure(true);
    JobResult result = testee.check(foo);
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getError()).isEqualTo("success match failed");
  }

  @Test
  public void testCheckRegexString() {
    JobResult result = testee.check(newJob("[a-z]{1}[o-o]{2}"));
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getError()).isNull();
  }

  @Test
  public void testCheckRegexStringFailure() {
    Job job = newJob("[a-z]{1}[o-o]{2}");
    job.setMatchFailure(true);
    JobResult result = testee.check(job);
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getError()).isEqualTo("success match failed");
  }

  @Test
  public void testCheckNonMatch() {
    JobResult result = testee.check(newJob("/d{2}"));
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getError()).isEqualTo("success match failed");
  }

  @Test
  public void testCheckIOException() throws IOException {
    when(client.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenThrow(new IOException("boom"));
    JobResult result = testee.check(newJob("foo"));
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getError()).contains("boom");
  }

  @Test
  public void testCheckDeploymentWrong() throws IOException {
    String now = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    setupMockResponse("1701.0.13-SNAPSHOT " + now + " foo");
    JobResult result = testee.check(newJobCheckDeployment("foo"));
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getBuildTimestamp()).isEqualTo(now);
    assertThat(result.getVersion()).isEqualTo("1701.0.13-SNAPSHOT");
    assertThat(result.getError()).isNull();
  }

  @Test
  public void testCheckDeploymentWrongBuildTimestamp() throws IOException {
    setupMockResponse("1701.0.13-SNAPSHOT 2017-01-06 00:37:45 foo");
    JobResult result = testee.check(newJobCheckDeployment("foo"));
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getBuildTimestamp()).isEqualTo("2017-01-06 00:37:45");
    assertThat(result.getVersion()).isEqualTo("1701.0.13-SNAPSHOT");
    assertThat(result.getError()).isEqualTo("deployment version check failed");
  }

  @Test
  public void testCheckDeploymentMissingVersion() throws IOException {
    setupMockResponse("2017-01-06 00:37:45 foo");
    JobResult result = testee.check(newJobCheckDeployment("foo"));
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getVersion()).isNull();
    assertThat(result.getBuildTimestamp()).isEqualTo("2017-01-06 00:37:45");
    assertThat(result.getError()).isEqualTo("deployment version check failed");
  }

  @Test
  public void testCheckDeploymentMissingBuildtimestamp() throws IOException {
    setupMockResponse("1701.0.13 foo");
    JobResult result = testee.check(newJobCheckDeployment("foo"));
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getBuildTimestamp()).isNull();
    assertThat(result.getVersion()).isEqualTo("1701.0.13");
    assertThat(result.getError()).isEqualTo("deployment version check failed");
  }

  private void setupMockResponse(String content) throws IOException {
    when(client.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenReturn(content);
  }

  private Job newJob(String successMatch) {
    Job job = new Job();
    job.setMatch(successMatch);
    job.setUrl("testing");
    return job;
  }

  private Job newJobCheckDeployment(String successMatch) {
    Job job = newJob(successMatch);
    job.setCheckDeployment(true);
    return job;
  }
}
