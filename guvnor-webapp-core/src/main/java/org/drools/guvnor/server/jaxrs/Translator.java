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

import org.drools.guvnor.server.jaxrs.jaxb.*;
import org.drools.guvnor.server.jaxrs.jaxb.Package;
import org.drools.guvnor.server.jaxrs.providers.atom.Content;
import org.drools.guvnor.server.jaxrs.providers.atom.Entry;
import org.drools.guvnor.server.jaxrs.providers.atom.Link;
import org.drools.guvnor.server.jaxrs.providers.atom.Person;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.ModuleItem;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
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
    public static final QName VERSION_NUMBER = new QName(NS, "versionNumber");
    public static final QName CHECKIN_COMMENT = new QName(NS, "checkinComment");

    public static Category toCategory(CategoryItem categoryItem, UriInfo uriInfo) {
        Category category = new Category();
        category.setPath(categoryItem.getFullPath());
        category.setRefLink(uriInfo.getBaseUriBuilder()
                .path("/categories/{categoryPath}")
                .build(categoryItem.getFullPath()));
        // TODO https://issues.jboss.org/browse/GUVNOR-1802
//        category.setRefLink(uriInfo.getBaseUriBuilder()
//                .path("categories").segment(categoryItem.getFullPath())
//                .build());
        return category;
    }

    public static Asset toAsset(AssetItem a, UriInfo uriInfo) {
        AssetMetadata metadata = new AssetMetadata();
        metadata.setUuid(a.getUUID());
        metadata.setCreated(a.getCreatedDate().getTime());
        metadata.setDisabled(a.getDisabled());
        metadata.setFormat(a.getFormat());
        metadata.setState(a.getState() == null ? "" : a.getState().getName());
        metadata.setNote("<![CDATA[ " + a.getCheckinComment() + " ]]>");
        metadata.setCheckInComment(a.getCheckinComment());
        metadata.setVersionNumber(a.getVersionNumber());
        List<CategoryItem> categories = a.getCategories();
        //TODO: Is this a bug since cat's are never assigned to metadata after this?
        String[] cats = new String[categories.size()];
        int counter = 0;
        for (CategoryItem c : categories) {
            cats[counter++] = c.getName();
        }

        Asset ret = new Asset();
        ret.setTitle(a.getTitle());
        ret.setBinaryContentAttachmentFileName(a.getBinaryContentAttachmentFileName());
        ret.setPublished(a.getLastModified().getTime());
        ret.setAuthor(a.getLastContributor());
        ret.setMetadata(metadata);
        ret.setDescription(a.getDescription());
        ret.setRefLink(uriInfo.getBaseUriBuilder()
                .path("/packages/{packageName}/assets/{assetName}")
                .build(a.getModule().getName(), a.getName()));
        ret.setBinaryLink(uriInfo.getBaseUriBuilder()
                .path("/packages/{packageName}/assets/{assetName}/binary")
                .build(a.getModule().getName(), a.getName()));
        ret.setSourceLink(uriInfo.getBaseUriBuilder()
                .path("/packages/{packageName}/assets/{assetName}/source")
                .build(a.getModule().getName(), a.getName()));
        return ret;
    }

    public static Package toPackage(ModuleItem p, UriInfo uriInfo) {
        PackageMetadata metadata = new PackageMetadata();
        metadata.setUuid(p.getUUID());
        metadata.setCreated(p.getCreatedDate().getTime());
        metadata.setState((p.getState() != null) ? p.getState().getName() : "");
        metadata.setArchived(p.isArchived());
        metadata.setVersionNumber(p.getVersionNumber());
        metadata.setCheckinComment(p.getCheckinComment());
        
        Package ret = new Package();
        ret.setMetadata(metadata);
        ret.setTitle(p.getTitle());
        ret.setAuthor(p.getLastContributor());
        ret.setPublished(p.getLastModified().getTime());
        ret.setDescription(p.getDescription());

        ret.setBinaryLink(uriInfo.getBaseUriBuilder()
                .path("/packages/{packageName}/binary")
                .build(p.getName()));
        ret.setSourceLink(uriInfo.getBaseUriBuilder()
                .path("/packages/{packageName}/source")
                .build(p.getName()));
        //ret.setSnapshot(p.getSnapshotName());

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

    public static Entry toPackageEntryAbdera(ModuleItem p, UriInfo uriInfo) {
        URI baseUri;
        if (p.isHistoricalVersion()) {
            baseUri = uriInfo.getBaseUriBuilder()
                    .path("packages/{packageName}/versions/{version}").build(p.getName(), Long.toString(p.getVersionNumber()));
        } else {
            baseUri = uriInfo.getBaseUriBuilder().path("packages/{packageName}").build(p.getName());
        }

        Entry e = new Entry();
        e.setTitle(p.getTitle());
        e.setSummary(p.getDescription());
        e.setPublished(new Date(p.getLastModified().getTimeInMillis()));
        e.setBase(baseUri);
        e.getAuthors().add(new Person(p.getLastContributor()));

        e.setId(baseUri);

        Iterator<AssetItem> i = p.getAssets();
        while (i.hasNext()) {
            AssetItem item = i.next();
            Link l = new Link();

            l.setHref(UriBuilder.fromUri(baseUri).path("assets/{assetName}").build(item.getName()));
            l.setTitle(item.getTitle());
            l.setRel("asset");
            e.getLinks().add(l);
        }
        //generate meta data
        AtomPackageMetadata metaData = (AtomPackageMetadata) e.getAnyOtherJAXBObject();
        if (metaData == null) {
            metaData = new AtomPackageMetadata();
        }
        metaData.setArchived(p.isArchived());
        metaData.setUuid(p.getUUID());
        metaData.setState(p.getState() == null ? "" : p.getState().getName());
        metaData.setVersionNumber(p.getVersionNumber());
        metaData.setCheckinComment(p.getCheckinComment());

        e.setAnyOtherJAXBObject(metaData);
        Content content = new Content();
        content.setSrc(UriBuilder.fromUri(baseUri).path("binary").build());
        content.setType(MediaType.APPLICATION_OCTET_STREAM_TYPE);
//        content.setContentType(Type.MEDIA); // TODO remove me if not it's base64 encoded fine
        e.setContent(content);
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
        URI baseUri;
        if (a.isHistoricalVersion()) {
            baseUri = uriInfo.getBaseUriBuilder()
                    .path("packages/{packageName}/assets/{assetName}/versions/{version}")
                    .build(a.getModuleName(), a.getName(), Long.toString(a.getVersionNumber()));
        } else {
            baseUri = uriInfo.getBaseUriBuilder()
                    .path("packages/{packageName}/assets/{assetName}")
                    .build(a.getModuleName(), a.getName());
        }

        Entry e = new Entry();
        e.setTitle(a.getTitle());
        e.setSummary(a.getDescription());
        e.setPublished(new Date(a.getLastModified().getTimeInMillis()));
        e.setBase(baseUri);
        e.getAuthors().add(new Person(a.getLastContributor()));

        e.setId(baseUri);
        try {
            AtomAssetMetadata atomAssetMetadata  = e.getAnyOtherJAXBObject(AtomAssetMetadata.class);
            if (atomAssetMetadata == null) {
                atomAssetMetadata = new AtomAssetMetadata();
            }
            atomAssetMetadata.setArchived(a.isArchived());
            atomAssetMetadata.setUuid(a.getUUID());
            atomAssetMetadata.setState(a.getState() == null ? "" : a.getState().getName());
            atomAssetMetadata.setFormat(a.getFormat());
            atomAssetMetadata.setVersionNumber(a.getVersionNumber());
            atomAssetMetadata.setCheckinComment(a.getCheckinComment());
            String[] categories = new String[a.getCategories().size()];
            int i = 0;
            for (CategoryItem cItem : a.getCategories()) {
                categories[i] = cItem.getName();
                i++;
            }
            atomAssetMetadata.setCategories(categories);
            e.setAnyOtherJAXBObject(atomAssetMetadata);
        } catch (Exception ex) {
            throw new WebServiceException(ex);
        }
        Content content = new Content();
        content.setSrc(UriBuilder.fromUri(baseUri).path("binary").build());
        content.setType(MediaType.APPLICATION_OCTET_STREAM_TYPE);
//        content.setContentType(Type.MEDIA); // TODO remove me if not it's base64 encoded fine
        e.setContent(content);

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
