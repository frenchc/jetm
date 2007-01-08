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
import etm.core.metadata.AggregatorMetaData;
import etm.core.monitor.EtmMonitorContext;
import etm.core.monitor.MeasurementPoint;
import etm.core.renderer.MeasurementRenderer;

/**
 * Simple test aggregator used for xml config test.
 *
 * @author void.fm
 * @version $Revision$
 */
public class TestAggregator implements Aggregator {

  private Aggregator target;
  private boolean booleanTrue;
  private boolean booleanFalse;
  private long longValue;
  private int intValue;
  private String stringValue;

  public TestAggregator(Aggregator aTarget) {
    target = aTarget;
  }

  public Aggregator getTarget() {
    return target;
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
    return null;
  }

  public void init(EtmMonitorContext ctx) {
  }

  public void start() {
  }

  public void stop() {
  }


  public boolean isBooleanTrue() {
    return booleanTrue;
  }

  public void setBooleanTrue(boolean aBooleanTrue) {
    booleanTrue = aBooleanTrue;
  }

  public boolean isBooleanFalse() {
    return booleanFalse;
  }

  public void setBooleanFalse(boolean aBooleanFalse) {
    booleanFalse = aBooleanFalse;
  }

  public long getLongValue() {
    return longValue;
  }

  public void setLongValue(long aLongValue) {
    longValue = aLongValue;
  }

  public int getIntValue() {
    return intValue;
  }

  public void setIntValue(int aIntValue) {
    intValue = aIntValue;
  }

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String aStringValue) {
    stringValue = aStringValue;
  }
}
