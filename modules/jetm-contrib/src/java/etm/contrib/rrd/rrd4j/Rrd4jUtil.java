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
import org.rrd4j.core.XmlTemplate;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;
import org.rrd4j.graph.RrdGraphDefTemplate;
import org.rrd4j.graph.RrdGraphInfo;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

/**
 * Util class for various RRD4j tasks.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class Rrd4jUtil {
  private static final LogAdapter log = Log.getLog(Rrd4jUtil.class);


  public void createImage(URL templateUrl, Map properties) {
    String fileName = (String) properties.get("imagefile");

    if (fileName == null) {
      throw new NullPointerException("Variable 'filename' may not be null");
    }
    if (templateUrl == null) {
      throw new NullPointerException("Template URL may not be null.");
    }

    File path = new File(fileName);
    log.debug("Creating image at " + path.getAbsolutePath() + " using template " + templateUrl + ".");

    File parentDir = path.getParentFile();
    if (!parentDir.exists()) {
      parentDir.mkdirs();
    }

    try {
      URLConnection connection = templateUrl.openConnection();

      InputStream in = connection.getInputStream();
      try {
        RrdGraphDefTemplate template = new RrdGraphDefTemplate(new InputSource(in));
        setProperties(template, properties);

        RrdGraphDef graphDef = template.getRrdGraphDef();
        RrdGraphInfo info = new RrdGraph(graphDef).getRrdGraphInfo();
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

  public void createDb(URL templateUrl, Map properties) {
    String fileName = (String) properties.get("filename");

    if (fileName == null) {
      throw new NullPointerException("Variable 'filename' may not be null");
    }
    if (templateUrl == null) {
      throw new NullPointerException("Template URL may not be null.");
    }

    File path = new File(fileName);
    log.debug("Creating rrd db at " + path.getAbsolutePath() + " using template " + templateUrl + ".");

    File parentDir = path.getParentFile();
    if (!parentDir.exists()) {
      parentDir.mkdirs();
    }

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

  protected void setProperties(XmlTemplate aTemplate, Map properties) {
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

  public static void main(String[] args) {

  }

}
