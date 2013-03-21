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

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;

/**
 * If invoked within the context of a JAX-RS call, it will automatically build a
 * URI based the base URI of the JAX-RS application.  Same URI as UriInfo.getRequestUri().
 *
 * TODO remove this file when JBoss AS includes RESTEasy 2.3.4.Final or higher
 */
@XmlRootElement(name = "link")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class RelativeLink extends Link
{
   public RelativeLink()
   {
   }

   public RelativeLink(String rel, String relativeLink)
   {
      UriInfo uriInfo = ResteasyProviderFactory.getContextData(UriInfo.class);
      if (uriInfo == null)
         throw new IllegalStateException("This constructor must be called in the context of a JAX-RS request");
      URI uri = uriInfo.getAbsolutePathBuilder().path(relativeLink).build();
      setHref(uri);
      setRel(rel);
   }

   public RelativeLink(String rel, String relativeLink, MediaType mediaType)
   {
      this(rel, relativeLink);
      this.setType(mediaType);
   }

   public RelativeLink(String rel, String relativeLink, String mediaType)
   {
      this(rel, relativeLink);
      this.setType(MediaType.valueOf(mediaType));
   }
}
