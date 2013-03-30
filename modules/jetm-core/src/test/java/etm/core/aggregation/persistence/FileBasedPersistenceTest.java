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
package etm.core.aggregation.persistence;

import etm.core.aggregation.persistence.FileSystemPersistenceBackend;
import etm.core.aggregation.persistence.PersistentRootAggregator;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.monitor.NestedMonitor;
import etm.core.renderer.SimpleTextRenderer;
import junit.framework.TestCase;

import java.io.File;
import java.io.StringWriter;
import java.util.Date;

/**
 *
 * Tests store and load to file persistence for flat and nested monitors.
 * @author void.fm
 * @version $Revision:96 $
 */
public class FileBasedPersistenceTest extends TestCase {

  private File file;

  protected void setUp() throws Exception {
    super.setUp();
    file = new File(System.getProperty("java.io.tmpdir"), "junit-ser");
    if (file.exists()) {
      file.delete();
    }
  }

  protected void tearDown() throws Exception {
    if (file.exists()) {
      file.delete();
    }
  }

  public void testPersistence() {
    FileSystemPersistenceBackend backend = new FileSystemPersistenceBackend();
    backend.setFilename("junit-ser");

    PersistentRootAggregator memoryAggregator = new PersistentRootAggregator();
    memoryAggregator.setPersistenceBackend(backend);
    EtmMonitor memoryMonitor = new NestedMonitor(memoryAggregator);
    memoryMonitor.start();

    EtmPoint point = memoryMonitor.createPoint("test");
    point.collect();

    StringWriter memoryWriter = new StringWriter();
    SimpleTextRenderer memoryRenderer = new SimpleTextRenderer(memoryWriter);
    memoryMonitor.render(memoryRenderer);

    memoryMonitor.stop();

    assertTrue(file.exists());
    Date startDate = memoryMonitor.getMetaData().getStartTime();
    Date lastReset = memoryMonitor.getMetaData().getLastResetTime();

    // create new monitor and compare loaded details
    PersistentRootAggregator persistentStateAggregator = new PersistentRootAggregator();
    persistentStateAggregator.setPersistenceBackend(backend);
    EtmMonitor persistentStateMonitor = new NestedMonitor(persistentStateAggregator);
    persistentStateMonitor.start();

    assertEquals(startDate.getTime(), persistentStateMonitor.getMetaData().getStartTime().getTime());
    assertEquals(lastReset.getTime(), persistentStateMonitor.getMetaData().getLastResetTime().getTime());

    StringWriter persistentWriter = new StringWriter();
    SimpleTextRenderer persistentRenderer = new SimpleTextRenderer(persistentWriter);
    persistentStateMonitor.render(persistentRenderer);

    assertEquals(memoryWriter.toString(), persistentWriter.toString());

    persistentStateMonitor.stop();
  }
}
