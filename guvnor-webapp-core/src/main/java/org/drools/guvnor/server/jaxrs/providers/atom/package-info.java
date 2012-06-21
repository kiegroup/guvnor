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

@XmlSchema(namespace = "http://www.w3.org/2005/Atom",
//        attributeFormDefault = XmlNsForm.QUALIFIED,
        xmlns = {@javax.xml.bind.annotation.XmlNs(prefix = "atom", namespaceURI ="http://www.w3.org/2005/Atom")},
        elementFormDefault = XmlNsForm.QUALIFIED
)
@XmlJavaTypeAdapters(
        {
                @XmlJavaTypeAdapter(type = URI.class, value = UriAdapter.class),
                @XmlJavaTypeAdapter(type = MediaType.class, value = MediaTypeAdapter.class)
        }) package org.drools.guvnor.server.jaxrs.providers.atom;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.net.URI;