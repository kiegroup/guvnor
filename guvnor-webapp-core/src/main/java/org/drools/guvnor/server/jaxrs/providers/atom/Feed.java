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

import org.drools.guvnor.server.jaxrs.jaxb.AtomAssetMetadata;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Per RFC4287:</p>
 * <p/>
 * <pre>
 * The "atom:feed" element is the document (i.e., top-level) element of
 * an Atom Feed Document, acting as a container for metadata and data
 * associated with the feed.  Its element children consist of metadata
 * elements followed by zero or more atom:entry child elements.
 * <p/>
 * atomFeed =
 *   element atom:feed {
 *       atomCommonAttributes,
 *       (atomAuthor*
 *        &amp; atomCategory*
 *        &amp; atomContributor*
 *        &amp; atomGenerator?
 *        &amp; atomIcon?
 *        &amp; atomId
 *        &amp; atomLink*
 *        &amp; atomLogo?
 *        &amp; atomRights?
 *        &amp; atomSubtitle?
 *        &amp; atomTitle
 *        &amp; atomUpdated
 *        &amp; extensionElement*),
 *       atomEntry*
 *   }
 * <p/>
 * This specification assigns no significance to the order of atom:entry
 * elements within the feed.
 * <p/>
 * The following child elements are defined by this specification (note
 * that the presence of some of these elements is required):
 * <p/>
 * o  atom:feed elements MUST contain one or more atom:author elements,
 *    unless all of the atom:feed element's child atom:entry elements
 *    contain at least one atom:author element.
 * o  atom:feed elements MAY contain any number of atom:category
 *    elements.
 * o  atom:feed elements MAY contain any number of atom:contributor
 *    elements.
 * o  atom:feed elements MUST NOT contain more than one atom:generator
 *    element.
 * o  atom:feed elements MUST NOT contain more than one atom:icon
 *    element.
 * o  atom:feed elements MUST NOT contain more than one atom:logo
 *    element.
 * o  atom:feed elements MUST contain exactly one atom:id element.
 * o  atom:feed elements SHOULD contain one atom:link element with a rel
 *    attribute value of "self".  This is the preferred URI for
 *    retrieving Atom Feed Documents representing this Atom feed.
 * o  atom:feed elements MUST NOT contain more than one atom:link
 *    element with a rel attribute value of "alternate" that has the
 *    same combination of type and hreflang attribute values.
 * o  atom:feed elements MAY contain additional atom:link elements
 *    beyond those described above.
 * o  atom:feed elements MUST NOT contain more than one atom:rights
 *    element.
 * o  atom:feed elements MUST NOT contain more than one atom:subtitle
 *    element.
 * o  atom:feed elements MUST contain exactly one atom:title element.
 * o  atom:feed elements MUST contain exactly one atom:updated element.
 * <p/>
 * If multiple atom:entry elements with the same atom:id value appear in
 * an Atom Feed Document, they represent the same entry.  Their
 * atom:updated timestamps SHOULD be different.  If an Atom Feed
 * Document contains multiple entries with the same atom:id, Atom
 * Processors MAY choose to display all of them or some subset of them.
 * One typical behavior would be to display only the entry with the
 * latest atom:updated timestamp.
 * </pre>
 *
 * TODO remove this file when JBoss AS includes RESTEasy 2.3.4.Final or higher
 */
@XmlRootElement(namespace = "http://www.w3.org/2005/Atom", name = "feed")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlSeeAlso({AtomAssetMetadata.class})
public class Feed extends Source
{

   private List<Entry> entries = new ArrayList<Entry>();

   @XmlElementRef(namespace = "http://www.w3.org/2005/Atom")
   public List<Entry> getEntries()
   {
      return entries;
   }

}
