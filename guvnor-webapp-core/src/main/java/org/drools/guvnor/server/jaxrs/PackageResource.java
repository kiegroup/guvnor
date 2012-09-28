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
import org.drools.guvnor.server.jaxrs.jaxb.*;
import org.drools.guvnor.server.jaxrs.jaxb.Package;
import org.drools.guvnor.server.jaxrs.providers.atom.Entry;
import org.drools.guvnor.server.jaxrs.providers.atom.Feed;
import org.drools.guvnor.server.jaxrs.providers.atom.Link;
import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;


import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.server.builder.ModuleAssembler;
import org.drools.guvnor.server.builder.ModuleAssemblerManager;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.repository.AssetHistoryIterator;
import org.drools.repository.AssetItem;
import org.drools.repository.ModuleHistoryIterator;
import org.drools.repository.ModuleItem;
import org.drools.repository.ModuleIterator;
import org.jboss.seam.security.annotations.LoggedIn;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

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
import javax.xml.bind.JAXBException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
@Path("/packages")
@RequestScoped
@Named
@LoggedIn
@GZIP
public class PackageResource extends Resource {
    private HttpHeaders headers;

    @Context
    public void setHttpHeaders(HttpHeaders theHeaders) {
        headers = theHeaders;
    }

    @GET
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getPackagesAsFeed() {
        Feed f = new Feed();
        f.setTitle("Packages");
        f.setBase(uriInfo.getBaseUriBuilder().path("packages").build());
        ModuleIterator iter = rulesRepository.listModules();
        while (iter.hasNext()) {
            ModuleItem item = iter.next();
            Entry e = new Entry();
            e.setTitle(item.getName());
            Link l = new Link();
            l.setHref(uriInfo.getBaseUriBuilder()
                    .path("packages/{itemName}")
                    .build(item.getName()));
            e.getLinks().add(l);
            f.getEntries().add(e);
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
            //NOTE: no need to call packageItem.updateBinaryUpToDate(false) as this done by importClassicDRL
            return toPackageEntryAbdera(rulesRepository.loadModule(packageName), uriInfo);
        } catch (RuntimeException e) {
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
            //NOTE: no need to call packageItem.updateBinaryUpToDate(false) as this done by importClassicDRL
            return toPackage(rulesRepository.loadModule(packageName), uriInfo);
        } catch (RuntimeException e) {
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry createPackageFromAtom(Entry entry) {
        try {
            String checkinComment = "Initial";
            entry.getAnyOtherElement();

            AtomPackageMetadata metaData = entry.getAnyOtherJAXBObject(AtomPackageMetadata.class);
            if (metaData != null) {
                checkinComment = metaData.getCheckinComment();
            }
            ModuleItem packageItem = rulesRepository.createModule(entry.getTitle(), entry.getSummary(), ModuleItem.MODULE_FORMAT, null, checkinComment);
            //NOTE: no need to call packageItem.updateBinaryUpToDate(false) for a newly created module as the default value of BinaryUpToDate is false
            return toPackageEntryAbdera(packageItem, uriInfo);
        } catch (JAXBException e) {
            throw new WebApplicationException(e);
        } catch (RuntimeException e) {
            //catch RulesRepositoryException and other exceptions. For example when the package already exists.
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Package createPackageFromJAXB(Package p) {
        try {
            String checkinComment = "Initial";
            if(p.getMetadata() != null && p.getMetadata().getCheckinComment() != null) {
                checkinComment = p.getMetadata().getCheckinComment();
            }
            
            ModuleItem packageItem = rulesRepository.createModule(p.getTitle(), p.getDescription(), ModuleItem.MODULE_FORMAT, null, checkinComment);
            //NOTE: no need to call packageItem.updateBinaryUpToDate(false) for a newly created module as the default value of BinaryUpToDate is false
            return toPackage(packageItem, uriInfo);
        } catch (RuntimeException e) {
            //catch RulesRepositoryException and other exceptions. For example when the package already exists.
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry getPackageAsEntry(@PathParam("packageName") String packageName) {
        if (!rulesRepository.containsModule(packageName)) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Package [" + packageName + "] does not exist").build());
        }
        try {
            ModuleItem packageItem = rulesRepository.loadModule(packageName);
            return toPackageEntryAbdera(packageItem, uriInfo);
        } catch (RuntimeException e) {
            //catch RulesRepositoryException and other exceptions. For example when the package does not exists.
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Package getPackageAsJAXB(@PathParam("packageName") String packageName) {
        if (!rulesRepository.containsModule(packageName)) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Package [" + packageName + "] does not exist").build());
        }
        try {
            ModuleItem packageItem = rulesRepository.loadModule(packageName);
            return toPackage(packageItem, uriInfo);
        } catch (RuntimeException e) {
            //catch RulesRepositoryException and other exceptions. For example when the package does not exists.
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getPackageSource(@PathParam("packageName") String packageName) {
        if (!rulesRepository.containsModule(packageName)) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Package [" + packageName + "] does not exist").build());
        }
        try {
            ModuleItem moduleItem = rulesRepository.loadModule(packageName);
            ModuleAssembler moduleAssembler = ModuleAssemblerManager.getModuleAssembler(moduleItem.getFormat(), moduleItem, null);
            String drl = moduleAssembler.getCompiledSource();
            return Response.ok(drl).header("Content-Disposition", "attachment; filename=" + packageName).
                    header("Last-Modified", createDateFormat().format(this.convertToGmt(moduleItem.getLastModified()).getTime())).build();
        } catch (RuntimeException e) {
            //catch RulesRepositoryException and other exceptions. For example when the package does not exists.
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPackageBinary(@PathParam("packageName") String packageName) throws SerializationException {
        if (!rulesRepository.containsModule(packageName)) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Package [" + packageName + "] does not exist").build());
        }
        try {
            ModuleItem p = compileModuleIfNeeded(packageName);
            byte[] result = p.getCompiledBinaryBytes();
            ModuleAssembler moduleAssembler = ModuleAssemblerManager.getModuleAssembler(p.getFormat(), p, null);
            String fileName = packageName + "." + moduleAssembler.getBinaryExtension();
            return Response.ok(result).header("Content-Disposition", "attachment; filename=" + fileName).
                    header("Last-Modified", createDateFormat().format(this.convertToGmt(p.getLastModified()).getTime())).build();
        } catch (RuntimeException e) {
            //catch RulesRepositoryException and other exceptions. For example when the package does not exists.
            throw new WebApplicationException(e);
        }
    }

    private ModuleItem compileModuleIfNeeded(String packageName) throws SerializationException {
        ModuleItem p = rulesRepository.loadModule(packageName);
        if (p.isBinaryUpToDate()) {
            return p;
        } else {
            BuilderResult builderResult = repositoryPackageService.buildPackage(p.getUUID(), true);
            if (builderResult != null && !builderResult.getLines().isEmpty()) {
                StringBuilder errs = new StringBuilder();
                errs.append("Unable to build package name [").append(packageName).append("]\n");
                for (BuilderResultLine resultLine : builderResult.getLines()) {
                    errs.append(resultLine.toString()).append("\n");
                }
                throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(errs.toString()).build());
            }
            return rulesRepository.loadModule(packageName);
        }
    }


    @GET
    @Path("{packageName}/versions")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getPackageVersionsAsFeed(@PathParam("packageName") String packageName) throws SerializationException {
        if (!rulesRepository.containsModule(packageName)) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Package [" + packageName + "] does not exist").build());
        }
        ModuleItem p = rulesRepository.loadModule(packageName);

        Feed f = new Feed();
        f.setTitle("Version history of " + p.getName());
        f.setBase(uriInfo.getBaseUriBuilder().path("packages").build());
        ModuleHistoryIterator it = p.getHistory();

        while (it.hasNext()) {
            try {
                ModuleItem historicalPackage = it.next();
                if (historicalPackage.getVersionNumber() != 0) {
                    Entry e = new Entry();
                    e.setTitle(Long.toString(historicalPackage
                            .getVersionNumber()));
                    e.setUpdated(historicalPackage.getLastModified().getTime());
                    Link l = new Link();
                    l.setHref(uriInfo
                            .getBaseUriBuilder()
                            .path("packages/{packageName}/versions/{versionNumber}")
                            .build(p.getName(), Long.toString(historicalPackage.getVersionNumber())));
                    e.getLinks().add(l);
                    f.getEntries().add(e);
                }
            } catch (RuntimeException e) {
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
        ModuleAssembler moduleAssembler = ModuleAssemblerManager.getModuleAssembler(item.getFormat(), item, null);
        String drl = moduleAssembler.getCompiledSource();
        return Response.ok(drl).header("Content-Disposition", "attachment; filename=" + packageName).
                    header("Last-Modified", createDateFormat().format(this.convertToGmt(item.getLastModified()).getTime())).build();
    }

    @GET
    @Path("{packageName}/versions/{versionNumber}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getHistoricalPackageBinary(@PathParam("packageName") String packageName,
                                               @PathParam("versionNumber") long versionNumber) throws SerializationException {
        ModuleItem p = rulesRepository.loadModule(packageName, versionNumber);
        byte[] result = p.getCompiledBinaryBytes();
        
        //REVISIT: https://issues.jboss.org/browse/GUVNOR-1232: Force a package rebuild before every package check in operation. 
        if (result != null) {
            ModuleAssembler moduleAssembler = ModuleAssemblerManager.getModuleAssembler(p.getFormat(), p, null);
            String fileName = packageName + "." + moduleAssembler.getBinaryExtension();
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
            ModuleItem existingModuleItem = rulesRepository.loadModule(packageName);
            
            //Rename:
            if (!existingModuleItem.getTitle().equalsIgnoreCase(entry.getTitle())) {
                rulesRepository.renameModule(existingModuleItem.getUUID(), entry.getTitle());
            }

            if (entry.getSummary() != null) {
                existingModuleItem.updateDescription(entry.getSummary());
            }
            
            // TODO: support LastContributor
            if (!entry.getAuthors().isEmpty()) {
            }

            String checkinComment = "";

           AtomPackageMetadata packageMetadata = entry.getAnyOtherJAXBObject(AtomPackageMetadata.class);
           if ( packageMetadata != null ) {
               existingModuleItem.archiveItem(packageMetadata.isArchived());
               checkinComment = packageMetadata.getCheckinComment();
           }

            existingModuleItem.updateBinaryUpToDate(false);
            existingModuleItem.checkin(checkinComment);
            rulesRepository.save();
       } catch (JAXBException e) {
           throw new WebApplicationException(e);
       } catch (RuntimeException e) {
            throw new WebApplicationException(e);
        }
    }

    @PUT
    @Path("{packageName}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updatePackageFromJAXB(@PathParam("packageName") String packageName, Package module) {
        try {
            ModuleItem existingModuleItem = rulesRepository.loadModule(packageName);
            
            //Rename:
            if (!existingModuleItem.getTitle().equalsIgnoreCase(module.getTitle())) {
                rulesRepository.renameModule(existingModuleItem.getUUID(), module.getTitle());
            }
            
            existingModuleItem.updateDescription(module.getDescription());
            
            /* TODO: add more updates to package item from JSON */
            existingModuleItem.updateBinaryUpToDate(false);
            existingModuleItem.checkin(module.getMetadata().getCheckinComment());
            rulesRepository.save();
        } catch (RuntimeException e) {
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
        } catch (RuntimeException e) {
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
            Feed feed = new Feed();
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
                feed.getEntries().add(toAssetEntryAbdera(iter.next(), uriInfo));
            return feed;
        } catch (RuntimeException e) {
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
        } catch (RuntimeException e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/assets/{assetName}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry getAssetAsAtom(@PathParam("packageName") String packageName, @PathParam("assetName") String assetName) {
        if (!assetExists(packageName, assetName)) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Asset [" + assetName + "] of package [" + packageName + "] does not exist").build());
        }
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName);
            return toAssetEntryAbdera(asset, uriInfo);
        } catch (RuntimeException e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/assets/{assetName}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})

    public Asset getAssetAsJaxB(@PathParam("packageName") String packageName, @PathParam("assetName") String assetName) {
        if (!assetExists(packageName, assetName)) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Asset [" + assetName + "] of package [" + packageName + "] does not exist").build());
        }
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName);
            return toAsset(asset, uriInfo);
        } catch (RuntimeException e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/assets/{assetName}/binary")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getAssetBinary(@PathParam("packageName") String packageName, @PathParam("assetName") String assetName) {
        if (!assetExists(packageName, assetName)) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Asset [" + assetName + "] of package [" + packageName + "] does not exist").build());
        }
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName);
            String fileName = null;
            String binaryContentAttachmentFileName = asset.getBinaryContentAttachmentFileName();                    
            //Note the file extension name may not be same as asset format name in some cases.
            if(binaryContentAttachmentFileName !=null && !"".equals(binaryContentAttachmentFileName)) {
                fileName = binaryContentAttachmentFileName;
            } else {
                fileName = asset.getName() + "." + asset.getFormat();
            }

            return Response.ok(asset.getBinaryContentAttachment()).header("Content-Disposition", "attachment; filename=" + fileName).build();
        } catch (RuntimeException e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("{packageName}/assets/{assetName}/source")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAssetSource(@PathParam("packageName") String packageName, @PathParam("assetName") String assetName) {
        if (!assetExists(packageName, assetName)) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Asset [" + assetName + "] of package [" + packageName + "] does not exist").build());
        }
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName);
            return asset.getContent();
        } catch (RuntimeException e) {
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
            String[] categories = null;
            AtomAssetMetadata assetMetadata = entry.getAnyOtherJAXBObject(AtomAssetMetadata.class);
            categories = assetMetadata.getCategories();

            AssetItem ai = rulesRepository.loadModule(packageName).addAsset(entry.getTitle(), entry.getSummary(), null, assetMetadata.getFormat());

            if (categories != null) {
                ai.updateCategoryList(categories);
            }
            
            //The categories are not saved by addAsset(). Need to force it here.
            ai.getModule().updateBinaryUpToDate(false);
            rulesRepository.save();

            return toAssetEntryAbdera(ai, uriInfo);
        } catch (JAXBException e) {
            throw new WebApplicationException(e);
        } catch (RuntimeException e) {
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
                try {
                    assetName = URLDecoder.decode(assetName, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("This server does not support UTF-8 encoding.", e);
                }
            }
            String fileName = null;
            String extension = null;
            if (assetName.lastIndexOf(".") != -1) {
                fileName = assetName.substring(0, assetName.lastIndexOf("."));                
                extension = assetName.substring(assetName.lastIndexOf(".")+1); 
            } else {
                fileName = assetName;                
            }            
            
            AssetItem ai = rulesRepository.loadModule(packageName).addAsset(fileName, "");
            ai.checkout();
            ai.updateBinaryContentAttachmentFileName(fileName);
            
            //Asset format and asset file extension are 2 different things. We simply do not have the asset type information available when the 
            //asset is created from binary. The asset format needs to be filled by a following update operation.
            //But we can do our best to guess the format here, i.e., assume the format is equal to the extension type. User can always update
            //the format later on
            if (extension != null) {
                ai.updateFormat(extension);
            }
            ai.updateBinaryContentAttachment(is);
            ai.getModule().updateBinaryUpToDate(false);
            ai.updateValid(assetValidator.validate(ai));
            ai.checkin("update binary");
            rulesRepository.save();
            return toAssetEntryAbdera(ai, uriInfo);
        } catch (RuntimeException e) {
            //catch RulesRepositoryException and other exceptions. For example when the package already exists.
            throw new WebApplicationException(e);
        }
    }

    @PUT
    @Path("{packageName}/assets/{assetName}")
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    public void updateAssetFromAtom(@PathParam("packageName") String packageName, @PathParam("assetName") String assetName, Entry assetEntry) {
        try {
            AtomAssetMetadata assetMetadata = assetEntry.getAnyOtherJAXBObject(AtomAssetMetadata.class);

            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem ai = rulesRepository.loadModule(packageName).loadAsset(assetName);
            //Update asset 
            ai.checkout();
            ai.updateTitle(assetEntry.getTitle());
            ai.updateDescription(assetEntry.getSummary());
            if (assetMetadata.getFormat() != null) {
                ai.updateFormat(assetMetadata.getFormat());
            }
            
            //REVISIT: What if the client really wants to set content to ""?
            if (assetEntry.getContent().getText() != null && !"".equals(assetEntry.getContent().getText())) {
                ai.updateContent(assetEntry.getContent().getText());
            }
            if (assetMetadata.getCategories() != null && assetMetadata.getCategories().length > 0) {
                ai.updateCategoryList(assetMetadata.getCategories());
            }
            if (assetMetadata.getState() != null) {
                ai.updateState(assetMetadata.getState());
            }
            ai.updateValid(assetValidator.validate(ai));
            if (AssetFormats.affectsBinaryUpToDate(ai.getFormat())) {
                ModuleItem pkg = ai.getModule();
                pkg.updateBinaryUpToDate(false);
                RuleBaseCache.getInstance().remove(pkg.getUUID());
            }
            ai.checkin("Check-in (summary): " + assetEntry.getSummary());
            rulesRepository.save();
        } catch (JAXBException e) {
            throw new WebApplicationException(e);
        } catch (RuntimeException e) {
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
            ai.updateTitle(asset.getTitle());
            ai.updateDescription(asset.getDescription());
            ai.updateValid(assetValidator.validate(ai));
            if (AssetFormats.affectsBinaryUpToDate(ai.getFormat())) {
                ModuleItem pkg = ai.getModule();
                pkg.updateBinaryUpToDate(false);
                RuleBaseCache.getInstance().remove(pkg.getUUID());
            }
            ai.checkin(asset.getMetadata().getCheckInComment());
            rulesRepository.save();
        } catch (RuntimeException e) {
            throw new WebApplicationException(e);
        }
    }

    @PUT
    @Path("{packageName}/assets/{assetName}/source")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN})
    @Produces({MediaType.WILDCARD})
    public void updateAssetSource(@PathParam("packageName") String packageName, @PathParam("assetName") String assetName, String content) {
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName);
            asset.checkout();
            asset.updateContent(content);
            asset.updateValid(assetValidator.validate(asset));
            if (AssetFormats.affectsBinaryUpToDate(asset.getFormat())) {
                ModuleItem pkg = asset.getModule();
                pkg.updateBinaryUpToDate(false);
                RuleBaseCache.getInstance().remove(pkg.getUUID());
            }
            asset.checkin("Updated asset source from REST interface");
            rulesRepository.save();
        } catch (RuntimeException e) {
            throw new WebApplicationException(e);
        }
    }

    @PUT
    @Path("{packageName}/assets/{assetName}/binary")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.WILDCARD)
    public void updateAssetBinary(@PathParam("packageName") String packageName, @PathParam("assetName") String assetName, InputStream is) {
        try {
            //Throws RulesRepositoryException if the package or asset does not exist
            AssetItem asset = rulesRepository.loadModule(packageName).loadAsset(assetName);
            asset.checkout();
            asset.updateBinaryContentAttachment(is);
            asset.updateValid(assetValidator.validate(asset));
            if (AssetFormats.affectsBinaryUpToDate(asset.getFormat())) {
                ModuleItem pkg = asset.getModule();
                pkg.updateBinaryUpToDate(false);
                RuleBaseCache.getInstance().remove(pkg.getUUID());
            }
            asset.checkin("Update binary");
            rulesRepository.save();
        } catch (RuntimeException e) {
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
            ModuleItem module = ai.getModule();
            // assetService.archiveAsset(ai.getUUID());
            repositoryAssetService.removeAsset(ai.getUUID());
            module.updateBinaryUpToDate(false);
            rulesRepository.save();
        } catch (RuntimeException e) {
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
            
            Feed f = new Feed();
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
            f.setBase(base);
                        
            AssetHistoryIterator it = asset.getHistory();
            while (it.hasNext()) {
                AssetItem historicalAsset = it.next();
                if (historicalAsset.getVersionNumber() != 0) {
                    Entry e = new Entry();
                    e.setTitle(Long.toString(historicalAsset
                            .getVersionNumber()));
                    e.setUpdated(historicalAsset.getLastModified().getTime());
                    Link l = new Link();
                    l.setHref(uriInfo
                            .getBaseUriBuilder()
                            .path("packages/{packageName}/assets/{assetName}/versions/{versionNumber}")
                            .build(asset.getModuleName(), asset.getName(),
                                    Long.toString(historicalAsset.getVersionNumber())));
                    e.getLinks().add(l);
                    f.getEntries().add(e);
                }
            }
            return f;
        } catch (RuntimeException e) {
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
        } catch (RuntimeException e) {
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
        } catch (RuntimeException e) {
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
            String fileName = null;
            String binaryContentAttachmentFileName = asset.getBinaryContentAttachmentFileName();                    
            //Note the file extension name may not be same as asset format name in some cases.
            if(binaryContentAttachmentFileName !=null && !"".equals(binaryContentAttachmentFileName)) {
                fileName = binaryContentAttachmentFileName;
            } else {
                fileName = asset.getName() + "." + asset.getFormat();
            }
            return Response.ok(asset.getBinaryContentAttachment()).header("Content-Disposition", "attachment; filename=" + fileName).build();
        } catch (RuntimeException e) {
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
            @PathParam("snapshotName") final String snapshotName) throws SerializationException {
        compileModuleIfNeeded(packageName);
        repositoryModuleOperations.createModuleSnapshot(packageName,
                snapshotName, true, "REST API Snapshot");
    }

    private boolean assetExists(final String packageName, final String assetName) {
        /* Asset does not exist if package does not exist */
        final boolean packageExists = rulesRepository.containsModule(packageName);
        if (!packageExists){
            return false;
        }

        /* Load module and determine if it contains an asset */
        final ModuleItem packageItem = rulesRepository.loadModule(packageName);
        return packageItem.containsAsset(assetName);
    }

    @POST
    @Path("{packageName}/assets")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Asset createAssetFromBinaryAndJAXB(@PathParam("packageName") String packageName, @MultipartForm AssetMultipartForm assetMultipartForm) {
        /* Verify passed in asset object */
        if (assetMultipartForm == null || assetMultipartForm.getAsset() == null || assetMultipartForm.getAsset().getMetadata() == null ){
            throw new WebApplicationException(Response.status(500).entity("Request must contain asset and metadata").build());
        }
        final String assetName = assetMultipartForm.getAsset().getTitle();

        /* Check for existence of asset name */
        if (assetName == null) {
            throw new WebApplicationException(Response.status(500).entity("Asset name must be specified (Asset.metadata.title)").build());
        }

        AssetItem ai = rulesRepository.loadModule(packageName).addAsset(assetName, assetMultipartForm.getAsset().getDescription());
        ai.checkout();
        ai.updateBinaryContentAttachmentFileName(assetMultipartForm.getAsset().getBinaryContentAttachmentFileName());
        ai.updateFormat(assetMultipartForm.getAsset().getMetadata().getFormat());
        ai.updateBinaryContentAttachment(assetMultipartForm.getBinary());
        ai.getModule().updateBinaryUpToDate(false);
        ai.updateValid(assetValidator.validate(ai));
        ai.checkin(assetMultipartForm.getAsset().getMetadata().getCheckInComment());
        rulesRepository.save();
        return assetMultipartForm.getAsset();
    }

    @PUT
    @Path("{packageName}/assets/{assetName}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Asset updateAssetFromBinaryAndJAXB(@PathParam("packageName") final String packageName,@MultipartForm AssetMultipartForm assetMultipartForm, @PathParam("assetName") final String assetName) {
        /* Verify passed in asset object */
        if (assetMultipartForm == null || assetMultipartForm.getAsset() == null || assetMultipartForm.getAsset().getMetadata() == null ){
            throw new WebApplicationException(Response.status(500).entity("Request must contain asset and metadata").build());
        }

        /* Asset must exist to update */
        if (!assetExists(packageName, assetName)){
            throw new WebApplicationException(Response.status(500).entity("Asset [" + assetName + "] does not exist in package [" + packageName + "]").build());
        }

        AssetItem ai = rulesRepository.loadModule(packageName).loadAsset(assetName);
        ai.checkout();
        ai.updateDescription(assetMultipartForm.getAsset().getDescription());
        ai.updateBinaryContentAttachmentFileName(assetMultipartForm.getAsset().getBinaryContentAttachmentFileName());
        ai.updateFormat(assetMultipartForm.getAsset().getMetadata().getFormat());
        ai.updateBinaryContentAttachment(assetMultipartForm.getBinary());
        ai.getModule().updateBinaryUpToDate(false);
        ai.updateValid(assetValidator.validate(ai));
        ai.checkin(assetMultipartForm.getAsset().getMetadata().getCheckInComment());
        rulesRepository.save();
        return assetMultipartForm.getAsset();
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


