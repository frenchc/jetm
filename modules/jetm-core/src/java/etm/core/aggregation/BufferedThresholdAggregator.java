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

/**
 * <p/>
 * The BufferedThresholdAggregator wraps an Aggregator
 * instance and prevents processing of every measurement result
 * by buffering them until specified threshold is reached. If this
 * threshold is reached all buffered measurements will be flushed to
 * the underlying aggregator.
 * </p>
 * <p/>
 * Please note that this aggregator blocks further collection while processing the buffered EtmPoints.
 * As an alternative you may use an buffering aggregator {@link etm.core.aggregation.BufferedThresholdAggregator}
 * </p>
 *
 * @author void.fm
 * @version $Revision$
 */
public class BufferedThresholdAggregator implements Aggregator {
  private static final String DESCRIPTION = "A buffering aggregator with a threshold of ";
  private static final int DEFAULT_SIZE = 12500;
  private static final int MIN_THRESHOLD = 1000;

  protected final Aggregator delegate;
  protected int threshold = DEFAULT_SIZE;

  protected final Object localBufferLock = new Object();

  protected BoundedCopyOnResetBuffer buffer;

  /**
   * Creates a new BufferedThresholdAggregator for the given
   * aggregator instance. Uses the default threshold size of 1000 elements.
   *
   * @param aAggregator The underlying aggregator.
   */

  public BufferedThresholdAggregator(Aggregator aAggregator) {
    delegate = aAggregator;
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
    synchronized (localBufferLock) {
      if (buffer.add(point)) {
        flush();
      }
    }
  }

  public void flush() {
    EtmPoint[] points;
    int length;

    synchronized (localBufferLock) {
      length = buffer.length();
      points = buffer.reset();
    }

    synchronized (delegate) {
      for (int i = 0; i < length; i++) {
        delegate.add(points[i]);
      }
    }
  }

  public void reset() {
    delegate.reset();
  }


  public void reset(String symbolicName) {
    delegate.reset(symbolicName);
  }

  public void render(MeasurementRenderer renderer) {
    flush();
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
    buffer = new BoundedCopyOnResetBuffer(threshold);
    delegate.start();
  }

  public void stop() {
    delegate.stop();
  }


  class BoundedCopyOnResetBuffer {
    private EtmPoint[] buffer;
    private int currentPos = 0;

    public BoundedCopyOnResetBuffer(int size) {
      buffer = new EtmPoint[size];
    }

    public boolean add(EtmPoint point) {
      buffer[currentPos] = point;
      currentPos++;

      return currentPos == buffer.length;
    }

    public EtmPoint[] reset() {
      EtmPoint[] old = buffer;
      buffer = new EtmPoint[old.length];

      currentPos = 0;
      return old;
    }

    public int length() {
      return currentPos;
    }
  }
}
