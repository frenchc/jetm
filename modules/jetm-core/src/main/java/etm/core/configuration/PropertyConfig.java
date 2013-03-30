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

package etm.core.configuration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Base class for property capable configurations.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class PropertyConfig {
  private Map properties;

  public Map getProperties() {
    return properties;
  }

  public void addProperty(String propertyName, String propertyValue) {
    if (propertyValue.startsWith("${") && propertyValue.endsWith("}")) {
      String systemKey = propertyValue.substring(2, propertyValue.length() - 1);
      propertyValue = System.getProperty(systemKey, propertyValue);
    }

    if (properties == null) {
      properties = new HashMap();
    }
    int dotIndex = propertyName.indexOf(".");
    if (dotIndex > 0) {
      String name = propertyName.substring(0, dotIndex);
      String key = propertyName.substring(dotIndex + 1);
      if (properties.containsKey(name)) {
        ((Map) properties.get(name)).put(key, propertyValue);
      } else {
        Map map = new HashMap();
        map.put(key, propertyValue);
        properties.put(name, map);
      }
    } else {
      if (properties.containsKey(propertyName)) {
        Object currentValue = properties.get(propertyName);
        List valueList = new LinkedList();
        valueList.add(currentValue);
        valueList.add(propertyValue);
        properties.put(propertyName, valueList);
      } else {
        properties.put(propertyName, propertyValue);
      }
    }
  }

}
