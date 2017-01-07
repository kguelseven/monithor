package org.korhan.monithor.service;

import lombok.extern.log4j.Log4j;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.persistence.JobRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Log4j
@RestController
@RequestMapping(value = "jobs")
public class JobController {

  private final JobRepository repo;

  public JobController(JobRepository repo) {
    this.repo = repo;
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public List<Job> queryJobs(@RequestParam("queryString") String query) {
    if (query != null && !query.trim().isEmpty()) {
      return repo.findByTagOrName(query);
    } else {
      return repo.findAllByOrderByLastTimestampDesc();
    }
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public Job jobById(@PathVariable("id") Long id) {
    return repo.findOne(id);
  }

  @RequestMapping(value = "/tag/{tag}", method = RequestMethod.GET)
  public List<Job> jobsByTag(@PathVariable("tag") String tag) {
    return repo.findByTagOrderByLastTimestampDesc(tag.toUpperCase());
  }

  @RequestMapping(value = "/name/{name}", method = RequestMethod.GET)
  public List<Job> jobsByName(@PathVariable("name") String name) {
    return repo.findAllByNameIgnoreCaseContainingOrderByLastTimestampDesc(name);
  }

  @RequestMapping(value = "/", method = RequestMethod.POST)
  public Job save(@RequestBody @Valid Job job, BindingResult bindingResult) {
    uppercaseAllTags(job);
    return repo.save(job);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public Job update(@RequestBody @Valid Job job, @PathVariable("id") Long id, BindingResult bindingResult) {
    uppercaseAllTags(job);
    return repo.save(job);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public void delete(@PathVariable("id") Long id) {
    repo.delete(id);
  }

  private void uppercaseAllTags(Job job) {
    job.setTags(job.getTags().stream()
      .map(String::toUpperCase)
      .collect(Collectors.toSet()));
  }
}

