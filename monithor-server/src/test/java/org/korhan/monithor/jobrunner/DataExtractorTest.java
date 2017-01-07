package org.korhan.monithor.jobrunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DataExtractorTest {

  private DataExtractor extractor;

  @Before
  public void setup() throws IOException {
    extractor = new DataExtractor();
  }

  @Test
  public void testExtractVersionDefault() {
    assertThat(extractor.extractVersion(null, "<p>1701.0.13</p> ")).isEqualTo("1701.0.13");
    assertThat(extractor.extractVersion(null, " 1701.0.13 ")).isEqualTo("1701.0.13");
    assertThat(extractor.extractVersion(null, " 1.0.0 ")).isEqualTo("1.0.0");
    assertThat(extractor.extractVersion(null, "1212asd1<td>1.0.11-SNAPSHOT</td></tr><tr><td> applicationVersion=1701.0.14-SNAPSHOT")).isEqualTo("1.0.11-SNAPSHOT");
    assertThat(extractor.extractVersion(null, "<p> 1701.10.13-SNAPSHOT </p>abc")).isEqualTo("1701.10.13-SNAPSHOT");
  }

  @Test
  public void testExtractVersionGivenPattern() {
    assertThat(extractor.extractVersion(".*(\\d{4}).*", "<p>1701</p>1 a")).isEqualTo("1701");
    assertThat(extractor.extractVersion(".*Version = (\\d+).*", "<p>Version = 1701</p>1 a")).isEqualTo("1701");
  }

  @Test
  public void testExtractBuildTimestampDefault() {
    assertThat(extractor.extractBuildTimestamp(null, "<strong>build.timestamp</strong></td><td>2017-01-06 00:37:45</td>")).isEqualTo("2017-01-06 00:37:45");
    assertThat(extractor.extractBuildTimestamp(null, " 2017-01-06 00:37:45 2017-01-06 00:00:00 ")).isEqualTo("2017-01-06 00:37:45");
  }

  @Test
  public void testExtractBuildTimestampGivenPattern() {
    assertThat(extractor.extractBuildTimestamp(".*(\\d{4}).*", " 2017-01-06 00:37:45 ")).isEqualTo("2017");
  }
}
