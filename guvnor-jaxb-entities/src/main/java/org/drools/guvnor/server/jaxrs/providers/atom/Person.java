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
import javax.xml.bind.annotation.XmlElement;
import java.net.URI;

/**
 * <p>Per RFC4287:</p>
 * <p/>
 * <pre>
 *   A Person construct is an element that describes a person,
 *   corporation, or similar entity (hereafter, 'person').
 * <p/>
 *   atomPersonConstruct =
 *     atomCommonAttributes,
 *     (element atom:name { text }
 *      &amp; element atom:uri { atomUri }?
 *      &amp; element atom:email { atomEmailAddress }?
 *      &amp; extensionElement*)
 * <p/>
 * </pre>
 *
 * TODO remove this file when JBoss AS includes RESTEasy 2.3.4.Final or higher
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Person extends CommonAttributes
{
   private String name;

   private URI uri;

   private String email;

   public Person()
   {
   }

   public Person(String name)
   {
      this.name = name;
   }

   @XmlElement
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @XmlElement
   public URI getUri()
   {
      return uri;
   }

   public void setUri(URI uri)
   {
      this.uri = uri;
   }

   @XmlElement
   public String getEmail()
   {
      return email;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }
}
