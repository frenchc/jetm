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
package etm.contrib.rrd.rrd4j;

import etm.core.metadata.PluginMetaData;
import etm.core.monitor.EtmMonitorContext;
import etm.core.plugin.EtmPlugin;
import org.rrd4j.core.Util;

import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.TimerTask;

/**
 * Generates rrd4j images using a RrdGraphDefTemplate definitions.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class Rrd4jImageGeneratorPlugin implements EtmPlugin {

  private long generationInterval = DEFAULT_INTERVAL;
  private String templateName;
  private Map templateProperties;
  private int timeframe = 60 * 60;
  private int offset;

  private static final long DEFAULT_INTERVAL = 5000;

  private ImageTask task;

  private EtmMonitorContext ctx;

  public void setGenerationInterval(long aGenerationInterval) {
    generationInterval = aGenerationInterval * 1000;
  }

  public void setTemplateName(String aTemplateName) {
    templateName = aTemplateName;
  }

  public void setTemplateProperties(Map aProperties) {
    templateProperties = aProperties;
  }

  public void setTimeframe(int aTimeframe) {
    timeframe = aTimeframe;
  }

  public void setOffset(int aOffset) {
    offset = aOffset;
  }

  public void init(EtmMonitorContext aCtx) {
    ctx = aCtx;
  }

  public void start() {
    task = new ImageTask();
    ctx.getScheduler().schedule(task, 0, generationInterval);
  }

  public void stop() {
    if (task != null) {
      task.cancel();
    }
  }

  public PluginMetaData getPluginMetaData() {
    PluginMetaData metaData = new PluginMetaData(getClass(), "Rrd4jImageGenerator Plugin .");

    return metaData;
  }


  class ImageTask extends TimerTask {
    private URL template;
    Rrd4jUtil util;

    public ImageTask() {
      util = new Rrd4jUtil();
      template = util.locateTemplate(templateName);
    }

    public void run() {
      long intervalend = Util.getTimestamp(new Date());
      long intervalstart = intervalend - offset - timeframe;

      util.createGraph(template, intervalstart, intervalend, templateProperties);
    }
  }
}
