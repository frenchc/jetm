/*
 *
 * Copyright (c) 2004, 2005, 2006 void.fm
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

package etm.core.metadata;

import java.io.Serializable;
import java.util.Date;

/**
 * Etm Monitor metadata describe the configuration of an EtmMonitor
 * instance.
 *
 * @author void.fm
 * @version $Revision$
 */

public class EtmMonitorMetaData implements Serializable {
  private Class monitorClazz;
  private String monitorDescription;
  private TimerMetaData timerMetaData;
  private AggregatorMetaData aggregatorMetaData;
  private Date startTime;
  private Date lastResetTime;

  public EtmMonitorMetaData(Class aMonitorClazz, String aMonitorDescription, Date aStartTime, Date aLastReset,
                            AggregatorMetaData aAggregatorMetaData, TimerMetaData aTimerMetaData) {
    monitorClazz = aMonitorClazz;
    monitorDescription = aMonitorDescription;
    timerMetaData = aTimerMetaData;
    aggregatorMetaData = aAggregatorMetaData;
    startTime = aStartTime;
    lastResetTime = aLastReset;
  }

  /**
   * Returns the monitor implementation class.
   *
   * @return The class.
   */

  public Class getImplementationClass() {
    return monitorClazz;
  }

  /**
   * Returns a short description about the monitor.
   *
   * @return A short description.
   */

  public String getDescription() {
    return monitorDescription;
  }

  /**
   * Returns the date the monitor was created.
   *
   * @return The start time.
   */

  public Date getStartTime() {
    return startTime;
  }

  /**
   * Returns the last reset time.
   *
   * @return Returns the last rest time.
   */
  public Date getLastResetTime() {
    return lastResetTime;
  }

  /**
   * Returns meta information about the used timer implementation.
   *
   * @return The timer meta data.
   */
  public TimerMetaData getTimerMetaData() {
    return timerMetaData;
  }

  /**
   * Returns meta information about the used aggregator chain.
   *
   * @return The aggregator chain meta data.
   */

  public AggregatorMetaData getAggregatorMetaData() {
    return aggregatorMetaData;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer("Monitor ");
    buffer.append(monitorClazz);
    buffer.append(" (");
    buffer.append(monitorDescription);
    buffer.append(")");
    buffer.append(" start time ");
    buffer.append(startTime);
    buffer.append(System.getProperty("line.separator"));
    buffer.append("Aggregator Chain [");
    buffer.append(aggregatorMetaData.toString());
    buffer.append("]");
    buffer.append(System.getProperty("line.separator"));
    buffer.append(timerMetaData.toString());
    return buffer.toString();
  }

}
