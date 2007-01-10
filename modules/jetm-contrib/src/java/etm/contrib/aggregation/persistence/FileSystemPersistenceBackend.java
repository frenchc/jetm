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
package etm.contrib.aggregation.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author void.fm
 * @version $Revision$
 */
public class FileSystemPersistenceBackend implements PersistenceBackend {

  private File persistencePath = new File(System.getProperty("java.io.tmpdir"));
  private String persistenceFile = "jetm-state.ser";

  public FileSystemPersistenceBackend() {
  }

  public FileSystemPersistenceBackend(String aPersistenceFile) {
    persistenceFile = aPersistenceFile;
  }


  public FileSystemPersistenceBackend(File aPersistencePath) {
    persistencePath = aPersistencePath;
  }


  public FileSystemPersistenceBackend(File aPersistencePath, String aPersistenceFile) {
    persistencePath = aPersistencePath;
    persistenceFile = aPersistenceFile;
  }

  public PersistentEtmState load() {
    PersistentEtmState state = null;
    File file = new File(persistencePath, persistenceFile);
    if (file.exists()) {
      if (file.canRead()) {
        ObjectInputStream in = null;
        try {
          in = new ObjectInputStream(new FileInputStream(file));
          state = (PersistentEtmState) in.readObject();
        } catch (Exception e) {
          // ignored
          e.printStackTrace();
        } finally {
          if (in != null) {
            try {
              in.close();
            } catch (IOException e) {
              // ignored
            }
          }
        }
      }
    }
    return state;
  }

  public void store(PersistentEtmState state) {
    if (!persistencePath.exists()) {
      persistencePath.mkdirs();
    }

    File tmpFile = new File(persistencePath, persistenceFile + ".tmp");

    if (tmpFile.exists()) {
      tmpFile.delete();
    }

    ObjectOutputStream out = null;
    try {
      out = new ObjectOutputStream(new FileOutputStream(tmpFile));
      out.writeObject(state);
      File dest = new File(persistencePath, persistenceFile);
      if (dest.exists()) {
        dest.delete();
      }
      tmpFile.renameTo(dest);
    } catch (Exception e) {
      // ignored
      e.printStackTrace();
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          // ignored
        }
      }
    }
  }
}
