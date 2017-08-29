package org.korhan.monithor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.persistence.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = IntegrationTestConfig.class)
public class CheckControllerIT {

  private Job job1;

  @Autowired
  private IntegrationTestUtils restUtils;

  @Autowired
  private JobRepository repo;

  @Before
  public void setup() {
    job1 = restUtils.createJobWithTags("dog fooding", true, new String[]{"tag1", "tag2", "tag3"});
  }

  @After
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
    jobSaved = repo.findOne(jobSaved.getId());
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