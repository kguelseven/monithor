package org.korhan.monithor.jobrunner;


import lombok.extern.log4j.Log4j;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.model.JobResult;
import org.korhan.monithor.data.persistence.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j
@Component
public class JobRunner {

  private final JobRepository repo;
  private final Checker checker;
  private final CompletionService<Job> completionService;

  public JobRunner(JobRepository repo, Executor executor, Checker checker) {
    this.repo = repo;
    this.checker = checker;
    this.completionService = new ExecutorCompletionService<>(executor);
  }

  @Scheduled(fixedDelayString = "${jobrunner.delay}")
  public void run() throws Exception {
    List<Job> jobs = loadDueJobs();
    startJobs(jobs);
    Set<Job> jobsChecked = waitForResults(jobs.size());
    setNotCheckedResult(jobs, jobsChecked);
    updateJobs(jobs);
  }

  private void updateJobs(List<Job> jobs) {
    for (Job job : jobs) {
      repo.save(job);
    }
    log.debug("All jobs updated");
  }

  private void startJobs(List<Job> jobs) {
    for (Job job : jobs) {
      completionService.submit(new JobTask(checker, job));
      log.debug("Job started " + job);
    }
  }

  private List<Job> loadDueJobs() {
    List<Job> jobs = repo.findAll()
      .stream()
      .filter(j -> j.isDue())
      .collect(Collectors.toList());
    log.debug("Total due jobs " + jobs.size());
    return jobs;
  }

  private Set<Job> waitForResults(int jobsCount) throws InterruptedException {
    Set<Job> results = new HashSet<>();
    for (int i = 0; i < jobsCount; i++) {
      try {
        Future<Job> future = completionService.poll(30, TimeUnit.SECONDS);
        if (future != null) {
          results.add(future.get());
        } else {
          log.error("Job timed out");
        }
      } catch (Exception ex) {
        log.error("Error polling task", ex);
      }
    }
    return results;
  }

  private void setNotCheckedResult(List<Job> allJobs, Set<Job> jobsChecked) {
    Set<Job> jobsNotChecked = new HashSet<>(allJobs);
    jobsNotChecked.removeAll(jobsChecked);
    for (Job job : jobsNotChecked) {
      job.setLastResult(false);
      job.setLastTimestamp(System.currentTimeMillis());
      job.setLastMessage("timeout?");
      job.setLastDuration(null);
      job.setLastVersion(null);
    }
  }


  class JobTask implements Callable<Job> {

    private final Job job;
    private final Checker checker;

    public JobTask(Checker checker, Job job) {
      this.checker = checker;
      this.job = job;
    }

    @Override
    public Job call() throws Exception {
      JobResult result = checker.check(job);
      job.setLastResult(result.isSuccess());
      job.setLastTimestamp(System.currentTimeMillis());
      job.setLastMessage(result.getError());
      job.setLastDuration(result.getDuration());
      job.setLastVersion(result.getVersion());
      return job;
    }
  }
}
