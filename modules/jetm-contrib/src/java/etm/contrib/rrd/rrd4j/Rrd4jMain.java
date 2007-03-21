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
import org.rrd4j.core.Util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Command line tool for various RRD4j related tasks such as creating a RRD4j db, creating images
 * or importing raw data from a log file to the rrd4j db.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0 
 */
public class Rrd4jMain {
  private static final String DATE_FORMAT = "yyyy.MM.dd";


  public static void main(String[] args) {
    if (args.length < 3) {
      printUsage("Missing command line parameters.");
      System.exit(-1);
    }

    Rrd4jUtilCommand command = new Rrd4jUtilCommand(args);

    if ("create-graph".equalsIgnoreCase(command.getCommand())) {
      // rrd4j-util create-graph -t template -d destination -z interval [-o offset|-b startDate -e endDate] -p key1=value1,key2=value2,key3=value3
      Rrd4jUtil util = new Rrd4jUtil();
      URL url = util.locateTemplate(command.getTemplate());
      File destination = new File(command.getDestination());

      long intervalStart;
      long intervalEnd;

      if (command.getOffset() != null) {
        if (command.getOffset().endsWith("!")) {
          Calendar calendar = Calendar.getInstance();
          calendar.set(Calendar.HOUR_OF_DAY, 0);
          calendar.set(Calendar.MINUTE, 0);
          calendar.set(Calendar.SECOND, 0);
          calendar.set(Calendar.MILLISECOND, 0);

          String offset = command.getOffset();
          offset = offset.substring(0, offset.length() - 1);
          intervalEnd = Util.getTimestamp(calendar.getTime()) - calculate(offset);
        } else {
          intervalEnd = Util.getTimestamp() - calculate(command.getOffset());

        }
        intervalStart = intervalEnd - calculate(command.getInterval());
      } else if (command.getBeginDate() != null) {
        String beginDate = command.getBeginDate();
        intervalStart = Util.getTimestamp(getCalendar(beginDate).getTime());

        if (command.getEndDate() != null) {
          String endDate = command.getEndDate();
          intervalEnd = Util.getTimestamp(getCalendar(endDate).getTime());
        } else {
          if (command.getInterval() != null) {
            intervalEnd = intervalStart + calculate(command.getInterval());
          } else {
            intervalEnd = Util.getTimestamp();
          }
        }
      } else {
        intervalEnd = Util.getTimestamp();
        intervalStart = intervalEnd - calculate(command.getInterval());
      }


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
      if (command.getFilter() != null) {
        parser.setPattern(command.getFilter());
      }


      String destinations = command.getDestination();
      StringTokenizer tk = new StringTokenizer(destinations, ";");
      while (tk.hasMoreTokens()) {
        String s = tk.nextToken();
        int index = s.indexOf('!');
        String filename = s.substring(0, index);
        String pattern = s.substring(index + 1);
        parser.register(new Rrd4jDestination(pattern, new File(filename)));
      }

      try {
        parser.parse(new File(command.getSource()));
      } catch (IOException e) {
        System.err.print("Error importing from '" + command.getSource() + ": ");
        e.printStackTrace();
        System.exit(-1);
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
    System.out.println("rrd4j-tool create-graph -t template -d destination -i interval -o offset -p key1=value1,key2=value2,key3=value3");
    System.out.println("rrd4j-tool import -s sourcefile -d destinationDb -f pattern");
  }

  private static long calculate(String aTimeframe) {
    // h, d , m, y
    if (aTimeframe == null || aTimeframe.length() < 2) {
      return 60 * 60;
    }

    int value = Integer.parseInt(aTimeframe.substring(0, aTimeframe.length() - 1));
    switch (aTimeframe.charAt(aTimeframe.length() - 1)) {
      case 'h':
        return (value * 60 * 60);
      case 'd':
        return (value * 60 * 60 * 24);
      case 'm':
        return (value * 60 * 60 * 24 * 30);
      case 'y':
        return (value * 60 * 60 * 24 * 365);
    }

    return 60 * 60;
  }

  private static Calendar getCalendar(String aDate) {
    Calendar calendar = Calendar.getInstance();
    try {
      calendar.setTime(new SimpleDateFormat(DATE_FORMAT).parse(aDate));
    } catch (ParseException e) {
      System.err.print("Error parsing date '" + aDate + "' using date format " + DATE_FORMAT + ": ");
      e.printStackTrace();
      System.exit(-1);
    }
    // todo required??!
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar;
  }

  static class Rrd4jUtilCommand {
    private String command;
    private String template;
    private String destination;

    private String beginDate;
    private String endDate;
    private String interval;
    private String offset;

    private String source;
    private String filter;
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
            case 'o':
              offset = args[i];
              break;
            case 's':
              source = args[i];
              break;
            case 'f':
              filter = args[i];
              break;
            case 'b':
              beginDate = args[i];
              break;
            case 'e':
              endDate = args[i];
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

    public String getOffset() {
      return offset;
    }

    public String getFilter() {
      return filter;
    }

    public String getBeginDate() {
      return beginDate;
    }

    public String getEndDate() {
      return endDate;
    }
  }

}
