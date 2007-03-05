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
package etm.core.aggregation.persistence;

import etm.core.aggregation.RootAggregator;
import etm.core.metadata.AggregatorMetaData;
import etm.core.monitor.EtmMonitorContext;
import etm.core.monitor.event.AggregationStateLoadedEvent;
import etm.core.util.PropertySupport;

import java.util.Map;

/**
 * An aggregator that supports persistence provided by a {@link PersistenceBackend}. By default the persistence
 * backend is {@link etm.core.aggregation.persistence.FileSystemPersistenceBackend}, however you may supply a
 * custom backends during initialization.
 *
 * @author void.fm
 * @version $Revision:96 $
 * @since 1.2.0
 */
public class PersistentRootAggregator extends RootAggregator {
  protected EtmMonitorContext context;
  protected PersistenceBackend persistenceBackend;
  protected Map backendProperties;

  public void init(EtmMonitorContext ctx) {
    super.init(ctx);
    context = ctx;

    if (persistenceBackend == null) {
      persistenceBackend = new FileSystemPersistenceBackend();
    }

    if (backendProperties != null) {
      PropertySupport.setProperties(persistenceBackend, backendProperties);
    }
  }

  public void start() {
    super.start();
    PersistentEtmState state = persistenceBackend.load();
    if (state != null) {
      aggregates = state.getAggregates();
      context.fireEvent(new AggregationStateLoadedEvent(state, this));
    }
  }

  public void stop() {
    PersistentEtmState state = new PersistentEtmState();
    state.setStartTime(context.getEtmMonitor().getMetaData().getStartTime());
    state.setLastResetTime(context.getEtmMonitor().getMetaData().getLastResetTime());

    state.setAggregates(aggregates);
    persistenceBackend.store(state);

    super.stop();
  }

  public void setPersistenceBackend(PersistenceBackend aPersistenceBackend) {
    if (persistenceBackend != null) {
      throw new IllegalStateException("Persistence backend already set. Please use setPersistenceBackend or setPersistenceBackendClass");
    }
    persistenceBackend = aPersistenceBackend;
  }

  public void setBackendProperties(Map someProperties) {
    backendProperties = someProperties;
  }

  public void setPersistenceBackendClass(Class aPersistenceBackendClazz) {
    if (persistenceBackend != null) {
      throw new IllegalStateException("Persistence backend already set. Please use setPersistenceBackend or setPersistenceBackendClass");
    }
    try {
      persistenceBackend = (PersistenceBackend) aPersistenceBackendClazz.newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException("Error instantiating persistence class " +
        aPersistenceBackendClazz +
        ":" +
        e.getMessage());
    }
  }

  public AggregatorMetaData getMetaData() {
    return new AggregatorMetaData(PersistentRootAggregator.class, "A cummulating aggregator for nested and flat representation " +
      "that restores previous state from a persistence backend.", false);
  }
}
