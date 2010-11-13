/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.server.builder;

import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.VersionableItem;

/**
 * This class is used to accumulate error reports for asset.
 * This can then be used to feed back to the user where the problems are.
 * 
 * @author Michael Neale
 */
public class ContentAssemblyError {

    private final String  errorReport;

    private final String  format;
    private final String  name;
    private final String  uuid;
    private final boolean isPackageItem;
    private final boolean isAssetItem;

    public ContentAssemblyError(VersionableItem itemInError,
                                String errorReport) {
        format = itemInError.getFormat();
        name = itemInError.getName();
        uuid = itemInError.getUUID();
        isPackageItem = itemInError instanceof PackageItem;
        isAssetItem = itemInError instanceof AssetItem;

        this.errorReport = errorReport;
    }

    public ContentAssemblyError(RuleAsset itemInError,
                                String errorReport) {
        format = itemInError.metaData.format;
        name = itemInError.metaData.name;
        uuid = itemInError.uuid;
        isPackageItem = false;
        isAssetItem = true;

        this.errorReport = errorReport;
    }

    public String toString() {
        return this.getErrorReport();
    }

    public String getFormat() {
        return format;
    }

    public String getName() {
        return name;
    }

    public String getUUID() {
        return uuid;
    }

    public boolean isAssetItem() {
        return isAssetItem;
    }

    public boolean isPackageItem() {
        return isPackageItem;
    }

    public String getErrorReport() {
        return errorReport;
    }

}