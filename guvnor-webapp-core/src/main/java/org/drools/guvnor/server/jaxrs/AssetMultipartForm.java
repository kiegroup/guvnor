package org.drools.guvnor.server.jaxrs;

import org.drools.guvnor.server.jaxrs.jaxb.Asset;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

public class AssetMultipartForm {
    @FormParam("asset")
    @PartType(MediaType.APPLICATION_JSON)
    private Asset asset;
    @FormParam("binary")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private InputStream binary;

    public InputStream getBinary() {
        return binary;
    }

    public void setBinary(InputStream binary) {
        this.binary = binary;
    }



    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }
}
