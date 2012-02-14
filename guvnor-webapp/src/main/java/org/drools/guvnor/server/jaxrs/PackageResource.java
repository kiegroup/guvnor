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

import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.server.RepositoryPackageOperations;
import org.drools.guvnor.server.builder.PackageDRLAssembler;
import org.drools.guvnor.server.files.RepositoryServlet;
import org.drools.guvnor.server.jaxrs.jaxb.Asset;
import org.drools.guvnor.server.jaxrs.jaxb.Package;
import org.drools.repository.AssetHistoryIterator;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageHistoryIterator;
import org.drools.repository.PackageItem;
import org.drools.repository.PackageIterator;
import org.jboss.seam.annotations.Name;

import com.google.gwt.user.client.rpc.SerializationException;

import static org.drools.guvnor.server.jaxrs.Translator.toAsset;
import static org.drools.guvnor.server.jaxrs.Translator.toAssetEntryAbdera;
import static org.drools.guvnor.server.jaxrs.Translator.toPackage;
import static org.drools.guvnor.server.jaxrs.Translator.toPackageEntryAbdera;

/**
 * Contract:  Package names and asset names within a package namespace
 * must be unique.  REST API avoids use of asset UUIDs through this
 * contract.
 * Exception handling: At the moment we catch all exceptions thrown from underlying repository and wrap
 * them with WebApplicationException. We need to set detailed exception message on WebApplicationException.
 * We also need to set HTTP error code on WebApplicationException if we want a HTTP response code other
 * than 500 (Internal Server Error).
 * In the future, we may use ExceptionMapper provider to map a checked or runtime exception to a
 * HTTP response. Note, if there are no mappers found for custom exceptions, they will be propagated
 * (wrapped in ServletException) to the underlying container as required by the spec.
 */
@Name("PackageResource")
@Path("/packages")
public class PackageResource extends Resource {
    private HttpHeaders headers;

    /* Handles common package operations */
    private final RepositoryPackageOperations repositoryPackageOperations;

    /**
     * Constructor initialising all required properties/members
     */
    public PackageResource() {
        super();
        repositoryPackageOperations = new RepositoryPackageOperations();
        repositoryPackageOperations.setRulesRepository(repository);
    }

    @Context
    public void setHttpHeaders(HttpHeaders theHeaders) {
        headers = theHeaders;
    }

    @GET
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getPackagesAsFeed() {
        Factory factory = Abdera.getNewFactory();
        Feed f = factory.getAbdera().newFeed();
        f.setTitle("Packages");
        f.setBaseUri(uriInfo.getBaseUriBuilder().path("packages").build().toString());
        PackageIterator iter = repository.listPackages();
        while (iter.hasNext()) {
            PackageItem item = iter.next();
            Entry e = factory.getAbdera().newEntry();
            e.setTitle(item.getName());
            Link l = factory.newLink();
            l.setHref(uriInfo.getBaseUriBuilder()
                    .path("packages/{itemName}")
                    .build(item.getName())
                    .toString());
            e.addLink(l);
            f.addEntry(e);
        }

        return f;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Collection<Package> getPackagesAsJAXB() {
        List<Package> ret = new ArrayList<Package>();
        PackageIterator iter = repository.listPackages();
        while (iter.hasNext()) {
            //REVIST: Do not return detailed package info here. Package title and link should be enough. 
            ret.add(toPackage(iter.next(), uriInfo));
        }
        return ret;
    }

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry createPackageFromDRLAndReturnAsEntry(InputStream is, @Context UriInfo uriInfo) {
        /*
         * Passes the DRL to the FileManagerUtils and has it import the asset as
         * a package
         */
        try {
            String packageName = RepositoryServlet.getFileManager().importClassicDRL(is, null);
            return toPackageEntryAbdera(repository.loadPackage(packageName), uriInfo);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Package createPackageFromDRLAndReturnAsJaxB(InputStream is) {
        /*
         * Passes the DRL to the FileManagerUtils and has it import the asset as
         * a package
         */
        try {
            String packageName = RepositoryServlet.getFileManager()
                    .importClassicDRL(is, null);
            return toPackage(repository.loadPackage(packageName), uriInfo);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry createPackageFromAtom(Entry entry) {
        try {
            PackageItem packageItem = repository.createPackage(entry.getTitle(), entry.getSummary());
            return toPackageEntryAbdera(packageItem, uriInfo);
        } catch (Exception e) {
            //catch RulesRepositoryException and other exceptions. For example when the package already exists.
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Package createPackageFromJAXB(Package p) {
        try {
            PackageItem packageItem = repository.createPackage(p.getTitle(), p.getDescription());
            return toPackage(packageItem, uriInfo);
        } catch (Exception e) {
            //catch RulesRepositoryException and other exceptions. For example when the package already exists.
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry getPackageAsEntry(@PathParam("packageName") String packageName) {
        try {
            PackageItem packageItem = repository.loadPackage(packageName);
            return toPackageEntryAbdera(packageItem, uriInfo);
        } catch (Exception e) {
            //catch RulesRepositoryException and other exceptions. For example when the package does not exists.
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Package getPackageAsJAXB(@PathParam("packageName") String packageName) {
        try {
            PackageItem packageItem = repository.loadPackage(packageName);
            return toPackage(packageItem, uriInfo);
        } catch (Exception e) {
            //catch RulesRepositoryException and other exceptions. For example when the package does not exists.
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getPackageSource(@PathParam("packageName") String packageName) {
        try {
            PackageItem packageItem = repository.loadPackage(packageName);
            PackageDRLAssembler asm = new PackageDRLAssembler(packageItem);
            String drl = asm.getDRL();
            return Response.ok(drl).header("Content-Disposition", "attachment; filename=" + packageName).build();
        } catch (Exception e) {
            //catch RulesRepositoryException and other exceptions. For example when the package does not exists.
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPackageBinary(@PathParam("packageName") String packageName) throws SerializationException {
        try {
            PackageItem p = repository.loadPackage(packageName);
            String fileName = packageName + ".pkg";
            byte[] result;
            if (p.isBinaryUpToDate()) {
                result = p.getCompiledPackageBytes();
            } else {
                BuilderResult builderResult = packageService.buildPackage(p.getUUID(), true);
                if (builderResult != null) {
                    StringBuilder errs = new StringBuilder();
                    errs.append("Unable to build package name [").append(packageName).append("]\n");
                    for (BuilderResultLine resultLine : builderResult.getLines()) {
                        errs.append(resultLine.toString()).append("\n");
                    }
                    return Response.status(500).entity(errs.toString()).build();
                }
                result = repository.loadPackage(packageName).getCompiledPackageBytes();
            }
            return Response.ok(result).header("Content-Disposition", "attachment; filename=" + fileName).build();
        } catch (Exception e) {
            //catch RulesRepositoryException and other exceptions. For example when the package does not exists.
            throw new WebApplicationException(e);
        }
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
                            .path("packages/{packageName}/versions/{versionNumber}")
                            .build(p.getName(), Long.toString(historicalPackage.getVersionNumber()))
                            .toString());
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
        return toPackageEntryAbdera(repository.loadPackage(packageName, versionNumber), uriInfo);
    }

    @GET
    @Path("{packageName}/versions/{versionNumber}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getHistoricalPackageSource(@PathParam("packageName") String packageName,
                                               @PathParam("versionNumber") long versionNumber) {
        PackageItem item = repository.loadPackage(packageName, versionNumber);
        PackageDRLAssembler asm = new PackageDRLAssembler(item);
        String drl = asm.getDRL();
        return Response.ok(drl).header("Content-Disposition", "attachment; filename=" + packageName).build();
    }

    @GET
    @Path("{packageName}/versions/{versionNumber}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getHistoricalPackageBinary(@PathParam("packageName") String packageName,
                                               @PathParam("versionNumber") long versionNumber) throws SerializationException {
        PackageItem p = repository.loadPackage(packageName, versionNumber);
        byte[] result = p.getCompiledPackageBytes();
        if (result != null) {
            String fileName = packageName + ".pkg";
            return Response.ok(result).header("Content-Disposition", "attachment; filename=" + fileName).build();
        } else {
            return Response.status(500).entity("This package version has no compiled binary").type("text/plain").build();
        }
    }

    @PUT
    @Path("{packageName}")
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    public void updatePackageFromAtom(@PathParam("packageName") String packageName, Entry entry) {
        try {
            PackageItem existingPackage = repository.loadPackage(packageName);
            
            //Rename:
            if (!existingPackage.getTitle().equalsIgnoreCase(entry.getTitle())) {
                repository.renamePackage(existingPackage.getUUID(), entry.getTitle());
            }

            if (entry.getSummary() != null) {
                existingPackage.updateDescription(entry.getSummary());
            }
            // TODO: support LastContributor
            if (entry.getAuthor() != null) {
            }

            ExtensibleElement metadataExtension = entry
                    .getExtension(Translator.METADATA);
            String checkinComment = "";
            if (metadataExtension != null) {
                ExtensibleElement archivedExtension = metadataExtension
                        .getExtension(Translator.ARCHIVED);
                if (archivedExtension != null) {
                    existingPackage.archiveItem(Boolean.getBoolean(archivedExtension
                            .getSimpleExtension(Translator.VALUE)));
                }

                ExtensibleElement checkinCommentExtension = metadataExtension.getExtension(Translator.CHECKIN_COMMENT);
                if (checkinCommentExtension != null) {
                    checkinComment =  checkinCommentExtension.getSimpleExtension(Translator.VALUE);
                }
                // TODO: Package state is not fully supported yet
                /*
                 * ExtensibleElement stateExtension =
                 * metadataExtension.getExtension(Translator.STATE);
                 * if (stateExtension != null) {
                 * p.updateState(stateExtension.getSimpleExtension
                 * (Translator.STATE)); }
                 */
            }

            existingPackage.checkin(checkinComment);
            repository.save();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @PUT
    @Path("{packageName}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updatePackageFromJAXB(@PathParam("packageName") String packageName, Package newPackage) {
        try {
            PackageItem existingPackage = repository.loadPackage(packageName);
            
            //Rename:
            if (!existingPackage.getTitle().equalsIgnoreCase(newPackage.getTitle())) {
                repository.renamePackage(existingPackage.getUUID(), newPackage.getTitle());
            }            

            existingPackage.updateDescription(newPackage.getDescription());

            /* TODO: add more updates to package item from JSON */
            String checkInComment = "";
            if (newPackage.getCheckInComment() != null) {
                checkInComment = newPackage.getCheckInComment();
            }
            existingPackage.checkin(checkInComment);
            repository.save();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @DELETE
    @Path("{packageName}")
    public void deletePackage(@PathParam("packageName") String packageName) {
        try {
            //Throws RulesRepositoryException if the package does not exist
            PackageItem p = repository.loadPackage(packageName);
            packageService.removePackage(p.getUUID());
        } catch (Exception e) {
            // catch RulesRepositoryException and other exceptions.
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/assets")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getAssetsAsAtom(
            @PathParam("packageName") String packageName,
            @QueryParam("format") List<String> formats) {
        try {
            Factory factory = Abdera.getNewFactory();
            Feed feed = factory.getAbdera().newFeed();
            PackageItem p = repository.loadPackage(packageName);
            feed.setTitle(p.getTitle() + "-asset-feed");
            
            Iterator<AssetItem> iter = null;
            
            if (formats.isEmpty()){
                //no format specified? Return all assets
                iter = p.getAssets();
            }else{
                //if the format is specified, return only the assets of
                //the specified formats.
                iter = p.listAssetsByFormat(formats);
            }
            
            while (iter.hasNext())
                feed.addEntry(toAssetEntryAbdera(iter.next(), uriInfo));
            return feed;
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/assets")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Collection<Asset> getAssetsAsJAXB(
            @PathParam("packageName") String packageName,
            @QueryParam("format") List<String> formats) {
        try {
            List<Asset> ret = new ArrayList<Asset>();
            PackageItem p = repository.loadPackage(packageName);
            
            Iterator<AssetItem> iter = null;
            
            if (formats.isEmpty()){
                //no format specified? Return all assets
                iter = p.getAssets();
            }else{
                //if the format is specified, return only the assets of
                //the specified formats.
                iter = p.listAssetsByFormat(formats);
            }
            
            while (iter.hasNext()) {
                ret.add(toAsset(iter.next(), uriInfo));
            }
            return ret;
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/assets/{assetName}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry getAssetAsAtom(@PathParam("packageName") String packageName, @PathParam("assetName") String assetName) {
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = repository.loadPackage(packageName).loadAsset(assetName);
            return toAssetEntryAbdera(asset, uriInfo);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/assets/{assetName}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Asset getAssetAsJaxB(@PathParam("packageName") String packageName, @PathParam("assetName") String assetName) {
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = repository.loadPackage(packageName).loadAsset(assetName);
            return toAsset(asset, uriInfo);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/assets/{assetName}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getAssetBinary(@PathParam("packageName") String packageName, @PathParam("assetName") String assetName) {
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = repository.loadPackage(packageName).loadAsset(assetName);
            String fileName = asset.getName() + "." + asset.getFormat();
            return Response.ok(asset.getBinaryContentAttachment()).header("Content-Disposition", "attachment; filename=" + fileName).build();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/assets/{assetName}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAssetSource(@PathParam("packageName") String packageName, @PathParam("assetName") String assetName) {
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = repository.loadPackage(packageName).loadAsset(assetName);
            return asset.getContent();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Path("{packageName}/assets")
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry createAssetFromAtom(@PathParam("packageName") String packageName, Entry entry) {
        try {
            String format = null;
            String initialCategory = null;
            ExtensibleElement metadataExtension = entry.getExtension(Translator.METADATA);
            if (metadataExtension != null) {
                ExtensibleElement formatExtension = metadataExtension.getExtension(Translator.FORMAT);
                format = formatExtension != null ? formatExtension.getSimpleExtension(Translator.VALUE) : null;
                ExtensibleElement categoryExtension = metadataExtension.getExtension(Translator.CATEGORIES);
                initialCategory = formatExtension != null ? categoryExtension.getSimpleExtension(Translator.VALUE) : null;
            }

            AssetItem ai = repository.loadPackage(packageName).addAsset(entry.getTitle(), entry.getSummary(), initialCategory, format);
            
            //The categories are not saved by addAsset(). Need to force it here.
            repository.getSession().save();
            
            return toAssetEntryAbdera(ai, uriInfo);
        } catch (Exception e) {
            //catch RulesRepositoryException and other exceptions. For example when the package already exists.
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Path("{packageName}/assets")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry createAssetFromBinary(@PathParam("packageName") String packageName, InputStream is) {
        try {
            String assetName = getHttpHeader(headers, "slug");
            if (assetName == null) {
                throw new WebApplicationException(Response.status(500).entity("Slug header is missing").build());
            } else {
                assetName = URLDecoder.decode(assetName, "UTF-8");
            }
            String fileName = null;
            String extension = null;
            if (assetName.lastIndexOf(".") != -1) {
                fileName = assetName.substring(0, assetName.lastIndexOf("."));                
                extension = assetName.substring(assetName.lastIndexOf(".")+1); 
            } else {
                fileName = assetName;                
            }            
            
            AssetItem ai = repository.loadPackage(packageName).addAsset(fileName, "");
            ai.checkout();
            ai.updateBinaryContentAttachmentFileName(fileName);
            if (extension != null) {
                ai.updateFormat(extension);
            }
            ai.updateBinaryContentAttachment(is);
            ai.getPackage().updateBinaryUpToDate(false);
            ai.checkin("update binary");
            repository.save();
            return toAssetEntryAbdera(ai, uriInfo);
        } catch (Exception e) {
            //catch RulesRepositoryException and other exceptions. For example when the package already exists.
            throw new WebApplicationException(e);
        }
    }

    @PUT
    @Path("{packageName}/assets/{assetName}")
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    public void updateAssetFromAtom(@PathParam("packageName") String packageName, @PathParam("assetName") String assetName, Entry assetEntry) {
        try {
            String format = null;
            String[] categories = null;
            String state = null;
            ExtensibleElement metadataExtension = assetEntry.getExtension(Translator.METADATA);
            if (metadataExtension != null) {
                ExtensibleElement formatExtension = metadataExtension.getExtension(Translator.FORMAT);
                format = formatExtension != null ? formatExtension.getSimpleExtension(Translator.VALUE) : null;
                ExtensibleElement categoryExtension = metadataExtension.getExtension(Translator.CATEGORIES);
                if (categoryExtension != null) {
                    List<Element> categoryValues = categoryExtension
                            .getExtensions(Translator.VALUE);
                    categories = new String[categoryValues.size()];
                    for (int i=0; i< categoryValues.size(); i++) {
                        String catgoryValue = categoryValues.get(i).getText();
                        categories[i] = catgoryValue;
                    }
                }
                ExtensibleElement stateExtension = metadataExtension.getExtension(Translator.STATE);
                state = stateExtension != null ? stateExtension.getSimpleExtension(Translator.VALUE) : null;
            }

            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem ai = repository.loadPackage(packageName).loadAsset(assetName);
            //Update asset 
            ai.checkout();
            ai.updateTitle(assetEntry.getTitle());
            ai.updateDescription(assetEntry.getSummary());
            if (format != null) {
                ai.updateFormat(format);
            }
            
            //REVISIT: What if the client really wants to set content to ""?
            if (assetEntry.getContent() != null && !"".equals(assetEntry.getContent())) {
                ai.updateContent(assetEntry.getContent());
            }
            if (categories != null) {
                ai.updateCategoryList(categories);
            }
            if (state != null) {
                ai.updateState(state);
            }
            ai.checkin("Check-in (summary): " + assetEntry.getSummary());
            repository.save();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @PUT
    @Path("{packageName}/assets/{assetName}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updateAssetFromJAXB(
            @PathParam("packageName") String packageName,
            @PathParam("assetName") String assetName, Asset asset) {
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem ai = repository.loadPackage(packageName).loadAsset(assetName);
            /* Update asset */
            ai.checkout();
            ai.updateTitle(asset.getMetadata().getTitle());
            ai.updateDescription(asset.getDescription());
            ai.checkin(asset.getCheckInComment());
            repository.save();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @PUT
    @Path("{packageName}/assets/{assetName}/source")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updateAssetSource(@PathParam("packageName") String packageName, @PathParam("assetName") String assetName, String content) {
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = repository.loadPackage(packageName).loadAsset(assetName);
            asset.checkout();
            asset.updateContent(content);
            repository.save();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @PUT
    @Path("{packageName}/assets/{assetName}/binary")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public void updateAssetBinary(@PathParam("packageName") String packageName, @PathParam("assetName") String assetName, InputStream is) {
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = repository.loadPackage(packageName).loadAsset(assetName);
            asset.checkout();
            asset.updateBinaryContentAttachment(is);
            asset.checkin("Update binary");
            repository.save();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @DELETE
    @Path("{packageName}/assets/{assetName}/")
    public void deleteAsset(@PathParam("packageName") String packageName,
                            @PathParam("assetName") String assetName) {
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem ai = repository.loadPackage(packageName).loadAsset( assetName );
            // assetService.archiveAsset(ai.getUUID());
            assetService.removeAsset(ai.getUUID());
            repository.save();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }
    
    @GET
    @Path("{packageName}/assets/{assetName}/versions")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getAssetVersionsAsFeed(@PathParam("packageName") String packageName, 
                                       @PathParam("assetName") String assetName) {
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = repository.loadPackage(packageName).loadAsset(assetName);
            
            Factory factory = Abdera.getNewFactory();
            Feed f = factory.getAbdera().newFeed();
            f.setTitle("Version history of " + asset.getName());

            URI base;
            if (asset.isHistoricalVersion()) {
                base = uriInfo.getBaseUriBuilder()
                        .path("packages/{packageName}/assets/{assetName}/versions/{versionNumber}")
                        .build(asset.getPackageName(), asset.getName(), Long.toString(asset.getVersionNumber()));
            } else {
                base = uriInfo.getBaseUriBuilder()
                        .path("packages/{packageName}/assets/{assetName}/versions")
                        .build(asset.getPackageName(), asset.getName());
            }
            f.setBaseUri(base.toString());
                        
            AssetHistoryIterator it = asset.getHistory();
            while (it.hasNext()) {
                    AssetItem historicalAsset = it.next();
                    if (historicalAsset.getVersionNumber() != 0) {
                        Entry e = factory.getAbdera().newEntry();
                        e.setTitle(Long.toString(historicalAsset
                                .getVersionNumber()));
                        e.setUpdated(historicalAsset.getLastModified().getTime());
                        Link l = factory.newLink();
                        l.setHref(uriInfo
                                .getBaseUriBuilder()
                                .path("packages/{packageName}/assets/{assetName}/versions/{versionNumber}")
                                .build(asset.getPackageName(), asset.getName(),
                                        Long.toString(historicalAsset.getVersionNumber())).toString());
                        e.addLink(l);
                        f.addEntry(e);
                    }
            }
            return f;
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }    

    @GET
    @Path("{packageName}/assets/{assetName}/versions/{versionNumber}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry getHistoricalAssetAsEntry(@PathParam("packageName") String packageName,
                                           @PathParam("assetName") String assetName,
                                           @PathParam("versionNumber") long versionNumber) {
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = repository.loadPackage(packageName).loadAsset(assetName, versionNumber);
            return toAssetEntryAbdera(asset, uriInfo);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }   

    @GET
    @Path("{packageName}/assets/{assetName}/versions/{versionNumber}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public String getHistoricalAssetSource(@PathParam("packageName") String packageName,
                                           @PathParam("assetName") String assetName,
                                           @PathParam("versionNumber") long versionNumber) {
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = repository.loadPackage(packageName).loadAsset(assetName, versionNumber);
            return asset.getContent();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/assets/{assetName}/versions/{versionNumber}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getHistoricalAssetBinary(@PathParam("packageName") String packageName,
                                             @PathParam("assetName") String assetName,
                                             @PathParam("versionNumber") long versionNumber) {
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = repository.loadPackage(packageName).loadAsset(assetName, versionNumber);
            String fileName = asset.getName() + "." + asset.getFormat();
            return Response.ok(asset.getBinaryContentAttachment()).header("Content-Disposition", "attachment; filename=" + fileName).build();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }
    
    //HTTP header names are case-insensitive
    private String getHttpHeader(HttpHeaders headers, String headerName) {

        MultivaluedMap<String, String> heads = headers.getRequestHeaders();
        Iterator<String> it = heads.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (headerName.equalsIgnoreCase(key)) {
                List<String> h = heads.get(key);

                if (h != null && h.size() > 0) {
                    return h.get(0);
                }
            }
        }

        return null;
    }
    
    @POST
    @Path("{packageName}/snapshot/{snapshotName}")
    public void createPackageSnapshot(
            @PathParam("packageName") final String packageName,
            @PathParam("snapshotName") final String snapshotName) {
        repositoryPackageOperations.createPackageSnapshot(packageName,
                snapshotName, true, "REST API Snapshot");
    }
    
    @GET
    @Path("{packageName}/exists")
    public boolean packageExists(@PathParam("packageName") final String packageName){
        /* Determine if package exists in repository */
        final boolean packageExists = repository.containsPackage(packageName);
        return packageExists;
    }
    
    @GET
    @Path("{packageName}/assets/{assetName}/exists")
    public boolean assetExists(@PathParam("packageName") final String packageName,@PathParam("assetName") final String assetName){
        /* Asset does not exist if package does not exist */
        final boolean packageExists = repository.containsPackage(packageName);
        if (!packageExists){
            return false;
        }
        
        /* Load package and determine if it contains an asset */
        final PackageItem packageItem = repository.loadPackage(packageName);
        return packageItem.containsAsset(assetName);
    }
    
    @POST
    @Path("{packageName}/assets")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Asset createAssetFromBinaryAndJAXB(@PathParam("packageName") String packageName, @Multipart(value = "asset", type = MediaType.APPLICATION_JSON) Asset asset,
            @Multipart(value = "binary", type = MediaType.APPLICATION_OCTET_STREAM) InputStream is) {
        /* Verify passed in asset object */
        if (asset == null || asset.getMetadata() == null ){
            throw new WebApplicationException(Response.status(500).entity("Request must contain asset and metadata").build());
        }
        final String assetName = asset.getMetadata().getTitle();
        
        /* Check for existence of asset name */
        if (assetName == null) {
            throw new WebApplicationException(Response.status(500).entity("Asset name must be specified (Asset.metadata.title)").build());
        } 
        
        AssetItem ai = repository.loadPackage(packageName).addAsset(assetName, asset.getDescription());
        ai.checkout();
        ai.updateBinaryContentAttachmentFileName(asset.getMetadata().getBinaryContentAttachmentFileName());
        ai.updateFormat(asset.getMetadata().getFormat());
        ai.updateBinaryContentAttachment(is);
        ai.getPackage().updateBinaryUpToDate(false);
        ai.checkin(asset.getCheckInComment());
        repository.save();
        return asset;
    }
    
    @PUT
    @Path("{packageName}/assets/{assetName}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Asset updateAssetFromBinaryAndJAXB(@PathParam("packageName") final String packageName, @Multipart(value = "asset", type = MediaType.APPLICATION_JSON) Asset asset,
            @Multipart(value = "binary", type = MediaType.APPLICATION_OCTET_STREAM) InputStream is, @PathParam("assetName") final String assetName) {
        /* Verify passed in asset object */
        if (asset == null || asset.getMetadata() == null ){
            throw new WebApplicationException(Response.status(500).entity("Request must contain asset and metadata").build());
        }
        
        /* Asset must exist to update */
        if (!assetExists(packageName, assetName)){
            throw new WebApplicationException(Response.status(500).entity("Asset [" + assetName + "] does not exist in package [" + packageName + "]").build());
        }
        
        AssetItem ai = repository.loadPackage(packageName).loadAsset(assetName);
        ai.checkout();
        ai.updateDescription(asset.getDescription());
        ai.updateBinaryContentAttachmentFileName(asset.getMetadata().getBinaryContentAttachmentFileName());
        ai.updateFormat(asset.getMetadata().getFormat());
        ai.updateBinaryContentAttachment(is);
        ai.getPackage().updateBinaryUpToDate(false);
        ai.checkin(asset.getCheckInComment());
        repository.save();
        return asset;
    }
}


