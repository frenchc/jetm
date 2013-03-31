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

package etm.contrib.renderer.swing;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

/**
 * The EtmPanel is an embeddable Swing component that may be used to
 * render performance details within an Swing application. It does not support remoting.
 *
 * @version $Revision$
 * @author void.fm
 */
public class EtmPanel extends JPanel {

  public EtmPanel() {
    this(EtmManager.getEtmMonitor());
  }

  public EtmPanel(EtmMonitor etmMonitor) {
    JTabbedPane tabbedPane = new JTabbedPane();
    setLayout(new BorderLayout());

    PerformancePanel performancePanel = new PerformancePanel(etmMonitor);
    PreferencesPanel preferencesPanel = new PreferencesPanel(etmMonitor);

    tabbedPane.addTab("Performance Stats", performancePanel);
    tabbedPane.addTab("Preferences", preferencesPanel);

    JScrollPane pane = new JScrollPane();
    pane.setViewportView(tabbedPane);

    add(pane, BorderLayout.CENTER);
  }

}
