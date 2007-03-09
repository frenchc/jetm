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
package etm.contrib.rrd.core;

import etm.core.util.Log;
import etm.core.util.LogAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses arbitrary files written by {@link etm.contrib.aggregation.log.AbstractLogAggregator}. Assumes
 * {@link etm.contrib.aggregation.log.DefaultOutputFormatter} as default format.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class OfflineLogParser {

  private static final LogAdapter log = Log.getLog(OfflineLogParser.class);


  private static final String DEFAULT_SCAN_PATTERN = "^(.*)measurementPoint=<([\\w\\s\\d]*)>, parent=<([\\w\\s]*)>, transactionTime=<(\\d*[,.]\\d*)>, recordingTime=<(\\d*)>";
  private String pattern = DEFAULT_SCAN_PATTERN;

  private List destinations;
  private NumberFormat numberFormat;


  public OfflineLogParser() {
    destinations = new ArrayList();
    // todo?? pattern
    numberFormat = NumberFormat.getInstance();
    numberFormat.setMinimumFractionDigits(3);
    numberFormat.setMaximumFractionDigits(3);
    numberFormat.setGroupingUsed(false);
  }


  public void setPattern(String aPattern) {
    pattern = aPattern;
  }

  public void register(RrdDestination aDestination) {
    destinations.add(aDestination);
  }

  public void parse(File aFile) throws IOException {
    int totalLines = 0;
    int processedLines = 0;
    Pattern regex = Pattern.compile(pattern);

    BufferedReader in = new BufferedReader(new FileReader(aFile));

    try {
      for (int i = 0; i < destinations.size(); i++) {
        RrdDestination destination = (RrdDestination) destinations.get(i);
        destination.start();
      }

      String line;
      while ((line = in.readLine()) != null) {
        totalLines++;
        try {
          Matcher matcher = regex.matcher(line);
          if (matcher.matches()) {
            processedLines++;
            OfflineExecution result = new OfflineExecution(
              matcher.group(2),
              matcher.group(3),
              Long.parseLong(matcher.group(5)),
              numberFormat.parse(matcher.group(4)).doubleValue()
            );
            for (int i = 0; i < destinations.size(); i++) {
              RrdDestination destination = (RrdDestination) destinations.get(i);
              if(destination.matches(result)) {
                destination.write(result);
              }
            }

          }
        } catch (ParseException e) {
          log.warn("Error reading line " + line, e);
        }
      }
      for (int i = 0; i < destinations.size(); i++) {
        RrdDestination destination = (RrdDestination) destinations.get(i);
        destination.stop();
      }

    } finally {
      in.close();
    }
    log.debug("Finished parsing " + aFile.getAbsolutePath() +
      ". Processed " + processedLines + " out of " + totalLines + " lines.");
  }
}


