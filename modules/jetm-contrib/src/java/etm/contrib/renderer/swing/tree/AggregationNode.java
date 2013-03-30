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

package etm.contrib.renderer.swing.tree;

import etm.core.aggregation.Aggregate;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * A AggregationNode represents one aggregated performance result,
 * whether this aggregation is a top level or nested element.
 *
 * @author void.fm
 * @version $Revision$
 */
public class AggregationNode extends DefaultMutableTreeNode {

  protected Set childSet = new HashSet();

  public AggregationNode(Aggregate aAggregate) {
    this(aAggregate, null);
  }

  public AggregationNode(Aggregate aAggregate, MutableTreeNode aParent) {
    userObject = aAggregate;
    parent = aParent;
  }


  public void insert(MutableTreeNode newChild, int childIndex) {
    // TODO: this is wired - is there no way to locate a given
    // Node containing a specific user object?
    super.insert(newChild, childIndex);
    childSet.add(((Aggregate) ((AggregationNode) newChild).getUserObject()).getName());
  }


  public boolean contains(String childName) {
    return childSet.contains(childName);
  }
}

