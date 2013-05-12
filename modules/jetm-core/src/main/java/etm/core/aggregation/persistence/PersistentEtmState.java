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

package etm.core.aggregation.persistence;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.Map;

/**
 * Represents aggregated state to be persistet.
 *
 * @author void.fm
 * @version $Revision$
 * @since 1.2.0
 */
public class PersistentEtmState implements Externalizable {
  private static final long serialVersionUID = 1L;

  private Date startTime;
  private Date lastResetTime;

  private Map aggregates;

  public Date getStartTime() {
    return new Date(startTime.getTime());
  }

  public void setStartTime(Date aStartTime) {
    startTime = new Date(aStartTime.getTime());
  }

  public Date getLastResetTime() {
    return new Date(lastResetTime.getTime());
  }

  public void setLastResetTime(Date aLastResetTime) {
    lastResetTime = new Date(aLastResetTime.getTime());
  }

  public Map getAggregates() {
    return aggregates;
  }

  public void setAggregates(Map aAggregates) {
    aggregates = aAggregates;
  }

  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(startTime);
    out.writeObject(lastResetTime);
    out.writeObject(aggregates);
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    startTime = (Date) in.readObject();
    lastResetTime = (Date) in.readObject();
    aggregates = (Map) in.readObject();
  }
}
