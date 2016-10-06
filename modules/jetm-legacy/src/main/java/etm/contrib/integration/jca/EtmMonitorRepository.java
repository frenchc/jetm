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
package etm.contrib.integration.jca;

import etm.core.monitor.EtmException;
import etm.core.monitor.EtmMonitor;

import java.util.HashMap;
import java.util.Map;

/**
 * The EtmMonitorRespository keeps track of local EtmMonitor instances. It assumes
 * an EtmMonitor can be loaded statically.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.2
 */
class EtmMonitorRepository {

  private static Map managedMonitors = new HashMap();

  private EtmMonitorRepository() {
  }

  public static void register(String reference, EtmMonitor aMonitor) {
    if (!managedMonitors.containsKey(reference)) {
      managedMonitors.put(reference, aMonitor);
    } else {
      throw new EtmException("Reference " + reference + " already assigned. Ensure that the same jetm-config file is not " +
        "reused accross multiple EtmMonitor instances.");
    }
  }

  public static EtmMonitor deregister(String reference) {
    return (EtmMonitor) managedMonitors.remove(reference);
  }

  public static EtmMonitor getMonitor(String reference) {
    return (EtmMonitor) managedMonitors.get(reference);
  }
}
