/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public InputStream getBinary() {
        return binary;
    }

    public void setBinary(InputStream binary) {
        this.binary = binary;
    }

}
