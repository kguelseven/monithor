package org.korhan.monithor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.persistence.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = IntegrationTestConfig.class)
public class JobControllerCrudIT {

  @Autowired
  private IntegrationTestUtils restUtils;

  @Autowired
  private JobRepository repo;

  @BeforeEach
  public void setup() {
  }

  @AfterEach
  public void tearDown() {
    repo.deleteAll();
  }

  @Test
  public void testCreate() throws Exception {
    List<Job> jobs = restUtils.createJobs("job1", "job2", "job3");
    assertEquals(3, jobs.size());
    for (Job job : jobs) {
      assertEquals(job, restUtils.load(job.getId()));
    }
  }

  @Test
  public void testUpdate() throws Exception {
    List<Job> jobs = restUtils.createJobs("job1", "job2", "job3");
    for (Job job : jobs) {
      job.setName(job.getName() + "_1");
      restUtils.update(job);
    }
    for (Job job : jobs) {
      assertEquals(job.getName(), restUtils.load(job.getId()).getName());
    }
  }

  @Test
  public void testDelete() {
    List<Job> jobs = restUtils.createJobs("job1", "job2", "job3");
    jobs.forEach(restUtils::delete);
    for (Job job : jobs) {
      assertNull(restUtils.load(job.getId()));
    }
  }

}