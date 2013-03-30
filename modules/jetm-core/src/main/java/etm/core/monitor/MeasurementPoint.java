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

import java.util.HashMap;
import java.util.Map;


/**
 * The MeasurementPoint represents one measurement.
 * <p/>
 * <p/>
 * Usage example:
 * </p>
 * <pre>
 *  EtmMonitor monitor = ...;
 *  MeasurementPoint point = new MeasurementPoint(monitor, "name");
 *  try {
 * <p/>
 *   // execute business code
 * <p/>
 *  } finally {
 *    point.collect();
 *  }
 *  </pre>
 *
 * @author void.fm
 * @version $Revision$
 * @see EtmMonitor
 * @deprecated Please use {@link etm.core.monitor.EtmMonitor#createPoint(String)} instead. Will be made
 *             package visible with JETM 2.0.0.
 */

public class MeasurementPoint implements EtmPoint {
  private static final long SECOND_MULTIPLIER = 1000L;

  private final EtmMonitorSupport monitor;
  private MeasurementPoint parent = null;

  private String name;
  private long startTime = 0;
  private long endTime = 0;
  private long ticks = 0;
  private long startTimeMillis = 0;
  private Map context;

  /**
   * Creates a new measurement point using the given
   * monitor and name.
   *
   * @param aMonitor The monitor to be associated with.
   * @param aName    The name of this measurement point, may be null at construction time.
   *                 In this case you may need to set the name using {@link #alterName(String)}
   *                 before calling {@link #collect()}.
   */

  public MeasurementPoint(EtmMonitor aMonitor, String aName) {
    // will be removed with JETM 2.0.0
    monitor = (EtmMonitorSupport) aMonitor;
    name = aName;
    startTimeMillis = System.currentTimeMillis();
    monitor.visitPreMeasurement(this);
  }

  public void collect() {
    if (name == null) {
      throw new IllegalStateException("A measurement point may not be collected without a proper name.");
    }

    monitor.visitPostCollect(this);

  }

  public void alterName(String newName) {
    name = newName;
  }


  public String getName() {
    return name;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public long getTicks() {
    return ticks;
  }

  /**
   * Sets a parent measurement point.
   *
   * @param aParent The parent.
   */
  protected void setParent(MeasurementPoint aParent) {
    parent = aParent;
  }

  public EtmPoint getParent() {
    return parent;
  }

  public boolean isCollectable() {
    // We always assume that parent is master.
    // if the root parent is collectable, everything else should be collectable too.
    if (parent != null) {
      return parent.isCollectable();
    }
    
    return endTime > 0l;
  }

  /**
   * Sets the start time of the measurement.
   *
   * @param aStartTime The start time.
   */

  protected void setStartTime(long aStartTime) {
    startTime = aStartTime;
  }

  /**
   * Sets the end time of the measurement.
   *
   * @param aEndTime The end time.
   */

  protected void setEndTime(long aEndTime) {
    endTime = aEndTime;
  }

  /**
   * Sets the number of ticks per millsecond.
   *
   * @param aTicks The number of ticks.
   */

  protected void setTicks(long aTicks) {
    ticks = aTicks;
  }

  public double getTransactionTime() {
    return ((double) ((endTime - startTime) * SECOND_MULTIPLIER)) / (double) ticks;
  }

  public long getStartTimeMillis() {
    return startTimeMillis;
  }

  public void addContextDetail(String key, Object value) {
    if (context == null) {
      context = new HashMap();
    }
    context.put(key, value);
  }

  public Map getContext() {
    return context;
  }

  public String toString() {
    return "MeasurementPoint{" +
      "monitor=" + monitor +
      ", parent=" + parent +
      ", name='" + name + "'" +
      ", startTime=" + startTime +
      ", endTime=" + endTime +
      ", ticks=" + ticks +
      ", contextSize=" + ( context != null? context.size() : 0) +

      "}";
  }
}
