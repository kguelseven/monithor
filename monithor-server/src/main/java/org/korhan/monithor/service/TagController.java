package org.korhan.monithor.service;

import lombok.extern.log4j.Log4j;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.model.TagResult;
import org.korhan.monithor.data.persistence.JobRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Log4j
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
    List<Job> jobs = repo.findByTagOrderByLastTimestampDesc(tag);
    List<Job> success = new ArrayList<>();
    List<Job> failure = new ArrayList<>();
    Long timestamp = 0l;
    for (Job job : jobs) {
      if (job.getLastResult()) {
        success.add(job);
      } else {
        failure.add(job);
      }
      timestamp = Math.max(timestamp, job.getLastTimestamp());
    }
    return TagResult.builder()
      .jobsFailure(failure)
      .jobsSuccess(success)
      .lastTimestamp(timestamp)
      .tag(tag)
      .success(failure.isEmpty())
      .build();
  }
}
