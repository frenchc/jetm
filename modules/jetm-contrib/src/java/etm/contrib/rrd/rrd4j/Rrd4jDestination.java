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

package etm.contrib.rrd.rrd4j;

import etm.contrib.aggregation.filter.RegexEtmFilter;
import etm.contrib.rrd.core.RrdDestination;
import etm.contrib.rrd.core.RrdExecutionWriter;
import etm.core.aggregation.EtmFilter;
import etm.core.monitor.EtmException;
import etm.core.monitor.EtmPoint;
import org.rrd4j.core.RrdDb;

import java.io.File;
import java.io.IOException;

/**
 *
 * Destination for Rrd4j files.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class Rrd4jDestination implements RrdDestination {
  private String pattern;
  private File rrdFilePath;

  private RrdDb rrdDb;
  private EtmFilter filter;
  private RrdExecutionWriter writer;

  public Rrd4jDestination(String aPattern, File aRrdFilePath) {
    pattern = aPattern;
    rrdFilePath = aRrdFilePath;
  }

  public void start() {
    if ("*".equals(pattern)) {
      filter = new EtmFilter() {
        public boolean matches(EtmPoint aEtmPoint) {
          return true;
        }
      };
    } else {
      filter = new RegexEtmFilter(pattern);
    }
    try {
      rrdDb = new RrdDb(rrdFilePath.getAbsolutePath());
    } catch (IOException e) {
      throw new EtmException(e);
    }

    writer = new Rrd4jAggregationWriter(rrdDb);
    writer.onBegin();
  }

  public void stop() {
    writer.onFinish();
    try {
      if (rrdDb != null) {
        rrdDb.close();
      }
    } catch (IOException e) {
      throw new EtmException(e);
    }
  }

  public boolean matches(EtmPoint point) {
    return filter.matches(point);
  }

  public void write(EtmPoint point) {
    writer.onNextMeasurement(point);
  }

  public String toString() {
    return "Rrd4jDestination [" + rrdFilePath.getAbsolutePath() + "|" + pattern + "]";
  }
}

