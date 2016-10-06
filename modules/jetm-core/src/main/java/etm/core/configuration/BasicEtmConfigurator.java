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

package etm.core.configuration;

import etm.core.aggregation.Aggregator;
import etm.core.monitor.FlatMonitor;
import etm.core.monitor.NestedMonitor;
import etm.core.timer.ExecutionTimer;

/**
 * BasicEtmConfigurator configuration implementation for
 * programmatic monitor configuration.
 * <p/>
 * For custom configuration see {@link XmlEtmConfigurator}.
 * <p/>
 * Be aware that you need to start and stop the EtmMonitor before
 * using it. Example:
 * <pre>
 *  BasicEtmConfigurator.configure();
 *
 *  EtmMonitor etmMonitor = EtmManager.getEtmMonitor();
 *  etmMonitor.start();
 *  ...
 * </pre>
 *
 * @author void.fm
 * @version $Revision$
 */
public class BasicEtmConfigurator {

  private BasicEtmConfigurator() {
  }

  /**
   *
   * Configures {@link EtmManager} to use a {@link FlatMonitor}
   * with default settings. Same as <code>configure(false)</code>.
   *
   */
  public static void configure() {
    configure(false, null, null);
  }

  /**
   *
   * Configures {@link EtmManager} to use a {@link FlatMonitor}
   * or {@link NestedMonitor} depending on the given parameter
   * with default settings.
   *
   * @param nested True creates an NestedMonitor, false a FlatMonitor.
   */

  public static void configure(boolean nested) {
    configure(nested, null, null);
  }

  /**
   *
   * Configures {@link EtmManager} to use a {@link FlatMonitor}
   * or {@link NestedMonitor} depending on the given parameter
   * with default timer and given aggregator chain.
   *
   * @param nested True creates an NestedMonitor, false a FlatMonitor.
   * @param aggregator The aggregator chain to be used.
   */
  public static void configure(boolean nested, Aggregator aggregator) {
    configure(nested, null, aggregator);
  }

  /**
   *
   * Configures {@link EtmManager} to use a {@link FlatMonitor}
   * or {@link NestedMonitor} depending on the given parameter
   * with default aggregator settings and given time.
   *
   * @param nested True creates an NestedMonitor, false a FlatMonitor.
   * @param timer The ExecutionTimer to be used.
   */

  public static void configure(boolean nested, ExecutionTimer timer) {
    configure(nested, timer, null);
  }

  /**
   *
   * Configures {@link EtmManager} to use a {@link FlatMonitor}
   * or {@link NestedMonitor} depending on the given parameter
   * with the given ExecutionTimer and Aggregator chain..
   *
   * @param nested True creates an NestedMonitor, false a FlatMonitor.
   * @param aggregator The aggregator chain to be used.
   * @param timer The ExecutionTimer to be used.
   */

  public static void configure(boolean nested, ExecutionTimer timer, Aggregator aggregator) {
    if (nested) {
      EtmManager.configure(new NestedMonitor(timer, aggregator));
    } else {
      EtmManager.configure(new FlatMonitor(timer, aggregator));
    }
  }

}
