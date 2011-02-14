/*
 * Copyright 2010 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.rpc;


/**
 * Returned by the builder.
 */
public class BuilderResultLine extends AbstractPageRow {

    private String assetFormat;
    private String assetName;
    private String uuid;
    private String message;

    public String getAssetFormat() {
        return assetFormat;
    }

    public void setAssetFormat(String assetFormat) {
        this.assetFormat = assetFormat;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return "Asset: "
               + assetName
               + "."
               + assetFormat
               + "\n"
               + // NON-NLS
               "Message: "
               + message
               + "\n"
               + // NON-NLS
               "UUID: "
               + uuid; // NON-NLS
    }

}
