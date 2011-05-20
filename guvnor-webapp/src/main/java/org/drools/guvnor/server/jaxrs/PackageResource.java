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

import static org.drools.guvnor.server.jaxrs.Translator.ToAsset;
import static org.drools.guvnor.server.jaxrs.Translator.ToAssetEntryAbdera;
import static org.drools.guvnor.server.jaxrs.Translator.ToPackage;
import static org.drools.guvnor.server.jaxrs.Translator.ToPackageEntryAbdera;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.server.builder.PackageDRLAssembler;
import org.drools.guvnor.server.files.RepositoryServlet;
import org.drools.guvnor.server.jaxrs.jaxb.Asset;
import org.drools.guvnor.server.jaxrs.jaxb.Package;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageHistoryIterator;
import org.drools.repository.PackageItem;
import org.drools.repository.PackageIterator;
import org.jboss.seam.annotations.Name;

import com.google.gwt.user.client.rpc.SerializationException;

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
        Factory factory = Abdera.getNewFactory();
        Feed f = factory.getAbdera().newFeed();
        f.setTitle("Packages");
        f.setBaseUri(uriInfo.getBaseUriBuilder().path("packages").build().toString());
        PackageIterator iter = repository.listPackages();
        while (iter.hasNext()) {
            try {
                PackageItem item = iter.next();
                Entry e = factory.getAbdera().newEntry();
                e.setTitle(item.getName());        
                Link l = factory.newLink();
                l.setHref(uriInfo.getBaseUriBuilder().path("packages").path(item.getName()).build().toString());
                e.addLink(l);
                f.addEntry(e);
            } catch (Exception e) {
                throw new WebApplicationException(e);
            }
        }

        return f;
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

/*    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry createPackageFromDRLAndReturnAsEntry(InputStream is, @Context UriInfo uriInfo) throws IOException,
            DroolsParserException
    {
         Passes the DRL to the FileManagerUtils and has it import the asset as a package 
        String packageName = RepositoryServlet.getFileManager().importClassicDRL (is, null);
        Entry e = ToPackageEntry(repository.loadPackage(packageName), uriInfo);
        return e;
    }*/

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Package createPackageFromDRLAndReturnAsJaxB(InputStream is, @Context UriInfo uriInfo) throws IOException,
            DroolsParserException
    {
        /* Passes the DRL to the FileManagerUtils and has it import the asset as a package */
        String packageName = RepositoryServlet.getFileManager().importClassicDRL (is, null);
        Package p = ToPackage(repository.loadPackage(packageName), uriInfo);
        return p;
    }

    @POST
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    @Produces(MediaType.APPLICATION_ATOM_XML)
	public Response createPackageFromAtom(Entry entry, @Context UriInfo uriInfo) {
		repository.createPackage(entry.getTitle(), entry.getSummary());
		URI uri = uriInfo.getBaseUriBuilder().path("packages").path(entry.getTitle()).build();
		entry.setBaseUri(uri.toString());
		return Response.created(uri).entity(entry).build();
	}

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void createPackageFromJAXB (Package p) {
    	repository.createPackage(p.getTitle(), p.getDescription());
    }

    @GET
    @Path("{packageName}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry getPackageAsEntry(@PathParam("packageName") String packageName) {
		return ToPackageEntryAbdera(repository.loadPackage(packageName), uriInfo);
    }

    @GET
    @Path("{packageName}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public org.drools.guvnor.server.jaxrs.jaxb.Package getPackageAsJAXB(@PathParam("packageName") String packageName) {
        return ToPackage(repository.loadPackage(packageName), uriInfo);
    }
        
    @GET
    @Path("{packageName}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getPackageSource(@PathParam("packageName") String packageName) {
    	PackageItem item = repository.loadPackage( packageName );
        PackageDRLAssembler asm = new PackageDRLAssembler( item );
        String fileName = packageName;
        String drl = asm.getDRL();
        return Response.ok(drl).header("Content-Disposition", "attachment; filename=" + fileName).build();
    }

    @GET
    @Path("{packageName}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPackageBinary(@PathParam("packageName") String packageName) throws SerializationException {
        PackageItem p = repository.loadPackage(packageName);
        String fileName = packageName + ".pkg";
        byte[] result;
        if(p.isBinaryUpToDate()) {
            result = p.getCompiledPackageBytes();
        } else {
            StringBuilder errs = new StringBuilder();
            BuilderResult builderResult = packageService.buildPackage(p.getUUID(), true);
            if ( builderResult != null ) {
                errs.append("Unable to build package name [").append(packageName).append("]\n");
                StringBuilder buf = createStringBuilderFrom( builderResult );
                return Response.status(500).entity(buf.toString()).build();
            }
            result = repository.loadPackage(packageName).getCompiledPackageBytes(); 
        }
        
        return Response.ok(result).header("Content-Disposition", "attachment; filename=" + fileName).build();
    }
    
    private StringBuilder createStringBuilderFrom(BuilderResult res) {
        StringBuilder buf = new StringBuilder();
        for ( int i = 0; i < res.getLines().size(); i++ ) {
            buf.append( res.getLines().get( i ).toString() );
            buf.append( '\n' );
        }
        return buf;
    }
    
    @GET
    @Path("{packageName}/versions")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getPackageVersionsAsFeed(@PathParam("packageName") String packageName) throws SerializationException { 
    	PackageItem p = repository.loadPackage(packageName);
        
        Factory factory = Abdera.getNewFactory();
        Feed f = factory.getAbdera().newFeed();
        f.setTitle("Version history of " + p.getName());
        f.setBaseUri(uriInfo.getBaseUriBuilder().path("packages").build().toString());
        PackageHistoryIterator it = p.getHistory();        

        while (it.hasNext()) {
            try {
                PackageItem historicalPackage = it.next();
				if (historicalPackage.getVersionNumber() != 0) {
					Entry e = factory.getAbdera().newEntry();
					e.setTitle(Long.toString(historicalPackage
							.getVersionNumber()));
					e.setUpdated(historicalPackage.getLastModified().getTime());
					Link l = factory.newLink();
					l.setHref(uriInfo
							.getBaseUriBuilder()
							.path("packages")
							.path(p.getName())
							.path("versions")
							.path(Long.toString(historicalPackage.getVersionNumber())).build().toString());
					e.addLink(l);
					f.addEntry(e);
				}
            } catch (Exception e) {
                throw new WebApplicationException(e);
            }
        }
        
        return f;
    }

    @GET
    @Path("{packageName}/versions/{versionNumber}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry getHistoricalPackageAsEntry(@PathParam("packageName") String packageName,
    		@PathParam("versionNumber") long versionNumber) throws SerializationException {
        return ToPackageEntryAbdera(repository.loadPackage(packageName, versionNumber), uriInfo);
    }
    
    @GET
    @Path("{packageName}/versions/{versionNumber}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getHistoricalPackageSource(@PathParam("packageName") String packageName,
    		@PathParam("versionNumber") long versionNumber) {
    	PackageItem item = repository.loadPackage(packageName,  versionNumber);
        PackageDRLAssembler asm = new PackageDRLAssembler( item );
        String fileName = packageName;
        String drl = asm.getDRL();
        return Response.ok(drl).header("Content-Disposition", "attachment; filename=" + fileName).build();
    }    
    
    @GET
    @Path("{packageName}/versions/{versionNumber}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getHistoricalPackageBinary(@PathParam("packageName") String packageName,
    		@PathParam("versionNumber") long versionNumber) throws SerializationException {
        PackageItem p = repository.loadPackage(packageName, versionNumber);
        byte[] result = p.getCompiledPackageBytes();
        if(result != null) {
            String fileName = packageName + ".pkg";  
            return Response.ok(result).header("Content-Disposition", "attachment; filename=" + fileName).build();
        } else {
            return Response.status(500).entity("This package version has no compiled binary").type("text/plain").build();
        }
    }
    
    @GET
    @Path("{packageName}/assets")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getAssetsAsAtom(@PathParam("packageName") String packageName) {
        Factory factory = Abdera.getNewFactory();
        Feed feed = factory.getAbdera().newFeed();
        PackageItem p = repository.loadPackage(packageName);
        feed.setTitle(p.getTitle() + "-asset-feed");
        Iterator<AssetItem> iter = p.getAssets();
        while (iter.hasNext())
        	feed.addEntry(ToAssetEntryAbdera(iter.next(), uriInfo));
        return feed;
    }

    @PUT
    @Path("{packageName}")
    @Consumes (MediaType.APPLICATION_ATOM_XML)
    public void updatePackageFromAtom (@PathParam("packageName") String packageName, org.apache.abdera.model.Entry entry) {
        PackageItem p = repository.loadPackage(packageName);
        p.checkout();
        //TODO: support rename package. 
        //p.updateTitle(entry.getTitle());
       	if(entry.getSummary() != null) {
            p.updateDescription(entry.getSummary());
		}
        //TODO: support LastContributor 
       	if(entry.getAuthor() != null) {       		
       	}
        
		ExtensibleElement metadataExtension  = entry.getExtension(Translator.METADATA); 
		if(metadataExtension != null) {
            ExtensibleElement archivedExtension = metadataExtension.getExtension(Translator.ARCHIVED);  
            if(archivedExtension != null) {
            	p.archiveItem(Boolean.getBoolean(archivedExtension.getSimpleExtension(Translator.VALUE)));
            }
            
            //TODO: Package state is not fully supported yet
/*            ExtensibleElement stateExtension = metadataExtension.getExtension(Translator.STATE);  
            if(stateExtension != null) {
            	p.updateState(stateExtension.getSimpleExtension(Translator.STATE));
            }   */     
            
		}

        p.checkin("Update from ATOM.");
        repository.save();
    }
    
    @DELETE
    @Path("{packageName}")
    public void deletePackage (@PathParam("packageName") String packageName) {
        PackageItem p = repository.loadPackage(packageName);
        packageService.removePackage(p.getUUID());
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
    @Path("{packageName}/assets/{assetName}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry getAssetAsAtom(@PathParam ("packageName") String packageName, @PathParam("assetName") String assetName) {
    	Entry ret = null;
        PackageItem item = repository.loadPackage(packageName);
        Iterator<AssetItem> iter = item.getAssets();
        while (iter.hasNext()) {
            AssetItem a = iter.next();
            if (a.getName().equals(assetName)) {
                ret = ToAssetEntryAbdera(a, uriInfo);
                break;
            }
        }
        return ret;
    }

    @GET
    @Path("{packageName}/assets/{assetName}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Asset getAssetAsJaxB(@PathParam ("packageName") String packageName, @PathParam("assetName") String assetName) {
        Asset ret = null;
        PackageItem item = repository.loadPackage(packageName);
        Iterator<AssetItem> iter = item.getAssets();
        while (iter.hasNext()) {
            AssetItem a = iter.next();
            if (a.getName().equals(assetName)) {
                ret = ToAsset(a, uriInfo);
                break;
            }
        }
        return ret;
    }

    @GET
    @Path("{packageName}/assets/{assetName}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream getAssetBinary(@PathParam ("packageName") String packageName, @PathParam("assetName") String assetName) {
        InputStream ret = null;
        PackageItem item = repository.loadPackage(packageName);
        Iterator<AssetItem> iter = item.getAssets();
        while (iter.hasNext()) {
            AssetItem a = iter.next();
            if (a.getName().equals(assetName)) {
                ret = a.getBinaryContentAttachment();
                break;
            }
        }
        return ret;
    }

    @GET
    @Path("{packageName}/assets/{assetName}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAssetSource(@PathParam ("packageName") String packageName, @PathParam("assetName") String assetName) {
        String ret = null;
        if (repository.containsPackage(packageName)) {
            PackageItem item = repository.loadPackage(packageName);
            Iterator<AssetItem> iter = item.getAssets();
            while (iter.hasNext()) {
                AssetItem a = iter.next();
                if (a.getName().equals(assetName)) {
                    ret = a.getContent();
                    break;
                }
            }

            return ret;
        } else {
            throw new WebApplicationException(new RuntimeException ("Package '" + packageName + "' does not exist!"));
        }
    }

    @PUT
    @Path("{packageName}/assets/{assetName}")
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    public void updateAssetFromAtom(@PathParam ("packageName") String packageName, @PathParam("assetName") String assetName, Entry assetEntry)
    {
        AssetItem ai = null;
        PackageItem item = repository.loadPackage(packageName);
        Iterator<AssetItem> iter = item.getAssets();
        while (iter.hasNext()) {
            AssetItem a = iter.next();
            if (a.getName().equals(assetName)) {
                ai = a;
                break;
            }
        }

        //  Update asset 
        ai.checkout();
        ai.updateTitle(assetEntry.getTitle());
        ai.updateDescription(assetEntry.getSummary());
        ai.updateContent(assetEntry.getContent());
        ai.checkin("Check-in (summary): " + assetEntry.getSummary());
        repository.save();
    }

    @PUT
    @Path("{packageName}/assets/{assetName}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updateAssetFromJAXB(@PathParam ("packageName") String packageName, @PathParam("assetName") String assetName, Asset asset)
    {
        AssetItem ai = null;
        PackageItem pi = repository.loadPackage(packageName);
        Iterator<AssetItem> iter = pi.getAssets();
        while (iter.hasNext()) {
            AssetItem item = iter.next();
            if (item.getName().equals(assetName)) {
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
    @Path("{packageName}/assets/{assetName}/")
    public void deleteAsset(@PathParam ("packageName") String packageName, @PathParam("assetName") String assetName) {
        AssetItem asset = null;
        PackageItem item = repository.loadPackage(packageName);
        Iterator<AssetItem> iter = item.getAssets();
        while (iter.hasNext()) {
            AssetItem a = iter.next();
            if (a.getName().equals(assetName)) {
                asset = a;
                break;
            }
        }
        assetService.archiveAsset(asset.getUUID());
    }
}


