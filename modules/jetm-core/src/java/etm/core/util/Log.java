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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * While writing your own wrapper to a log framework seems to be pretty stupid
 * JETM does not want to add a dependency to a log framework in its core package.
 * Therefore this class acts as simple wrapper for the limited use of message output
 * JETM requires.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class Log {

  private static AdapterFactory adapterFactory = new AdapterFactory();

  public static LogAdapter getLog(Class clazzName) {
    return adapterFactory.getLog(clazzName);
  }

  static class AdapterFactory {
    Constructor constructor;

    public AdapterFactory() {
      Class adapterClazz = DefaultLogAdapter.class;

      // try log4j first
      try {
        Class aClass = Class.forName("etm.core.util.Log4jAdapter");
        Method method = aClass.getMethod("isConfigured", new Class[]{});
        Boolean available = (Boolean) method.invoke(null, new Object[]{});
        if (available.booleanValue()) {
          adapterClazz = aClass;
        }
      } catch (Throwable t) {
        // ignore this one
      }

      // fallback to java util logging?
      if (DefaultLogAdapter.class.equals(adapterClazz)) {
        try {
          Class aClass = Class.forName("etm.core.util.Java14LogAdapter");
          Method method = aClass.getMethod("isConfigured", new Class[]{});
          Boolean available = (Boolean) method.invoke(null, new Object[]{});
          if (available.booleanValue()) {
            adapterClazz = aClass;
          }
        } catch (Throwable t) {
          // ignore this one
        }
      }


      try {
        constructor = adapterClazz.getConstructor(new Class[]{Class.class});
      } catch (NoSuchMethodException e) {
        throw new EtmException(e);
      }
    }

    public LogAdapter getLog(Class aClazzName) {
      try {
        return (LogAdapter) constructor.newInstance(new Object[]{aClazzName});
      } catch (Exception e) {
        throw new EtmException(e);
      }
    }
  }
}
