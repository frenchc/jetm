/*
 *
 * Copyright (c) 2004, 2005, 2006, 2007 void.fm
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name void.fm nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package etm.core.monitor;

import etm.core.aggregation.Aggregator;
import etm.core.metadata.AggregatorMetaData;
import etm.core.renderer.MeasurementRenderer;

/**
 * The NullMonitor does nothing and this way may
 * limit the overhead in a production environment.
 *
 * @author void.fm
 * @version $Revision$
 */

public class NullMonitor extends EtmMonitorSupport {
  private static final String DESCRIPTION = "A monitor that does not record executions at all.";

  private boolean notCollectionWarningFlag = false;

  public NullMonitor() {
    super(DESCRIPTION, null, null);
  }

  protected void doVisitPreMeasurement(MeasurementPoint aMeasurementPoint) {
    if (!notCollectionWarningFlag) {
      showWarning();
    }
  }

  protected void doVisitPostCollect(MeasurementPoint aMeasurementPoint) {
  }

  public void render(MeasurementRenderer renderer) {
    if (!notCollectionWarningFlag) {
      showWarning();
    }
  }

  public String toString() {
    return "etm.core.monitor.NullMonitor{ timer=<none>, aggregator=<none> }";
  }

  protected Aggregator getDefaultAggregator() {
    return new NullAggregator();
  }


  private void showWarning() {
    System.err.println("Warning - NullMonitor active. Performance results are discarded.");
    System.err.println("This usually happens if you used EtmManager.getEtmMonitor() to retrieve");
    System.err.println("the current EtmMonitor instance and did not configure the Performance");
    System.err.println("sub system before. For further details see EtmManager documentation at ");
    System.err.println("http://jetm.void.fm/howto/etm_manager_configuration.html");


    notCollectionWarningFlag = true;
  }


  static class NullAggregator implements Aggregator {

    NullAggregator() {
    }

    public void add(MeasurementPoint point) {
    }

    public void flush() {
    }

    public void reset() {
    }


    public void reset(String measurementPoint) {
    }

    public void render(MeasurementRenderer renderer) {
    }

    public AggregatorMetaData getMetaData() {
      return new AggregatorMetaData(NullAggregator.class, "Mock aggregator - discards all performance results.", false);
    }

    public void start() {
    }

    public void stop() {
    }

    public void init(EtmMonitorContext ctx) {

    }
  }

}
