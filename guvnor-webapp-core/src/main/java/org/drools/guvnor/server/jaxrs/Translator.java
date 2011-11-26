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
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.drools.guvnor.server.jaxrs.jaxb.Asset;
import org.drools.guvnor.server.jaxrs.jaxb.AssetMetadata;
import org.drools.guvnor.server.jaxrs.jaxb.Category;
import org.drools.guvnor.server.jaxrs.jaxb.Package;
import org.drools.guvnor.server.jaxrs.jaxb.PackageMetadata;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;
import java.net.URI;
import java.util.*;


public class Translator {
    public static final String NS = "";
    public static final QName METADATA = new QName(NS, "metadata");
    public static final QName VALUE = new QName(NS, "value");
    public static final QName ARCHIVED = new QName(NS, "archived");
    public static final QName UUID = new QName(NS, "uuid");
    public static final QName STATE = new QName(NS, "state");
    public static final QName FORMAT = new QName(NS, "format");
    public static final QName CATEGORIES = new QName(NS, "categories");

    public static Category toCategory(CategoryItem categoryItem, UriInfo uriInfo) {
        Category category = new Category();
        category.setPath(categoryItem.getFullPath());
        return category;
    }

    public static Asset toAsset(AssetItem a, UriInfo uriInfo) {
        AssetMetadata metadata = new AssetMetadata();
        metadata.setUuid(a.getUUID());
        metadata.setTitle(a.getTitle());
        metadata.setLastModified(a.getLastModified().getTime());
        metadata.setCreated(a.getCreatedDate().getTime());
        metadata.setCreatedBy(a.getCreator());
        metadata.setDisabled(a.getDisabled());
        metadata.setFormat(a.getFormat());
        metadata.setNote("<![CDATA[ " + a.getCheckinComment() + " ]]>");
        List<CategoryItem> categories = a.getCategories();
        //TODO: Is this a bug since cat's are never assigned to metadata after this?
        String[] cats = new String[categories.size()];
        int counter = 0;
        for (CategoryItem c : categories) {
            cats[counter++] = c.getName();
        }

        Asset ret = new Asset();
        ret.setMetadata(metadata);
        ret.setType(a.getType());
        ret.setCheckInComment(a.getCheckinComment());
        ret.setDescription(a.getDescription());
        ret.setRefLink(uriInfo.getBaseUriBuilder()
                .path("/packages/{packageName}/assets/{assetName}")
                .build(a.getPackage().getName(), a.getName()));
        ret.setBinaryLink(uriInfo.getBaseUriBuilder()
                .path("/packages/{packageName}/assets/{assetName}/binary")
                .build(a.getPackage().getName(), a.getName()));
        ret.setSourceLink(uriInfo.getBaseUriBuilder()
                .path("/packages/{packageName}/assets/{assetName}/source")
                .build(a.getPackage().getName(), a.getName()));
        ret.setVersion(a.getVersionNumber());
        return ret;
    }

    public static Package toPackage(PackageItem p, UriInfo uriInfo) {
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

        ret.setBinaryLink(uriInfo.getBaseUriBuilder()
                .path("/packages/{packageName}/binary")
                .build(p.getName()));
        ret.setSourceLink(uriInfo.getBaseUriBuilder()
                .path("/packages/{packageName}/source")
                .build(p.getName()));
        //ret.setSnapshot(p.getSnapshotName());
        ret.setVersion(p.getVersionNumber());
        Iterator<AssetItem> iter = p.getAssets();
        Set<URI> assets = new HashSet<URI>();
        while (iter.hasNext()) {
            AssetItem a = iter.next();
            Asset asset = toAsset(a, uriInfo);
            assets.add(asset.getRefLink());
        }

        ret.setAssets(assets);
        return ret;
    }

    public static Entry toPackageEntryAbdera(PackageItem p, UriInfo uriInfo) {
        URI baseURL;
        if (p.isHistoricalVersion()) {
            baseURL = uriInfo.getBaseUriBuilder()
                    .path("packages/{packageName}/versions/{version}").build(p.getName(), Long.toString(p.getVersionNumber()));
        } else {
            baseURL = uriInfo.getBaseUriBuilder().path("packages/{packageName}").build(p.getName());
        }

        Factory factory = Abdera.getNewFactory();

        org.apache.abdera.model.Entry e = factory.getAbdera().newEntry();
        e.setTitle(p.getTitle());
        e.setSummary(p.getDescription());
        e.setPublished(new Date(p.getLastModified().getTimeInMillis()));
        e.setBaseUri(baseURL.toString());
        e.addAuthor(p.getLastContributor());

        e.setId(baseURL.toString());

        Iterator<AssetItem> i = p.getAssets();
        while (i.hasNext()) {
            AssetItem item = i.next();
            org.apache.abdera.model.Link l = factory.newLink();

            l.setHref(UriBuilder.fromUri(baseURL).path("assets/{assetName}").build(item.getName()).toString());
            l.setTitle(item.getTitle());
            l.setRel("asset");
            e.addLink(l);
        }

        //generate meta data
        ExtensibleElement extension = e.addExtension(METADATA);
        ExtensibleElement childExtension = extension.addExtension(ARCHIVED);
        //childExtension.setAttributeValue("type", ArtifactsRepository.METADATA_TYPE_STRING);
        childExtension.addSimpleExtension(VALUE, p.isArchived() ? "true" : "false");

        childExtension = extension.addExtension(UUID);
        childExtension.addSimpleExtension(VALUE, p.getUUID());

        childExtension = extension.addExtension(STATE);
        childExtension.addSimpleExtension(VALUE, p.getState() == null ? "" : p.getState().getName());

        org.apache.abdera.model.Content content = factory.newContent();
        content.setSrc(UriBuilder.fromUri(baseURL).path("binary").build().toString());
        content.setMimeType("application/octet-stream");
        content.setContentType(Type.MEDIA);
        e.setContentElement(content);

        return e;
    }

    /*    public static Entry ToPackageEntry(PackageItem p, UriInfo uriInfo) {
        UriBuilder base;
        if(p.isHistoricalVersion()) {
            base = uriInfo.getBaseUriBuilder().path("packages").path(p.getName()).path("versions").path(Long.toString(p.getVersionNumber()));
        } else {
            base = uriInfo.getBaseUriBuilder().path("packages").path(p.getName());
        }        

        //NOTE: Entry extension is not supported in RESTEasy. We need to either use Abdera or get extension 
        //supported in RESTEasy
        //PackageMetadata metadata = new PackageMetadata();
        //metadata.setUuid(p.getUUID());
        //metadata.setCreated(p.getCreatedDate().getTime());
        //metadata.setLastModified(p.getLastModified().getTime());
        //metadata.setLastContributor(p.getLastContributor());
        //c.setJAXBObject(metadata);

        Entry e =new Entry();
        e.setTitle(p.getTitle());
        e.setSummary(p.getDescription());
        e.setPublished(new Date(p.getLastModified().getTimeInMillis()));
        e.setBase(base.clone().build());

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
    }*/
    public static Entry toAssetEntryAbdera(AssetItem a, UriInfo uriInfo) {
        URI baseURL;
        if (a.isHistoricalVersion()) {
            baseURL = uriInfo.getBaseUriBuilder()
                    .path("packages/{packageName}/assets/{assetName}/versions/{version}")
                    .build(a.getPackageName(), a.getName(), Long.toString(a.getVersionNumber()));
        } else {
            baseURL = uriInfo.getBaseUriBuilder()
                    .path("packages/{packageName}/assets/{assetName}")
                    .build(a.getPackageName(), a.getName());
        }

        Factory factory = Abdera.getNewFactory();

        org.apache.abdera.model.Entry e = factory.getAbdera().newEntry();
        e.setTitle(a.getTitle());
        e.setSummary(a.getDescription());
        e.setPublished(new Date(a.getLastModified().getTimeInMillis()));
        e.setBaseUri(baseURL.toString());
        e.addAuthor(a.getLastContributor());

        e.setId(baseURL.toString());

/*        Iterator<AssetItem> i = p.getAssets();
while (i.hasNext()) {
    AssetItem item = i.next();
    org.apache.abdera.model.Link l = factory.newLink();
    l.setHref((base.clone().path("assets").path(item.getName())).build().toString());
    l.setTitle(item.getTitle());
    l.setRel("asset");
    e.addLink(l);
}*/

        //generate meta data
        ExtensibleElement extension = e.addExtension(METADATA);
        ExtensibleElement childExtension = extension.addExtension(ARCHIVED);
        //childExtension.setAttributeValue("type", ArtifactsRepository.METADATA_TYPE_STRING);
        childExtension.addSimpleExtension(VALUE, a.isArchived() ? "true" : "false");

        childExtension = extension.addExtension(UUID);
        childExtension.addSimpleExtension(VALUE, a.getUUID());

        childExtension = extension.addExtension(STATE);
        childExtension.addSimpleExtension(VALUE, a.getState() == null ? "" : a.getState().getName());

        childExtension = extension.addExtension(FORMAT);
        childExtension.addSimpleExtension(VALUE, a.getFormat());

        List<CategoryItem> categories = a.getCategories();
        childExtension = extension.addExtension(CATEGORIES);
        for (CategoryItem c : categories) {
            childExtension.addSimpleExtension(VALUE, c.getName());
        }

        org.apache.abdera.model.Content content = factory.newContent();
        content.setSrc(UriBuilder.fromUri(baseURL).path("binary").build().toString());
        content.setMimeType("application/octet-stream");
        content.setContentType(Type.MEDIA);
        e.setContentElement(content);

        return e;
    }

/*    public static Entry ToAssetEntry(AssetItem a, UriInfo uriInfo) {
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
    }*/
}
