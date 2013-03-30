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

package test.etm.core.aggregation;

import etm.core.aggregation.BufferedTimedAggregator;
import etm.core.aggregation.RootAggregator;
import etm.core.configuration.EtmMonitorFactory;
import etm.core.monitor.FlatMonitor;
import etm.core.timer.DefaultTimer;

/**
 * Test to check possible concurrency issues during aggregation in flat monitors.
 * Uses BufferedTimedAggregator for buffering.
 *
 * @author void.fm
 * @version $Revision$
 */
public class ConcurrentFlatTimedAggregationTest extends CommonConcurrentFlatAggregationTests {


  protected void tearDown() throws Exception {
    monitor.stop();
    monitor.reset();
    monitor = null;
    super.tearDown();
  }


  protected void setUp() throws Exception {
    super.setUp();
    monitor = new FlatMonitor(EtmMonitorFactory.bestAvailableTimer(), new BufferedTimedAggregator(new RootAggregator()));
    monitor.start();
  }


}
