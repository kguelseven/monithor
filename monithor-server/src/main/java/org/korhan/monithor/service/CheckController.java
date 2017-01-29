package org.korhan.monithor.service;

import javax.validation.Valid;

import org.korhan.monithor.check.Checker;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.model.JobResult;
import org.korhan.monithor.data.persistence.JobRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "check")
public class CheckController {

  private final JobRepository repo;
  private final Checker checker;

  public CheckController(Checker checker, JobRepository repo) {
    this.checker = checker;
    this.repo = repo;
  }

  @RequestMapping(value = "/", method = RequestMethod.POST)
  public Job check(@RequestBody @Valid Job job, BindingResult bindingResult) {
    JobResult jobResult = checker.check(job);
    job.populateFromResult(jobResult);
    if(job.getId() != null) {
      job = repo.save(job);
    }
    return job;
  }
}
