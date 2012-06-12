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
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.cxf.annotations.GZIP;
import org.drools.guvnor.server.jaxrs.jaxb.*;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemPageResult;
import org.drools.repository.CategoryItem;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.drools.guvnor.server.jaxrs.Translator.*;

/*import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Link;*/

@Path("/categories")
@RequestScoped
@Named
@GZIP
public class CategoryResource extends Resource {

    private final int pageSize = 10;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Collection<Category> getCategoriesAsJAXB() {
        Collection<Category> ret = new ArrayList<Category>();
        CategoryItem rootItem = rulesRepository.loadCategory("/");
        addChildrenRecursively(ret, rootItem);
        return ret;
    }

    private void addChildrenRecursively(Collection<Category> ret, CategoryItem categoryItem) {
        List<CategoryItem> children = categoryItem.getChildTags();
        for (CategoryItem child : children) {
            ret.add(toCategory(child, uriInfo));
            addChildrenRecursively(ret, child);
        }
    }

    @GET
    @Path("{categoryPath:.+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Category getCategoryAsJAXB(@PathParam("categoryPath") String categoryPath) {
        CategoryItem categoryItem = rulesRepository.loadCategory(categoryPath);
        return toCategory(categoryItem, uriInfo);
    }
    
    @GET
    @Path("{categoryPath:.+}/children")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Collection<Category> getCategoryChildrenAsJAXB(@PathParam("categoryPath") String categoryPath) {
        //get the requested category
        CategoryItem categoryItem = rulesRepository.loadCategory(categoryPath);
        
        //get its children and add them into a List
        Collection<Category> ret = new ArrayList<Category>();
        List<CategoryItem> children = categoryItem.getChildTags();
        for (CategoryItem child : children) {
            ret.add(toCategory(child, uriInfo));
        }
        return ret;
    }

    @GET
    @Path("{categoryPath:.+}/assets")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getAssetsAsAtom(@PathParam("categoryPath") String categoryPath) {
        Factory factory = Abdera.getNewFactory();
        Feed f = factory.getAbdera().newFeed();
        f.setTitle(categoryPath);
        AssetItemPageResult result = rulesRepository.findAssetsByCategory(
                categoryPath, 0, pageSize);
        List<AssetItem> assets = result.assets;
        for (AssetItem item : assets) {
            Entry e = toAssetEntryAbdera(item, uriInfo);
            f.addEntry(e);
        }

        if (result.hasNext) {
            Link l = factory.newLink();
            l.setRel("next-page");
            l.setHref(uriInfo.getBaseUriBuilder()
                    .path("categories/{categoryPath}/assets//page/{pageNumber}")
                    .build(categoryPath, (Integer) 1)
                    .toString());
            f.addLink(l);
        }

        return f;
    }

    @GET
    @Path("{categoryPath:.+}/assets")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Collection<Asset> getAssetsAsJAXB(@PathParam("categoryPath") String categoryPath) {
        Collection<Asset> ret = new ArrayList<Asset>();
        AssetItemPageResult result = rulesRepository.findAssetsByCategory(categoryPath, 0, pageSize);
        List<AssetItem> assets = result.assets;
        for (AssetItem item : assets) {
            ret.add(toAsset(item, uriInfo));
        }
        return ret;
    }

    @PUT
    @Path("{categoryPath:.+}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void createCategory(@PathParam("categoryPath") String categoryPath) {
        String parentPath;
        String name;
        int lastDotIndex = categoryPath.lastIndexOf("/");
        if (lastDotIndex >= 0) {
            parentPath = categoryPath.substring(0, lastDotIndex);
            name = categoryPath.substring(lastDotIndex + 1);
        } else {
            parentPath = "";
            name = categoryPath;
        }
        repositoryCategoryService.createCategory(parentPath, name, "TODO"); // TODO description is ignored by back-end
    }

    @DELETE
    @Path("{categoryPath:.+}")
    public void deleteCategory(@PathParam("categoryPath") String categoryPath) {
        try {
            repositoryCategoryService.removeCategory(categoryPath);
        } catch (SerializationException e) {
            // TODO that SerializationException is not about serialization at all
            throw new WebApplicationException(e);
        }
    }

/*    @GET
    @Path("{categoryName}/page/{page}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getAssetsAsAtom(@PathParam("categoryName") String categoryName, @PathParam ("page") String page) {
        Feed ret = new Feed();

        try {
            int p = new Integer(page).intValue();
            ret.setTitle(categoryName);
            AssetItemPageResult result = repository.findAssetsByCategory(
                    categoryName, p, pageSize);
            List<AssetItem> assets = result.assets;
            for (AssetItem item : assets) {
                Entry e = ToAssetEntry(item, uriInfo);
                ret.getEntries().add(e);
            }

            if (result.hasNext) {
                Link l = new Link();
                l.setRel("next-page");
                UriBuilder builder = uriInfo.getAbsolutePathBuilder();
                l.setHref(uriInfo.getBaseUriBuilder()
                        .path("categories/{categoryName}/assets//page/{pageNumber}")
                        .build(categoryName, (Integer) ++p)
                        .toString());
                ret.getLinks().add(l);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException (e);
        }

        return ret;
    }*/

    @GET
    @Path("{categoryName:.+}/assets//page/{page}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Collection<Asset> getAssetsAsJAXBIndex(@PathParam("categoryName") String categoryName,
            @PathParam("page") int page) {
        Collection<Asset> ret = new ArrayList<Asset>();

        AssetItemPageResult result = rulesRepository.findAssetsByCategory(
                categoryName, page, pageSize);
        List<AssetItem> assets = result.assets;
        for (AssetItem item : assets) {
            ret.add(toAsset(item, uriInfo));
        }

        return ret;
    }

}
