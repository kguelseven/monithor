package org.korhan.monithor.check;

import org.korhan.monithor.data.model.Job;
import org.korhan.monithor.data.model.JobResult;

public interface Checker {

  public JobResult check(Job job);

}
