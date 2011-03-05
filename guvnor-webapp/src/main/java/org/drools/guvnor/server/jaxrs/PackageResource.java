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

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.server.files.RepositoryServlet;
import org.drools.guvnor.server.jaxrs.jaxb.*;
import org.drools.guvnor.server.jaxrs.jaxb.Package;
import org.jboss.resteasy.plugins.providers.atom.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.drools.repository.*;
import org.jboss.seam.annotations.Name;

import javax.ws.rs.*;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import static org.drools.guvnor.server.jaxrs.Translator.*;

/**
 * Contract:  Package names and asset names within a package namespace
 * must be unique.  REST API avoids use of asset UUIDs through this
 * contract.
 */
@Name("PackageResource")
@Path("/packages")
public class PackageResource extends Resource {

    @GET
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getPackagesAsFeed() {
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        Feed f = new Feed();
        PackageIterator iter = repository.listPackages();
        while (iter.hasNext()) {
            try {
                PackageItem item = iter.next();
                Entry e = new Entry();
                e.setTitle(item.getName());                                
                Link l = new Link();
                l.setHref(builder.path("/packages/ " + item.getName()).build());
                e.getLinks().add(l);
                f.getEntries().add(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return f;
    }

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry createPackageFromInputAndReturnAsEntry(InputStream is, @Context UriInfo uriInfo) throws IOException,
            DroolsParserException
    {
        /* Passes the DRL to the FileManagerUtils and has it import the asset as a package */
        String packageName = RepositoryServlet.getFileManager().importClassicDRL (is, null);
        Entry e = ToPackageEntry(repository.loadPackage(packageName), uriInfo);
        return e;
    }

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Package createPackageFromInputAndReturnAsJaxB(InputStream is, @Context UriInfo uriInfo) throws IOException,
            DroolsParserException
    {
        /* Passes the DRL to the FileManagerUtils and has it import the asset as a package */
        String packageName = RepositoryServlet.getFileManager().importClassicDRL (is, null);
        Package p = ToPackage(repository.loadPackage(packageName), uriInfo);
        return p;
    }

    @POST
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    public void createPackageFromAtom (Entry entry) {
        PackageService.createPackage(entry.getTitle(),entry.getSummary());
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void createPackageFromJAXB (Package p) {
        PackageService.createPackage(p.getTitle(), p.getDescription());
    }


    @GET
    @Path("{packageName}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry getPackageAsEntryByName(@PathParam("packageName") String packageName) {
        return ToPackageEntry(repository.loadPackage(packageName), uriInfo);
    }

    @GET
    @Path("{packageName}/assets")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getAssetsAsAtom(@PathParam("packageName") String packageName) {
        Feed feed = new Feed();
        PackageItem p = repository.loadPackage(packageName);
        feed.setTitle(p.getTitle() + "-asset-feed");
        Iterator<AssetItem> iter = p.getAssets();
        while (iter.hasNext())
            feed.getEntries().add(ToAssetEntry(iter.next(), uriInfo));
        return feed;
    }

    @PUT
    @Path("{packageName}")
    @Consumes (MediaType.APPLICATION_ATOM_XML)
    public void updatePackageFromAtom (@PathParam("packageName") String packageName, Entry entry) {
        PackageItem p = repository.loadPackage(packageName);
        p.checkout();
        p.updateTitle(entry.getTitle());
        p.updateDescription(entry.getSummary());
        /* TODO: add more updates to package item from JSON */
        p.checkin("Update from ATOM.");
        repository.save();
    }

    @DELETE
    @Path("{packageName}")
    public void archivePackage (@PathParam("packageName") String packageName) {
        PackageItem p = repository.loadPackage(packageName);
        PackageService.removePackage(p.getUUID());
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Collection<Package> getPackagesAsJAXB() {
        List<Package> ret = new ArrayList<Package>();
        PackageIterator iter = repository.listPackages();
        while (iter.hasNext())
            ret.add(ToPackage(iter.next(), uriInfo));
        return ret;
    }

    @GET
    @Path("{packageName}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public org.drools.guvnor.server.jaxrs.jaxb.Package getPackageAsJAXB(@PathParam("packageName") String packageName) {
        return ToPackage(repository.loadPackage(packageName), uriInfo);
    }

    @GET
    @Path("{packageName}/assets")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Collection<Asset> getAssetsAsJAXB(@PathParam("packageName") String packageName) {
        List<Asset> ret = Collections.EMPTY_LIST;
        PackageItem p = repository.loadPackage(packageName);
        Iterator<AssetItem> iter = p.getAssets();
        if (iter.hasNext())
            ret = new ArrayList<Asset>();
        while (iter.hasNext())
            ret.add(ToAsset(iter.next(), uriInfo));
        return ret;
    }

    @PUT
    @Path("{packageName}")
    @Consumes ({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updatePackageFromJAXB (@PathParam("packageName") String packageName, Package p) {
        PackageItem item = repository.loadPackage(packageName);
        item.checkout();
        item.updateDescription(p.getDescription());
        item.updateTitle(p.getTitle());
        /* TODO: add more updates to package item from JSON */
        item.checkin(p.getCheckInComment());
        repository.save();
    }

    @GET
    @Path("{packageName}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] getBinaryPackageByName(@PathParam("packageName") String packageName) throws SerializationException {
        PackageItem p = repository.loadPackage(packageName);
        PackageService.buildPackage(p.getUUID(), true);
        return repository.loadPackage(packageName).getCompiledPackageBytes();
    }

    @GET
    @Path("{packageName}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSourcePackageByName(@PathParam("packageName") String packageName) {
        return repository.loadPackage(packageName).getExternalSource();
    }

    @GET
    @Path("{packageName}/asset/{name}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry getAssetByIdAsAtom(@PathParam ("packageName") String packageName, @PathParam("name") String name) {
        Entry ret = null;
        PackageItem item = repository.loadPackage(packageName);
        Iterator<AssetItem> iter = item.getAssets();
        while (iter.hasNext()) {
            AssetItem a = iter.next();
            if (a.getName().equals(name)) {
                ret = ToAssetEntry(a, uriInfo);
                break;
            }
        }
        return ret;
    }

    @GET
    @Path("{packageName}/asset/{name}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Asset getAssetByIdAsJaxB(@PathParam ("packageName") String packageName, @PathParam("name") String name) {
        Asset ret = null;
        PackageItem item = repository.loadPackage(packageName);
        Iterator<AssetItem> iter = item.getAssets();
        while (iter.hasNext()) {
            AssetItem a = iter.next();
            if (a.getName().equals(name)) {
                ret = ToAsset(a, uriInfo);
                break;
            }
        }
        return ret;
    }

    @GET
    @Path("{packageName}/asset/{name}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream getBinaryAssetById(@PathParam ("packageName") String packageName, @PathParam("name") String name) {
        InputStream ret = null;
        PackageItem item = repository.loadPackage(packageName);
        Iterator<AssetItem> iter = item.getAssets();
        while (iter.hasNext()) {
            AssetItem a = iter.next();
            if (a.getName().equals(name)) {
                ret = a.getBinaryContentAttachment();
                break;
            }
        }
        return ret;
    }


    @GET
    @Path("{packageName}/asset/{name}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSourceAssetById(@PathParam ("packageName") String packageName, @PathParam("name") String name) {
        String ret = null;
        if (repository.containsPackage(packageName)) {
            PackageItem item = repository.loadPackage(packageName);
            Iterator<AssetItem> iter = item.getAssets();
            while (iter.hasNext()) {
                AssetItem a = iter.next();
                if (a.getName().equals(name)) {
                    ret = a.getContent();
                    break;
                }
            }
        } else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");

        return ret;
    }

    @PUT
    @Path("{packageName}/asset/{name}")
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    public void updateAssetFromAtom(@PathParam ("packageName") String packageName, @PathParam("name") String name, Entry assetEntry)
    {
        AssetItem ai = null;
        PackageItem item = repository.loadPackage(packageName);
        Iterator<AssetItem> iter = item.getAssets();
        while (iter.hasNext()) {
            AssetItem a = iter.next();
            if (a.getName().equals(name)) {
                ai = a;
                break;
            }
        }

        /* Update asset */
        ai.checkout();
        ai.updateTitle(assetEntry.getTitle());
        ai.updateDescription(assetEntry.getSummary());
        ai.updateContent(assetEntry.getContent().getText());
        ai.checkin("Check-in (summary): " + assetEntry.getSummary());
        repository.save();
    }

    @PUT
    @Path("{packageName}/asset/{name}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updateAssetFromJAXB(@PathParam ("packageName") String packageName, @PathParam("name") String name, Asset asset)
    {
        AssetItem ai = null;
        PackageItem pi = repository.loadPackage(packageName);
        Iterator<AssetItem> iter = pi.getAssets();
        while (iter.hasNext()) {
            AssetItem item = iter.next();
            if (item.getName().equals(name)) {
                ai = item;
                break;
            }
        }

        /* Update asset */
        ai.checkout();
        ai.updateTitle(asset.getMetadata().getTitle());
        ai.updateDescription(asset.getDescription());
        ai.checkin(asset.getCheckInComment());
        repository.save();
    }

    @DELETE
    @Path("{packageName}/asset/{name}/")
    public void deleteAsset(@PathParam ("packageName") String packageName, @PathParam("name") String name) {
        AssetItem asset = null;
        PackageItem item = repository.loadPackage(packageName);
        Iterator<AssetItem> iter = item.getAssets();
        while (iter.hasNext()) {
            AssetItem a = iter.next();
            if (a.getName().equals(name)) {
                asset = a;
                break;
            }
        }
        AssetService.archiveAsset(asset.getUUID());
    }
}


