package org.korhan.monithor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.persistence.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = IntegrationTestConfig.class)
public class JobControllerQueryIT {

  private Job job1, job2, job3;

  @Autowired
  private IntegrationTestUtils restUtils;

  @Autowired
  private JobRepository repo;

  @Before
  public void setup() {
    job1 = restUtils.createJobWithTags("foo1", true, new String[]{"tag1", "tag2", "tag3"});
    job2 = restUtils.createJobWithTags("foo2", true, new String[]{"tag1", "tag20"});
    job3 = restUtils.createJobWithTags("foo3", true, new String[]{"tag40", "foo1"});
  }

  @After
  public void tearDown() {
    repo.deleteAll();
  }

    /*
    @Test
    public void testPagingSorting() throws Exception {
        assertThat(getAll("name", 0, 1, null)).isEqualTo(Arrays.asList(job1));
        assertThat(getAll("name", 1, 1, null)).isEqualTo(Arrays.asList(job2));
        assertThat(getAll("name", 2, 1, null)).isEqualTo(Arrays.asList(job3));
        assertThat(getAll("name", 3, 1, null)).isEmpty();
    }
    */

  @Test
  public void testQueryTagOrName() {
    assertThat(restUtils.queryTagOrName("foo1")).isEqualTo(Arrays.asList(job1, job3));
    assertThat(restUtils.queryTagOrName("foo")).isEqualTo(Arrays.asList(job1, job2, job3));
    assertThat(restUtils.queryTagOrName("koo")).isEmpty();
  }

  @Test
  public void testGetByName() throws Exception {
    assertThat(restUtils.getByName("oo")).isEqualTo(Arrays.asList(job1, job2, job3));
    assertThat(restUtils.getByName("foo2")).isEqualTo(Arrays.asList(job2));
    assertThat(restUtils.getByName("xoo")).isEmpty();
  }

  @Test
  public void testGetByTag() throws Exception {
    assertThat(restUtils.getByTag("tag1")).isEqualTo(Arrays.asList(job1, job2));
    assertThat(restUtils.getByTag("tag2")).isEqualTo(Arrays.asList(job1));
    assertThat(restUtils.getByTag("tag40")).isEqualTo(Arrays.asList(job3));
  }
}