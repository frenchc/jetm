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

package etm.contrib.renderer.swing.tree;

import etm.core.aggregation.Aggregate;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

/**
 *
 * The backing model for a {@link etm.contrib.renderer.swing.tree.EtmResultTree}.
 *
 * @version $Revision$
 * @author void.fm
 *
 */
public class EtmTreeModel extends DefaultTreeModel {

  public EtmTreeModel(MutableTreeNode root) {
    super(root);
  }

  /**
   *
   * Validates whether this model is still in synch with
   * the backing Etm Aggregation model.
   *
   */
  public void synchronizeToEtmState() {
     MutableTreeNode root = (MutableTreeNode) getRoot();

     Enumeration enumeration = root.children();
     while (enumeration.hasMoreElements()) {
       AggregationNode aggregationNode = (AggregationNode) enumeration.nextElement();
       synchronizeNodeToEtmState(aggregationNode);
     }
   }



   protected void synchronizeNodeToEtmState(AggregationNode aAggregationNode) {
    Aggregate aggregate = (Aggregate) aAggregationNode.getUserObject();
    if (aggregate.hasChilds() && aggregate.getChilds().size() > aAggregationNode.getChildCount()) {
      // add missing child nodes recursivly
      Collection collection = aggregate.getChilds().values();
      for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
        Aggregate potentialChild = (Aggregate) iterator.next();
        if (!aAggregationNode.contains(potentialChild.getName())) {
          AggregationNode child = new AggregationNode(potentialChild);
          insertNodeInto(child, aAggregationNode, aAggregationNode.getChildCount());
          synchronizeNodeToEtmState(child);
        }
      }
    }
  }
}
