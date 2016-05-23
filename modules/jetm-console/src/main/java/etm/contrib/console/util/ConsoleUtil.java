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

package etm.contrib.console.util;

import etm.contrib.console.HttpConsoleServer;
import etm.core.monitor.EtmException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Helper class for HTTP requests.
 *
 * @author void.fm
 * @version $Revision$
 */

public class ConsoleUtil {

  private ConsoleUtil() {
  }

  public static String appendParameters(String url, Map parameters) {
    return appendParameters(url, parameters, false);
  }

  public static String appendParameters(String url, Map parameters, boolean removeDetails) {
    StringBuffer result = new StringBuffer(url);

    try {
      if (parameters != null && parameters.size() > 0) {
        if (url.indexOf('?') < 0) {
          result.append("?");
        } else {
          result.append("&amp;");
        }
        for (Iterator iterator = parameters.keySet().iterator(); iterator.hasNext();) {
          String name = (String) iterator.next();
          if (removeDetails && "point".equals(name)) {
            continue;
          }
          result.append( URLEncoder.encode(name, HttpConsoleServer.DEFAULT_ENCODING));
          result.append("=");
          result.append(URLEncoder.encode((String) parameters.get(name), HttpConsoleServer.DEFAULT_ENCODING));
          result.append("&amp;");
        }

        if (url.endsWith("&amp;")) {
          result.delete(url.length() - 5, 5);
        }
      }
      return result.toString();
    } catch (UnsupportedEncodingException e) {
      // will hopefully never happen since UTF-8 should be supported.
      throw new EtmException(e);
    }
  }

  public static Map extractRequestParameters(byte[] aTemp, int parameterStart, int endOfLine) {
    try {
      Map map = new HashMap();

      int index = parameterStart;
      int lastEnd = parameterStart;
      int currentDelimiter = parameterStart;

      while (index < endOfLine) {
        switch (aTemp[index]) {
          case '=':
            currentDelimiter = index;
            break;
          case '&': {
            parseParameters(map, aTemp, index, lastEnd, currentDelimiter);
            currentDelimiter = index;
            lastEnd = index;
            break;
          }
          default:
            // ignored
        }

        index++;
      }

      parseParameters(map, aTemp, index, lastEnd, currentDelimiter);
      return map;
    } catch (UnsupportedEncodingException e) {
      // will hopefully never happen since UTF-8 should be supported.
      throw new EtmException(e);
    }
  }

  private static void parseParameters(Map aMap, byte[] aTemp, int aIndex, int aLastEnd, int aCurrentDelimiter)
  throws UnsupportedEncodingException{
    if (aCurrentDelimiter <= aLastEnd) {
      String key = new String(aTemp, aLastEnd + 1, aIndex - aLastEnd - 1, HttpConsoleServer.DEFAULT_ENCODING);
      try {
        aMap.put(URLDecoder.decode(key, HttpConsoleServer.DEFAULT_ENCODING), "");
      } catch (UnsupportedEncodingException e) {
        // ignored
      }
    } else {
      try {
        String key = new String(aTemp, aLastEnd + 1, aCurrentDelimiter - aLastEnd - 1, HttpConsoleServer.DEFAULT_ENCODING);
        String value = new String(aTemp, aCurrentDelimiter + 1, aIndex - aCurrentDelimiter - 1, HttpConsoleServer.DEFAULT_ENCODING).trim();
        aMap.put(URLDecoder.decode(key,HttpConsoleServer.DEFAULT_ENCODING),
                 URLDecoder.decode(value, HttpConsoleServer.DEFAULT_ENCODING));
      } catch (UnsupportedEncodingException e) {
        // ignored
      }
    }
  }

}