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

package etm.core.jmx;

import etm.core.aggregation.Aggregate;
import etm.core.metadata.AggregatorMetaData;
import etm.core.metadata.EtmMonitorMetaData;
import etm.core.monitor.EtmException;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.MeasurementRenderer;
import etm.core.renderer.SimpleTextRenderer;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfo;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfo;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import java.io.StringWriter;
import java.util.Map;

/**
 * An MBean that provides access to an EtmMonitor instance.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class EtmMonitorMBean extends JmxSupport implements DynamicMBean {

  private EtmMonitor etmMonitor;
  private String measurementDomain;

  private OpenMBeanInfoSupport activeInfo;
  private OpenMBeanInfoSupport inactiveInfo;
  protected TabularType tabularType;

  public EtmMonitorMBean(EtmMonitor aEtmMonitor, String aMeasurementDomain) throws OpenDataException {
    etmMonitor = aEtmMonitor;
    measurementDomain = aMeasurementDomain;

    String mbeanClassName = etmMonitor.getClass().getName();
    EtmMonitorMetaData etmMonitorMetaData = etmMonitor.getMetaData();

    AggregatorMetaData metaData = etmMonitorMetaData.getAggregatorMetaData();
    StringBuilder chain = new StringBuilder(metaData.getImplementationClass().getName());
    metaData = metaData.getNestedMetaData();

    while (metaData != null) {
      chain.append(",");
      chain.append(metaData.getImplementationClass().getName());
      metaData = metaData.getNestedMetaData();
    }

    String description = etmMonitorMetaData.getDescription() + " Configuration: Aggregator Chain (" +
      chain + "), Timer Implementation (" +
      etmMonitorMetaData.getTimerMetaData().getImplementationClass().getClass().getName() +
      ") ]";

    tabularType = new TabularType("performanceDetails",
      "Some performance details",
      new CompositeType("etmPoint", "An EtmPoint",
        new String[]{"name", "measurements", "average", "min", "max", "total", "objectname"},
        new String[]{"EtmPoint name", "Number of measurements", "Average time (ms)", "Minimum time (ms)", "Maximum Time (ms)", "Total Time (ms)", "JMX ObjectName"},
        new OpenType[]{SimpleType.STRING, SimpleType.LONG, SimpleType.DOUBLE, SimpleType.DOUBLE, SimpleType.DOUBLE, SimpleType.DOUBLE, SimpleType.OBJECTNAME}),
      new String[]{"name", "measurements", "average", "min", "max", "total", "objectname"});

    activeInfo = new OpenMBeanInfoSupport(
      mbeanClassName,
      description,
      getAttributeInfos(),
      new OpenMBeanConstructorInfo[]{},
      getActiveMonitorOperation(),
      new MBeanNotificationInfo[]{}
    );

    inactiveInfo = new OpenMBeanInfoSupport(
      mbeanClassName,
      description,
      getAttributeInfos(),
      new OpenMBeanConstructorInfo[]{},
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
    for (int i = 0; i < strings.length; i++) {
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
    } else if ("enableCollection".equals(string)) {
      etmMonitor.enableCollection();
    } else if ("reset".equals(string)) {
      etmMonitor.reset();
    } else if ("renderResultsAsText".equals(string)) {
      return renderResultsAsText();
    } else if ("results".equals(string)) {
      return renderResults();
    }

    return null;
  }

  public MBeanInfo getMBeanInfo() {
    if (etmMonitor.isCollecting()) {
      return activeInfo;
    } else {
      return inactiveInfo;
    }
  }

  private TabularData renderResults() {
    final TabularDataSupport data = new TabularDataSupport(tabularType);
    etmMonitor.render(new MeasurementRenderer() {
      public void render(Map points) {
        for (Object o : points.values()) {
          Aggregate aggregate = (Aggregate) o;
          try {
            data.put(new CompositeDataSupport(
                tabularType.getRowType(),
                new String[]{"name", "measurements", "average", "min", "max", "total", "objectname"},
                new Object[]{
                    aggregate.getName(),
                    new Long(aggregate.getMeasurements()),
                    new Double(aggregate.getAverage()),
                    new Double(aggregate.getMin()),
                    new Double(aggregate.getMax()),
                    new Double(aggregate.getTotal()),
                    calculateObjectName(measurementDomain, aggregate)
                }));
          } catch (Exception e) {
            throw new EtmException(e);
          }
        }
      }
    });
    return data;
  }

  public String renderResultsAsText() {
    StringWriter writer = new StringWriter();
    SimpleTextRenderer renderer = new SimpleTextRenderer(writer);
    etmMonitor.render(renderer);

    return writer.toString();
  }

  private OpenMBeanOperationInfo[] getActiveMonitorOperation() {
    try {
      return new OpenMBeanOperationInfoSupport[]{
        new OpenMBeanOperationInfoSupport("disableCollection", "Disables performance monitoring.", null, SimpleType.VOID, MBeanOperationInfo.ACTION),
        new OpenMBeanOperationInfoSupport("reset", "Resets current performance data.", null, SimpleType.VOID, MBeanOperationInfo.ACTION),
        new OpenMBeanOperationInfoSupport("renderResultsAsText", "Renders aggregated performance statistics in text format", null, SimpleType.STRING, MBeanOperationInfo.ACTION),
        new OpenMBeanOperationInfoSupport("results", "Current top level performance results.", null, tabularType, MBeanOperationInfo.INFO)
      };
    } catch (Exception e) {
      // this should be save
      throw new EtmException(e.getMessage());
    }
  }

  private OpenMBeanOperationInfo[] getInactiveMonitorOperation() {
    try {
      return new OpenMBeanOperationInfoSupport[]{
        new OpenMBeanOperationInfoSupport("enableCollection", "Enables performance monitoring.", null, SimpleType.VOID, MBeanOperationInfo.ACTION),
        new OpenMBeanOperationInfoSupport("reset", "Resets current performance data.", null, SimpleType.VOID, MBeanOperationInfo.ACTION),
        new OpenMBeanOperationInfoSupport("renderResultsAsText", "Renders aggregated performance statistics in text format", null, SimpleType.STRING, MBeanOperationInfo.ACTION),
        new OpenMBeanOperationInfoSupport("results", "Current top level performance results.", null, tabularType, MBeanOperationInfo.INFO)
      };
    } catch (Exception e) {
      // this should be save
      throw new EtmException(e.getMessage());
    }
  }

  private OpenMBeanAttributeInfo[] getAttributeInfos() {
    try {
      return new OpenMBeanAttributeInfoSupport[]{
        new OpenMBeanAttributeInfoSupport("started", "Whether the monitor is started or not.", SimpleType.BOOLEAN, true, false, true),
        new OpenMBeanAttributeInfoSupport("collecting", "Whether the monitor is collecting or not.", SimpleType.BOOLEAN, true, false, true),
        new OpenMBeanAttributeInfoSupport("startDate", "The date the application was started.", SimpleType.DATE, true, false, false),
        new OpenMBeanAttributeInfoSupport("lastResetDate", "The date the monitor was resetted.", SimpleType.DATE, true, false, false)
      };


    } catch (Exception e) {
      // this should be save
      throw new EtmException(e.getMessage());
    }
  }

}
