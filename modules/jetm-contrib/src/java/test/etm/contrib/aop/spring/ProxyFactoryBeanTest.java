/*
 *
 * Copyright (c) 2004, 2005, 2006 void.fm
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
package test.etm.contrib.aop.spring;

import etm.core.monitor.EtmMonitor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import test.etm.contrib.aop.AopTestBase;
import test.etm.contrib.aop.resources.BarService;
import test.etm.contrib.aop.resources.FooService;
import test.etm.contrib.aop.resources.YaddaService;

/**
 * Setup/Teardown methods for spring AOP using
 * spring factory beans.
 *
 * @author void.fm
 * @version $Revision$
 */
public class ProxyFactoryBeanTest extends AopTestBase {

  private DefaultListableBeanFactory beanFactory;

  protected void setUp() throws Exception {
    super.setUp();
    beanFactory = new XmlBeanFactory(new ClassPathResource("test/etm/contrib/aop/spring/factory-bean.xml"));
    beanFactory.preInstantiateSingletons();

    etmMonitor = (EtmMonitor) beanFactory.getBean("etmMonitor");
    yaddaService = (YaddaService) beanFactory.getBean("yaddaService");
    barService = (BarService) beanFactory.getBean("barService");
    fooService = (FooService) beanFactory.getBean("fooService");
  }

  protected void tearDown() throws Exception {
    beanFactory.destroySingletons();
    super.tearDown();
  }


}
