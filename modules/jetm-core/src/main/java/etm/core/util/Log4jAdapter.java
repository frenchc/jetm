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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.Collection;
import java.util.Map;


/**
 * Adapter to log4j.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
class Log4jAdapter implements LogAdapter {

  private Logger log;

  public Log4jAdapter(Class aClazz) {
    log = LogManager.getLogger(aClazz);
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
    log.fatal(message, t);
  }

  public static boolean isConfigured() {
    Logger logger = LogManager.getLogger();
    Map<String, Appender> appenderMap =
        ((org.apache.logging.log4j.core.Logger) logger).getAppenders();

    if (!appenderMap.isEmpty()) {
      return true;
    } else {
      LoggerContext loggerContext =
          (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
      Collection<LoggerConfig> loggerConfigs = loggerContext.getConfiguration().getLoggers().values();

      for (LoggerConfig loggerConfig : loggerConfigs) {
        if (!loggerConfig.getAppenders().isEmpty())
          return true;
      }
    }
    return false;
  }
}
