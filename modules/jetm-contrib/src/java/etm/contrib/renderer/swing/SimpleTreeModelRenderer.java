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

import etm.core.aggregation.Aggregate;
import etm.core.renderer.MeasurementRenderer;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The SimpleTreeModelRenderer renders all measurement results in a
 * Swing TreeModel.
 * <p/>
 * </p>
 * This implementation is considered alpha quality.
 *
 * @author void.fm
 * @version $Revision$
 * @see javax.swing.tree.TreeModel
 * @deprecated Please don't use this renderer any more. With JETM 1.3.0 this renderer will be removed. 
 */

public class SimpleTreeModelRenderer extends DefaultTreeModel implements MeasurementRenderer {

  /**
   * Creates a SimpleTreeModelRenderer TreeModel.
   *
   * @param aRootName The name of the root node.
   */
  public SimpleTreeModelRenderer(String aRootName) {
    super(new DefaultMutableTreeNode(aRootName));
  }


  public void render(Map points) {

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) getRoot();
    renderNested(node, points);
  }

  private void renderNested(final DefaultMutableTreeNode parent, final Map map) {
    Map workingCopy = new TreeMap(map);

    List toBeRemoved = new ArrayList();
    Enumeration childs = parent.children();

    while (childs.hasMoreElements()) {

      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) childs.nextElement();
      ExecutionPointWrapper currentAggregate = (ExecutionPointWrapper) currentNode.getUserObject();
      Aggregate newAggregate = (Aggregate) workingCopy.get(currentAggregate.getName());

      if (newAggregate == null) {
        toBeRemoved.add(currentNode);
      } else {
        workingCopy.remove(currentAggregate.getName());
        currentAggregate.setAggregate(newAggregate);
        if (newAggregate.hasChilds()) {
          renderNested(currentNode, newAggregate.getChilds());
        }
        nodeChanged(currentNode);
      }
    }

    for (int i = 0; i < toBeRemoved.size(); i++) {
      DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) toBeRemoved.get(i);
      removeNodeFromParent(defaultMutableTreeNode);
    }

    for (Iterator iterator = workingCopy.keySet().iterator(); iterator.hasNext();) {
      Aggregate aggregate = (Aggregate) workingCopy.get(iterator.next());
      DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(new ExecutionPointWrapper(aggregate));
      insertNodeInto(newChild, parent, parent.getChildCount());
      if (aggregate.hasChilds()) {
        renderNested(newChild, aggregate.getChilds());
      }
    }
  }


  class ExecutionPointWrapper {

    private Aggregate aggregate;

    public ExecutionPointWrapper(Aggregate aAggregate) {
      aggregate = aAggregate;
    }

    public void setAggregate(Aggregate aAggregate) {
      aggregate = aAggregate;
    }

    public String toString() {
      return aggregate.toString();
    }

    public String getName() {
      return aggregate.getName();
    }

  }

}
