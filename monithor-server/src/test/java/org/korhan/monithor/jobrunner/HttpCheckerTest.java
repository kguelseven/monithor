package org.korhan.monithor.jobrunner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.model.JobResult;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpCheckerTest {

    @Mock
    private HttpClient client;
    private VersionExtractor extractor = new VersionExtractor();
    private Job job;

    private HttpChecker testee;

    @Before
    public void setup() throws IOException {
        this.testee = new HttpChecker(client, extractor);
        HttpResponse httpResponse = mock(HttpResponse.class);
        HttpEntity httpEntity = mock(HttpEntity.class);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream("YfooU 12 x13 ver = 1.20<br> a".getBytes(StandardCharsets.UTF_8)));
        when(client.execute(any(HttpGet.class))).thenReturn(httpResponse);

        job = new Job();
        job.setUrl("http://localhost:8080");
    }

    @Test
    public void testCheckSimpleString() {
        job.setSuccessMatch("foo");
        JobResult result = testee.check(job);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    public void testCheckRegexString() {
        job.setSuccessMatch("[a-z]{1}[o-o]{2}");
        JobResult result = testee.check(job);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    public void testCheckNonMatch() {
        job.setSuccessMatch("/d{2}");
        JobResult result = testee.check(job);
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    public void testCheckIOException() throws IOException {
        job.setSuccessMatch("foo");
        when(client.execute(any(HttpGet.class))).thenThrow(new IOException("boom"));
        JobResult result = testee.check(job);
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).contains("boom");
    }
}
