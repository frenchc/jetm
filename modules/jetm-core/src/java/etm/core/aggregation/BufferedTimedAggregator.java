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
import etm.core.util.Log;
import etm.core.util.LogAdapter;

import java.util.TimerTask;

/**
 * The BufferedTimedAggregator buffers measurement results
 * for a certain period of time. The default interval is 5000 miliseconds.
 *
 * @author void.fm
 * @version $Revision$
 */
public class BufferedTimedAggregator implements Aggregator {
  private static final LogAdapter log = Log.getLog(BufferedTimedAggregator.class);


  private static final String DESCRIPTION_PREFIX = "A time based buffering aggregator with a flush interval of ";
  private static final String DESCRIPTION_POSTFIX = " ms.";

  private static final long DEFAULT_AGGREGATION_INTERVAL = 5000L;
  private static final int DEFAULT_BUFFER_SIZE = 12500;
  private static final int MIN_AGGREGATION_INTERVAL = 10;

  protected final UnboundedCopyOnResetBuffer buffer = new UnboundedCopyOnResetBuffer(BufferedTimedAggregator.DEFAULT_BUFFER_SIZE);

  protected final Aggregator delegate;

  private boolean started = false;

  private long sleepInterval;
  private EtmMonitorContext ctx;


  /**
   * Creates a BufferedTimedAggregator with default
   * sleep interval (5000ms).
   *
   * @param aAggregator The nested aggregator.
   */
  public BufferedTimedAggregator(Aggregator aAggregator) {
    delegate = aAggregator;
    setAggregationInterval(BufferedTimedAggregator.DEFAULT_AGGREGATION_INTERVAL);
  }

  public void add(EtmPoint point) {
    // prevent memory leaks by collecting EtmPoints
    // for non started aggregators.
    if (!started) {
      return;
    }

    synchronized (buffer) {
      buffer.add(point);
    }
  }


  public void flush() {
    EtmPoint[] points;
    int length;

    synchronized (buffer) {
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

  // lifecycle methods


  /**
   * Initializes current monitor .
   *
   * @param aCtx The runtime context.
   */
  public void init(EtmMonitorContext aCtx) {
    ctx = aCtx;
    delegate.init(aCtx);
  }

  /**
   * Starts the internal aggregation timer.
   */
  public void start() {
    delegate.start();

    // this one is dangerous
    // if we run into VM level problems we might add a potential
    // out of memory too
    // we need to ensure that in those rare cases the OOM does not occur
    ctx.getScheduler().scheduleAtFixedRate(new TimerTask() {
      public void run() {
        try {
          flush();
        } catch (Throwable t) {
          if (t instanceof ThreadDeath) {
            started = false;
            cancel();
            log.warn("Error occured in BufferedTimedAggregator. Disable collection to prevent memory leak.");
            throw (ThreadDeath) t;
          }
          if (t instanceof Error) {
            started = false;
            cancel();
            log.warn("Error occured in BufferedTimedAggregator. Disable collection to prevent memory leak.");
            throw (Error) t;
          }
          log.fatal("Error in aggregation buffer.", t);
        }
      }
    }, sleepInterval, sleepInterval);

    started = true;
  }

  /**
   * Stops the internal aggregation timer.
   */
  public void stop() {
    started = false;

    // lets flush all we have.
    flush();

    // we don't need to stop the time since this one is hopefully
    // stopped by the enclosing EtmMonitor instance.


    delegate.stop();
  }

  public AggregatorMetaData getMetaData() {
    return new AggregatorMetaData(BufferedTimedAggregator.class,
      BufferedTimedAggregator.DESCRIPTION_PREFIX + sleepInterval + BufferedTimedAggregator.DESCRIPTION_POSTFIX,
      true,
      delegate.getMetaData());
  }

  /**
   * Sets a custom sleep interval.
   *
   * @param aAggregationInterval The custom sleep interval in miliseconds.
   * @throws IllegalArgumentException Thrown for sleep interval < 10ms
   */

  public void setAggregationInterval(long aAggregationInterval) {
    if (aAggregationInterval < BufferedTimedAggregator.MIN_AGGREGATION_INTERVAL) {
      throw new IllegalArgumentException("Aggregation intervals lower than " +
        BufferedTimedAggregator.MIN_AGGREGATION_INTERVAL +
        " miliseconds not supported.");
    }

    sleepInterval = aAggregationInterval;
  }


  class UnboundedCopyOnResetBuffer {
    private EtmPoint[] buffer;
    private int currentPos = 0;
    private static final double NEWSIZE_MULTIPLIER = 1.5;

    public UnboundedCopyOnResetBuffer(int size) {
      buffer = new EtmPoint[size];
    }

    public void add(EtmPoint point) {
      buffer[currentPos] = point;
      currentPos++;
      if (currentPos == buffer.length) {
        EtmPoint[] newBuffer = new EtmPoint[(int) (currentPos * NEWSIZE_MULTIPLIER)];
        System.arraycopy(buffer, 0, newBuffer, 0, currentPos);
        buffer = newBuffer;
      }
    }

    public int length() {
      return currentPos;
    }

    public EtmPoint[] reset() {
      EtmPoint[] old = buffer;
      buffer = new EtmPoint[old.length];

      currentPos = 0;
      return old;
    }

  }

}
