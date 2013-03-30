/*
 *
 * Copyright (c) void.fm
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
import etm.core.timer.ExecutionTimer;

/**
 * The FlatMonitor records all measurement points separately even if they
 * are nested.
 *
 * @author void.fm
 * @version $Revision$
 */
public class FlatMonitor extends EtmMonitorSupport {
  private static final String DESCRIPTION = "A monitor recording all executions separately.";


  public FlatMonitor() {
    this(null, null);
  }

  /**
   * Creates a new flat monitor instance.
   *
   * @param aTimer The timer to use.
   */

  public FlatMonitor(ExecutionTimer aTimer) {
    this(aTimer, null);
  }

  public FlatMonitor(Aggregator aAggregator) {
    this(null, aAggregator);
  }

  public FlatMonitor(ExecutionTimer aTimer, Aggregator aAggregator) {
    super(DESCRIPTION, aTimer, aAggregator);
  }

  protected void doVisitPreMeasurement(MeasurementPoint measurementPoint) {
    // don't do anything
  }

  protected void doVisitPostCollect(MeasurementPoint aPoint) {
    // don't do anything
  }

  public String toString() {
    return "etm.core.monitor.FlatMonitor{ timer=<" + timer + ">, aggregator=<" + aggregator + "> }";
  }
}
