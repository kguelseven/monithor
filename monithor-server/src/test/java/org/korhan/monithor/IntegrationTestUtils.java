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

  final static String JOBS_URL = "http://localhost:8888/jobs/";
  final static String TAGS_URL = "http://localhost:8888/tags/";
  final static String CHECK_URL = "http://localhost:8888/check/";

  @Autowired
  private TestRestTemplate restTemplate;

  Job createDisabledJobWithTags(String name, boolean result, String... tags) {
    Job job = newJob(name, result, tags);
    job.setDisabled(true);
    return restTemplate.postForObject(JOBS_URL, job, Job.class);
  }


  Job createJobWithTags(String name, boolean result, String... tags) {
    Job job = newJob(name, result, tags);
    return restTemplate.postForObject(JOBS_URL, job, Job.class);
  }

  Job newJob(String name, boolean result, String... tags) {
    Job job = new Job();
    job.setIntervalSecs(1);
    job.setUrl("http://korhan.org");
    job.setMatch("testsuccess");
    job.setLastResult(result);
    job.setName(name);
    job.getTags().addAll(Arrays.asList(tags));
    return job;
  }

  List<Job> createJobs(String... names) {
    List jobs = new ArrayList();
    for (String name : names) {
      jobs.add(createJobWithTags(name, true));
    }
    return jobs;
  }

  List<Job> queryTagOrName(String queryString) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(JOBS_URL)
                                                       .queryParam("queryString", queryString);
    URI uri = builder.build().encode().toUri();
    return restTemplate.exchange(uri, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<Job>>() {
        }).getBody();
  }

  List<Job> getByTag(String tag) {
    return restTemplate.exchange(JOBS_URL + "tag/{tag}", HttpMethod.GET, null,
        new ParameterizedTypeReference<List<Job>>() {
        }, tag).getBody();
  }

  List<Job> getByName(String name) {
    return restTemplate.exchange(JOBS_URL + "name/{name}", HttpMethod.GET, null,
        new ParameterizedTypeReference<List<Job>>() {
        }, name).getBody();
  }

  List<String> getTags() {
    return restTemplate.exchange(TAGS_URL, HttpMethod.GET, null,
        new ParameterizedTypeReference<List<String>>() {
        }).getBody();
  }

  TagResult getTagStatus(String tag) {
    return restTemplate.getForObject(TAGS_URL + "status/{tag}", TagResult.class, tag);
  }

  Job check(Job job) {
    return restTemplate.postForObject(CHECK_URL, job, Job.class);
  }

  void delete(Job job) {
    restTemplate.delete(JOBS_URL + "{id}", job.getId());
  }

  void update(Job job) {
    restTemplate.put(JOBS_URL + "{id}", job, job.getId());
  }

  Job load(Long id) {
    return restTemplate.getForObject(JOBS_URL + "{id}", Job.class, id);
  }
}
