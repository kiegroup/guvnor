
package org.drools.atom;


import java.net.URI;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.drools.repository.PackageItem;
import org.drools.repository.PackageIterator;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;


/**
 * AtomRulesRepository provides an AtomPub interface on top of RulesRepository. 
 * 
 * A HTTP GET request to URL http://host:portnumber/repository/packages
 * returns a list of packages in the repository in Atom feed format. An example looks like below:

<feed xml:base="http://localhost:9080/repository/packages">
  <title type="text">Packages</title>
  
  <entry xml:base="http://localhost:9080/repository/packages">
    <title type="text">defaultPackage</title>
    <link href="http://localhost:9080/repository/packages/defaultPackage"/>
  </entry>

  <entry xml:base="http://localhost:9080/repository/packages">
    <title type="text">testPackage1</title>
    <link href="http://localhost:9080/repository/packages/testPackage1"/>
  </entry>
</feed>

 * A HTTP GET request to URL http://host:portnumber/repository/packages/testPackage1 
 * returns testPackag1 in the repository in Atom entry format. An example looks like below:

<entry xml:base="http://localhost:9080/repository/packages/testPackage1">
  <title type="text">testPackage1</title>
  <id>5632cf6c-0ef5-4ccc-b7e5-293285c4ce19</id>
  <link href="http://localhost:9080/repository/packages/testPackage1"/>
  <summary type="text">desc1</summary>
  <updated>2008-10-17T08:12:42.046Z</updated>
  <content type="text">archived=false</content>
</entry>   


 * A HTTP POST request to URL http://host:portnumber/repository/packages with the data:

<entry xml:base="http://localhost:9080/repository/packages/testPackage1">
  <title type="text">testPackage1</title>
</entry>   

 * adds a package named testPackage1
 *  
 *  
 * A HTTP PUT request to URL http://host:portnumber/repository/packages with the data:

<entry xml:base="http://localhost:9080/repository/packages/testPackage1">
  <title type="text">testPackage1</title>
  <summary type="text">desc2</summary>
  <content type="text">archived=false</content>
</entry>     

 * A HTTP DELETE request to URL http://host:portnumber/repository/packages/testPackage1  
 * delete the package testPackage1
 * 
 * NOTE: The mapping between Atom Entry element and Drools PackageItem:
 * 
 * atom:title - PackageItem.name
 * atom:id - PackageItem.UUID
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
    @ProduceMime({"application/atom+xml" })
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
    @Path("/packages/{packageName}/")
    @ProduceMime({"application/atom+xml"})
    public Entry getPackageAsEntry(@PathParam("packageName") String packageName, @Context UriInfo uParam) throws PackageNotFoundFault {
        System.out.println("----invoking getPackageAsEntry with name: " + packageName);
        
        try {
            PackageItem packageItem = repository.loadPackage(packageName);
            return createDetailedPackageItemEntry(packageItem, uParam);
        } catch (RulesRepositoryException e) {
        	PackageNotFoundDetails details = new PackageNotFoundDetails();
            details.setName(packageName);
            throw new PackageNotFoundFault(details);       	
        }
    }
            
    @POST
    @Path("/packages")
    @ConsumeMime("application/atom+xml")
    @ProduceMime({"application/atom+xml"})
    public Response addPackageAsEntry(Entry e, @Context UriInfo uParam) {
        System.out.println("----invoking addPackageAsEntry with package name: " + e.getTitle());

        try {
        	String packageName = e.getTitle();

        	PackageItem packageItem = repository.createPackage(packageName, "desc");
            
            URI uri = 
            	uParam.getBaseUriBuilder().path("repository", "packages", 
                                                packageItem.getName()).build();
            return Response.created(uri).entity(e).build();
        } catch (Exception ex) {
            return Response.serverError().build();
        }
    }
    
    
    @PUT
    @Path("/packages")
    @ConsumeMime("application/atom+xml")
    @ProduceMime({"application/atom+xml"})
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
            	uParam.getBaseUriBuilder().path("repository", "packages", 
            			item.getName()).build();
            return Response.ok(uri).entity(e).build();
        } catch (Exception ex) {
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
        
    private static Entry createPackageItemEntry(PackageItem pkg, UriInfo baseUri) {
        Factory factory = Abdera.getNewFactory();
        
        Entry e = factory.getAbdera().newEntry();
        if (baseUri != null) {
            e.setBaseUri(baseUri.getAbsolutePath().toString());
        }
        e.setTitle(pkg.getName());
        URI uri = 
        	baseUri.getBaseUriBuilder().path("repository", "packages", 
        			pkg.getName()).build();
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
        	baseUri.getBaseUriBuilder().path("repository", "packages",  
        			pkg.getName()).build();
        e.addLink(uri.toString());
        e.setUpdated(pkg.getLastModified().getTime());
        
        //TODO: What content to return?
        e.setContentElement(factory.newContent());
        e.getContentElement().setContentType(Content.Type.TEXT);
        e.getContentElement().setValue("archived=" +  pkg.isArchived());
        
        return e;
    }
    
	public RulesRepository getRulesRepository() {
		return repository;
	}

	public void setRulesRepository(RulesRepository repository) {
		this.repository = repository;
	}
 }


