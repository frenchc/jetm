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

/**
 * An EtmMonitor uses a instance of Aggregator to collect and aggregate
 * measurement results. In order to provide multiple aggregation and
 * collection features at the same time aggregator instance may wrap
 * other aggregators.
 * <p/>
 * Usually there is no need to synchronize shared ressources within a aggregator
 * instance unless the aggregator uses internal threads which also access the
 * data structures used within the aggregator methods.
 * <p/>
 * See {@link etm.core.monitor.EtmMonitor} for further synchronization details.
 * <p/>
 * Custom Aggregator implementations need to provide an empty default constructor
 * or a constructor taking an Aggregator instance as argument. Be aware that
 * the last aggregator in an aggregator chains needs to have an default empty
 * constructor, every other aggregator needs to provide an constructor taking the
 * aggregator argument.
 *
 * @author void.fm
 * @version $Revision$
 */

public interface Aggregator {

  /**
   * Adds a new measurement point to the aggregator.
   *
   * @param point A new collected measurement point.
   */
  public void add(MeasurementPoint point);

  /**
   * Flushes the current aggregation details.
   * Buffering Aggragetors should clean up their process buffer.
   */

  public void flush();

  /**
   * Resets the internal aggregator state. Persistent
   * states usually aren't affected.
   */

  public void reset();

  /**
   * Resets the internal aggregator state for a measurement point.
   * Persistent states usually aren't affected.
   *
   * @param measurementPoint The name of the measurement point to be resetted.
   */

  public void reset(String measurementPoint);

  /**
   * Renders the current state of the aggregator
   * using the provided renderer instance.
   *
   * @param renderer The renderer.
   */

  public void render(MeasurementRenderer renderer);

  /**
   * Returns detailed information about the aggregator chain.
   *
   * @return The AggregatorMetaData.
   */

  public AggregatorMetaData getMetaData();

  /**
   *
   * Lifecycle Method, will be called before {@link #start()}, after initalization of
   * the current EtmMonitor runtime. Be aware that Plugins are instantiated, but not
   * started at this point.
   *
   * @param ctx The current EtmMonitor Context.
   * @since 1.2.0
   */
  public void init(EtmMonitorContext ctx);

  /**
   * Lifecycle method, will be called after all Aggregator instances are initialized
   * by calling {@link #init(etm.core.monitor.EtmMonitorContext)} and  before the
   * EtmMonitor instance will becomes available.
   */
  public void start();

  /**
   * Lifecycle method, will be called after the EtmMonitor instance
   * was shutdown.
   */
  public void stop();
}
