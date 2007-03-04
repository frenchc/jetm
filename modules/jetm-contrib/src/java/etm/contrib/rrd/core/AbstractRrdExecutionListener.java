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
package etm.contrib.rrd.core;

import etm.core.monitor.EtmPoint;

/**
 *
 * Base implementation for RrdExecutionListeners.
 *
 * @version $Revision$
 * @author void.fm
 * @since 1.2.0
 */
public abstract class AbstractRrdExecutionListener implements RrdExecutionListener {

  protected long startInterval;
  protected long endInterval;
  protected long increment;


  protected int transactions;
  protected double min;
  protected double max;
  protected double total;

  protected AbstractRrdExecutionListener(long aStartInterval, long aIncrement) {
    startInterval = aStartInterval;
    increment = aIncrement;
    endInterval = startInterval + increment;

    initAggregation();

  }

  public void onNextMeasurement(EtmPoint measurement) {
    long l = calculateTimestamp(measurement);

    if (l > endInterval) {
      if (startInterval == 0) {
        startInterval = l;
        endInterval = startInterval + increment;
      } else {
        flushStatus();
        // proceed to next interval
        startInterval = endInterval;
        endInterval = startInterval + increment;
      }
      initAggregation();
    }

    if (l >= startInterval) {
      transactions++;
      double transactionTime = measurement.getTransactionTime();
      min = transactionTime < min ? transactionTime : min;
      max = transactionTime > max ? transactionTime : max;
      total += transactionTime;
    } // else ingore, must be historical data


  }


  protected void initAggregation() {
    transactions = 0;
    min = Double.MAX_VALUE;
    max = Double.MIN_VALUE;
    total = 0;
  }

  protected abstract long calculateTimestamp(EtmPoint measurement); 

  protected abstract void flushStatus();


}
