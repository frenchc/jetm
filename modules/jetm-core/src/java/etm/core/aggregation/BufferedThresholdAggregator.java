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

package etm.core.aggregation;

import etm.core.metadata.AggregatorMetaData;
import etm.core.monitor.EtmMonitorContext;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.MeasurementRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * The BufferedThresholdAggregator wraps an Aggregator
 * instance and prevents processing of every measurement result
 * by buffering them until specified threshold is reached. If this
 * threshold is reached all buffered measurements will be flushed to
 * the underlying aggregator.
 * </p>
 * <p>
 * Please note that this aggregator blocks further collection while processing the buffered EtmPoints.
 * As an alternative you may use an buffering aggregator {@link etm.core.aggregation.BufferedThresholdAggregator}
 * </p>
 *
 * @author void.fm
 * @version $Revision$
 */
public class BufferedThresholdAggregator implements Aggregator {
  private static final String DESCRIPTION = "A buffering aggregator with a threshold of ";
  private static final int DEFAULT_SIZE = 1000;
  private static final int MIN_THRESHOLD = 100;

  protected final Aggregator delegate;
  protected List list;
  protected int threshold = DEFAULT_SIZE;

  /**
   * Creates a new BufferedThresholdAggregator for the given
   * aggregator instance. Uses the default threshold size of 1000 elements.
   *
   * @param aAggregator The underlying aggregator.
   */

  public BufferedThresholdAggregator(Aggregator aAggregator) {
    delegate = aAggregator;
    list = new ArrayList(threshold);
  }

  /**
   * Sets the threshold to the given value.
   *
   * @param aThreshold The threshold.
   * @throws IllegalArgumentException Thrown for threshold sizes &lt; 100.
   */

  public void setThreshold(int aThreshold) {
    if (aThreshold < MIN_THRESHOLD) {
      throw new IllegalArgumentException("Thresholds may not be lower than 100.");
    }

    threshold = aThreshold;
  }

  public void add(EtmPoint point) {
    list.add(point);
    if (list.size() > threshold) {
      flush();
    }
  }

  public void flush() {
    List collectedList = list;
    list = new ArrayList(threshold);

    for (int i = 0; i < collectedList.size(); i++) {
      delegate.add((EtmPoint) collectedList.get(i));
    }
  }

  public void reset() {
    delegate.reset();
    list.clear();
  }


  public void reset(String symbolicName) {
    delegate.reset(symbolicName);
  }

  public void render(MeasurementRenderer renderer) {
    delegate.render(renderer);
  }

  public AggregatorMetaData getMetaData() {
    return new AggregatorMetaData(BufferedThresholdAggregator.class, DESCRIPTION + threshold, true, delegate.getMetaData());
  }


  public void init(EtmMonitorContext ctx) {
    // don't do anything local, just delegate
    delegate.init(ctx);
  }

  public void start() {
    delegate.start();
  }

  public void stop() {
    delegate.stop();
  }
}
