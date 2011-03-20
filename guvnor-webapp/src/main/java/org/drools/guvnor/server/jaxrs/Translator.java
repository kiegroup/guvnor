/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.jaxrs;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Content.Type;
import org.apache.abdera.model.ExtensibleElement;
import org.drools.guvnor.server.jaxrs.jaxb.*;
import org.drools.guvnor.server.jaxrs.jaxb.Package;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;

import java.net.URI;
import java.util.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Link;

public class Translator {
    public static String NS = "";
    public static QName METADATA = new QName(NS, "metadata");
    static QName VALUE = new QName(NS, "value");
    
    public static Asset ToAsset(AssetItem a, UriInfo uriInfo) {
        AssetMetadata metadata = new AssetMetadata();
        metadata.setUuid(a.getUUID());
        metadata.setTitle(a.getTitle());
        metadata.setLastModified(a.getLastModified().getTime());
        metadata.setCreated(a.getCreatedDate().getTime());
        metadata.setCreatedBy(a.getCreator());
        metadata.setDisabled(a.getDisabled());
        metadata.setFormat(a.getFormat());
        metadata.setNote(a.getContent());
        List<CategoryItem> categories = a.getCategories();
        String[] cats = new String [categories.size()];
        int counter = 0;
        for (CategoryItem c : categories) {
            cats [ counter++ ] = c.getName();
        }

        Asset ret = new Asset();
        ret.setMetadata(metadata);
        ret.setType(a.getType());
        ret.setCheckInComment(a.getCheckinComment());
        ret.setDescription(a.getDescription());
        UriBuilder builder = uriInfo.getBaseUriBuilder();
        ret.setRefLink(
            builder.path("/packages/" + a.getPackage().getName() + "/asset/" + a.getName()).build());
        builder = uriInfo.getBaseUriBuilder();
        ret.setBinaryLink(
                builder.path("/packages/" + a.getPackage().getName() + "/asset/" + a.getName() + "/binary").build());
        builder = uriInfo.getBaseUriBuilder();
        ret.setSourceLink(
                builder.path("/packages/" + a.getPackage().getName() + "/asset/" + a.getName() + "/source").build());
        ret.setVersion(a.getVersionNumber());
        return ret;
    }

    public static Package ToPackage(PackageItem p, UriInfo uriInfo) {
        PackageMetadata metadata = new PackageMetadata();
        metadata.setUuid(p.getUUID());
        metadata.setCreated(p.getCreatedDate().getTime());
        metadata.setLastModified(p.getLastModified().getTime());
        metadata.setLastContributor(p.getLastContributor());
        metadata.setState((p.getState() != null) ? p.getState().getName() : "");

        Package ret = new Package();
        ret.setMetadata(metadata);
        ret.setVersion(p.getVersionNumber());
        ret.setTitle(p.getTitle());
        ret.setCheckInComment(p.getCheckinComment());
        ret.setDescription(p.getDescription());

        UriBuilder builder = uriInfo.getBaseUriBuilder();
        ret.setBinaryLink(
                builder.path("/packages/" + p.getName() + "/binary").build());
        builder = uriInfo.getBaseUriBuilder();
        ret.setSourceLink(
                builder.path("/packages/" + p.getName() + "/source").build());
        //ret.setSnapshot(p.getSnapshotName());
        ret.setVersion(p.getVersionNumber());
        Iterator<AssetItem> iter = p.getAssets();
        Set<URI> assets = new HashSet<URI>();
        while (iter.hasNext()) {
            AssetItem a = iter.next();
            Asset asset = ToAsset(a, uriInfo);
            assets.add(asset.getRefLink());
        }

        ret.setAssets(assets);
        return ret;
    }
    
    public static org.apache.abdera.model.Entry ToPackageEntryAbdera(PackageItem p, UriInfo uriInfo) {
        UriBuilder base;
        if(p.isHistoricalVersion()) {
        	base = uriInfo.getBaseUriBuilder().path("packages").path(p.getName()).path("versions").path(Long.toString(p.getVersionNumber()));
        } else {
        	base = uriInfo.getBaseUriBuilder().path("packages").path(p.getName());
        }
        
        Factory factory = Abdera.getNewFactory();

        org.apache.abdera.model.Entry e = factory.getAbdera().newEntry();
        e.setTitle(p.getTitle());
        e.setSummary(p.getDescription());
        e.setPublished(new Date(p.getLastModified().getTimeInMillis()));
        e.setBaseUri(base.clone().build().toString());       
        e.addContributor(p.getLastContributor());

        e.setId(base.clone().build().toString());
        
        Iterator<AssetItem> i = p.getAssets();
        while (i.hasNext()) {
            AssetItem item = i.next();
            org.apache.abdera.model.Link l = factory.newLink();
            l.setHref((base.clone().path("assets").path(item.getName())).build().toString());
            l.setTitle(item.getTitle());
            l.setRel("asset");
            e.addLink(l);
        }

        //generate meta data
        ExtensibleElement extension = e.addExtension(METADATA);
       	QName extQName = new QName(NS, "archived");
        ExtensibleElement childExtension = extension.addExtension(extQName);
        //childExtension.setAttributeValue("type", ArtifactsRepository.METADATA_TYPE_STRING);
        childExtension.addSimpleExtension(VALUE, p.isArchived()?"true":"false");
        
       	extQName = new QName(NS, "UUID");
        childExtension = extension.addExtension(extQName);
        childExtension.addSimpleExtension(VALUE, p.getUUID());
       	
       	extQName = new QName(NS, "state");
        childExtension = extension.addExtension(extQName);
        childExtension.addSimpleExtension(VALUE, p.getState()== null?"" : p.getState().getName());

        org.apache.abdera.model.Content content = factory.newContent();
        content.setSrc(base.clone().path("binary").build().toString());
        content.setMimeType("application/octet-stream");
        content.setContentType(Type.MEDIA);
		e.setContentElement(content);

		return e;
    }
    
    public static Entry ToPackageEntry(PackageItem p, UriInfo uriInfo) {
        UriBuilder base;
        if(p.isHistoricalVersion()) {
        	base = uriInfo.getBaseUriBuilder().path("packages").path(p.getName()).path("versions").path(Long.toString(p.getVersionNumber()));
        } else {
        	base = uriInfo.getBaseUriBuilder().path("packages").path(p.getName());
        }        

        //NOTE: Entry extension is not supported in RESTEasy. We need to either use Abdera or get extension 
        //supported in RESTEasy
/*        PackageMetadata metadata = new PackageMetadata();
        metadata.setUuid(p.getUUID());
        metadata.setCreated(p.getCreatedDate().getTime());
        metadata.setLastModified(p.getLastModified().getTime());
        metadata.setLastContributor(p.getLastContributor());
        //c.setJAXBObject(metadata);
*/
        Entry e =new Entry();
        e.setTitle(p.getTitle());
        e.setSummary(p.getDescription());
        e.setPublished(new Date(p.getLastModified().getTimeInMillis()));
        e.setBase(base.clone().build());

/*        Link l = new Link();
        l.setHref(base.build());
        l.setRel("self");*/
        e.setId(base.clone().build());
        
        Iterator<AssetItem> i = p.getAssets();
        while (i.hasNext()) {
            AssetItem item = i.next();
            Link link = new Link();
            link.setHref((base.clone().path("assets").path(item.getName())).build());
            link.setTitle(item.getTitle());
            link.setRel("asset");
            e.getLinks().add(link);
        }
        
        Content c = new Content();
        c.setType(MediaType.APPLICATION_OCTET_STREAM_TYPE);
        c.setSrc(base.clone().path("binary").build());       	
        e.setContent(c);
        
        return e;
    }
    
    public static Entry ToAssetEntry(AssetItem a, UriInfo uriInfo) {
        Entry e = new Entry();
        e.setTitle(a.getTitle());
        e.setSummary(a.getDescription());

        Content content = new Content();
        content.setType(MediaType.APPLICATION_XML_TYPE);
        AssetMetadata metadata = new AssetMetadata();
        metadata.setUuid(a.getUUID());
        metadata.setTitle(a.getTitle());
        metadata.setLastModified(a.getLastModified().getTime());
        metadata.setCreated(a.getCreatedDate().getTime());
        metadata.setCreatedBy(a.getCreator());
        metadata.setDisabled(a.getDisabled());
        metadata.setFormat(a.getFormat());
        metadata.setNote(a.getContent());
        metadata.setState((a.getState() != null) ? a.getState().getName() : "");
        content.setJAXBObject(metadata);
        e.setContent(content);

        List<CategoryItem> categories = a.getCategories();
        String[] cats = new String [categories.size()];
        int counter = 0;
        for (CategoryItem c : categories) {
            cats [ counter++ ] = c.getName();
        }

        UriBuilder builder = uriInfo.getBaseUriBuilder();
        Link l = new Link();
        l.setHref(builder.path("/packages/" + a.getPackageName() + "/asset/" +  a.getName()).build());
        l.setRel("self");
        builder = uriInfo.getBaseUriBuilder();
        e.setId(l.getHref());
        e.setPublished(new Date(a.getLastModified().getTimeInMillis()));
        e.setSummary(a.getDescription());
        e.setRights(a.getRights());

        builder = uriInfo.getBaseUriBuilder();
        l = new Link();
        l.setHref(builder.path("/packages/" + a.getPackageName() + "/asset/" +  a.getName() + "/binary").build());
        l.setRel("binary");
        e.getLinks().add(l);

        builder = uriInfo.getBaseUriBuilder();
        l = new Link();
        l.setHref(builder.path("/packages/" + a.getPackageName() + "/asset/" +  a.getName() + "/source").build());
        l.setRel("source");
        e.getLinks().add(l);                
        
        return e;
    }
}
