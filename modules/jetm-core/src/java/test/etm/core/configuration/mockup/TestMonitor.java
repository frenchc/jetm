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
package test.etm.core.configuration.mockup;

import etm.core.aggregation.Aggregator;
import etm.core.aggregation.FlatAggregator;
import etm.core.monitor.EtmMonitorSupport;
import etm.core.monitor.MeasurementPoint;
import etm.core.renderer.MeasurementRenderer;
import etm.core.timer.ExecutionTimer;

import java.util.List;

/**
 *
 * Simple test monitor used for xml config test.
 *
 * @version $Revision$
 * @author void.fm
 *
 */
public class TestMonitor extends EtmMonitorSupport {

  private ExecutionTimer executionTimer;
  private Aggregator aggregator;

  public TestMonitor(ExecutionTimer aExecutionTimer, Aggregator aAggregator) {
    super("testMonitor", aExecutionTimer, aAggregator);
    executionTimer = aExecutionTimer;
    aggregator = aAggregator;
  }

  public Aggregator getAggregator() {
    return aggregator;
  }

  public ExecutionTimer getExecutionTimer() {
    return executionTimer;
  }

  public List getPlugins() {
    return plugins;
  }

  public void render(MeasurementRenderer renderer) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void reset() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isStarted() {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  protected void doVisitPreMeasurement(MeasurementPoint aMeasurementPoint) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  protected void doVisitPostCollect(MeasurementPoint aPoint) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  protected Aggregator getDefaultAggregator() {
    return new FlatAggregator();
  }
}
