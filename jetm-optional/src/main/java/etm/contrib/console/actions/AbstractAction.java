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

package etm.contrib.console.actions;

import etm.contrib.console.ConsoleAction;
import etm.contrib.console.ConsoleRequest;
import etm.contrib.util.ExecutionAggregateComparator;

/**
 * Base class for all actions.
 *
 * @author void.fm
 * @version $Revision$
 */
public abstract class AbstractAction implements ConsoleAction {

  protected ExecutionAggregateComparator getComparator(ConsoleRequest request) {
    String sort = request.getRequestParameter("sort");
    // default to descending
    boolean isDescending = !"asc".equals(request.getRequestParameter("order"));

    if ("name".equals(sort)) {
      return new ExecutionAggregateComparator(ExecutionAggregateComparator.TYPE_NAME, isDescending);
    } else if ("executions".equals(sort)) {
      return new ExecutionAggregateComparator(ExecutionAggregateComparator.TYPE_EXCECUTIONS, isDescending);
    } else if ("average".equals(sort)) {
      return new ExecutionAggregateComparator(ExecutionAggregateComparator.TYPE_AVERAGE, isDescending);
    } else if ("min".equals(sort)) {
      return new ExecutionAggregateComparator(ExecutionAggregateComparator.TYPE_MIN, isDescending);
    } else if ("max".equals(sort)) {
      return new ExecutionAggregateComparator(ExecutionAggregateComparator.TYPE_MAX, isDescending);
    } else if ("total".equals(sort)) {
      return new ExecutionAggregateComparator(ExecutionAggregateComparator.TYPE_TOTAL, isDescending);
    } else {
      return new ExecutionAggregateComparator(ExecutionAggregateComparator.TYPE_AVERAGE, true);
    }
  }
}
