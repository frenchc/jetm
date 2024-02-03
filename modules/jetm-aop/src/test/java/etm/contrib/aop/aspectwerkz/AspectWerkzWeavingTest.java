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

package etm.contrib.aop.aspectwerkz;

import etm.core.configuration.EtmManager;
import junit.framework.TestCase;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

/**
 * Tests AspectWerkz weaving.
 *
 * @author void.fm
 * @version $Revision$
 */

public class AspectWerkzWeavingTest extends TestCase {

  public void setUp() {
    EtmManager.reset();
    Configurator.initialize(new DefaultConfiguration());
    EtmManager.getEtmMonitor().start();
  }

  public void tearDown() {
    EtmManager.getEtmMonitor().stop();
  }

  public void testAspectWerkzWeaving() throws Exception {
//    // todo
//    // 1. load classes using dynamic aspectwerkz weaver
//
//    Java javaRunner = new Java();
//    javaRunner.setClassname("org.codehaus.aspectwerkz.ProcessStarter");
//    javaRunner.executeJava();
//
//    // 2. run test
  }

}
