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

package etm.contrib.renderer.swing;

import etm.contrib.renderer.swing.tree.AggregationNode;
import etm.contrib.renderer.swing.tree.EtmResultTree;
import etm.contrib.renderer.swing.tree.EtmTreeModel;
import etm.core.metadata.PluginMetaData;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmMonitorContext;
import etm.core.monitor.event.AggregationFinishedEvent;
import etm.core.monitor.event.AggregationListener;
import etm.core.monitor.event.AggregationStateListener;
import etm.core.monitor.event.AggregationStateLoadedEvent;
import etm.core.monitor.event.MonitorResetEvent;
import etm.core.monitor.event.PreMonitorResetEvent;
import etm.core.monitor.event.PreRootResetEvent;
import etm.core.monitor.event.RootCreateEvent;
import etm.core.monitor.event.RootResetEvent;
import etm.core.plugin.EtmPlugin;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The main panel that renders performance results and refreshes
 * the current results
 *
 * @author void.fm
 * @version $Revision$
 */
public class PerformancePanel extends EtmPanelPane {
  private EtmMonitor etmMonitor;

  protected JTree performanceGraph;
  protected EtmTreeModel model;

  private Timer timer;


  public PerformancePanel(EtmMonitor aEtmMonitor) {
    super();

    etmMonitor = aEtmMonitor;

    // GUI stuff
    model = new EtmTreeModel(new DefaultMutableTreeNode());
    performanceGraph = new EtmResultTree(model);

    setLayout(new BorderLayout());
    add(performanceGraph, BorderLayout.CENTER);


    etmMonitor.addPlugin(new EtmEventHandler());


    timer = new Timer(5000, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            model.synchronizeToEtmState();
            performanceGraph.repaint();
          }
        });
      }
    });

    timer.start();
  }

  class EtmEventHandler implements AggregationListener, AggregationStateListener, EtmPlugin {

    public PluginMetaData getPluginMetaData() {
      return null;
    }

    public void init(EtmMonitorContext ctx) {
    }

    public void start() {
    }

    public void stop() {
    }

    public void onRootCreate(RootCreateEvent event) {
      final AggregationNode child = new AggregationNode(event.getAggregate());
      model.insertNodeInto(child, (MutableTreeNode) model.getRoot(), ((MutableTreeNode) model.getRoot()).getChildCount());

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          model.reload((MutableTreeNode) model.getRoot());
        }
      });

    }

    public void onRootReset(RootResetEvent event) {
    }

    public void onStateLoaded(AggregationStateLoadedEvent event) {
//      PersistentEtmState persistentEtmState = event.getState();
//      Collection collection = persistentEtmState.getAggregates().values();
//      DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode) tree.getRoot();
//
//      for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
//        Aggregate o = (Aggregate) iterator.next();
//        mutableTreeNode.add(new AggregationNode(o));
//      }
//      SwingUtilities.invokeLater(new Runnable() {
//        public void run() {
//          tree.reload();
//        }
//      });
    }

    public void onAggregationFinished(AggregationFinishedEvent event) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          model.synchronizeToEtmState();
          performanceGraph.validate();
          performanceGraph.repaint();
        }
      });
    }

    public void onStateReset(MonitorResetEvent event) {

    }

    public void preRootReset(PreRootResetEvent event) {
    }

    public void preStateReset(PreMonitorResetEvent event) {
    }
  }
}

