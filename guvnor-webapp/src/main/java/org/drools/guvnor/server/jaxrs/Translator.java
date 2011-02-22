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

import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Link;

public class Translator {

    public static Asset ToAsset(AssetItem a, UriInfo uriInfo) {
        Asset ret = new Asset();
        ret.setId(a.getUUID());
        ret.setType(a.getType());
        ret.setLastmodified(a.getLastModified().getTime());
        ret.setTitle(a.getTitle());
        ret.setCheckInComment(a.getCheckinComment());
        ret.setDescription(a.getDescription());
        UriBuilder builder = uriInfo.getBaseUriBuilder();
        ret.setBinaryLink(
                builder.path("/packages/" + a.getPackage().getName() + "/asset/" + a.getName() + "/binary").build());
        builder = uriInfo.getBaseUriBuilder();
        ret.setSourceLink(
                builder.path("/packages/" + a.getPackage().getName() + "/asset/" + a.getName() + "/source").build());
        ret.setVersion(a.getVersionNumber());
        return ret;
    }

    public static Package ToPackage(PackageItem p, UriInfo uriInfo) {
        Package item = new Package();  
        item.setId(p.getUUID());
        item.setLastmodified(p.getLastModified().getTime());
        item.setTitle(p.getTitle());
        item.setCheckInComment(p.getCheckinComment());
        item.setDescription(p.getDescription());
        UriBuilder builder = uriInfo.getBaseUriBuilder();
        item.setBinaryLink(
                builder.path("/packages/" + p.getName() + "/binary").build());
        builder = uriInfo.getBaseUriBuilder();
        item.setSourceLink(
                builder.path("/packages/" + p.getName() + "/source").build());
        item.setSnapshot(p.getSnapshotName());
        item.setVersion(p.getVersionNumber());
        Iterator<AssetItem> iter = p.getAssets();
        List<Asset> assets = new ArrayList<Asset>();
        while (iter.hasNext()) {
            AssetItem a = iter.next();
            assets.add(ToAsset(a, uriInfo));
        }
        item.setAssets(assets.toArray(new Asset[assets.size()]));
        return item;
    }
    
    public static Entry ToPackageEntry(PackageItem p, UriInfo uriInfo) {
        Content c = new Content();
        c.setType(MediaType.APPLICATION_ATOM_XML_TYPE);                       
        
        Entry e =new Entry();
        e.setContent(c);               
        e.setTitle(p.getTitle());
        e.setUpdated(p.getLastModified().getTime());
        e.setPublished(p.getCreatedDate().getTime());

        Link l = new Link();
        UriBuilder builder = uriInfo.getBaseUriBuilder();
        l.setHref(builder.path("/packages/" + p.getName()).build());
        l.setRel("self");
        
        e.setId(l.getHref());
        e.setPublished(new Date(p.getLastModified().getTimeInMillis()));
        e.setSummary(p.getDescription());
        e.setRights(p.getRights());
        
        Iterator<AssetItem> i = p.getAssets();
        while (i.hasNext()) {
            AssetItem item = i.next();
            Link link = new Link();
            builder = uriInfo.getBaseUriBuilder();
            link.setHref(builder.path("/packages/" + p.getName() + "/asset/" + item.getName()).build());
            link.setTitle(item.getTitle());
            link.setRel("asset");
            e.getLinks().add(link);
        }
        
        return e;
    }
    
    public static Entry ToAssetEntry(AssetItem a, UriInfo uriInfo) {
        Entry e = new Entry();
        e.setTitle(a.getTitle());
        e.setSummary(a.getDescription());
        Content c = new Content();
        c.setType(MediaType.APPLICATION_ATOM_XML_TYPE);                       
        e.setContent(c);
        e.setTitle(a.getTitle());
        
        Link l = new Link();
        UriBuilder builder = uriInfo.getBaseUriBuilder();
        l.setHref(builder.path("/packages/" + a.getPackageName() + "/asset/" +  a.getName()).build());
        l.setRel("self");
        builder = uriInfo.getBaseUriBuilder();
        e.setId(l.getHref());
        e.setPublished(new Date(a.getLastModified().getTimeInMillis()));
        e.setSummary(a.getDescription());
        e.setRights(a.getRights());
        
        l = new Link();
        builder = uriInfo.getBaseUriBuilder();
        l.setHref(builder.path("/packages/" + a.getPackageName() + "/asset/" +  a.getName() + "/binary").build());
        l.setRel("binary");
        e.getLinks().add(l);
        
        l = new Link();
        builder = uriInfo.getBaseUriBuilder();
        l.setHref(builder.path("/packages/" + a.getPackageName() + "/asset/" +  a.getName() + "/source").build());
        l.setRel("source");
        e.getLinks().add(l);                
        
        return e;
    }
}
