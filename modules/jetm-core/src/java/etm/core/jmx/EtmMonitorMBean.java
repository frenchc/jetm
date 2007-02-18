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

package etm.core.jmx;

import etm.core.metadata.AggregatorMetaData;
import etm.core.metadata.EtmMonitorMetaData;
import etm.core.monitor.EtmException;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.SimpleTextRenderer;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;
import java.io.StringWriter;

/**
 * An MBean that provides access to an EtmMonitor instance. The management
 * interfaces supports start/stop of monitoring
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class EtmMonitorMBean implements DynamicMBean {

  private EtmMonitor etmMonitor;

  private MBeanInfo activeInfo;
  private MBeanInfo inactiveInfo;

  public EtmMonitorMBean(EtmMonitor aEtmMonitor) {
    etmMonitor = aEtmMonitor;

    String mbeanClassName = etmMonitor.getClass().getName();
    EtmMonitorMetaData etmMonitorMetaData = etmMonitor.getMetaData();

    AggregatorMetaData metaData = etmMonitorMetaData.getAggregatorMetaData();
    String chain = metaData.getImplementationClass().getName();
    metaData = metaData.getNestedMetaData();

    while (metaData != null) {
      chain += "," + metaData.getImplementationClass().getName();
      metaData = metaData.getNestedMetaData();
    }

    String description = etmMonitorMetaData.getDescription() + " Configuration: Aggregator Chain (" +
      chain + "), Timer Implementation (" +
      etmMonitorMetaData.getTimerMetaData().getImplementationClass().getClass().getName() +
      ") ]";
    activeInfo = new MBeanInfo(
      mbeanClassName,
      description,
      getAttributeInfos(),
      new MBeanConstructorInfo[]{},
      getActiveMonitorOperation(),
      new MBeanNotificationInfo[]{}
    );

    inactiveInfo = new MBeanInfo(
      mbeanClassName,
      description,
      getAttributeInfos(),
      new MBeanConstructorInfo[]{},
      getInactiveMonitorOperation(),
      new MBeanNotificationInfo[]{}
    );
  }


  public Object getAttribute(String string) throws AttributeNotFoundException, MBeanException, ReflectionException {
    if ("started".equals(string)) {
      return (etmMonitor.isStarted()) ? Boolean.TRUE : Boolean.FALSE;
    } else if ("collecting".equals(string)) {
      return (etmMonitor.isCollecting()) ? Boolean.TRUE : Boolean.FALSE;
    } else if ("startDate".equals(string)) {
      return (etmMonitor.getMetaData().getStartTime());
    } else if ("lastResetDate".equals(string)) {
      return etmMonitor.getMetaData().getLastResetTime();
    } else {
      throw new AttributeNotFoundException(string);
    }
  }

  public void setAttribute(Attribute attribute) throws
    AttributeNotFoundException,
    InvalidAttributeValueException, MBeanException, ReflectionException {
    throw new AttributeNotFoundException(attribute.getName());
  }

  public AttributeList getAttributes(String[] strings) {
    AttributeList list = new AttributeList();
    for (int i=0; i < strings.length; i++) {
      try {
        list.add(i, new Attribute(strings[i], getAttribute(strings[i])));
      } catch (Exception e) {
        list.add(i, new Attribute(strings[i], null));        
      }
    }
    return list;
  }

  public AttributeList setAttributes(AttributeList attributeList) {
    return new AttributeList();
  }

  public Object invoke(String string, Object[] objects, String[] strings) throws MBeanException, ReflectionException {
    if ("disableCollection".equals(string)) {
      etmMonitor.disableCollection();
      return null;
    } else if ("enableCollection".equals(string)) {
      etmMonitor.enableCollection();
      return null;
    } else if ("renderResultsAsText".equals(string)) {
      return renderResultsAsText();
    } else {
      return null;
    }
  }

  public MBeanInfo getMBeanInfo() {
    if (etmMonitor.isCollecting()) {
      return activeInfo;
    } else {
      return inactiveInfo;
    }
  }

  public String renderResultsAsText() {
    StringWriter writer = new StringWriter();
    SimpleTextRenderer renderer = new SimpleTextRenderer(writer);
    etmMonitor.render(renderer);

    return writer.toString();
  }

  private MBeanOperationInfo[] getActiveMonitorOperation() {
    try {
      return new MBeanOperationInfo[]{
        new MBeanOperationInfo("Disables performance monitoring.", etmMonitor.getClass().getMethod("disableCollection", new Class[]{})),
        new MBeanOperationInfo("Renders aggregated performance statistics in text format", getClass().getMethod("renderResultsAsText", new Class[]{}))
      };
    } catch (Exception e) {
      // this should be save
      throw new EtmException(e.getMessage());
    }
  }

  private MBeanOperationInfo[] getInactiveMonitorOperation() {
    try {
      return new MBeanOperationInfo[]{
        new MBeanOperationInfo("Enables performance monitoring.", etmMonitor.getClass().getMethod("enableCollection", new Class[]{})),
        new MBeanOperationInfo("Renders aggregated performance statistics in text format", getClass().getMethod("renderResultsAsText", new Class[]{}))
      };
    } catch (Exception e) {
      // this should be save
      throw new EtmException(e.getMessage());
    }
  }

  private MBeanAttributeInfo[] getAttributeInfos() {
    try {
      return new MBeanAttributeInfo[]{
        new MBeanAttributeInfo("started", "Whether the monitor is started or not.", etmMonitor.getClass().getMethod("isStarted", new Class[]{}), null),
        new MBeanAttributeInfo("collecting", "Whether the monitor is collecting or not.", etmMonitor.getClass().getMethod("isCollecting", new Class[]{}), null),
        new MBeanAttributeInfo("startDate", "The date the application was started.", etmMonitor.getMetaData().getClass().getMethod("getStartTime", new Class[]{}), null),
        new MBeanAttributeInfo("lastResetDate", "The date the monitor was resetted.", etmMonitor.getMetaData().getClass().getMethod("getLastResetTime", new Class[]{}), null),
      };


    } catch (Exception e) {
      // this should be save
      throw new EtmException(e.getMessage());
    }
  }

}
