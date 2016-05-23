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

package etm.core.metadata;

import java.io.Serializable;

/**
 * The aggregator metadata contain information about an
 * aggregator and optionally about a nested aggregator.
 *
 * @author void.fm
 * @version $Revision$
 */
public class AggregatorMetaData implements Serializable {
  private Class implementationClass;
  private String description;
  private boolean buffering;
  private AggregatorMetaData nestedMetaData;

  public AggregatorMetaData(Class aClazz, String aDescription, boolean aBuffering) {
    this(aClazz, aDescription, aBuffering, null);
  }

  public AggregatorMetaData(Class aClazz, String aDescription, boolean aBuffering, AggregatorMetaData aNestingAggregatorMetaData) {
    implementationClass = aClazz;
    description = aDescription;
    buffering = aBuffering;
    nestedMetaData = aNestingAggregatorMetaData;
  }

  /**
   * Returns the aggregator implementation class.
   *
   * @return The class.
   */
  public Class getImplementationClass() {
    return implementationClass;
  }

  /**
   * Returns a short description about the aggregator.
   *
   * @return A short description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns wether the aggregator buffers or not.
   *
   * @return True for buffering, otherwhise false.
   */
  public boolean isBuffering() {
    return buffering;
  }

  /**
   * Returns the metadata for a possibly nested aggregator.
   *
   * @return The metadata for the nested aggregator. May be null.
   */
  public AggregatorMetaData getNestedMetaData() {
    return nestedMetaData;
  }

  public String toString() {
    StringBuilder buffer = new StringBuilder("implementationClass=");
    buffer.append(implementationClass.getName());
    buffer.append(", description='");
    buffer.append(description);
    buffer.append(", buffering=");
    buffer.append(buffering);
    return buffer.toString();
  }

}
