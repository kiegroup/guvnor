
package org.drools.atom;


import java.io.InputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.drools.repository.Artifact;
import org.drools.repository.ArtifactManager;
import org.drools.repository.AssetItem;
import org.drools.repository.MetaData;
import org.drools.repository.PackageItem;
import org.drools.repository.PackageIterator;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.drools.repository.security.PermissionManager;


/**
 * AtomRulesRepository provides an AtomPub interface on top of RulesRepository. 
 * 
 * A HTTP GET request to URL http://host:portnumber/repository/packages
 * returns a list of packages in the repository in Atom feed format. An example looks like below:
 * 
 * <feed xml:base="http://localhost:9080/repository/packages">
 *   <title type="text">Packages</title>
 *   
 *   <entry xml:base="http://localhost:9080/repository/packages">
 *     <title type="text">defaultPackage</title>
 *     <link href="http://localhost:9080/repository/packages/defaultPackage"/>
 *   </entry>
 * 
 *   <entry xml:base="http://localhost:9080/repository/packages">
 *     <title type="text">testPackage1</title>
 *     <link href="http://localhost:9080/repository/packages/testPackage1"/>
 *   </entry>
 * </feed>
 *
 *
 * You can navigate from packages to a specific package using the URL link returned.
 * A HTTP GET request to URL http://host:portnumber/repository/packages/testPackage1 
 * returns testPackag1 in the repository in Atom entry format. An example looks like below:
 * 
 * <entry xml:base="http://localhost:9080/repository/packages/testPackage1">
 *   <title type="text">testPackage1</title>
 *   <id>5632cf6c-0ef5-4ccc-b7e5-293285c4ce19</id>
 *   <link href="http://localhost:9080/repository/packages/testPackage1"/>
 *   <summary type="text">desc1</summary>
 *   <updated>2008-10-17T08:12:42.046Z</updated>
 *   <content type="text">archived=false</content>
 * </entry>    
 * 
 * A HTTP POST request to URL http://host:portnumber/repository/packages with the data:
 * 
 * <entry xml:base="http://localhost:9080/repository/packages">
 *   <title type="text">testPackage1</title>
 * </entry>   
 * 
 * creates a package named testPackage1 in the repository
 *  
 *  
 * A HTTP PUT request to URL http://host:portnumber/repository/packages with the data:
 * 
 * <entry xml:base="http://localhost:9080/repository/packages">
 *   <title type="text">testPackage1</title>
 *   <summary type="text">desc2</summary>
 *   <content type="text">archived=false</content>
 * </entry>     
 * 
 * updates testPackage1 in the repository

 * A HTTP DELETE request to URL http://host:portnumber/repository/packages/testPackage1  
 * deletes the package testPackage1
 * 
 * 
 * A HTTP GET request to URL http://host:portnumber/repository/packages/testPackage1/assets
 * returns a list of assets under the testPackage1 in the repository in Atom feed format. 
 * An example looks like below:
 * 
 * <feed xmlns="http://www.w3.org/2005/Atom" xmlns:xml="http://www.w3.org/XML/1998/namespace" xml:base="http://localhost:9080/repository/packages/testPackage1/assets">
 *   <title type="text">Packages</title>
 *   
 *   <entry xml:base="http://localhost:9080/repository/packages/testPackage1/assets">
 *     <title type="text">testAsset1</title>
 *     <link href="http://localhost:9080/repository/packages/packageName/asset/testAsset1" />
 *   </entry>
 *   
 *   <entry xml:base="http://localhost:9080/repository/packages/testPackage1/assets">
 *     <title type="text">testAsset2</title>
 *     <link href="http://localhost:9080/repository/packages/packageName/asset/testAsset2" />
 *   </entry>
 * </feed>
 * 
 * NOTE: The mapping between Atom Entry element and Drools PackageItem is as below:
 * 
 * atom:title   - PackageItem.name
 * atom:id      - PackageItem.UUID
 * atom:updated - PackageItem.lastModified 
 * atom:summary - PackageItem.description
 * 
 * @author Jervis Lliu
 */

@Path("/repository/")
public class AtomRulesRepository {
	public RulesRepository repository;

    private HttpHeaders headers;

    public AtomRulesRepository() {
    }
    
    @Context
    public void setHttpHeaders(HttpHeaders theHeaders) {
        headers = theHeaders;
    }
    
    @GET
    @Path("/packages")
    @Produces({"application/atom+xml" })
    public Feed getPackagesAsFeed(@Context UriInfo uParam) {
        System.out.println("----invoking getPackagesAsFeed");

        Factory factory = Abdera.getNewFactory();
        Feed f = factory.newFeed();
        f.setBaseUri(uParam.getAbsolutePath().toString());
		
        f.setTitle("Packages");
        //f.setId("");
        //f.addAuthor("");
        
        boolean archive = false;
		PackageIterator pkgs = repository.listPackages();
		pkgs.setArchivedIterator(archive);
		while (pkgs.hasNext()) {
			PackageItem pkg = (PackageItem) pkgs.next();
            Entry e = createPackageItemEntry(pkg, uParam);
            
            f.addEntry(e);
		}

        return f;
    }
    
    @GET
    @Path("/packages/{packageName}")
    @Produces({"application/atom+xml"})
    public Entry getPackageAsEntry(@PathParam("packageName") String packageName, @Context UriInfo uParam) throws ResourceNotFoundFault {
        System.out.println("----invoking getPackageAsEntry with packageName: " + packageName);
        
        try {
            PackageItem packageItem = repository.loadPackage(packageName);
            return createDetailedPackageItemEntry(packageItem, uParam);
        } catch (RulesRepositoryException e) {
        	ResourceNotFoundDetails details = new ResourceNotFoundDetails();
            details.setName(packageName);
            throw new ResourceNotFoundFault(details);       	
        }
    }
                   
    @POST
    @Path("/packages")
    @Consumes("application/atom+xml")
    @Produces({"application/atom+xml"})
    public Response addPackageAsEntry(Entry e, @Context UriInfo uParam) {
        System.out.println("----invoking addPackageAsEntry with package name: " + e.getTitle());

        try {
        	String packageName = e.getTitle();

        	PackageItem packageItem = repository.createPackage(packageName, "desc");
            
            URI uri = 
            	uParam.getBaseUriBuilder().path("repository").path("packages").path(packageItem.getName()).build();
            return Response.created(uri).entity(e).build();
        } catch (RulesRepositoryException ex) {
            return Response.serverError().build();
        }
    }   
    
    @PUT
    @Path("/packages")
    @Consumes("application/atom+xml")
    @Produces({"application/atom+xml"})
    public Response updatePackageAsEntry(Entry e, @Context UriInfo uParam) {
        System.out.println("----invoking updatePackageAsEntry, package name is: " + e.getTitle());
        try {      	
        	PackageItem item = repository.loadPackage(e.getTitle());
        	
    		item.updateDescription(e.getSummary());
    		//item.archiveItem(data.archived);
    		//item.updateBinaryUpToDate(false);
    		//this.ruleBaseCache.remove(data.uuid);
    		item.checkin(e.getSummary());
    		
            URI uri = 
            	uParam.getBaseUriBuilder().path("repository").path("packages").path(item.getName()).build();
            return Response.ok(uri).entity(e).build();
        } catch (RulesRepositoryException ex) {
        	ex.printStackTrace();
            return Response.serverError().build();
        }
    }
        
    @DELETE
    @Path("/packages/{packageName}/")
    public Response deletePackage(@PathParam("packageName") String packageName) {
        System.out.println("----invoking deletePackage with packageName: " + packageName);
        Response response;

		try {
			PackageItem item = repository.loadPackage(packageName);
			item.remove();
			repository.save();
			
			response = Response.ok().build();
		} catch (RulesRepositoryException e) {
			response = Response.notModified().build();
		}

        return response;
    }
    
    @GET
    @Path("/packages/{packageName}/assets")
    @Produces({"application/atom+xml" })
    public Feed getAssetsAsFeed(@Context UriInfo uParam, @PathParam("packageName") String packageName) {
        System.out.println("----invoking getRulesAsFeed with packageName: " + packageName);

        Factory factory = Abdera.getNewFactory();
        Feed f = factory.newFeed();
        f.setBaseUri(uParam.getAbsolutePath().toString());
		
        f.setTitle("Assets");
        
        PackageItem packageItem = repository.loadPackage(packageName);
        for ( Iterator iter = packageItem.getAssets(); iter.hasNext(); ) {
            AssetItem as = (AssetItem) iter.next();
            Entry e = createAssetItemEntry(as, uParam, packageName);
            
            f.addEntry(e);
        }

        return f;
    }

    @GET
    @Path("/packages/{packageName}/assets/{assetName}")
    @Produces({"application/atom+xml"})
    public Entry getAssetAsEntry(@PathParam("packageName") String packageName, 
    		@Context UriInfo uParam,
    		@PathParam("assetName") String assetName) throws ResourceNotFoundFault {
        System.out.println("----invoking getPackageAsEntry with packageName: " + packageName + ", assetName: " + assetName);
        
        try {             
            PackageItem packageItem = repository.loadPackage(packageName);
            for ( Iterator iter = packageItem.getAssets(); iter.hasNext(); ) {
                AssetItem as = (AssetItem) iter.next();
                if (as.getName().equals(assetName)) {
                	return createDetailedAssetItemEntry(as, uParam);
                }
            }
        } catch (RulesRepositoryException e) {
        	ResourceNotFoundDetails details = new ResourceNotFoundDetails();
            details.setName(packageName);
            throw new ResourceNotFoundFault(details);       	
        }
        
        //TODO: Better exception handling
    	ResourceNotFoundDetails details = new ResourceNotFoundDetails();
        details.setName(assetName);
        throw new ResourceNotFoundFault(details);       	
    }
    
    @GET
    @Path("/artifacts/{artifactName}")
    @Produces({"application/atom+xml"})
    public Entry getArtifactAsEntry(@PathParam("artifactName") String artifactName, 
    		@Context UriInfo uParam) throws ResourceNotFoundFault {
        System.out.println("----invoking getArtifactAsEntry with artifactName: " + artifactName);
        
        try {             
        	ArtifactManager artifactManager = new ArtifactManager(repository);
        	Artifact artifact = artifactManager.getArtifact(artifactName);
        	Map<String, MetaData>metadataTypes = artifactManager.getMetadataTypes();
 
            return createDetailedArtifactEntry(artifact, metadataTypes, uParam);
        } catch (RulesRepositoryException e) {
        	ResourceNotFoundDetails details = new ResourceNotFoundDetails();
            details.setName(artifactName);
            throw new ResourceNotFoundFault(details);       	
        }
     	
    }
    
    @POST
    @Path("/artifacts")
    @Consumes("application/atom+xml")
    @Produces({"application/atom+xml"})
    public Response addArtifactAsEntry(Entry e, @Context UriInfo uParam) {
        System.out.println("----invoking addArtifactAsEntry with package name: " + e.getTitle());

        try {
        	String artifactName = e.getTitle();

        	ArtifactManager artifactManager = new ArtifactManager(repository);
        	Artifact artifact = new Artifact();
        	artifact.setName(artifactName);
        	artifact.setDescription(e.getSummary());
        	artifactManager.createArtifact(artifact);
            
            URI uri = 
            	uParam.getBaseUriBuilder().path("repository").path("artifacts").path( 
            			artifactName).build();
            return Response.created(uri).entity(e).build();
        } catch (RulesRepositoryException ex) {
            return Response.serverError().build();
        }
    }  
    
    @POST
    @Path("/esbs")
    @Consumes("application/esb")
    @Produces({"application/atom+xml"})
    public Response addArtifactAsEntryMediaType(InputStream is, @Context UriInfo uParam) {
        System.out.println("----invoking addArtifactAsEntryMediaType----");

    	String name = null;
    	List<String> slugHeaders = headers.getRequestHeader("Slug");
    	if(slugHeaders != null) {
    		name = slugHeaders.get(0);
    	}
    	
    	if(name == null || name.equals("")) {
    		return Response.serverError().build();
    	}
    	
        try {
        	ArtifactManager artifactManager = new ArtifactManager(repository);
        	artifactManager.createEBSJar(name, is);

         	Artifact artifact = artifactManager.getArtifact(name);
         	
         	Map<String, MetaData>metadataTypes = artifactManager.getMetadataTypes();

            Entry e = createDetailedArtifactEntry(artifact, metadataTypes, uParam);
            URI uri = uParam.getBaseUriBuilder().path("repository").path("artifacts").path(name).build();
            return Response.created(uri).entity(e).build();
        } catch (Exception ex) {
        	ex.printStackTrace();
            return Response.serverError().build();
        }
    } 
    
    @PUT
    @Path("/artifacts")
    @Consumes("application/atom+xml")
    @Produces({"application/atom+xml"})
    public Response updateArtifactAsEntry(Entry e, @Context UriInfo uParam) {
        System.out.println("----invoking updatePackageAsEntry, package name is: " + e.getTitle());
        try {      	
           	ArtifactManager artifactManager = new ArtifactManager(repository);

           	Artifact artifact = artifactManager.getArtifact(e.getTitle());

           	if (e.getSummary() != null) {
				artifact.setDescription(e.getSummary());
			}
           	
           	if (!(e.getContentType() == Content.Type.MEDIA) && e.getContent() != null) {
				artifact.setContent(e.getContent());
			}
           	artifact.setLastModified(Calendar.getInstance());
        	artifactManager.updateArtifact(artifact);
  		
            URI uri = 
            	uParam.getBaseUriBuilder().path("repository").path("artifacts").path(e.getTitle()).build();
            
            Map<String, MetaData>metadataTypes = artifactManager.getMetadataTypes();
        	Entry updatedEntry = createDetailedArtifactEntry(artifact, metadataTypes, uParam);

            return Response.ok(uri).entity(updatedEntry).build();
        } catch (RulesRepositoryException ex) {
        	ex.printStackTrace();
            return Response.serverError().build();
        }
    }    

    @GET
    @Path("/esbs/{esbName}")
    @Produces({"application/esb"})
    public InputStream getArtifactAsEntryMediaType(@PathParam("esbName") String esbName, @Context UriInfo uParam) {
        System.out.println("----invoking getArtifactAsEntryMediaType----");

        try {
        	ArtifactManager artifactManager = new ArtifactManager(repository);
         	InputStream is = artifactManager.getEBSJar(esbName);

            URI uri = uParam.getBaseUriBuilder().path("repository").path("esbs").path(esbName).build();
            return is;
        } catch (Exception ex) {
        	ex.printStackTrace();
        	return null;
            //return Response.serverError().build();
        }
    }
        
    @GET
    @Path("/metadatatypes")
    @Produces({"application/atom+xml" })
    public Feed getMetadataTypesAsFeed(@Context UriInfo uParam) {
        System.out.println("----invoking getMetadataTypesAsFeed");

        Factory factory = Abdera.getNewFactory();
        Feed f = factory.newFeed();
        f.setBaseUri(uParam.getAbsolutePath().toString());
		
        f.setTitle("Metadata types");
        
    	ArtifactManager artifactManager = new ArtifactManager(repository);
    	Map<String, MetaData>metadataTypes = artifactManager.getMetadataTypes();
    	
        for (String key : metadataTypes.keySet()) {
        	MetaData md = metadataTypes.get(key);
           
            f.addEntry(createMetadataTypeEntry(md, uParam));
        }

        return f;
    }    
    
    @POST
    @Path("/metadatatypes")
    @Consumes("application/atom+xml")
    @Produces({"application/atom+xml"})
    public Response addMetaDataTypeAsEntry(Entry e, @Context UriInfo uParam) {
        System.out.println("----invoking addMetaDataTypeAsEntry with metadata name: " + e.getTitle());

        try {
        	MetaData md = new MetaData();
        	md.setMetaDataName(e.getTitle());
        	md.setMetaDataType(e.getContent());
        	md.setDescription(e.getSummary());
        	
        	ArtifactManager artifactManager = new ArtifactManager(repository);
        	artifactManager.createMetadataType(md);
            
            URI uri = 
            	uParam.getBaseUriBuilder().path("repository").path("metadatatypes").path(e.getTitle()).build();
            return Response.created(uri).entity(e).build();
        } catch (RulesRepositoryException ex) {
            return Response.serverError().build();
        }
    }   
    
    @DELETE
    @Path("/metadatatypes/{metadataname}/")
    public Response deleteMetaDataType(@PathParam("metadataname") String metaDataName) {
        System.out.println("----invoking deleteMetaDataType with metaDataName: " + metaDataName);
        Response response;

		try {
        	ArtifactManager artifactManager = new ArtifactManager(repository);
        	
        	artifactManager.deleteMetadataType(metaDataName);
        	
			response = Response.ok().build();
		} catch (RulesRepositoryException e) {
			response = Response.notModified().build();
		}

        return response;
    }
    
    private static Entry createPackageItemEntry(PackageItem pkg, UriInfo baseUri) {
        Factory factory = Abdera.getNewFactory();
        
        Entry e = factory.getAbdera().newEntry();
        if (baseUri != null) {
            e.setBaseUri(baseUri.getAbsolutePath().toString());
        }
        e.setTitle(pkg.getName());
        URI uri = 
        	baseUri.getBaseUriBuilder().path("repository").path("packages").path(pkg.getName()).build();
        e.addLink(uri.toString());

        return e;
    }
    
    private static Entry createAssetItemEntry(AssetItem asset, UriInfo baseUri, String packageName) {
        Factory factory = Abdera.getNewFactory();
        
        Entry e = factory.getAbdera().newEntry();
        if (baseUri != null) {
            e.setBaseUri(baseUri.getAbsolutePath().toString());
        }
        e.setTitle(asset.getName());
        URI uri = 
        	baseUri.getBaseUriBuilder().path("repository").path("packages").path(packageName).path("assets").path(asset.getName()).build();
        e.addLink(uri.toString());

        return e;
    }
    
    private static Entry createDetailedPackageItemEntry(PackageItem pkg, UriInfo baseUri) {
        Factory factory = Abdera.getNewFactory();
        
        Entry e = factory.getAbdera().newEntry();
        if (baseUri != null) {
            e.setBaseUri(baseUri.getAbsolutePath().toString());
        }
        e.setTitle(pkg.getName());
        e.setId(pkg.getUUID());
        e.setSummary(pkg.getDescription());
        
        URI uri = 
        	baseUri.getBaseUriBuilder().path("repository").path("packages").path(pkg.getName()).build();
        e.addLink(uri.toString());
        e.setUpdated(pkg.getLastModified().getTime());
        
        return e;
    }
    
    private static Entry createDetailedAssetItemEntry(AssetItem asset, UriInfo baseUri) {
        Factory factory = Abdera.getNewFactory();
        
        Entry e = factory.getAbdera().newEntry();
        if (baseUri != null) {
            e.setBaseUri(baseUri.getAbsolutePath().toString());
        }
        e.setTitle(asset.getName());
        e.setId(asset.getUUID());
        e.setSummary(asset.getDescription());
        
        URI uri = 
        	baseUri.getBaseUriBuilder().path("repository").path("packages").path(asset.getName()).build();
        e.addLink(uri.toString());
        e.setUpdated(asset.getLastModified().getTime());
        
        //meta data
/*        StringProperty property = e.addExtension(MetaDataExtensionFactory.PROPERTY);
        property.setValue("false");*/
        String NS = "http://overlord.jboss.org/drools/1.0";
        QName METADATA = new QName(NS, "metadata");
        
        ExtensibleElement extension = e.addExtension(METADATA);
        //extension.declareNS(NS, "drools");
        QName PROPERTY = new QName(NS, "property");
        ExtensibleElement childExtension = extension.addExtension(PROPERTY);
        childExtension.setAttributeValue("name", "archived");
        childExtension.setText(asset.isArchived()?"true":"false");
        
        childExtension = extension.addExtension(PROPERTY);
        childExtension.setAttributeValue("name", "checkinComment");
        childExtension.setText(asset.getCheckinComment());        
        
        childExtension = extension.addExtension(PROPERTY);
        childExtension.setAttributeValue("name", "categorySummary");
        childExtension.setText(asset.getCategorySummary());  
        
        childExtension = extension.addExtension(PROPERTY);
        childExtension.setAttributeValue("name", "coverage");
        childExtension.setText(asset.getCoverage()); 
               
        childExtension = extension.addExtension(PROPERTY);
        childExtension.setAttributeValue("name", "creator");
        childExtension.setText(asset.getCreator()); 
        
        childExtension = extension.addExtension(PROPERTY);
        childExtension.setAttributeValue("name", "format");
        childExtension.setText(asset.getFormat()); 
        
        childExtension = extension.addExtension(PROPERTY);
        childExtension.setAttributeValue("name", "lastContributor");
        childExtension.setText(asset.getLastContributor());         
        
        childExtension = extension.addExtension(PROPERTY);
        childExtension.setAttributeValue("name", "packageName");
        childExtension.setText(asset.getPackageName());         
        
        childExtension = extension.addExtension(PROPERTY);
        childExtension.setAttributeValue("name", "publisher");
        childExtension.setText(asset.getPublisher()); 
                
        childExtension = extension.addExtension(PROPERTY);
        childExtension.setAttributeValue("name", "stateDescription");
        childExtension.setText(asset.getStateDescription());         
        
        if (!asset.isBinary()) {
			e.setContentElement(factory.newContent());
			e.getContentElement().setContentType(Content.Type.TEXT);
			e.getContentElement().setValue(asset.getContent());
		} else {
			// TODO: binary content
		}
        
        return e;
    }
    
    private static Entry createDetailedArtifactEntry(Artifact artifact, Map<String, MetaData>metadataTypes, UriInfo baseUri) {
    	String artifactName = artifact.getName();
    	Map<String, List<String>> metadata = artifact.getMetadata();
    	
        Factory factory = Abdera.getNewFactory();
        
        Entry e = factory.getAbdera().newEntry();
        if (baseUri != null) {
            e.setBaseUri(baseUri.getAbsolutePath().toString());
        }
        e.setTitle(artifactName);
        //e.setId(asset.getUUID());
        e.setSummary(artifact.getDescription());
        e.setUpdated(artifact.getLastModified().toString());
        
        URI uri = 
        	baseUri.getBaseUriBuilder().path("repository").path("artifacts").path(artifactName).build();
        e.addLink(uri.toString());
        //e.setUpdated(asset.getLastModified().getTime());
        
        //meta data
/*        StringProperty property = e.addExtension(MetaDataExtensionFactory.PROPERTY);
        property.setValue("false");*/
        String NS = "http://overlord.jboss.org/drools/1.0";
        QName METADATA = new QName(NS, "metadata");
        
        ExtensibleElement extension = e.addExtension(METADATA);
        //extension.declareNS(NS, "drools");
        QName PROPERTY = new QName(NS, "property");
        QName VALUE = new QName(NS, "value");
        
        for (String key : metadata.keySet()) {
        	String metadataType = null;
        	if(metadataTypes.get(key) != null) {
        		metadataType = metadataTypes.get(key).getMetaDataType(); 
        	}
        	
        	if(ArtifactManager.METADATA_TYPE_STRING.equals(metadataType)) {
            	List<String> value = metadata.get(key);
                ExtensibleElement childExtension = extension.addExtension(PROPERTY);
                childExtension.setAttributeValue("name", key);
                childExtension.addSimpleExtension(VALUE, value.get(0));
                //childExtension.setText(value.get(0));
			} else if (ArtifactManager.METADATA_TYPE_MULTI_VALUE_STRING.equals(metadataType)) {
				List<String> values = metadata.get(key);
				ExtensibleElement childExtension = extension
						.addExtension(PROPERTY);
				childExtension.setAttributeValue("name", key);
				for(String value : values) {
	                childExtension.addSimpleExtension(VALUE, value);					
				}
			} else {
				//Default to string
            	List<String> value = metadata.get(key);
                ExtensibleElement childExtension = extension.addExtension(PROPERTY);
                childExtension.setAttributeValue("name", key);
                childExtension.setText(value.get(0));		
			}
         }
         
        if (!artifact.isBinary()) {
			e.setContentElement(factory.newContent());
			e.getContentElement().setContentType(Content.Type.TEXT);
			e.getContentElement().setValue(artifact.getContent());
		} else {
			e.setContentElement(factory.newContent());
			e.getContentElement().setContentType(Content.Type.MEDIA);
			e.getContentElement().setSrc(artifact.getSrcLink());
		}
        
        return e;
    }
      
    private static Entry createMetadataTypeEntry(MetaData md, UriInfo baseUri) {
        Factory factory = Abdera.getNewFactory();
        
        Entry e = factory.getAbdera().newEntry();
        if (baseUri != null) {
            e.setBaseUri(baseUri.getAbsolutePath().toString());
        }
        e.setTitle(md.getMetaDataName());
        e.setSummary(md.getDescription());
        e.setContent(md.getMetaDataType());
        URI uri = 
        	baseUri.getBaseUriBuilder().path("repository").path("metadatatypes").path(md.getMetaDataName()
        		).build();
        e.addLink(uri.toString());

        return e;
    }
    
	public RulesRepository getRulesRepository() {
		return repository;
	}

	public void setRulesRepository(RulesRepository repository) {
		this.repository = repository;
	}
 }


