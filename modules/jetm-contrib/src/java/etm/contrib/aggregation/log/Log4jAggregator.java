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

import etm.core.aggregation.Aggregator;
import etm.core.metadata.AggregatorMetaData;
import etm.core.monitor.MeasurementPoint;
import org.apache.log4j.Logger;

/**
 * The Log4jAggregator uses Log4J logging
 * to log raw measurement results. Raw results will be logged at level
 * <code>INFO</code>.
 * <p/>
 * See {@link AbstractLogAggregator} for performance impact and
 * further details/configurations.
 *
 * @author void.fm
 * @version $Revision$
 */

public class Log4jAggregator extends AbstractLogAggregator {

  private static final String DESCRIPTION = "An aggregator that logs raw results using log4j. Log name: ";

  protected Logger log;

  public Log4jAggregator(Aggregator aAggregator) {
    super(aAggregator);
  }

  protected void logMeasurement(MeasurementPoint aPoint) {
    if (log.isInfoEnabled()) {
      log.info(formatter.format(aPoint));
    }
  }

  public AggregatorMetaData getMetaData() {
    return new AggregatorMetaData(Log4jAggregator.class, DESCRIPTION + log.getName(), false, delegate.getMetaData());
  }

  public void start() {
    log = Logger.getLogger(logName);
    super.start();
  }

}
