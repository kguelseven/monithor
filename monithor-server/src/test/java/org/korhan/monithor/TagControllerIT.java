package org.korhan.monithor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.model.TagResult;
import org.korhan.monithor.data.persistence.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = IntegrationTestConfig.class)
public class TagControllerIT {

  private Job job1, job2, job3;

  @Autowired
  private IntegrationTestUtils restUtils;

  @Autowired
  private JobRepository repo;

  @Before
  public void setup() {
    job1 = restUtils.createJobWithTags("foo1", true, new String[]{"tag1", "tag2", "tag3"});
    job2 = restUtils.createJobWithTags("foo2", false, new String[]{"tag1", "tag20"});
    job3 = restUtils.createJobWithTags("foo3", true, new String[]{"tag40", "tag2"});
  }

  @After
  public void tearDown() {
    repo.deleteAll();
  }

  @Test
  public void testGetTags() {
    assertThat(restUtils.getTags()).isEqualTo(Arrays.asList("TAG1", "TAG2", "TAG20", "TAG3", "TAG40"));
  }

  @Test
  public void testGetStatusSuccess() {
    TagResult statusTag2 = restUtils.getTagStatus("TAG2");
    assertThat(statusTag2.isSuccess()).isTrue();
    assertThat(statusTag2.getJobs()).isEqualTo(Arrays.asList(job1, job3));
  }

  @Test
  public void testGetStatusFailure() {
    TagResult statusTag1 = restUtils.getTagStatus("TAG1");
    assertThat(statusTag1.isSuccess()).isFalse();
    assertThat(statusTag1.getJobs()).isEqualTo(Arrays.asList(job1, job2));
    assertThat(statusTag1.getJobs().get(0).getLastResult()).isTrue();
    assertThat(statusTag1.getJobs().get(1).getLastResult()).isFalse();
  }
}