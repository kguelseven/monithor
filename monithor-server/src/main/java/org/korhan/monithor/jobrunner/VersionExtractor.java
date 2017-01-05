package org.korhan.monithor.jobrunner;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class VersionExtractor {
  private final static Pattern PATTERN = Pattern.compile(".*?(\\d+\\.\\d+\\.\\d+(-SNAPSHOT)?).*");

  public String extract(String patternOverwrite, String text) {
    Pattern pattern = PATTERN;
    if (patternOverwrite != null) {
      pattern = Pattern.compile(patternOverwrite);
    }
    return extract(pattern, text);
  }

  private String extract(Pattern pattern, String text) {
    Matcher matcher = pattern.matcher(text);
    if (matcher.matches()) {
      return matcher.group(1);
    }
    return null;
  }
}
