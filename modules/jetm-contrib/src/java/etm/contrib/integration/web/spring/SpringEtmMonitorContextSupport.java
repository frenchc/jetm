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
package etm.contrib.integration.web.spring;

import etm.core.monitor.EtmMonitor;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import java.util.Iterator;
import java.util.Map;

/**
 * Helper class to retrieve EtmMonitor instance from spring configuration.
 *
 * @author void.fm
 * @version $Revision$
 */
public class SpringEtmMonitorContextSupport {

  public static final String ETM_MONITOR_PARAMETER_NAME = "etmMonitorName";

  public static EtmMonitor locateEtmMonitor(ApplicationContext ctx, String etmMonitorName) throws ServletException {
    Map map = ctx.getBeansOfType(EtmMonitor.class);
    if (etmMonitorName != null) {
      return (EtmMonitor) ctx.getBean(etmMonitorName);
    } else {
      if (map.size() > 0) {
        if (map.size() == 1) {
          return (EtmMonitor) map.values().iterator().next();
        } else {
          StringBuffer beanNames = new StringBuffer();
          for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
            String beanName = (String) iterator.next();
            beanNames.append(beanName);
            beanNames.append(',');
          }
          beanNames.deleteCharAt(beanNames.length() - 1);
          throw new ServletException("Located more than one EtmMonitor instance. Please specify the name " +
            "of EtmMonitor instance. [Found: " + beanNames + "]");
        }
      } else {
        throw new ServletException("Unable to locate EtmMonitor instance in bean definitions.");
      }
    }
  }

}
