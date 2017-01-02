package org.korhan.monithor.jobrunner;

import lombok.extern.log4j.Log4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.model.JobResult;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

@Log4j
@Component
public class HttpChecker implements Checker {

    private final HttpClient client;
    private final VersionExtractor extractor;

    public HttpChecker(HttpClient client, VersionExtractor extractor) {
        this.client = client;
        this.extractor = extractor;
    }

    @Override
    public JobResult check(Job job) {
        long startMs = System.currentTimeMillis();
        JobResult.JobResultBuilder builder = JobResult.builder().job(job);
        try {
            HttpResponse response = httpCall(job);
            String text = readText(response);
            builder.success(match(job.getSuccessMatch(), text))
                    .version(extractor.extract(job.getVersionMatch(), text));
        } catch (Exception ex) {
            log.error("Error running check", ex);
            builder.success(false).error(getError(ex));
        }
        return builder.duration(System.currentTimeMillis() - startMs).build();
    }

    private boolean match(String successMatch, String text) {
        return Pattern.matches(".*" + successMatch + ".*", text);
    }

    private String readText(HttpResponse response) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    private HttpResponse httpCall(Job job) throws IOException {
        return client.execute(new HttpGet(job.getUrl()));
    }

    private String getError(Exception ex) {
        String error = ex.getMessage();
        if (error == null) {
            error = ex.getClass().getSimpleName();
        }
        return error;
    }
}
