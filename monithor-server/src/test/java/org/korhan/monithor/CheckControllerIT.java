package org.korhan.monithor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.persistence.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = IntegrationTestConfig.class)
public class CheckControllerIT {

  private Job job1;

  @Autowired
  private IntegrationTestUtils restUtils;

  @Autowired
  private JobRepository repo;

  @BeforeEach
  public void setup() {
    job1 = restUtils.createJobWithTags("dog fooding", true, new String[]{"tag1", "tag2", "tag3"});
  }

  @AfterEach
  public void tearDown() {
    repo.deleteAll();
  }

  @Test
  public void testCheckSuccess() {
    Job job = newJob("fooding");
    Job jobChecked = restUtils.check(job);
    assertThat(jobChecked.getLastResult()).isTrue();
  }

  @Test
  public void testCheckFailure() {
    Job job = newJob("fLooding");
    Job jobChecked = restUtils.check(job);
    assertThat(jobChecked.getLastResult()).isFalse();
  }

  @Test
  public void testCheckSuccessUpdate() {
    Job job = newJob("fooding");
    job.setLastResult(false);
    Job jobSaved = repo.save(job);
    Job jobChecked = restUtils.check(jobSaved);
    jobSaved = repo.findById(jobSaved.getId()).orElse(null);
    assertThat(jobSaved.getLastResult()).isTrue();
    assertThat(jobSaved).isEqualTo(jobChecked);
  }

  private Job newJob(String successMatch) {
    Job job = new Job();
    job.setName("Testing with " + successMatch);
    job.setUrl(IntegrationTestUtils.JOBS_URL + job1.getId());
    job.setMatch(successMatch);
    return job;
  }
}