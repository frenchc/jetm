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

import etm.core.util.Log;
import etm.core.util.LogAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A file based persistence store that uses Java Serialization. By default serialized results will be stored
 * at {java.io.tmpdir}/jetm-state.ser.
 *
 * @author void.fm
 * @version $Revision:96 $
 * @since 1.2.0
 */
public class FileSystemPersistenceBackend implements PersistenceBackend {

  private static final LogAdapter LOG = Log.getLog(FileSystemPersistenceBackend.class);

  private File path = new File(System.getProperty("java.io.tmpdir"));
  private String filename = "jetm-state.ser";

  public void setPath(String aPath) {
    path = new File(aPath);
  }

  public void setFilename(String aFilename) {
    filename = aFilename;
  }


  public PersistentEtmState load() {
    PersistentEtmState state = null;
    File file = new File(path, filename);
    if (file.exists() && file.canRead()) {
      ObjectInputStream in = null;
      try {
        in = new ObjectInputStream(new FileInputStream(file));
        state = (PersistentEtmState) in.readObject();
      } catch (Exception e) {
        // ignored
        LOG.warn("Error loading state from file " + file.getAbsolutePath(), e);
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
    return state;
  }

  public void store(PersistentEtmState state) {
    if (!path.exists() && !path.mkdirs()) {
      LOG.warn("Unable to create destination path " + path.getAbsolutePath() + ". Aborting.");
      return;
    }

    File destination = new File(path, filename);
    if (destination.exists()) {
      backupFile(destination);
      if (!destination.delete()) {
        LOG.warn("Unable to delete existing destination target " + destination.getAbsolutePath() + ". Aborting.");
      }
    }

    ObjectOutputStream out = null;

    try {
      out = new ObjectOutputStream(new FileOutputStream(destination));
      out.writeObject(state);
    } catch (Exception e) {
      // ignored
      LOG.warn("Error writing state to file " + destination.getAbsolutePath(), e);
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

  private void backupFile(File aDest) {
    File backup = new File(aDest.getAbsolutePath() + ".saved");

    ObjectInputStream in = null;
    ObjectOutputStream out = null;

    try {
      in = new ObjectInputStream(new FileInputStream(aDest));
      out = new ObjectOutputStream(new FileOutputStream(backup));
      out.writeObject(in.readObject());
    } catch (Exception e) {
      LOG.warn("Error writing backup file " + aDest.getAbsolutePath(), e);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException e) {
        // ingored
      }

      try {
        if (out != null) {
          out.close();
        }
      } catch (IOException e) {
        // ignored
      }
    }

  }
}
