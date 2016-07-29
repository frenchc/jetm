/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package etm.contrib.integration.jee.rest;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * A JAX-RS Filter that spans performance monitoring around HTTP requests.
 * Uses {@link etm.core.configuration.EtmManager#getEtmMonitor()}
 * to retrieve the currently active EtmMonitor.
 *
 * @author void.fm
 * @version $Revision$
 */

@Provider
public class JaxrsRequestPerformanceFilter implements ContainerRequestFilter, ContainerResponseFilter {

  @Context
  private HttpServletRequest request;

  @Context
  private ResourceInfo resourceInfo;

  private final EtmMonitor etmMonitor;

  private EtmPoint point;

  private String contextPath;

  private String servletPath;

  public JaxrsRequestPerformanceFilter() {
    etmMonitor = getEtmMonitor();
  }

  @PostConstruct
  private void init() {
    contextPath = request.getContextPath();
    servletPath = request.getServletPath();
  }

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    point = etmMonitor.createPoint("HTTP " + request.getMethod() + " request " + getPath());
  }

  private String getPath() {
    UriBuilder uriBuilder = UriBuilder
      .fromPath(contextPath)
      .path(servletPath);

    Method resourceMethod = resourceInfo.getResourceMethod();
    if (resourceMethod.getDeclaringClass().isAnnotationPresent(Path.class)) {
      Path classLevelPath = resourceMethod.getDeclaringClass().getAnnotation(Path.class);
      uriBuilder.path(classLevelPath.value());
    }
    if (resourceMethod.isAnnotationPresent(Path.class)) {
      Path methodLevelPath = resourceMethod.getAnnotation(Path.class);
      uriBuilder.path(methodLevelPath.value());
    }

    return uriBuilder.toTemplate();
  }

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
    if (point != null) {
      point.collect();
    }
  }

  protected EtmMonitor getEtmMonitor() {
    return EtmManager.getEtmMonitor();
  }
}
