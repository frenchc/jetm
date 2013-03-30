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

package etm.core.configuration.mockup;

import etm.core.metadata.PluginMetaData;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmMonitorContext;
import etm.core.plugin.EtmPlugin;

import java.util.List;
import java.util.Map;

/**
 *
 * Plugin for config tests
 *
 * @version $Revision$
 * @author void.fm
 *
 */
public class TestPlugin implements EtmPlugin {
  private EtmMonitor etmMonitor;
  private boolean started;

  private boolean booleanTrue;
  private boolean booleanFalse;
  private long longValue;
  private int intValue;
  private String stringValue;
  private Class clazzValue;
  private List listValue;
  private Map mapValue;


  public void init(EtmMonitorContext ctx) {
    etmMonitor = ctx.getEtmMonitor();  
  }

  public void start() {
    started = true;
  }

  public void stop() {
    started = false;
  }


  public EtmMonitor getEtmMonitor() {
    return etmMonitor;
  }

  public boolean isStarted() {
    return started;
  }

  public boolean isBooleanTrue() {
    return booleanTrue;
  }

  public void setBooleanTrue(boolean aBooleanTrue) {
    booleanTrue = aBooleanTrue;
  }

  public boolean isBooleanFalse() {
    return booleanFalse;
  }

  public void setBooleanFalse(boolean aBooleanFalse) {
    booleanFalse = aBooleanFalse;
  }

  public long getLongValue() {
    return longValue;
  }

  public void setLongValue(long aLongValue) {
    longValue = aLongValue;
  }

  public int getIntValue() {
    return intValue;
  }

  public void setIntValue(int aIntValue) {
    intValue = aIntValue;
  }

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String aStringValue) {
    stringValue = aStringValue;
  }

  public Class getClazzValue() {
    return clazzValue;
  }

  public void setClazzValue(Class aClazzValue) {
    clazzValue = aClazzValue;
  }

  public List getListValue() {
    return listValue;
  }

  public void setListValue(List aListValue) {
    listValue = aListValue;
  }

  public Map getMapValue() {
    return mapValue;
  }

  public void setMapValue(Map aMapValue) {
    mapValue = aMapValue;
  }

  public PluginMetaData getPluginMetaData() {
    return new PluginMetaData(TestPlugin.class, "A test plugin");
  }
}
