/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.jaxrs.providers.atom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.net.URI;

/**
 * <p>Per RFC4287</p>
 * <p/>
 * <pre>
 *  atomGenerator = element atom:generator {
 *     atomCommonAttributes,
 *     attribute uri { atomUri }?,
 *     attribute version { text }?,
 *     text
 *  }
 * </pre>
 *
 * TODO remove this file when JBoss AS includes RESTEasy 2.3.4.Final or higher
 */
@XmlRootElement(name = "generator")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Generator
{
   private URI uri;

   private String version;

   private String text;

   private String language;

   private URI base;

   @XmlAttribute(name = "lang")
   public String getLanguage()
   {
      return language;
   }

   public void setLanguage(String language)
   {
      this.language = language;
   }

   @XmlAttribute
   public URI getBase()
   {
      return base;
   }

   public void setBase(URI base)
   {
      this.base = base;
   }

   @XmlAttribute
   public URI getUri()
   {
      return uri;
   }

   public void setUri(URI uri)
   {
      this.uri = uri;
   }

   @XmlAttribute
   public String getVersion()
   {
      return version;
   }

   public void setVersion(String version)
   {
      this.version = version;
   }

   @XmlValue
   public String getText()
   {
      return text;
   }

   public void setText(String text)
   {
      this.text = text;
   }
}
