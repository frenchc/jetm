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
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Util class for various RRD4j tasks.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class Rrd4jUtil {
  private static final LogAdapter log = Log.getLog(Rrd4jUtil.class);


  public void createDb(File path, URL templateUrl) {
    File parentDir = path.getParentFile();
    if (!parentDir.exists()) {
      parentDir.mkdirs();
    }

    try {
      URLConnection connection= templateUrl.openConnection();

      try {

        RrdDefTemplate template = new JetmRrdDefTemplate(path, connection);
        RrdDef rrdDef = template.getRrdDef();

        RrdDb db = new RrdDb(rrdDef);
        db.close();

      } finally {
        try {
          connection.getInputStream().close();
        } catch (IOException e) {
          // ignored
        }
      }
    } catch (Exception e) {
      throw new EtmException(e);
    }


  }

  public static void main(String[] args) {

  }

  /**
   * Since the original template requries a path value
   * we override an interval method to return our path
   * to our rrd file.
   */
  class JetmRrdDefTemplate extends RrdDefTemplate {

    private File path;
    private URLConnection urlConnection;

    public JetmRrdDefTemplate(File aPath, URLConnection aURLConnection) throws IOException {
      super(new InputSource(aURLConnection.getInputStream()));
      path = aPath;
      urlConnection = aURLConnection;
    }

    protected String getChildValue(Node parentNode, String childName) {
      if ("path".equals(childName)) {
        try {
          String value = super.getChildValue(parentNode, childName);
          if (value != null && value.length() > 0) {
            log.warn("Rrd4j template " + urlConnection.getURL() + " defines path value " + value +
              ". It will be overriden by " + path.getAbsolutePath());
          }
        } catch (IllegalStateException e) {
          // expected
        }
        return path.getAbsolutePath();
      }

      return super.getChildValue(parentNode, childName);
    }
  }


}
