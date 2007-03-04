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

import etm.core.monitor.EtmException;
import etm.core.util.Log;
import etm.core.util.LogAdapter;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.RrdDefTemplate;
import org.rrd4j.core.Util;
import org.rrd4j.core.XmlTemplate;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;
import org.rrd4j.graph.RrdGraphDefTemplate;
import org.rrd4j.graph.RrdGraphInfo;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Util class for various RRD4j tasks.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class Rrd4jUtil {
  public static final String IMAGE_DESTINATION_FILE_VARIABLE = "imagefile";
  public static final String RRD_FILE_VARIABLE = "rrdfile";
  public static final String INTERVALSTART_VARIABLE = "intervalstart";
  public static final String INTERVALEND_VARIABLE = "intervalend";

  private static final LogAdapter log = Log.getLog(Rrd4jUtil.class);

  private static Map templates = new HashMap();

  static {
    templates.put("highres", "etm/contrib/rrd/rrd4j/template/db/highres-template.xml");
    templates.put("mediumres", "etm/contrib/rrd/rrd4j/template/db/mediumres-template.xml");
    templates.put("lowres", "etm/contrib/rrd/rrd4j/template/db/lowres-template.xml");
    templates.put("average-and-tx", "etm/contrib/rrd/rrd4j/template/graph/highres-template.xml");
    templates.put("average", "etm/contrib/rrd/rrd4j/template/graph/highres-template.xml");
    templates.put("min-max-average", "etm/contrib/rrd/rrd4j/template/graph/min-max-average-template.xml");
    templates.put("tx", "etm/contrib/rrd/rrd4j/template/graph/tx-template.xml");
  }

  /**
   * Creates a new image using the given template. Assumes
   * <ul>
   * <li>rrdfile</li>
   * <li>imagefile</li>
   * <li>intervalstart</li>
   * <li>intervalend</li>
   * </ul>
   *
   * @param templateUrl   The templateUrl to use. See {@link RrdGraphDefTemplate} for further details.
   * @param rrdFile       The rrdfile to use
   * @param destination   The image to create.
   * @param intervalStart Start of the rendering interval in seconds.
   * @param intervalEnd   End of the rendering interval in seconds.
   * @param properties    Optional properties providing variable values for the . May be null.
   */
  public static void createGraph(URL templateUrl, File rrdFile, File destination,
                                 long intervalStart, long intervalEnd, Map properties) {
    if (properties == null) {
      properties = new HashMap();
    }
    properties.put(RRD_FILE_VARIABLE, rrdFile.getAbsolutePath());

    createGraph(templateUrl, destination, intervalStart, intervalEnd, properties);
  }

  /**
   * Creates a new image using the given template. Assumes
   * <ul>
   * <li>imagefile</li>
   * <li>intervalstart</li>
   * <li>intervalend</li>
   * </ul>
   *
   * @param templateUrl   The templateUrl to use. See {@link RrdGraphDefTemplate} for further details.
   * @param destination   The image to create.
   * @param intervalStart Start of the rendering interval in seconds.
   * @param intervalEnd   End of the rendering interval in seconds.
   * @param properties    Optional properties providing variable values for the . May be null.
   */
  public static void createGraph(URL templateUrl, File destination,
                                 long intervalStart, long intervalEnd, Map properties) {

    log.debug("Creating image at " + destination.getAbsolutePath() + " using template " + templateUrl + ".");

    if (properties == null) {
      properties = new HashMap();
    }
    properties.put(IMAGE_DESTINATION_FILE_VARIABLE, destination.getAbsolutePath());
    File parentDir = destination.getParentFile();
    if (!parentDir.exists()) {
      parentDir.mkdirs();
    }

    createGraph(templateUrl, intervalStart, intervalEnd, properties);
  }

  /**
   * Creates a new image using the given template. Assumes
   * <ul>
   * <li>intervalstart</li>
   * <li>intervalend</li>
   * </ul>
   *
   * @param templateUrl   The templateUrl to use. See {@link RrdGraphDefTemplate} for further details.
   * @param intervalStart Start of the rendering interval in seconds.
   * @param intervalEnd   End of the rendering interval in seconds.
   * @param properties    Optional properties providing variable values for the . May be null.
   */
  public static void createGraph(URL templateUrl, long intervalStart, long intervalEnd, Map properties) {
    if (properties == null) {
      properties = new HashMap();
    }
    properties.put(INTERVALSTART_VARIABLE, new Long(intervalStart));
    properties.put(INTERVALEND_VARIABLE, new Long(intervalEnd));

    createGraph(templateUrl, properties);
  }


  /**
   * Creates a image using the given template URL and properties.
   *
   * @param templateUrl The url to the template, may be a classpath to.
   * @param properties  The properties used to replace in the template.
   */
  public static void createGraph(URL templateUrl, Map properties) {
    setImageDefaults(properties);
    try {
      URLConnection connection = templateUrl.openConnection();

      InputStream in = connection.getInputStream();
      try {
        RrdGraphDefTemplate template = new RrdGraphDefTemplate(new InputSource(in));
        setProperties(template, properties);

        RrdGraphDef graphDef = template.getRrdGraphDef();
        RrdGraphInfo info = new RrdGraph(graphDef).getRrdGraphInfo();

        log.debug("Created image " + info.getFilename() +
          " [" +
          info.getWidth() + "x" + info.getHeight() + ", " +
          info.getBytes().length + " bytes" +
          "]");
      } finally {
        try {
          in.close();
        } catch (IOException e) {
          // ignore
        }
      }
    } catch (IOException e) {
      throw new EtmException(e);
    }
  }

  /**
   * Creates a new Rrd4j DB using the given template.
   * Always assumes a variable <code>rrdfile</code> in the template that will be replaced
   * with the given file path.
   *
   * @param templateUrl The template url.
   * @param rrdFile     The rrdfile to create.
   * @param properties  Optional properties providing variable values for the . May be null.
   * @throws EtmException If the file already exists
   */
  public static void createRrdDb(URL templateUrl, File rrdFile, Map properties) {
    if (rrdFile.exists()) {
      throw new EtmException("Unable to create rrd file at " + rrdFile.getAbsolutePath() + ". File already available.");
    }

    log.debug("Creating rrd db at " + rrdFile.getAbsolutePath() + " using template " + templateUrl + ".");

    File parentDir = rrdFile.getParentFile();
    if (!parentDir.exists()) {
      parentDir.mkdirs();
    }


    if (properties == null) {
      properties = new HashMap();
    }
    properties.put(RRD_FILE_VARIABLE, rrdFile.getAbsolutePath());

    try {
      URLConnection connection = templateUrl.openConnection();
      InputStream in = connection.getInputStream();
      try {

        RrdDefTemplate template = new RrdDefTemplate(new InputSource(in));
        setProperties(template, properties);
        RrdDef rrdDef = template.getRrdDef();

        RrdDb db = new RrdDb(rrdDef);
        db.close();
      } finally {
        try {
          in.close();
        } catch (IOException e) {
          // ignored
        }
      }
    } catch (Exception e) {
      throw new EtmException(e);
    }
  }


  /**
   * Locates a given template and returns a URL to the template. Translates pre defined
   * templates to their url within the classpat.
   *
   * @param aTemplate A template name, might be predefined template, a classpath resource or file.
   * @return The URL to the resource
   * @throws EtmException Thrown to indicate that the given template could not be found. 
   */
  public static URL locateTemplate(String aTemplate) {
    if (templates.containsKey(aTemplate)) {

      aTemplate = (String) templates.get(aTemplate);
      log.debug("Using template " + aTemplate + " from classpath.");
    }

    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL resource = loader.getResource(aTemplate);
    if (resource != null) {
      return resource;
    }

    File file = new File(aTemplate);
    if (file.exists()) {
      try {
        return file.toURL();
      } catch (MalformedURLException e) {
        throw new EtmException(e);
      }
    }

    throw new EtmException("Unable to locate template " + aTemplate + " in ClassPath or Filesystem.");
  }

  public static void main(String[] args) {
    if (args.length < 3) {
      printUsage("Missing command line parameters.");
      System.exit(-1);
    }

    Rrd4jUtilCommand command = new Rrd4jUtilCommand(args);

    if ("create-graph".equalsIgnoreCase(command.getCommand())) {
      // rrd4j-util create-graph -t template -d destination -z interval -p key1=value1,key2=value2,key3=value3
      URL url = locateTemplate(command.getTemplate());
      File destination = new File(command.getDestination());
      long intervalEnd = Util.getTimestamp();
      long intervalStart = calculate(intervalEnd, command.getInterval());

      if (command.getSource() != null) {
        File source = new File(command.getSource());
        createGraph(url, source, destination, intervalStart, intervalEnd, command.getProperties());
      } else {
        createGraph(url, destination, intervalStart, intervalEnd, command.getProperties());

      }
    } else if ("create-db".equalsIgnoreCase(command.getCommand())) {
      // rrd4j-util create-db -t template -d destination -p key1=value1,key2=value2,key3=value3
      URL url = locateTemplate(command.getTemplate());
      File destination = new File(command.getDestination());

      createRrdDb(url, destination, command.getProperties());
    } else {
      printUsage("Unsupported command line parameters.");
      System.exit(-1);
    }
  }

  private static void setProperties(XmlTemplate aTemplate, Map properties) {
    Iterator it = properties.keySet().iterator();
    while (it.hasNext()) {
      String key = (String) it.next();
      Object value = properties.get(key);
      if (value instanceof String) {
        aTemplate.setVariable(key, (String) value);
      } else if (value instanceof Calendar) {
        aTemplate.setVariable(key, (Calendar) value);
      } else if (value instanceof Long) {
        aTemplate.setVariable(key, ((Long) value).longValue());
      } else {
        aTemplate.setVariable(key, String.valueOf(value));
      }
    }
  }

  private static void setImageDefaults(Map properties) {
    if (properties.get("logarithmic") == null) {
      properties.put("logarithmic", "false");
    }

    if (properties.get("imagetitle") == null) {
      properties.put("imagetitle", "Current performance results");
    }

    properties.put("generatedstamp", "Generated " + new Date() + "\\r");
  }


  private static void printUsage(String s) {
    System.out.print(s);
    System.out.println(" Usage: ");
    System.out.println("rrd4j-util create-db -t template -d destination -p key1=value1 key2=value2 key3=value3");
    System.out.println("rrd4j-util create-graph -t template -d destination -i interval -p key1=value1,key2=value2,key3=value3");
  }

  private static long calculate(long aIntervalEnd, String aTimeframe) {
    // h, d , m, y
    if (aTimeframe == null || aTimeframe.length() <= 2) {
      return aIntervalEnd - 60 * 60;
    }

    int value = Integer.parseInt(aTimeframe.substring(0, aTimeframe.length() - 2));
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
