package org.korhan.monithor.runner;


import lombok.extern.log4j.Log4j;
import org.korhan.monithor.check.Checker;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
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

  @Scheduled(fixedDelayString = "${runner.delay}")
  public void run() throws Exception {
    long startMillis = System.currentTimeMillis();
    List<Job> jobs = loadDueJobs();
    logJobsLoaded(jobs);
    startJobs(jobs);
    Set<Job> jobsChecked = waitForResults(jobs.size());
    setNotCheckedResult(jobs, jobsChecked);
    updateJobs(jobs);
    logJobsFinished(jobs, startMillis);
  }

  private void updateJobs(List<Job> jobs) {
    for (Job job : jobs) {
      repo.save(job);
    }
  }

  private void startJobs(List<Job> jobs) {
    for (Job job : jobs) {
      completionService.submit(new JobTask(checker, job));
    }
  }

  private List<Job> loadDueJobs() {
    List<Job> jobs = repo.findAll()
                         .stream()
                         .filter(j -> j.isDue() && !j.isDisabled())
                         .collect(Collectors.toList());
    return jobs;
  }

  private Set<Job> waitForResults(int jobsCount) throws InterruptedException {
    Set<Job> results = new HashSet<>();
    for (int i = 0; i < jobsCount; i++) {
      try {
        Future<Job> future = completionService.take();
        results.add(future.get());
      } catch (ExecutionException ee) {
        log.error("ExecutionException executing task", ee.getCause());
      } catch (Exception ex) {
        log.error("Error executing task", ex);
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
      job.setLastBuildTimestamp(null);
    }
  }

  private void logJobsFinished(List<Job> jobs, long startMillis) {
    if (!jobs.isEmpty()) {
      log.debug(jobs.size() + " jobs run in " + (System.currentTimeMillis() - startMillis) + "ms");
    }
  }

  private void logJobsLoaded(List<Job> jobs) {
    if (!jobs.isEmpty()) {
      log.debug("Total jobs loaded " + jobs.size());
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
      job.populateFromResult(result);
      return job;
    }
  }
}
