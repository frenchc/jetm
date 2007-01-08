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
import etm.core.monitor.MeasurementPoint;
import etm.core.renderer.MeasurementRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * The BufferedTimedAggregator buffers measurement results
 * for a certain period of time. The default interval is 5000 miliseconds.
 *
 * @author void.fm
 * @version $Revision$
 */
public class BufferedTimedAggregator implements Aggregator {
  private static final String DESCRIPTION_PREFIX = "A time based buffering aggregator with a flush interval of ";
  private static final String DESCRIPTION_POSTFIX = " ms.";

  private static final long DEFAULT_AGGREGATION_INTERVAL = 5000L;
  private static final int DEFAULT_BUFFER_SIZE = 1000;
  private static final int MIN_AGGREGATION_INTERVAL = 10;

  // the lock for our background thread
  protected final Object lock = new Object();

  protected final Aggregator delegate;

  private TimerThread timerThread;
  private boolean started = false;
  private List buffer;

  private long sleepInterval;


  /**
   * Creates a BufferedTimedAggregator with default
   * sleep interval (5000ms).
   *
   * @param aAggregator The nested aggregator.
   */
  public BufferedTimedAggregator(Aggregator aAggregator) {
    delegate = aAggregator;
    setAggregationInterval(BufferedTimedAggregator.DEFAULT_AGGREGATION_INTERVAL);
    buffer = new ArrayList(BufferedTimedAggregator.DEFAULT_BUFFER_SIZE);

  }

  public void add(MeasurementPoint point) {
    // prevent memory leaks by collecting measurement points
    // for non started aggregators.
    if (!started) {
      return;
    }

    synchronized (lock) {
      buffer.add(point);
    }
  }


  public void flush() {
    List collectedList;

    synchronized (lock) {
      collectedList = buffer;
      buffer = new ArrayList(collectedList.size() * 3 / 2);
    }

    synchronized (delegate) {
      for (int i = 0; i < collectedList.size(); i++) {
        delegate.add((MeasurementPoint) collectedList.get(i));
      }
    }
  }

  public void reset() {
    synchronized (delegate) {
      delegate.reset();
    }
  }


  public void reset(String measurementPoint) {
    synchronized (delegate) {
      delegate.reset(measurementPoint);
    }
  }

  public void render(MeasurementRenderer renderer) {
    synchronized (delegate) {
      delegate.render(renderer);
    }
  }


  public void init(EtmMonitorContext ctx) {
    // todo use timer task
    delegate.init(ctx);
  }

  /**
   * Starts the internal aggregation timer.
   */
  public void start() {
    delegate.start();

    if (timerThread != null) {
      return;
    }
    timerThread = new TimerThread();
    timerThread.setDaemon(true);
    timerThread.start();

    started = true;
  }

  /**
   * Stops the internal aggregation timer.
   */
  public void stop() {
    started = false;
    timerThread.shouldStop();
    timerThread.interrupt();
    timerThread = null;

    // lets flush all we have.
    flush();

    delegate.stop();
  }

  public AggregatorMetaData getMetaData() {
    return new AggregatorMetaData(BufferedTimedAggregator.class,
      BufferedTimedAggregator.DESCRIPTION_PREFIX + sleepInterval + etm.core.aggregation.BufferedTimedAggregator.DESCRIPTION_POSTFIX,
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
      throw new IllegalArgumentException("Aggregation intervals lower than 10 miliseconds not supported.");
    }

    sleepInterval = aAggregationInterval;
  }

  /**
   * Aggregation thread.
   */
  class TimerThread extends Thread {
    private boolean shouldRun = true;

    public TimerThread() {
      super("JETM BufferedTimedAggregator");
    }

    public void run() {
      while (shouldRun) {
        try {
          Thread.sleep(sleepInterval);
          flush();
        } catch (InterruptedException e) {
          shouldRun = false;
        }
      }
    }

    public void shouldStop() {
      shouldRun = false;
    }
  }

}
