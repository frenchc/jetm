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

/**
 * The EtmPoint represents one measurement.
 * <p/>
 * <p/>
 * Usage example:
 * </p>
 * <pre>
 *  EtmMonitor monitor = ...;
 *  EtmPoint point = monitor.createPoint("name");
 *  try {
 * <p/>
 *   // execute business code
 * <p/>
 *  } finally {
 *    point.collect();
 *  }
 *  </pre>
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public interface EtmPoint {

  /**
   * Marks the current measurement as finished.
   *
   * @throws IllegalStateException Thrown in case the name of the measurement point is null.
   */

  public void collect();

  /**
   * Alters the name of the measurement point.
   * This may be useful for executions where the outcome of an operation
   * may change the scope of the measurement, e.g. an Exception.
   *
   * @param newName The new name of the measurement point.
   */

  public void alterName(String newName);

  /**
   * Returns the name of the measurement point.
   *
   * @return The name.
   */

  public String getName();

  /**
   * Returns the start time of the measurement in the {@link etm.core.timer.ExecutionTimer}
   * dependend precision.
   *
   * @return The start time.
   * @see #getTicks()
   */

  public long getStartTime();

  /**
   * Returns the end time of the measurement in the {@link etm.core.timer.ExecutionTimer}
   * dependend precision.
   *
   * @return The end time.
   * @see #getTicks()
   */

  public long getEndTime();

  /**
   * Returns the number of ticks per millisecond as provided by the used {@link etm.core.timer.ExecutionTimer}.
   *
   * @return The number of ticks.
   */

  public long getTicks();

  /**
   * Returns the parent of this measurement point.
   *
   * @return The parent, may be null.
   */

  public EtmPoint getParent();

  /**
   * Returns the calculated processing time in miliseconds.
   *
   * @return The processing time.
   */
  public double getTransactionTime();


  /**
   * Returns the time the measurement was started.
   *
   * @return The time taken using <code>System.currentTimeMillis</code>
   */

  public long getStartTimeMillis();

  /**
   *
   * Adds arbitrary context information to a given
   * etm point.
   *
   * @param key A key describing the context information.
   * @param value  The context information.
   */

  public void addContextDetail(String key, Object value);


  /**
   *
   * Whether the current point can be collected. Check includes parents too.
   *
   * @return True for collectable events, otherwise false. 
   * @since 1.2.4
   */
  public boolean isCollectable();
}
