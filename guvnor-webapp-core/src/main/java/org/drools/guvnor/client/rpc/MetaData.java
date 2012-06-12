/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.rpc;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;
import org.drools.guvnor.shared.api.Valid;

/**
 * This is the DTO for a versionable asset's meta data.
 * ie basically everything except the payload.
 */
public class MetaData
    implements
    IsSerializable {

    public String   title                = "";
    

    public String   moduleName          = "";
    public String   moduleUUID          = "";
    public String[] categories           = new String[0];

    public String   type                 = "";
    public String   creator              = "";
    public String   externalSource       = "";
    public String   subject              = "";
    public String   externalRelation     = "";
    public String   rights               = "";
    public String   coverage             = "";
    public String   publisher            = "";

    private boolean binary               = false;

    public Valid valid                   = Valid.UNDETERMINED;

    public boolean  disabled             = false;
    public boolean  hasPreceedingVersion = false;
    public boolean  hasSucceedingVersion = false;

    public Date     dateEffective;
    public Date     dateExpired;

    /**
     * Remove a category.
     * @param idx The index of the cat to remove.
     */
    public void removeCategory(int idx) {
        String[] newList = new String[getCategories().length - 1];
        int newIdx = 0;
        for ( int i = 0; i < getCategories().length; i++ ) {

            if ( i != idx ) {
                newList[newIdx] = getCategories()[i];
                newIdx++;
            }

        }
        this.setCategories( newList );
    }

    /**
     * Add the given cat to the end of the cat list.
     */
    public void addCategory(String cat) {
        for ( int i = 0; i < this.getCategories().length; i++ ) {
            if ( getCategories()[i].equals( cat ) ) return;
        }
        String[] list = this.getCategories();
        String[] newList = new String[list.length + 1];

        for ( int i = 0; i < list.length; i++ ) {
            newList[i] = list[i];
        }
        newList[list.length] = cat;

        this.setCategories( newList );
    }

    public MetaData setBinary(boolean binary) {
        this.binary = binary;
        return this;
    }

    public boolean isBinary() {
        return binary;
    }

    public MetaData setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MetaData setModuleName(String packageName) {
        this.moduleName = packageName;
        return this;
    }

    public String getModuleName() {
        return moduleName;
    }

    public MetaData setModuleUUID(String moduleUUID) {
        this.moduleUUID = moduleUUID;
        return this;
    }

    public String getModuleUUID() {
        return moduleUUID;
    }

    public MetaData setCategories(String[] categories) {
        this.categories = categories;
        return this;
    }

    public String[] getCategories() {
        return categories;
    }

    public MetaData setType(String type) {
        this.type = type;
        return this;
    }

    public String getType() {
        return type;
    }

    public MetaData setCreator(String creator) {
        this.creator = creator;
        return this;
    }

    public String getCreator() {
        return creator;
    }

    public MetaData setExternalSource(String externalSource) {
        this.externalSource = externalSource;
        return this;
    }

    public String getExternalSource() {
        return externalSource;
    }

    public MetaData setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public MetaData setExternalRelation(String externalRelation) {
        this.externalRelation = externalRelation;
        return this;
    }

    public String getExternalRelation() {
        return externalRelation;
    }

    public MetaData setRights(String rights) {
        this.rights = rights;
        return this;
    }

    public String getRights() {
        return rights;
    }

    public MetaData setCoverage(String coverage) {
        this.coverage = coverage;
        return this;
    }

    public String getCoverage() {
        return coverage;
    }

    public MetaData setPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    public String getPublisher() {
        return publisher;
    }

    public MetaData setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public MetaData setHasPreceedingVersion(boolean hasPreceedingVersion) {
        this.hasPreceedingVersion = hasPreceedingVersion;
        return this;
    }

    public boolean isHasPreceedingVersion() {
        return hasPreceedingVersion;
    }

    public MetaData setHasSucceedingVersion(boolean hasSucceedingVersion) {
        this.hasSucceedingVersion = hasSucceedingVersion;
        return this;
    }

    public boolean isHasSucceedingVersion() {
        return hasSucceedingVersion;
    }

    public MetaData setDateEffective(Date dateEffective) {
        this.dateEffective = dateEffective;
        return this;
    }

    public Date getDateEffective() {
        return dateEffective;
    }

    public MetaData setDateExpired(Date dateExpired) {
        this.dateExpired = dateExpired;
        return this;
    }

    public Date getDateExpired() {
        return dateExpired;
    }

    public Valid getValid() {
        return valid;
    }

    public void setValid(Valid valid) {
        this.valid = valid;
    }


}
