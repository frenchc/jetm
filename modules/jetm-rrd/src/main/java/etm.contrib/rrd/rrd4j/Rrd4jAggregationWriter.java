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
package etm.contrib.rrd.rrd4j;

import etm.contrib.rrd.core.AbstractRrdExecutionWriter;
import etm.core.monitor.EtmException;
import etm.core.monitor.EtmPoint;
import etm.core.util.Log;
import etm.core.util.LogAdapter;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.Sample;
import org.rrd4j.core.Util;

import java.io.IOException;
import java.util.Date;

/**
 * A Rrd4j DB Writer writes 
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class Rrd4jAggregationWriter extends AbstractRrdExecutionWriter {

  private static final LogAdapter log = Log.getLog(AbstractRrdExecutionWriter.class);

  private RrdDb db;

  private boolean transactionsEnabled;
  private boolean minEnabled;
  private boolean maxEnabled;
  private boolean averageEnabled;

  /**
   * Creates a new writer that stores
   *
   * @param aDb A writeable RRD DB.
   * @throws IllegalArgumentException If the rrd db definition does not contain all required
   *                                  datasources
   */
  public Rrd4jAggregationWriter(RrdDb aDb) {
    super(extractLastTimestamp(aDb), extractStep(aDb));

    transactionsEnabled = validateDataSource(aDb, "transactions!");
    minEnabled = validateDataSource(aDb, "min");
    maxEnabled = validateDataSource(aDb, "max");
    averageEnabled = validateDataSource(aDb, "average");

    if (!(transactionsEnabled || minEnabled || maxEnabled || averageEnabled)) {
      throw new EtmException("Invalid datasource. " +
        "One of the datasources 'transactions', 'min', 'max' or 'average' should exist in " +
      aDb.getPath() + ".");
    }

    db = aDb;

    log.debug("Using Rrd4j destination " + aDb.getPath() + " starting at " +
      new Date(startInterval * 1000) + " with step " + increment + " seconds.");
  }

  public void onBegin() {

  }


  public void onFinish() {

  }

  protected long calculateTimestamp(EtmPoint measurement) {
    return Util.getTimestamp(new Date(measurement.getStartTimeMillis()));
  }

  protected void flushStatus() {
    if (transactions > 0) {
      try {
        Sample sample = db.createSample(endInterval);
        if (transactionsEnabled) {
          sample.setValue("transactions!", transactions);
        }
        if (minEnabled) {
          sample.setValue("min", min);
        }
        if (maxEnabled) {
          sample.setValue("max", max);
        }
        if (averageEnabled) {
          double value = total / (double) transactions;
          sample.setValue("average", value);
        }

        sample.update();
      } catch (IOException e) {
        throw new EtmException(e);
      }
    }
  }

  protected boolean validateDataSource(RrdDb aDb, String name) {
    try {
      return aDb.getDatasource(name) != null;
    } catch (IOException e) {
      throw new EtmException(e);
    }
  }

  private static long extractStep(RrdDb aDb) {
    try {
      return aDb.getRrdDef().getStep();
    } catch (IOException e) {
      throw new EtmException(e);
    }
  }

  private static long extractLastTimestamp(RrdDb aDb) {
    try {
      return aDb.getLastUpdateTime();
    } catch (IOException e) {
      throw new EtmException(e);
    }
  }

}
