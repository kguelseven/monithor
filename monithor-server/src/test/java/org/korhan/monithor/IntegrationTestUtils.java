package org.korhan.monithor;

import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.model.TagResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntegrationTestUtils {

    private final String jobsUrl;
    private final String tagsUrl;

    IntegrationTestUtils() {
        this.jobsUrl = "http://localhost:8888/jobs/";
        this.tagsUrl = "http://localhost:8888/tags/";
    }

    @Autowired
    private TestRestTemplate restTemplate;

    Job createJobWithTags(String name, boolean result, String... tags) {
        Job job = new Job();
        job.setIntervalSecs(1);
        job.setUrl("http://korhan.org");
        job.setSuccessMatch("testsuccess");
        job.setLastResult(result);
        job.setName(name);
        job.getTags().addAll(Arrays.asList(tags));
        return restTemplate.postForObject(jobsUrl, job, Job.class);
    }

    List<Job> createJobs(String... names) {
        List jobs = new ArrayList();
        for (String name : names) {
            jobs.add(createJobWithTags(name, true));
        }
        return jobs;
    }

    List<Job> queryTagOrName(String queryString) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(jobsUrl)
                .queryParam("queryString", queryString);
        URI uri = builder.build().encode().toUri();
        return restTemplate.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Job>>() {
                }).getBody();
    }

    List<Job> queryTagOrName(String sort, Integer page, Integer per_page, String queryString) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(jobsUrl)
                .queryParam("queryString", queryString);
        URI uri = builder.build().encode().toUri();
        return restTemplate.exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Job>>() {
                }).getBody();
    }

    List<Job> getByTag(String tag) {
        return restTemplate.exchange(jobsUrl + "tag/{tag}", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Job>>() {
                }, tag).getBody();
    }

    List<Job> getByName(String name) {
        return restTemplate.exchange(jobsUrl + "name/{name}", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Job>>() {
                }, name).getBody();
    }

    List<String> getTags() {
        return restTemplate.exchange(tagsUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<String>>() {
                }).getBody();
    }

    TagResult getTagStatus(String tag) {
        return restTemplate.getForObject(tagsUrl + "status/{tag}", TagResult.class, tag);
    }

    void delete(Job job) {
        restTemplate.delete(jobsUrl + "{id}", job.getId());
    }

    void update(Job job) {
        restTemplate.put(jobsUrl + "{id}", job, job.getId());
    }

    Job load(Long id) {
        return restTemplate.getForObject(jobsUrl + "{id}", Job.class, id);
    }
}
