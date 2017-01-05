package org.korhan.monithor.jobrunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class VersionExtractorTest {

  private VersionExtractor extractor;

  @Before
  public void setup() throws IOException {
    extractor = new VersionExtractor();
  }

  @Test
  public void testExtractVersionDefault() {
    assertThat(extractor.extract(null, "<p>1701.0.13</p> ")).isEqualTo("1701.0.13");
    assertThat(extractor.extract(null, " 1701.0.13 ")).isEqualTo("1701.0.13");
    assertThat(extractor.extract(null, " 1.0.0 ")).isEqualTo("1.0.0");
    assertThat(extractor.extract(null, "1212asd1<td>1.0.11-SNAPSHOT</td></tr><tr><td> applicationVersion=1701.0.14-SNAPSHOT")).isEqualTo("1.0.11-SNAPSHOT");
    assertThat(extractor.extract(null, "<p> 1701.10.13-SNAPSHOT </p>abc")).isEqualTo("1701.10.13-SNAPSHOT");
  }

  @Test
  public void testExtractVersionGivenPattern() {
    assertThat(extractor.extract(".*(\\d{4}).*", "<p>1701</p>1 a")).isEqualTo("1701");
    assertThat(extractor.extract(".*Version = (\\d+).*", "<p>Version = 1701</p>1 a")).isEqualTo("1701");
  }
}
