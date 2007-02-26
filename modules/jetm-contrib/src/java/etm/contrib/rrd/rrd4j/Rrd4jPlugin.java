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

import etm.contrib.rrd.core.AbstractRrdPlugin;
import etm.contrib.rrd.core.RrdDestination;
import etm.core.metadata.PluginMetaData;
import etm.core.util.Log;
import etm.core.util.LogAdapter;

import java.io.File;
import java.util.List;

/**
 * <a href="https://rrd4j.dev.java.net/">RRD4j</a> based implementation of an RRD plugin.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class Rrd4jPlugin extends AbstractRrdPlugin {

  private static final LogAdapter log = Log.getLog(Rrd4jPlugin.class);

  private String configPath = System.getProperty("java.io.tmpdir");
  private List destinationConfiguration;

  public void setRrdFilePath(String path) {
    configPath = path;
  }

  public void setDestinations(List aDestinations) {
    destinationConfiguration = aDestinations;
  }

  protected RrdDestination[] getDestinations() {
    if (destinationConfiguration == null) {
      return new RrdDestination[0];
    }

    destinations = new RrdDestination[destinationConfiguration.size()];

    for (int i = 0; i < destinationConfiguration.size(); i++) {
      String s = (String) destinationConfiguration.get(i);
      int index = s.indexOf('|');
      String filename = s.substring(0, index);
      String pattern = s.substring(index + 1);

      destinations[i] = new Rrd4jDestination(pattern, new File(configPath, filename));
    }

    return destinations;
  }


  public PluginMetaData getPluginMetaData() {
    PluginMetaData metaData = new PluginMetaData(getClass(), "RRD4j plugin.");

    return metaData;
  }

}
