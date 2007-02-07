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
package etm.core.configuration;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * Abstract configuration factory.
 *
 * @author void.fm
 * @version $Revision@
 */
abstract class XmlConfigParser {

  public static final String PUBLIC_DTD_1_0 = "-// void.fm //DTD JETM Config 1.0//EN";
  public static final String JETM_CONFIG_1_0_DTD_NAME = "jetm_config_1_0.dtd";

  public static final String PUBLIC_DTD_1_2 = "-// void.fm //DTD JETM Config 1.2//EN";
  public static final String JETM_CONFIG_1_2_DTD_NAME = "jetm_config_1_2.dtd";


  public static EtmMonitorConfig extractConfig(InputStream inStream) throws Exception {
    Document document = load(inStream);

    XmlConfigParser parser = getParser(document.getDoctype().getPublicId());
    return parser.parse(document);
  }

  public abstract EtmMonitorConfig parse(Document aDocument);

  protected static XmlConfigParser getParser(String publicId) {
    if (PUBLIC_DTD_1_0.equals(publicId)) {
      return new Xml10ConfigParser();
    } else if (PUBLIC_DTD_1_2.equals(publicId)) {
      return new Xml10ConfigParser();
    } else {
      throw new EtmConfigurationException("Unsupported public ID " + publicId);
    }
  }

  protected static Document load(InputStream inStream) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = factory.newDocumentBuilder();
    documentBuilder.setEntityResolver(new EntityResolver() {
      public InputSource resolveEntity(String string, String string1) throws SAXException {
        if (PUBLIC_DTD_1_0.equals(string)) {
          return new InputSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(JETM_CONFIG_1_0_DTD_NAME));
        } else if (PUBLIC_DTD_1_2.equals(string)) {
          return new InputSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(JETM_CONFIG_1_2_DTD_NAME));
        }
        throw new SAXException("Unsupported entity " + string);
      }
    });

    return documentBuilder.parse(inStream);

  }


}
