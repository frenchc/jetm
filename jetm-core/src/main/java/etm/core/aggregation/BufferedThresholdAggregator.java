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
import etm.core.monitor.event.AggregationFinishedEvent;
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
 * Please note that this aggregator may have a direct impact on executing threads since
 * the thread that reaches the threshold is used to aggregate the results. If you want to minimize this
 * effect use an interval based buffering aggregator {@link BufferedTimedAggregator}.
 * </p>
 *
 * @author void.fm
 * @version $Revision$
 */
public class BufferedThresholdAggregator implements Aggregator {
  private static final String DESCRIPTION = "A buffering aggregator with a threshold of ";
  private static final int DEFAULT_SIZE = 10000;
  private static final int MIN_THRESHOLD = 1000;

  protected final Aggregator delegate;
  protected int threshold = DEFAULT_SIZE;

  protected BoundedBuffer buffer;
  protected EtmMonitorContext context;

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
   * @throws IllegalArgumentException Thrown for threshold sizes &lt; 1000.
   */

  public void setThreshold(int aThreshold) {
    if (aThreshold < MIN_THRESHOLD) {
      throw new IllegalArgumentException("Thresholds may not be lower than " + threshold + ".");
    }

    threshold = aThreshold;
  }

  public void add(EtmPoint point) {
    buffer.add(point);
  }

  public void flush() {
    buffer.flush();
  }

  public void reset() {
    synchronized (delegate) {
      delegate.reset();
    }
  }


  public void reset(String symbolicName) {
    synchronized (delegate) {
      delegate.reset(symbolicName);
    }
  }

  public void render(MeasurementRenderer renderer) {
    flush();
    delegate.render(renderer);
  }

  public AggregatorMetaData getMetaData() {
    return new AggregatorMetaData(BufferedThresholdAggregator.class, DESCRIPTION + threshold, 
    							  true, delegate.getMetaData());
  }


  public void init(EtmMonitorContext ctx) {
    context = ctx;
    delegate.init(ctx);
  }

  public void start() {
    delegate.start();
    buffer = new BoundedBuffer(threshold);
  }

  public void stop() {
    flush();
    delegate.stop();
  }

  class BoundedBuffer {
    private EtmPoint[] buffer;
    private int currentPos = 0;

    public BoundedBuffer(int size) {
      buffer = new EtmPoint[size];
    }

    public void add(EtmPoint point) {
      int length;
      EtmPoint[] current;

      synchronized (this) {
        buffer[currentPos] = point;
        currentPos++;

        if (currentPos < buffer.length) {
          return;
        }

        length = currentPos;
        current = buffer;
        buffer = new EtmPoint[current.length];
        currentPos = 0;
      }

      doFlush(current, length);
    }

    public void flush() {
      int length;
      EtmPoint[] current;

      synchronized (this) {
        length = currentPos;
        current = buffer;
        buffer = new EtmPoint[current.length];
        currentPos = 0;
      }

      doFlush(current, length);
    }

    private void doFlush(EtmPoint[] aCurrent, int aLength) {
      synchronized (delegate) {
        for (int i = 0; i < aLength; i++) {
          delegate.add(aCurrent[i]);
        }
        context.fireEvent(new AggregationFinishedEvent(this));
      }
    }

  }
}
