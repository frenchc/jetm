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

package etm.contrib.aop.spring;

import etm.contrib.aop.aopalliance.EtmMethodCallInterceptor;
import etm.core.monitor.EtmMonitor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 * The EtmProxyFactoryBean works pretty much like the Spring
 * TransactionFactoryBean.
 *
 * @author void.fm
 * @version $Revision$
 */
public class EtmProxyFactoryBean extends ProxyConfig
  implements FactoryBean, InitializingBean {

  private EtmMonitor etmMonitor;

  private Object target;
  private Object proxy;

  public void setEtmMonitor(EtmMonitor aEtmMonitor) {
    etmMonitor = aEtmMonitor;
  }

  public void setTarget(Object aTarget) {
    target = aTarget;
  }

  public Object getObject() throws Exception {
    return proxy;
  }

  public Class getObjectType() {
    if (proxy != null)
      return proxy.getClass();
    if (target instanceof TargetSource)
      return ((TargetSource) target).getTargetClass();
    if (target != null)
      return target.getClass();
    else
      return null;

  }

  public boolean isSingleton() {
    return true;
  }

  public void afterPropertiesSet() throws Exception {
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.addAdvisor(new EtmAdvisor(new EtmMethodCallInterceptor(etmMonitor)));

    proxyFactory.copyFrom(this);
    TargetSource targetSource = createTargetSource(target);
    proxyFactory.setTargetSource(targetSource);

    proxy = getProxy(proxyFactory);

  }


  protected Object getProxy(AopProxy aopProxy) {
    return aopProxy.getProxy();
  }

  protected TargetSource createTargetSource(Object target) {
    if (target instanceof TargetSource)
      return (TargetSource) target;
    else
      return new SingletonTargetSource(target);
  }

}
