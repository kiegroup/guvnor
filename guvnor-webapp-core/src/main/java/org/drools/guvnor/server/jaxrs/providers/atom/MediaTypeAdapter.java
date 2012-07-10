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

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * TODO remove this file when JBoss AS includes RESTEasy 2.3.4.Final or higher
 */
public class MediaTypeAdapter extends XmlAdapter<String, MediaType>
{
   public MediaType unmarshal(String s) throws Exception
   {
      if (s == null) return null;
      return MediaType.valueOf(s);
   }

   public String marshal(MediaType mediaType) throws Exception
   {
      if (mediaType == null) return null;
      return mediaType.toString();
   }
}
