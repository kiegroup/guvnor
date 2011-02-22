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
import org.drools.guvnor.server.files.RepositoryServlet;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.plugins.providers.atom.*;

import java.io.InputStream;
import java.util.*;
import javax.ws.rs.core.MediaType;

import org.drools.repository.*;
import org.jboss.seam.annotations.Name;

import javax.ws.rs.*;
import javax.ws.rs.core.UriBuilder;

import static org.drools.guvnor.server.jaxrs.Translator.*;

/**
 * Contract:  Package names and asset names within a package namespace
 * must be unique.  REST API avoids use of asset UUIDs through this
 * contract.
 */
@Name("PackageResource")
@Path("/packages")
public class PackageResource extends Resource {

    public class BinaryForm {
        private byte[] filedata;

        public BinaryForm() {}

        public byte[] getData() {
            return filedata;
        }

        @FormParam("data")
        @PartType(value = "application/octet-stream")
        public void setData(final byte[] filedata) {
            this.filedata = filedata;
        }
    }


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
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void createPackageFromInputStream(@MultipartForm BinaryForm form) {
        RepositoryServlet.getFileManager().importPackageToRepository(form.getData(), true);
    }

    @POST
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    public void createPackageFromAtom (Entry entry) {
        Service.createPackage(entry.getTitle(),entry.getSummary());
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void createPackageFromJAXB (Package p) {
        Service.createPackage(p.getTitle(), p.getDescription());
    }


    @GET
    @Path("{packageName}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry getPackageAsEntryByName(@PathParam("packageName") String packageName) {
        Entry ret = null;
        if (repository.containsPackage(packageName) &&
                !repository.isPackageArchived(packageName))
            ret = ToPackageEntry(repository.loadPackage(packageName), uriInfo);
        else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");
        return ret;
    }

    @GET
    @Path("{packageName}/assets")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getAssetsAsAtom(@PathParam("packageName") String packageName) {
        Feed feed = new Feed();
        if (repository.containsPackage(packageName) &&
                !repository.isPackageArchived(packageName))
        {
            PackageItem p = repository.loadPackage(packageName);
            feed.setTitle(p.getTitle() + "-asset-feed");
            Iterator<AssetItem> iter = p.getAssets();
            while (iter.hasNext())
                feed.getEntries().add(ToAssetEntry(iter.next(), uriInfo));
        } else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");

        return feed;
    }

    @PUT
    @Path("{packageName}")
    @Consumes (MediaType.APPLICATION_ATOM_XML)
    public void updatePackageFromAtom (@PathParam("packageName") String packageName, Entry entry) {
        if (repository.containsPackage(packageName)) {
            PackageItem p = repository.loadPackage(packageName);
            Service.changeAssetPackage(p.getUUID(), entry.getTitle(), entry.getSummary());
        } else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");
    }

    @DELETE
    @Path("{packageName}")
    public void archivePackage (@PathParam("packageName") String packageName) {
        if (repository.containsPackage(packageName)) {
            PackageItem p = repository.loadPackage(packageName);
            Service.archiveAsset(p.getUUID());
        } else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");
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
    public Package getPackageAsJAXB(@PathParam("packageName") String packageName) {
        Package ret = null;
        if (repository.containsPackage(packageName) &&
                !repository.isPackageArchived(packageName))
            ret = ToPackage(repository.loadPackage(packageName), uriInfo);
        else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");
        return ret;
    }

    @GET
    @Path("{packageName}/assets")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Collection<Asset> getAssetsAsJAXB(@PathParam("packageName") String packageName) {
        List<Asset> ret = Collections.EMPTY_LIST;
        if (repository.containsPackage(packageName) &&
                !repository.isPackageArchived(packageName))
        {
            PackageItem p = repository.loadPackage(packageName);
            Iterator<AssetItem> iter = p.getAssets();
            if (iter.hasNext())
                ret = new ArrayList<Asset>();
            while (iter.hasNext())
                ret.add(ToAsset(iter.next(), uriInfo));
        } else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");

        return ret;
    }

    @PUT
    @Path("{packageName}")
    @Consumes ({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updatePackageFromJAXB (@PathParam("packageName") String packageName, Package p) {
        if (repository.containsPackage(packageName)) {
            PackageItem item = repository.loadPackage(packageName);
            item.checkout();
            item.updateDescription(p.getDescription());
            item.updateTitle(p.getTitle());
            item.updateType(p.getType());
            /* TODO: add more updates to package item from JSON */
            item.checkin(p.getCheckInComment());
            repository.save();
        } else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");
    }

    @GET
    @Path("{packageName}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] getBinaryPackageByName(@PathParam("packageName") String packageName) throws SerializationException {
        byte [] ret = null;
        if (repository.containsPackage(packageName)) {
            PackageItem p = repository.loadPackage(packageName);
            Service.buildPackage(p.getUUID(), true);
            ret = repository.loadPackage(packageName).getCompiledPackageBytes();
        } else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");
        return ret;
    }

    @GET
    @Path("{packageName}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public String getSourcePackageByName(@PathParam("packageName") String packageName) {
        String ret = null;
        if (repository.containsPackage(packageName)) {
            PackageItem p = repository.loadPackage(packageName);
            ret = p.getExternalSource();
        } else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");
        return ret;
    }

    /** Asset Sub-resources */

    @GET
    @Path("{packageName}/asset/{name}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry getAssetByIdAsAtom(@PathParam ("packageName") String packageName, @PathParam("name") String name) {
        Entry ret = null;
        if (repository.containsPackage(packageName)) {
            PackageItem item = repository.loadPackage(packageName);
            Iterator<AssetItem> iter = item.getAssets();
            while (iter.hasNext()) {
                AssetItem a = iter.next();
                if (a.getName().equals(name)) {
                    ret = ToAssetEntry(a, uriInfo);
                    break;
                }
            }
        } else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");

        return ret;
    }

    @GET
    @Path("{packageName}/asset/{name}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Asset getAssetByIdAsJaxB(@PathParam ("packageName") String packageName, @PathParam("name") String name) {
        Asset ret = null;
        if (repository.containsPackage(packageName)) {
            PackageItem item = repository.loadPackage(packageName);
            Iterator<AssetItem> iter = item.getAssets();
            while (iter.hasNext()) {
                AssetItem a = iter.next();
                if (a.getName().equals(name)) {
                    ret = ToAsset(a, uriInfo);
                    break;
                }
            }
        } else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");

        return ret;
    }

    @GET
    @Path("{packageName}/asset/{name}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream getBinaryAssetById(@PathParam ("packageName") String packageName, @PathParam("name") String name) {
        InputStream ret = null;
        if (repository.containsPackage(packageName)) {
            PackageItem item = repository.loadPackage(packageName);
            Iterator<AssetItem> iter = item.getAssets();
            while (iter.hasNext()) {
                AssetItem a = iter.next();
                if (a.getName().equals(name)) {
                    ret = a.getBinaryContentAttachment();
                    break;
                }
            }
        } else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");
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

        if (repository.containsPackage(packageName)) {
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
        } else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");
    }

    @PUT
    @Path("{packageName}/asset/{name}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updateAssetFromJAXB(@PathParam ("packageName") String packageName, @PathParam("name") String name, Asset asset)
    {
        AssetItem ai = null;
        if (repository.containsPackage(packageName)) {
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
            ai.updateTitle(asset.getTitle());
            ai.updateDescription(asset.getDescription());
            ai.checkin(asset.getCheckInComment());
            repository.save();
        } else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");
    }

    @DELETE
    @Path("{packageName}/asset/{name}/")
    public void deleteAsset(@PathParam ("packageName") String packageName, @PathParam("name") String name) {
        AssetItem asset = null;
        if (repository.containsPackage(packageName)) {
            PackageItem item = repository.loadPackage(packageName);
            Iterator<AssetItem> iter = item.getAssets();
            while (iter.hasNext()) {
                AssetItem a = iter.next();
                if (a.getName().equals(name)) {
                    asset = a;
                    break;
                }
            }
            Service.archiveAsset(asset.getUUID());
        } else
            throw new RuntimeException ("Package '" + packageName + "' does not exist!");
    }
}


