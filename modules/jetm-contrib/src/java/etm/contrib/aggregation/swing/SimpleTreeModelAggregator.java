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

package etm.contrib.aggregation.swing;

import etm.core.aggregation.Aggregator;
import etm.core.aggregation.ExecutionAggregate;
import etm.core.aggregation.NestedAggregator;
import etm.core.metadata.AggregatorMetaData;
import etm.core.monitor.EtmMonitorContext;
import etm.core.monitor.MeasurementPoint;
import etm.core.renderer.MeasurementRenderer;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.util.Enumeration;
import java.util.LinkedList;

/**
 * <p/>
 * The SimpleTreeModelAggregator builds up a Swing {@link javax.swing.tree.TreeModel}
 * for all measurement points.
 * </p>
 * <p/>
 * Please note that the current implementation isn't very efficient, it definitly adds
 * some overhead for every measurement.
 * </p>
 * </p>
 * This implementation is considered alpha quality and will be improved with release 1.1
 * </p>
 *
 * @author void.fm
 * @version $Revision$
 */

public class SimpleTreeModelAggregator extends DefaultTreeModel implements Aggregator {

  private static final String DESCRIPTION = "An aggregator which builds up a Swing TreeModel.";
  protected Aggregator delegate;

  /**
   * Creates a new SimpleTreeModelAggregator with a NestedAggregator as
   * backing aggregator instance.
   *
   * @param aName The name of the root node.
   * @see NestedAggregator
   */
  public SimpleTreeModelAggregator(String aName) {
    this(aName, new NestedAggregator());
  }

  /**
   * Creates a new SimpleTreeModelAggregator which delegates
   * {@link #add(etm.core.monitor.MeasurementPoint)} calls to the provided
   * aggregator instances after altering the treemodel.
   *
   * @param aName
   * @param aDelegate
   */
  public SimpleTreeModelAggregator(String aName, Aggregator aDelegate) {
    super(new DefaultMutableTreeNode(aName));
    delegate = aDelegate;
  }

  public void add(MeasurementPoint point) {
    LinkedList path = new LinkedList();
    path.add(point.getName());

    MeasurementPoint parent = point.getParent();
    while (parent != null) {
      path.addFirst(parent.getName());
      parent = parent.getParent();
    }

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) getRoot();


    while (path.size() != 0) {
      String currentString = (String) path.removeFirst();
      Enumeration childs = node.children();
      DefaultMutableTreeNode currentChild = null;

      while (currentChild == null && childs.hasMoreElements()) {
        DefaultMutableTreeNode measurementNode = (DefaultMutableTreeNode) childs.nextElement();
        ExecutionAggregate aggregate = (ExecutionAggregate) measurementNode.getUserObject();
        if (aggregate.getName().equals(currentString)) {
          currentChild = measurementNode;
        }
      }

      if (currentChild == null) {
        currentChild = new DefaultMutableTreeNode(new ExecutionAggregate(currentString));
        insertNodeInto(currentChild, node, getChildCount(currentChild));
      }

      node = currentChild;
    }


    ((ExecutionAggregate) node.getUserObject()).addTransaction(point);
    nodeChanged(node);

    delegate.add(point);
  }

  public void flush() {
    delegate.flush();
  }

  public void reset() {
    for (int i = getChildCount(root) - 1; i >= 0; i--) {
      removeNodeFromParent((MutableTreeNode) root.getChildAt(i));
    }
    delegate.reset();
  }


  public void reset(String measurementPoint) {
    // todo fix
    throw new UnsupportedOperationException();
  }

  public void render(MeasurementRenderer renderer) {
    delegate.render(renderer);
  }

  public AggregatorMetaData getMetaData() {
    return new AggregatorMetaData(SimpleTreeModelAggregator.class, DESCRIPTION, false, delegate.getMetaData());
  }

  public void init(EtmMonitorContext ctx) {
  }


  public void start() {

  }

  public void stop() {

  }

}
