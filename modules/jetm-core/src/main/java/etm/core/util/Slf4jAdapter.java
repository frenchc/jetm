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

package etm.core.util;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Adapter to slf4j. See patch
 * https://sourceforge.net/tracker/?func=detail&aid=3182135&group_id=109626&atid=654002
 *
 * @author Juergen Hermann
 * @since 1.3.0
 */
class Slf4jAdapter implements LogAdapter {
  static final boolean DEBUG = false;
  private Logger log;

  public Slf4jAdapter(Class aClazz) {
    log = LoggerFactory.getLogger(aClazz);
  }

  public void debug(String message) {
    log.debug(message);
  }

  public void info(String message) {
    log.info(message);
  }

  public void warn(String message) {
    log.warn(message);
  }

  public void warn(String message, Throwable t) {
    log.warn(message, t);
  }

  public void error(String message, Throwable t) {
    log.error(message, t);
  }

  public void fatal(String message, Throwable t) {
    log.error(message, t);
  }

  public static boolean isConfigured() {
    // Make sure there is a binding on the class path
    try {
      Class.forName("org.slf4j.impl.StaticLoggerBinder");
    } catch (ClassNotFoundException e) {
      if (DEBUG) {
        System.err.println("JETM: No SLF4J binding found (" + e + ")");
      }
      return false;
    }
    if (DEBUG) {
      System.err.println("JETM: SLF4J binding OK");
    }

    // Also ensure we get a valid root logger
    ILoggerFactory lf = LoggerFactory.getILoggerFactory();
    Logger root = lf.getLogger(Logger.ROOT_LOGGER_NAME);
    if (DEBUG) {
      System.err.println("JETM: SLF4J root logger is " + root + " of type " + root.getClass().getCanonicalName());
    }

    return null != root;
  }
}
