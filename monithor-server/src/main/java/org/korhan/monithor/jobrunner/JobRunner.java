package org.korhan.monithor.jobrunner;


import lombok.extern.log4j.Log4j;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.model.JobResult;
import org.korhan.monithor.data.persistence.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private final CompletionService<JobResult> completionService;

    public JobRunner(JobRepository repo, Executor executor, Checker checker) {
        this.repo = repo;
        this.checker = checker;
        this.completionService = new ExecutorCompletionService<>(executor);
    }

    @Scheduled(fixedDelayString = "${jobrunner.delay}")
    public void run() throws Exception {
        List<Job> jobs = repo.findAll()
                .stream()
                .filter(j -> j.isDue())
                .collect(Collectors.toList());

        for (Job job : jobs) {
            completionService.submit(new JobTask(checker, job));
            log.info("Job started " + job);
        }

        Set<JobResult> results = waitForResults(jobs);
        collectResults(jobs, results);

        for (Job job : jobs) {
            repo.save(job);
        }
    }

    private Set<JobResult> waitForResults(List<Job> jobs) throws InterruptedException {
        Set<JobResult> results = new HashSet<>();
        for (Job job : jobs) {
            try {
                Future<JobResult> future = completionService.poll(30, TimeUnit.SECONDS);
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

    private void collectResults(List<Job> jobs, Set<JobResult> results) {
        List<Job> tmpJobs = new ArrayList<>(jobs);
        for (JobResult result : results) {
            Job job = result.getJob();
            job.setLastResult(result.isSuccess());
            job.setLastTimestamp(System.currentTimeMillis());
            job.setLastMessage(result.getError());
            job.setLastDuration(result.getDuration());
            job.setLastVersion(result.getVersion());
            tmpJobs.remove(job);
        }

        for (Job job : tmpJobs) {
            job.setLastResult(false);
            job.setLastTimestamp(System.currentTimeMillis());
            job.setLastMessage("timeout?");
            job.setLastDuration(null);
            job.setLastVersion(null);
        }
    }

    class JobTask implements Callable<JobResult> {

        private final Job job;
        private final Checker checker;

        public JobTask(Checker checker, Job job) {
            this.checker = checker;
            this.job = job;
        }

        @Override
        public JobResult call() throws Exception {
            return checker.check(job);
        }
    }
}
