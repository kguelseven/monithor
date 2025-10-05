package org.korhan.monithor.check;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.korhan.monithor.check.DataExtractor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class DataExtractorTest {

  private DataExtractor extractor;

  @BeforeEach
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

    assertThat(extractor.extractVersion(null, "foo<tr><td><strong>version</strong></td><td>2</td></tr><tr><td>")).isEqualTo("2");
    assertThat(extractor.extractVersion(null, " foo <tr><td><strong>version</strong></td><td>2-SNAPSHOT</td></tr><tr><td>")).isEqualTo("2-SNAPSHOT");
    assertThat(extractor.extractVersion(null, "foo <tr><td><strong>version</strong></td><td>1.2.3-SNAPSHOT</td></tr><tr><td>")).isEqualTo("1.2.3-SNAPSHOT");
  }

  @Test
  public void testExtractVersionFallback() {
    assertThat(extractor.extractVersion(null, "version</strongX></td><td>1-SNAPSHOT</td></tr><tr><td>")).isNull();
    assertThat(extractor.extractVersion(null, "foo<td><p>1701.10.13-SNAPSHOT</p>abc")).isEqualTo("1701.10.13-SNAPSHOT");
    assertThat(extractor.extractVersion(null, "foo<td><p>1701.10.13-SNAPSHOT</p>abc foo<tr><td><strong>version</strong></td><td>1-SNAPSHOT</td></tr><tr><td>")).isEqualTo("1-SNAPSHOT");
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
