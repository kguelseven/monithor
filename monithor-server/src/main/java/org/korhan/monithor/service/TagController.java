package org.korhan.monithor.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.model.TagResult;
import org.korhan.monithor.data.persistence.JobRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "tags")
public class TagController {

  private final JobRepository repo;

  public TagController(JobRepository repo) {
    this.repo = repo;
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public List<String> allTags() {
    return repo.findAllTags();
  }

  @RequestMapping(value = "/status/{tag}", method = RequestMethod.GET)
  public TagResult getStatus(@PathVariable("tag") String tag) {
    List<Job> jobs = repo.findByTag(tag);
    Optional<Job> lastJob = jobs.stream().max(Comparator.comparing(j -> j.getLastTimestamp()));
    long lastTimestamp = 0l;
    if(lastJob.isPresent()) {
      lastTimestamp = lastJob.get().getLastTimestamp();
    }
    boolean successStatus = jobs.stream().allMatch(j -> j.getLastResult());
    return TagResult.builder()
      .jobs(jobs)
      .lastTimestamp(lastTimestamp)
      .tag(tag)
      .success(successStatus)
      .build();
  }
}
