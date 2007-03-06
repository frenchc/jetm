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

import etm.contrib.rrd.core.OfflineLogParser;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.Util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * Command line tool for various RRD4j related tasks such as creating a RRD4j db, creating images
 * or importing raw data from a log file to the rrd4j db.
 *
 * @version $Revision$
 * @author void.fm
 */
public class Rrd4jMain {

  public static void main(String[] args) {
    if (args.length < 3) {
      printUsage("Missing command line parameters.");
      System.exit(-1);
    }

    Rrd4jUtilCommand command = new Rrd4jUtilCommand(args);

    if ("create-graph".equalsIgnoreCase(command.getCommand())) {
      // rrd4j-util create-graph -t template -d destination -z interval -p key1=value1,key2=value2,key3=value3
      Rrd4jUtil util = new Rrd4jUtil();
      URL url = util.locateTemplate(command.getTemplate());
      File destination = new File(command.getDestination());
      long intervalEnd = Util.getTimestamp();
      long intervalStart = calculate(intervalEnd, command.getInterval());

      if (command.getSource() != null) {
        File source = new File(command.getSource());
        util.createGraph(url, source, destination, intervalStart, intervalEnd, command.getProperties());
      } else {
        util.createGraph(url, destination, intervalStart, intervalEnd, command.getProperties());

      }
    } else if ("create-db".equalsIgnoreCase(command.getCommand())) {
      // rrd4j-util create-db -t template -d destination -p key1=value1,key2=value2,key3=value3
      Rrd4jUtil util = new Rrd4jUtil();

      URL url = util.locateTemplate(command.getTemplate());
      File destination = new File(command.getDestination());

      util.createRrdDb(url, destination, command.getProperties());
    } else if ("import".equalsIgnoreCase(command.getCommand())) {
      OfflineLogParser parser = new OfflineLogParser();
      RrdDb db = null;

      try {
        db = new RrdDb(command.getDestination(), true);
        parser.register(new Rrd4jAggregationWriter(db));
        parser.parse(new File(command.getSource()));
      } catch (Exception e) {
        System.err.print("Error storing performance data in rrd db: ");
        e.printStackTrace();
        System.exit(-1);
      } finally {
        try {
          if (db != null) {
            db.close();
          }
        } catch (IOException e) {
          // ignore
        }
      }
    } else {
      printUsage("Unsupported command line parameters.");
      System.exit(-1);
    }
  }

  private static void printUsage(String s) {
    System.out.print(s);
    System.out.println(" Usage: ");
    System.out.println("rrd4j-tool create-db -t template -d destination -p key1=value1,key2=value2,key3=value3");
    System.out.println("rrd4j-tool create-graph -t template -d destination -i interval -p key1=value1,key2=value2,key3=value3");
    System.out.println("rrd4j-tool import -s sourcefile -d destinationDb");
  }

  private static long calculate(long aIntervalEnd, String aTimeframe) {
    // h, d , m, y
    if (aTimeframe == null || aTimeframe.length() < 2) {
      return aIntervalEnd - 60 * 60;
    }

    int value = Integer.parseInt(aTimeframe.substring(0, aTimeframe.length() - 1));
    switch (aTimeframe.charAt(aTimeframe.length() - 1)) {
      case 'h':
        return aIntervalEnd - (value * 60 * 60);
      case 'd':
        return aIntervalEnd - (value * 60 * 60 * 24);
      case 'm':
        return aIntervalEnd - (value * 60 * 60 * 24 * 30);
      case 'y':
        return aIntervalEnd - (value * 60 * 60 * 24 * 365);
    }

    return aIntervalEnd - 60 * 60;
  }


  static class Rrd4jUtilCommand {
    private String command;
    private String template;
    private String destination;
    private String interval;
    private String source;
    private Map properties;

    public Rrd4jUtilCommand(String[] args) {
      command = args[0];
      for (int i = 1; i < args.length; i++) {
        String current = args[i];
        if (current.length() > 1 && current.startsWith("-") && args.length > i + 1) {
          i++;
          switch (current.charAt(1)) {
            case 't':
              template = args[i];
              break;
            case 'd':
              destination = args[i];
              break;
            case 'i':
              interval = args[i];
              break;
            case 's':
              source = args[i];
              break;
            case 'p':
              properties = new HashMap();
              StringTokenizer tk = new StringTokenizer(args[i], ",");
              while (tk.hasMoreTokens()) {
                String s = tk.nextToken();
                int index = s.indexOf("=");
                properties.put(s.substring(0, index), s.substring(index + 1));
              }
              break;
            default:
          }
        }
      }
    }

    public String getSource() {
      return source;
    }

    public String getCommand() {
      return command;
    }

    public String getTemplate() {
      return template;
    }

    public String getDestination() {
      return destination;
    }

    public String getInterval() {
      return interval;
    }

    public Map getProperties() {
      return properties;
    }
  }

}
