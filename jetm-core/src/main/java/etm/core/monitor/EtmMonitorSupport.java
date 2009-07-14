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

package etm.core.monitor;

import etm.core.aggregation.Aggregator;
import etm.core.aggregation.BufferedThresholdAggregator;
import etm.core.aggregation.RootAggregator;
import etm.core.configuration.EtmMonitorFactory;
import etm.core.metadata.EtmMonitorMetaData;
import etm.core.monitor.event.AggregationStateListener;
import etm.core.monitor.event.AggregationStateLoadedEvent;
import etm.core.monitor.event.CollectionDisabledEvent;
import etm.core.monitor.event.CollectionEnabledEvent;
import etm.core.monitor.event.DefaultEventDispatcher;
import etm.core.monitor.event.EtmMonitorEvent;
import etm.core.monitor.event.EtmMonitorListener;
import etm.core.monitor.event.EventDispatcher;
import etm.core.plugin.EtmPlugin;
import etm.core.renderer.MeasurementRenderer;
import etm.core.timer.ExecutionTimer;
import etm.core.util.Log;
import etm.core.util.LogAdapter;
import etm.core.util.Version;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;


/**
 * Abstract base class for the execution time measurement monitors.
 * An EtmMonitor mandates the following life cycle for measurement
 * points.
 * </p>
 * <ol>
 * <li>
 * Newly created MeasurementPoint instances register
 * themself automatically before the actual measurement process using
 * {@link #visitPreMeasurement(MeasurementPoint)}.
 * </li>
 * <li>
 * Within {@link #visitPreMeasurement} the EtmMonitor sets the start time
 * of the measurement.
 * </li>
 * <li>
 * The calling business code executes.
 * </li>
 * <li>
 * After business code execution the Measurement Point calls
 * {@link #visitPostCollect(MeasurementPoint)}. This call is triggered by
 * {@link MeasurementPoint#collect()}.
 * </li>
 * <li>
 * Within {@link #visitPostCollect} the EtmMonitor sets the end time
 * of the measurement and stores this transaction for further aggregation.
 * </li>
 * </ol>
 *
 * @author void.fm
 * @version $Revision$
 */

public abstract class EtmMonitorSupport implements EtmMonitor, AggregationStateListener {

  private static final LogAdapter LOG = Log.getLog(EtmMonitor.class);

  protected final String description;
  protected final ExecutionTimer timer;
  protected final Aggregator aggregator;

  protected List plugins;
  private Timer scheduler;
  private EventDispatcher dispatcher;

  private Date startTime;
  private Date lastReset;
  private boolean started = false;
  private boolean collecting = true;


  private boolean noStartedErrorMessageFlag = false;

  /**
   * Creates a EtmMonitorSupport instance.
   *
   * @param aDescription The description for this monitor.
   * @param aTimer       The timer to use.
   * @param aAggregator  The aggregator to use.
   */

  protected EtmMonitorSupport(String aDescription, ExecutionTimer aTimer, Aggregator aAggregator) {
    description = aDescription;
    if (aTimer != null) {
      timer = aTimer;
    } else {
      timer = EtmMonitorFactory.bestAvailableTimer();
    }

    if (aAggregator != null) {
      aggregator = aAggregator;
    } else {
      aggregator = getDefaultAggregator();
    }
    startTime = new Date();
    lastReset = startTime;
  }

  public EtmPoint createPoint(String symbolicName) {
    return new MeasurementPoint(this, symbolicName);
  }

  public final void visitPreMeasurement(MeasurementPoint measurementPoint) {
    try {
      if (!collecting) {
        return;
      }

      if (!started) {
        if (!noStartedErrorMessageFlag) {
          showMonitorNotStartedMessage();
        }
        return;
      }

      if (measurementPoint == null) {
        return;
      }

      doVisitPreMeasurement(measurementPoint);

      measurementPoint.setTicks(timer.getTicksPerSecond());
      measurementPoint.setStartTime(timer.getCurrentTime());
      // catch all exceptions here
      // in order to avoid negative side effects for
      // our business logic
    } catch (Exception e) {
      LOG.warn("Caught exception within measurement code. ", e);
    }
  }

  public final void visitPostCollect(MeasurementPoint measurementPoint) {
    if (!collecting || !started) {
      return;
    }

    if (measurementPoint == null) {
      return;
    }

    try {
      measurementPoint.setEndTime(timer.getCurrentTime());

      doVisitPostCollect(measurementPoint);

      aggregator.add(measurementPoint);
      // catch all exceptions here
      // in order to avoid negative side effects for
      // our business logic
    } catch (Exception e) {
      LOG.warn("Caught exception within measurement code.", e);
    }
  }


  public final void aggregate() {
    aggregator.flush();
  }

  public void render(MeasurementRenderer renderer) {
    // we do not lock here since we assume non blocking read
    aggregator.render(renderer);
  }

  public void reset() {
    aggregator.reset();
    lastReset = new Date();
  }

  public void reset(String measurementPoint) {
    aggregator.reset(measurementPoint);
  }

  public final EtmMonitorMetaData getMetaData() {
    List pluginMetaData = getPluginMetaData();
    return new EtmMonitorMetaData(
      getClass(), description, startTime, lastReset,
      aggregator.getMetaData(), timer.getMetaData(),
      pluginMetaData);
  }

  public void start() {
    if (started) {
      collecting = true;
      return;
    }

    scheduler = new Timer(true);

    if (dispatcher == null) {
      dispatcher = new DefaultEventDispatcher();
    }

    dispatcher.register(this);

    // 1. start plugins
    startPlugins();

    // 2. init aggregators
    aggregator.init(new EtmMonitorSupportContext(this, scheduler));

    // 3. start aggregators
    aggregator.start();

    started = true;
    collecting = true;

    LOG.info("JETM " + Version.getVersion() + " started.");
  }

  public void stop() {
    LOG.info("Shutting down JETM.");

    if (!started) {
      collecting = false;
      return;
    }

    collecting = false;
    started = false;

    scheduler.cancel();

    aggregator.stop();
    shutdownPlugins();

    dispatcher.deregister(this);

  }

  public boolean isStarted() {
    return started;
  }

  public void enableCollection() {
    collecting = true;
    dispatcher.fire(new CollectionEnabledEvent(this));
  }

  public void disableCollection() {
    collecting = false;
    dispatcher.fire(new CollectionDisabledEvent(this));
  }

  public boolean isCollecting() {
    return collecting;
  }

  public void addPlugin(EtmPlugin aEtmPlugin) {
    if (plugins == null) {
      plugins = new ArrayList();
    }

    plugins.add(aEtmPlugin);

    if (started) {
      startPlugin(aEtmPlugin);
    }

  }

  public void setPlugins(List newPlugins) {
    if (plugins != null) {
      throw new IllegalStateException("Unable to set a list of plugins after plugins exists.");
    }
    for (int i = 0; i < newPlugins.size(); i++) {
      EtmPlugin plugin = (EtmPlugin) newPlugins.get(i);
      addPlugin(plugin);
    }
  }


  public void onStateLoaded(AggregationStateLoadedEvent event) {
    startTime = event.getState().getStartTime();
    lastReset = event.getState().getLastResetTime();
  }

  /**
   * <p/>
   * Callback method for derived classes.
   * </p>
   * <p/>
   * This method is called immediately after the measurement point was created. Note that
   * neither {@link MeasurementPoint#getTicks()} nor {@link MeasurementPoint#getStartTime()} are set
   * at that point.
   * </p>
   *
   * @param aMeasurementPoint The measurement point just created.
   */
  protected abstract void doVisitPreMeasurement(MeasurementPoint aMeasurementPoint);


  /**
   * <p/>
   * Callback method for derived classes.
   * </p>
   * <p/>
   * This method is called immediately after the measurement point was collected and marked as
   * closed. At that point the all required information are valid for that measurement point.
   * </p>
   *
   * @param aPoint The point to collect.
   */

  protected abstract void doVisitPostCollect(MeasurementPoint aPoint);


  protected Aggregator getDefaultAggregator() {
    return new BufferedThresholdAggregator(new RootAggregator());
  }

  protected void shutdownPlugins() {
    if (plugins != null) {

      for (int i = 0; i < plugins.size(); i++) {
        EtmPlugin etmPlugin = (EtmPlugin) plugins.get(i);
        try {
          if (etmPlugin instanceof EtmMonitorListener) {
            dispatcher.deregister((EtmMonitorListener) etmPlugin);
          }
          etmPlugin.stop();
        } catch (Exception e) {
          LOG.warn("Error while shutting down " + etmPlugin.getPluginMetaData(), e);
        }
      }
    }
  }

  protected void startPlugins() {
    if (plugins != null) {

      for (int i = 0; i < plugins.size(); i++) {
        EtmPlugin etmPlugin = (EtmPlugin) plugins.get(i);
        startPlugin(etmPlugin);
      }
    }
  }

  private void startPlugin(EtmPlugin aEtmPlugin) {
    try {
      aEtmPlugin.init(new EtmMonitorSupportContext(this, scheduler));
      aEtmPlugin.start();
      if (aEtmPlugin instanceof EtmMonitorListener) {
        dispatcher.register((EtmMonitorListener) aEtmPlugin);
      }
    } catch (Exception e) {
      LOG.warn("Error starting plugin " + aEtmPlugin.getPluginMetaData() + ". Keep plugin disabled. ", e);

    }
  }

  private List getPluginMetaData() {
    if (plugins != null) {
      List metaData = new ArrayList(plugins.size());
      for (int i = 0; i < plugins.size(); i++) {
        metaData.add(((EtmPlugin) plugins.get(i)).getPluginMetaData());
      }

      return metaData;
    }

    return null;
  }

  private void showMonitorNotStartedMessage() {
    LOG.warn("Warning - Performance Monitoring currently disabled. " 
    	   + "If you did not start the current EtmMonitor on purpose, " 
    	   + "you may ignore this warning. Otherwhise ensure to call EtmMonitor.start() " 
    	   + "at some point in your application.");
    noStartedErrorMessageFlag = true;
  }

  class EtmMonitorSupportContext implements EtmMonitorContext {
    private EtmMonitor monitor;
    private Timer scheduler;

    public EtmMonitorSupportContext(EtmMonitor aMonitor, Timer aScheduler) {
      monitor = aMonitor;
      scheduler = aScheduler;
    }

    public EtmMonitor getEtmMonitor() {
      return monitor;
    }

    public Timer getScheduler() {
      return scheduler;
    }

    public void fireEvent(EtmMonitorEvent event) {
      dispatcher.fire(event);
    }

  }
}
