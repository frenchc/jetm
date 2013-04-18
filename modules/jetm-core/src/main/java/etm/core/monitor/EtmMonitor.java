/*
 *
 * Copyright (c) void.fm
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

package etm.core.monitor;

import etm.core.metadata.EtmMonitorMetaData;
import etm.core.plugin.EtmPlugin;
import etm.core.renderer.MeasurementRenderer;

import java.util.List;

/**
 * <p/>
 * An EtmMonitor is responsible for collecting and aggregating
 * Measurements Point information. Currently JETM provides three
 * different EtmMonitor types: The {@link FlatMonitor} for flat
 * collection, the {@link NestedMonitor} for nested collection
 * and the {@link NullMonitor} for no collection at all.
 * See {@link etm.core.monitor.EtmPoint} for EtmMonitor usage.
 * </p>
 * <p/>
 * EtmMonitor implementations have to provide at least one of the following constructors
 * <ul>
 * <li>Default Empty Constructor</li>
 * <li>Constructor taking a ExecutionTimer as argument</li>
 * <li>Constructor taking a Aggregator as argument</li>
 * <li>Constructor taking a ExecutionTimer and Aggrgator as argument</li>
 * </ul>
 * </p>
 *
 * @author void.fm
 * @version $Revision$
 * @see etm.core.monitor.EtmPoint
 */

public interface EtmMonitor {

  /**
   *
   * Creates a new EtmPoint with the given name. The name may be
   * null until EtmMonitor#collect is called.
   *
   * @param symbolicName The symbolic name or null. Ensure to call {@link EtmPoint#alterName(String)}
   *        before collection if symbolic name was null.
   * @return A new EtmMpoint
   * @since 1.2.0
   */
  public EtmPoint createPoint(String symbolicName);

  /**
   * Aggregates the current measurement details.
   */

  public void aggregate();

  /**
   * Renders the current measurement results.
   *
   * @param renderer A measurement renderer.
   */

  public void render(MeasurementRenderer renderer);

  /**
   * Resets all available measurements.
   */

  public void reset();


  /**
   * Resets a specific measurement.
   *
   * @param symbolicName The symbolic name of the measurement to reset.
   */

  public void reset(String symbolicName);

  /**
   * Returns metadata for the monitor.
   *
   * @return The monitor meta data.
   */

  public EtmMonitorMetaData getMetaData();


  /**
   * Starts the EtmMonitor.
   */
  public void start();

  /**
   * Stops the EtmMonitor.
   */

  public void stop();

  /**
   * Returns wether the monitor is started or not.
   *
   * @return True if started, otherwhise false.
   */
  public boolean isStarted();

  /**
   * Enables result collection.
   */
  public void enableCollection();

  /**
   * Disables result collection.
   */

  public void disableCollection();

  /**
   * Returns whether the given monitor instance is collecting
   * results right now.
   *
   * @return True for enabled collection, otherwhise false.
   */
  public boolean isCollecting();

  /**
   * Adds a new plugin which is part of the EtmMonitor
   * lifecycle.
   *
   * @param aEtmPlugin The new EtmPlugin.
   */
  public void addPlugin(EtmPlugin aEtmPlugin);

  /**
   *
   * Adds a list of plugins.
   *
   * @param plugins A list of EtmPlugin.
   * @since 1.2.0
   */
  public void setPlugins(List plugins);

}
