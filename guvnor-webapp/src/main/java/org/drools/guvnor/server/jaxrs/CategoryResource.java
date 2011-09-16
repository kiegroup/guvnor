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

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.drools.guvnor.server.jaxrs.jaxb.Asset;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemPageResult;
import org.jboss.seam.annotations.Name;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.drools.guvnor.server.jaxrs.Translator.toAsset;
import static org.drools.guvnor.server.jaxrs.Translator.toAssetEntryAbdera;

/*import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Link;*/

@Name("CategoryResource")
@Path("/categories")
public class CategoryResource extends Resource {

    private static final String Encoding = "UTF-8";

    private final int pageSize = 10;


    @GET
    @Path("{categoryName}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getAssetsAsAtom(@PathParam("categoryName") String encoded) {
        Factory factory = Abdera.getNewFactory();
        Feed f = factory.getAbdera().newFeed();
        try {
            String decoded = URLDecoder.decode(encoded, Encoding);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            f.setTitle(encoded);
            AssetItemPageResult result = repository.findAssetsByCategory(
                    decoded, 0, pageSize);
            List<AssetItem> assets = result.assets;
            for (AssetItem item : assets) {
                Entry e = toAssetEntryAbdera(item, uriInfo);
                f.addEntry(e);
            }

            if (result.hasNext) {
                Link l = factory.newLink();
                l.setRel("next-page");
                l.setHref(builder.path("/" + encoded + "/page/1").build().toString());
                f.addLink(l);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return f;
    }

    @GET
    @Path("{categoryName}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Collection<Asset> getAssetsAsJAXB(@PathParam("categoryName") String encoded) {
        Collection<Asset> ret = Collections.EMPTY_LIST;

        try {
            String decoded = URLDecoder.decode(encoded, Encoding);
            AssetItemPageResult result = repository.findAssetsByCategory(decoded, 0, pageSize);
            List<AssetItem> assets = result.assets;
            if (assets.size() > 0) {
                ret = new ArrayList<Asset>();
                for (AssetItem item : assets) {
                    ret.add(toAsset(item, uriInfo));
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return ret;
    }

/*    @GET
    @Path("{categoryName}/page/{page}")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Feed getAssetsAsAtom(@PathParam("categoryName") String encoded, @PathParam ("page") String page) {
        Feed ret = new Feed();

        try {
            String decoded = URLDecoder.decode(encoded, Encoding);
            int p = new Integer(page).intValue();
            ret.setTitle(decoded);
            AssetItemPageResult result = repository.findAssetsByCategory(
                    decoded, p, pageSize);
            List<AssetItem> assets = result.assets;
            for (AssetItem item : assets) {
                Entry e = ToAssetEntry(item, uriInfo);
                ret.getEntries().add(e);
            }

            if (result.hasNext) {
                Link l = new Link();
                l.setRel("next-page");
                UriBuilder builder = uriInfo.getAbsolutePathBuilder();
                l.setHref(builder.path("/" + encoded + "/page/" + ++p).build());
                ret.getLinks().add(l);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException (e);
        }

        return ret;
    }*/

    @GET
    @Path("{categoryName}/page/{page}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Collection<Asset> getAssetsAsJAXBIndex(@PathParam("categoryName") String encoded, @PathParam("page") String page) {
        Collection<Asset> ret = Collections.EMPTY_LIST;

        try {
            String decoded = URLDecoder.decode(encoded, Encoding);
            int p = new Integer(page);
            AssetItemPageResult result = repository.findAssetsByCategory(
                    decoded, p, pageSize);
            List<AssetItem> assets = result.assets;
            if (assets.size() > 0) {
                ret = new ArrayList<Asset>();
                for (AssetItem item : assets) {
                    ret.add(toAsset(item, uriInfo));
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return ret;
    }

}
