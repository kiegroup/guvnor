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
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.server.builder.PackageDRLAssembler;
import org.drools.guvnor.server.jaxrs.jaxb.Asset;
import org.drools.guvnor.server.jaxrs.jaxb.Package;
import org.drools.repository.AssetHistoryIterator;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleHistoryIterator;
import org.drools.repository.ModuleItem;
import org.drools.repository.ModuleIterator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.drools.guvnor.server.jaxrs.Translator.*;

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
@Path("/packages")
@RequestScoped
@Named
public class PackageResource extends Resource {
    private HttpHeaders headers;

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
        ModuleIterator iter = rulesRepository.listModules();
        while (iter.hasNext()) {
            ModuleItem item = iter.next();
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
        ModuleIterator iter = rulesRepository.listModules();
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
         * Passes the DRL to the FileManagerService and has it import the asset as
         * a package
         */
        try {
            String packageName = fileManagerService.importClassicDRL(is, null);
            return toPackageEntryAbdera(rulesRepository.loadModule(packageName), uriInfo);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Package createPackageFromDRLAndReturnAsJaxB(InputStream is) {
        /*
         * Passes the DRL to the FileManagerService and has it import the asset as
         * a package
         */
        try {
            String packageName = fileManagerService.importClassicDRL(is, null);
            return toPackage(rulesRepository.loadModule(packageName), uriInfo);
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry createPackageFromAtom(Entry entry) {
        try {
            ModuleItem packageItem = rulesRepository.createModule(entry.getTitle(), entry.getSummary());
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
            ModuleItem packageItem = rulesRepository.createModule(p.getTitle(), p.getDescription());
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
            ModuleItem packageItem = rulesRepository.loadModule(packageName);
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
            ModuleItem packageItem = rulesRepository.loadModule(packageName);
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
            ModuleItem packageItem = rulesRepository.loadModule(packageName);
            PackageDRLAssembler asm = new PackageDRLAssembler(packageItem);
            String drl = asm.getDRL();
            return Response.ok(drl).header("Content-Disposition", "attachment; filename=" + packageName).
                    header("Last-Modified", createDateFormat().format(this.convertToGmt(packageItem.getLastModified()).getTime())).build();
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
            ModuleItem p = rulesRepository.loadModule(packageName);
            String fileName = packageName + ".pkg";
            byte[] result;
            if (p.isBinaryUpToDate()) {
                result = p.getCompiledPackageBytes();
            } else {
                BuilderResult builderResult = repositoryPackageService.buildPackage(p.getUUID(), true);
                if (builderResult != null) {
                    StringBuilder errs = new StringBuilder();
                    errs.append("Unable to build package name [").append(packageName).append("]\n");
                    for (BuilderResultLine resultLine : builderResult.getLines()) {
                        errs.append(resultLine.toString()).append("\n");
                    }
                    return Response.status(500).entity(errs.toString()).build();
                }
                result = rulesRepository.loadModule(packageName).getCompiledPackageBytes();
            }
            return Response.ok(result).header("Content-Disposition", "attachment; filename=" + fileName).
                    header("Last-Modified", createDateFormat().format(this.convertToGmt(p.getLastModified()).getTime())).build();
        } catch (Exception e) {
            //catch RulesRepositoryException and other exceptions. For example when the package does not exists.
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/versions")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getPackageVersionsAsFeed(@PathParam("packageName") String packageName) throws SerializationException {
        ModuleItem p = rulesRepository.loadModule(packageName);

        Factory factory = Abdera.getNewFactory();
        Feed f = factory.getAbdera().newFeed();
        f.setTitle("Version history of " + p.getName());
        f.setBaseUri(uriInfo.getBaseUriBuilder().path("packages").build().toString());
        ModuleHistoryIterator it = p.getHistory();

        while (it.hasNext()) {
            try {
                ModuleItem historicalPackage = it.next();
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
        return toPackageEntryAbdera(rulesRepository.loadModule(packageName, versionNumber), uriInfo);
    }

    @GET
    @Path("{packageName}/versions/{versionNumber}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getHistoricalPackageSource(@PathParam("packageName") String packageName,
                                               @PathParam("versionNumber") long versionNumber) {
        ModuleItem item = rulesRepository.loadModule(packageName, versionNumber);
        PackageDRLAssembler asm = new PackageDRLAssembler(item);
        String drl = asm.getDRL();
        return Response.ok(drl).header("Content-Disposition", "attachment; filename=" + packageName).
                    header("Last-Modified", createDateFormat().format(this.convertToGmt(item.getLastModified()).getTime())).build();
    }

    @GET
    @Path("{packageName}/versions/{versionNumber}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getHistoricalPackageBinary(@PathParam("packageName") String packageName,
                                               @PathParam("versionNumber") long versionNumber) throws SerializationException {
        ModuleItem p = rulesRepository.loadModule(packageName, versionNumber);
        byte[] result = p.getCompiledPackageBytes();
        if (result != null) {
            String fileName = packageName + ".pkg";
            return Response.ok(result).header("Content-Disposition", "attachment; filename=" + fileName).
                    header("Last-Modified", createDateFormat().format(this.convertToGmt(p.getLastModified()).getTime())).build();
        } else {
            return Response.status(500).entity("This package version has no compiled binary").type("text/plain").build();
        }
    }

    @PUT
    @Path("{packageName}")
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    public void updatePackageFromAtom(@PathParam("packageName") String packageName, Entry entry) {
        try {
            ModuleItem p = rulesRepository.loadModule(packageName);
            p.checkout();
            // TODO: support rename package.
            // p.updateTitle(entry.getTitle());

            if (entry.getSummary() != null) {
                p.updateDescription(entry.getSummary());
            }
            // TODO: support LastContributor
            if (entry.getAuthor() != null) {
            }

            ExtensibleElement metadataExtension = entry
                    .getExtension(Translator.METADATA);
            if (metadataExtension != null) {
                ExtensibleElement archivedExtension = metadataExtension
                        .getExtension(Translator.ARCHIVED);
                if (archivedExtension != null) {
                    p.archiveItem(Boolean.getBoolean(archivedExtension
                            .getSimpleExtension(Translator.VALUE)));
                }

                // TODO: Package state is not fully supported yet
                /*
                 * ExtensibleElement stateExtension =
                 * metadataExtension.getExtension(Translator.STATE);
                 * if(stateExtension != null) {
                 * p.updateState(stateExtension.getSimpleExtension
                 * (Translator.STATE)); }
                 */
            }

            p.checkin("Updated from ATOM.");
            rulesRepository.save();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @PUT
    @Path("{packageName}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updatePackageFromJAXB(@PathParam("packageName") String packageName, Package p) {
        try {
            ModuleItem item = rulesRepository.loadModule(packageName);
            item.checkout();
            item.updateDescription(p.getDescription());
            item.updateTitle(p.getTitle());
            /* TODO: add more updates to package item from JSON */
            item.checkin(p.getCheckInComment());
            rulesRepository.save();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @DELETE
    @Path("{packageName}")
    public void deletePackage(@PathParam("packageName") String packageName) {
        try {
            //Throws RulesRepositoryException if the package does not exist
            ModuleItem p = rulesRepository.loadModule(packageName);
            repositoryPackageService.removeModule(p.getUUID());
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
            ModuleItem p = rulesRepository.loadModule(packageName);
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
            ModuleItem p = rulesRepository.loadModule(packageName);
            
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
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName);
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
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName);
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
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName);
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
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName);
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

            AssetItem ai = rulesRepository.loadModule(packageName).addAsset(entry.getTitle(), entry.getSummary(), initialCategory, format);
            
            //The categories are not saved by addAsset(). Need to force it here.
            rulesRepository.getSession().save();
            
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
            if(assetName.lastIndexOf(".") != -1) {
                fileName = assetName.substring(0, assetName.lastIndexOf("."));                
                extension = assetName.substring(assetName.lastIndexOf(".")+1); 
            } else {
                fileName = assetName;                
            }            
            
            AssetItem ai = rulesRepository.loadModule(packageName).addAsset(fileName, "");
            ai.checkout();
            ai.updateBinaryContentAttachmentFileName(fileName);
            if(extension != null) {
                ai.updateFormat(extension);
            }
            ai.updateBinaryContentAttachment(is);
            ai.getModule().updateBinaryUpToDate(false);
            ai.checkin("update binary");
            rulesRepository.save();
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
            AssetItem ai = rulesRepository.loadModule(packageName).loadAsset(assetName);
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
            rulesRepository.save();
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
            AssetItem ai = rulesRepository.loadModule(packageName).loadAsset(assetName);
            /* Update asset */
            ai.checkout();
            ai.updateTitle(asset.getMetadata().getTitle());
            ai.updateDescription(asset.getDescription());
            ai.checkin(asset.getCheckInComment());
            rulesRepository.save();
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
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName);
            asset.checkout();
            asset.updateContent(content);
            asset.checkin("Updated asset source from REST interface");
            rulesRepository.save();
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
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName);
            asset.checkout();
            asset.updateBinaryContentAttachment(is);
            asset.checkin("Update binary");
            rulesRepository.save();
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
            AssetItem ai = rulesRepository.loadModule(packageName).loadAsset( assetName );
            // assetService.archiveAsset(ai.getUUID());
            repositoryAssetService.removeAsset(ai.getUUID());
            rulesRepository.save();
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
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName);
            
            Factory factory = Abdera.getNewFactory();
            Feed f = factory.getAbdera().newFeed();
            f.setTitle("Version history of " + asset.getName());

            URI base;
            if (asset.isHistoricalVersion()) {
                base = uriInfo.getBaseUriBuilder()
                        .path("packages/{packageName}/assets/{assetName}/versions/{versionNumber}")
                        .build(asset.getModuleName(), asset.getName(), Long.toString(asset.getVersionNumber()));
            } else {
                base = uriInfo.getBaseUriBuilder()
                        .path("packages/{packageName}/assets/{assetName}/versions")
                        .build(asset.getModuleName(), asset.getName());
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
                                .build(asset.getModuleName(), asset.getName(),
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
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName, versionNumber);
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
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName, versionNumber);
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
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName, versionNumber);
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
    
    private DateFormat createDateFormat(){
        return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
    }
    
    private Calendar convertToGmt(Calendar cal) {

        Date date = cal.getTime();
        TimeZone tz = cal.getTimeZone();

        //Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT 
        long msFromEpochGmt = date.getTime();

        //gives you the current offset in ms from GMT at the current date
        int offsetFromUTC = tz.getOffset(msFromEpochGmt);

        //create a new calendar in GMT timezone, set to this date and add the offset
        Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmtCal.setTime(date);
        gmtCal.add(Calendar.MILLISECOND, offsetFromUTC);

        return gmtCal;
    }
}


