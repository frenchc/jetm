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
package etm.contrib.aggregation.log;

import etm.contrib.aggregation.filter.RegexAggregationFilter;
import etm.core.aggregation.AggregationFilter;
import etm.core.aggregation.Aggregator;
import etm.core.monitor.EtmMonitorContext;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.MeasurementRenderer;

/**
 * Sometimes it is important to have access to raw measurement results. This
 * base class wrap an existing aggregator and dumps all EtmPoints to a
 * certain logging implementation using a common log format. Before dumping
 * the measurement poin the nested aggregator {@link Aggregator#add} method is
 * called.
 * <p/>
 * Logger implementations will use the default logger name {@link #DEFAULT_LOG_NAME}
 * unless this name was altered using {@link #setLogName(String)}.
 * <p/>
 * A EtmPoint will logged using the {@link DefaultOutputFormatter}. You may override
 * the default implementation by using {@link #setFormatter}.
 * <p/>
 * Due to the direct performance impact this aggregator
 * should be used in conjunction with a time based buffered aggregator,
 * such as {@link etm.core.aggregation.BufferedTimedAggregator}. Therefore a logging aggregator
 * chain should look like this: <code>BufferedTimedAggregator -> Implementation of
 * AbstractLogAggregator -> (Flat/Nested)Aggregator</code>. Be aware that
 * log timestamps and measurement timestamps may be out of synch to due buffering.
 *
 * @author void.fm
 * @version $Revision$
 */
public abstract class AbstractLogAggregator implements Aggregator {

  protected static final String DEFAULT_LOG_NAME = "etm-raw-data";

  protected Aggregator delegate;

  protected String logName = DEFAULT_LOG_NAME;
  protected LogOutputFormatter formatter;

  protected AggregationFilter filter;

  protected AbstractLogAggregator(Aggregator aAggregator) {
    delegate = aAggregator;
  }


  /**
   * Overrides the default logger name. Make sure to call this method
   * before starting the EtmMonitor, otherwhise changes will be unaffected.
   *
   * @param aLogName The new name of the logger.
   */
  public void setLogName(String aLogName) {
    logName = aLogName;
  }

  /**
   * Overrides the default log output formatter.  Make sure to call this method
   * before starting the EtmMonitor, otherwhise changes will be unaffected.
   *
   * @param aFormatter A new formatter.
   */
  public void setFormatter(LogOutputFormatter aFormatter) {
    formatter = aFormatter;
  }

  public void setFormatterClass(Class aFormatterClazz) {
    try {
      formatter = (LogOutputFormatter) aFormatterClazz.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }



  /**
   * Adds a filter for symbolic EtmPoint names that
   * should be logged. Uses {@link java.util.regex.Pattern}
   * for pattern matching. Multiple pattern may be supplied
   * separated by a ";". Requires JDK 1.4 or higher.
   *
   * @param matchingPattern One or more pattern, separated by ;
   * @see etm.contrib.aggregation.filter.RegexAggregationFilter
   */
  public void setFilterPattern(String matchingPattern) {
    filter = new RegexAggregationFilter(matchingPattern);
  }

  public void add(EtmPoint point) {
    delegate.add(point);
    if (filter == null || filter.matches(point)) {
      logMeasurement(point);
    }
  }

  public void flush() {
    delegate.flush();
  }

  public void reset() {
    delegate.reset();
  }

  public void reset(String symbolicName) {
    delegate.reset(symbolicName);
  }

  public void render(MeasurementRenderer renderer) {
    delegate.render(renderer);
  }


  public void init(EtmMonitorContext ctx) {
    delegate.init(ctx);
  }

  public void start() {
    if (formatter == null) {
      formatter = new DefaultOutputFormatter();
    }
    delegate.start();
  }

  public void stop() {
    delegate.stop();
  }

  /**
   * Logs a raw measurement result.
   *
   * @param aPoint The point to be logged.
   */
  protected abstract void logMeasurement(EtmPoint aPoint);

}
