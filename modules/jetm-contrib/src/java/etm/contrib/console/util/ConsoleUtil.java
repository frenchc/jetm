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

package etm.contrib.console.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for HTTP requests.
 *
 * @author void.fm
 * @version $Revision$
 */

public class ConsoleUtil {

  public static Map extractRequestParameters(byte[] aTemp, int parameterStart, int endOfLine) {
    Map map = new HashMap();

    int index = parameterStart;
    int lastEnd = parameterStart;
    int currentDelimiter = parameterStart;

    while (index < endOfLine) {
      switch (aTemp[index]) {
        case'=':
          currentDelimiter = index;
          break;
        case'&': {
          parseParameters(map, aTemp, index, lastEnd, currentDelimiter);
          currentDelimiter = index;
          lastEnd = index;
          break;
        }
      }

      index++;
    }

    parseParameters(map, aTemp, index, lastEnd, currentDelimiter);
    return map;
  }

  private static void parseParameters(Map aMap, byte[] aTemp, int aIndex, int aLastEnd, int aCurrentDelimiter) {
    if (aCurrentDelimiter <= aLastEnd) {
      String key = new String(aTemp, aLastEnd + 1, aIndex - aLastEnd - 1);
      try {
        aMap.put(URLDecoder.decode(key, "UTF-8"), "");
      } catch (UnsupportedEncodingException e) {
        // ignored
      }
    } else {
      try {
        String key = new String(aTemp, aLastEnd + 1, aCurrentDelimiter - aLastEnd - 1);
        String value = new String(aTemp, aCurrentDelimiter + 1, aIndex - aCurrentDelimiter - 1).trim();
        aMap.put(URLDecoder.decode(key, "UTF-8"), URLDecoder.decode(value, "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        // ignored
      }
    }
  }

}