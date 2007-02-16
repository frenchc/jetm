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

package etm.core.util;

import etm.core.monitor.EtmException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 *
 * Helper class to deal with properties.
 *
 * @version $Revision$
 * @author void.fm
 */
public class PropertySupport {

  public static Object create(String aClassName, Map properties) {
    try {
      return create(Class.forName(aClassName), properties);
    } catch (ClassNotFoundException e) {
      throw new EtmException(e.getMessage());
    }
  }

  public static Object create(Class aClazz, Map properties) {
    try {
      Object obj = aClazz.newInstance();
      if (properties != null) {
        setProperties(obj, properties);

      }
      return obj;

    } catch (InstantiationException e) {
      throw new EtmException(e.getMessage());
    } catch (IllegalAccessException e) {
      throw new EtmException(e.getMessage());
    }
  }

  public static void setProperties(Object aObj, Map properties) {
    
    try {
      Method[] methods = aObj.getClass().getMethods();
      for (int i = 0; i < methods.length; i++) {
        Method method = methods[i];
        String methodName = method.getName();
        if (methodName.startsWith("set") && methodName.length() >= 4 && method.getParameterTypes().length == 1) {
          String propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);

          if (properties.containsKey(propertyName) && method.getParameterTypes().length == 1) {
            Object value = properties.get(propertyName);

            Class clazz = method.getParameterTypes()[0];

            if (int.class.isAssignableFrom(clazz)) {
              method.invoke(aObj, new Object[]{new Integer(Integer.parseInt((String) value))});
            } else if (long.class.isAssignableFrom(clazz)) {
              method.invoke(aObj, new Object[]{new Long(Long.parseLong((String) value))});
            } else if (boolean.class.isAssignableFrom(clazz)) {
              if ("true".equals(value)) {
                method.invoke(aObj, new Object[]{Boolean.TRUE});
              } else if ("false".equals(value)) {
                method.invoke(aObj, new Object[]{Boolean.FALSE});
              }
            } else if (String.class.isAssignableFrom(clazz)) {
              method.invoke(aObj, new Object[]{value});
            } else if (Class.class.isAssignableFrom(clazz)) {
              method.invoke(aObj, new Object[]{Class.forName((String) value)});
            } else if (Map.class.isAssignableFrom(clazz)) {
              if (value instanceof Map) {
                method.invoke(aObj, new Object[]{value});
              }
            } else if (List.class.isAssignableFrom(clazz)) {
              if (value instanceof List) {
                method.invoke(aObj, new Object[]{value});
              }
            }
          }
        }
      }
    }
    catch (IllegalAccessException e) {
      throw new EtmException(e.getMessage());
    } catch (InvocationTargetException e) {
      throw new EtmException(e.getMessage());
    } catch (ClassNotFoundException e) {
      throw new EtmException(e.getMessage());
    }
  }
}
