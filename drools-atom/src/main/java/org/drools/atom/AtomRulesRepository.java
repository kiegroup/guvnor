
package org.drools.atom;


import java.net.URI;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
    @ProduceMime({"application/json", "application/atom+xml" })
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
    @ProduceMime({"application/atom+xml", "application/json" })
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
    @Path("/packages/feed")
    @ConsumeMime("application/atom+xml")
    public Response addPackageAsEntry(Entry e, @Context UriInfo uParam) {
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
        URI uri = 
        	baseUri.getBaseUriBuilder().path("repository", "packages",  
        			pkg.getName()).build();
        e.addLink(uri.toString());
        e.setUpdated(pkg.getLastModified().getTime());
        
        //What content to return?
        e.setContentElement(factory.newContent());
        e.getContentElement().setContentType(Content.Type.TEXT);
        e.getContentElement().setValue("description=" + pkg.getDescription() + ", archived=" +  pkg.isArchived());
        
        return e;
    }
    
	public RulesRepository getRulesRepository() {
		return repository;
	}

	public void setRulesRepository(RulesRepository repository) {
		this.repository = repository;
	}
 }


