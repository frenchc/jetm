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
 * Represents parsed performance result.
 * 
 * @version $Revision$
 * @author void.fm
 * @since 1.2.0
 */
class OfflineExecution implements EtmPoint {
  private String name;
  private double transactionTime;
  private long startTime;
  private long endTime;

  public OfflineExecution(String aName, String aParent, long aRecordingTime, double aTransactionTime) {
    name = aName;
    startTime = aRecordingTime;
    transactionTime = aTransactionTime;

    endTime = (long) (aTransactionTime * 1000);
  }

  public String getName() {
    return name;
  }

  public double getTransactionTime() {
    return transactionTime;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public long getTicks() {
    return 1000;
  }

  public long getStartTimeMillis() {
    return startTime;
  }

  public EtmPoint getParent() {
    throw new UnsupportedOperationException();
  }

  public void collect() {
    throw new UnsupportedOperationException();
  }

  public void alterName(String newName) {
    throw new UnsupportedOperationException();
  }

}
