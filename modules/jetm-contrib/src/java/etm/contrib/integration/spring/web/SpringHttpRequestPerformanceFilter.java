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
package etm.contrib.integration.spring.web;

import etm.contrib.integration.web.HttpRequestPerformanceFilter;
import etm.core.monitor.EtmMonitor;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;

/**
 * A Servlet Filter that spans performance monitoring around HTTP requests for
 * spring managed EtmMonitor instances. The filter requires the existence of an Spring
 * WebApplicationContext that was created through a Spring <code>ContextLoaderListener</code> or
 * <code>ContextLoaderServlet</code> (<i>It should be possible to locate the WebApplicationContext with
 * WebApplicationContextUtils.getRequiredWebApplicationContext()</i>).
 * <p/>
 * Usually the filter is able to locate the EtmMonitor automatically, however if you have more than one EtmMonitor
 * instance you need specify the monitor bean name as specified in your spring configuration. Therefore add the filter
 * init parameter called <code>etmMonitorName</code> holding its name.
 * <p/>
 * Example:
 * <pre>
 * &lt;filter&gt;
 *   &lt;filter-name&gt;requestPerformanceFilter&lt;/filter-name&gt;
 *   &lt;filter-class&gt;etm.contrib.integration.spring.web.SpringHttpRequestPerformanceFilter&lt;/filter-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;etmMonitorName&lt;/param-name&gt;
 *     &lt;param-value&gt;lala&lt;/param-value&gt;
 *   &lt;/init-param&gt;
 * &lt;/filter&gt;
 * </pre>
 * @author void.fm
 * @version $Revision$
 */
public class SpringHttpRequestPerformanceFilter extends HttpRequestPerformanceFilter {

  protected EtmMonitor getEtmMonitor() throws ServletException {
    // retrieve name of EtmMonitor to use. may be null    
    String etmMonitorName = filterConfig.getInitParameter(SpringEtmMonitorContextSupport.ETM_MONITOR_PARAMETER_NAME);
    WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());

    return SpringEtmMonitorContextSupport.locateEtmMonitor(ctx, etmMonitorName);
  }

}