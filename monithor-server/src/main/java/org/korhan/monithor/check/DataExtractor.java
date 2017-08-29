package org.korhan.monithor.check;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DataExtractor {

  private final static Pattern VERSION_1_PATTERN = Pattern.compile(".*?(\\d+\\.\\d+\\.\\d+(-SNAPSHOT)?).*");
  private final static Pattern VERSION_2_PATTERN = Pattern.compile(".*?version</strong></td><td>(\\d+(-SNAPSHOT)?)</td>.*");
  private final static Pattern BUILD_TIMESTAMP_PATTERN = Pattern.compile(".*?(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}).*");

  public String extractVersion(String patternOverwrite, String text) {
    String version = extractVersion(getPattern(VERSION_2_PATTERN, patternOverwrite), text);
    if (version == null) {
      version = extractVersion(getPattern(VERSION_1_PATTERN, patternOverwrite), text);
    }
    return version;
  }

  public String extractBuildTimestamp(String patternOverwrite, String text) {
    return extractVersion(getPattern(BUILD_TIMESTAMP_PATTERN, patternOverwrite), text);
  }

  private Pattern getPattern(Pattern pattern, String patternOverwrite) {
    if (patternOverwrite != null) {
      return Pattern.compile(patternOverwrite);
    }
    return pattern;
  }

  private String extractVersion(Pattern pattern, String text) {
    Matcher matcher = pattern.matcher(text);
    if (matcher.matches()) {
      return matcher.group(1);
    }
    return null;
  }
}
