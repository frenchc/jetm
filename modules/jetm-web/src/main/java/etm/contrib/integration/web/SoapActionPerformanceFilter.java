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

package etm.contrib.integration.web;

import etm.core.monitor.EtmPoint;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A Servlet Filter that spans performance monitoring around WebService document/literal
 * requests that contain a SoapAction HTTP header. Uses {@link etm.core.configuration.EtmManager#getEtmMonitor()}
 * to retrieve the currently active EtmMonitor. Therefore it is recommended to use
 * this filter in conjunction with {@link EtmMonitorContextListener}.
 * <p/>
 * Delegates monitoring to {@link HttpRequestPerformanceFilter} for missing or empty soap actions.
 *
 * @author void.fm
 * @version $Revision$
 */
public class SoapActionPerformanceFilter extends HttpRequestPerformanceFilter {

  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    String soapAction = ((HttpServletRequest) servletRequest).getHeader("SoapAction");
    if (soapAction == null || soapAction.length() == 0) {
      super.doFilter(servletRequest, servletResponse, filterChain);
    } else {
      EtmPoint point = etmMonitor.createPoint("SoapAction " + soapAction);
      try {
        filterChain.doFilter(servletRequest, servletResponse);
      } finally {
        point.collect();
      }
    }
  }

  public void destroy() {
  }
}
