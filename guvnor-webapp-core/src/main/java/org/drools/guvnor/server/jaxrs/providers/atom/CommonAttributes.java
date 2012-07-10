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
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Attributes common across all atom types
 *
 * TODO remove this file when JBoss AS includes RESTEasy 2.3.4.Final or higher
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CommonAttributes
{
   private String language;
   private URI base;


   private Map extensionAttributes = new HashMap();

   @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
   public String getLanguage()
   {
      return language;
   }

   public void setLanguage(String language)
   {
      this.language = language;
   }

   @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace")
   public URI getBase()
   {
      return base;
   }

   public void setBase(URI base)
   {
      this.base = base;
   }

   @XmlAnyAttribute
   public Map getExtensionAttributes()
   {
      return extensionAttributes;
   }
}
