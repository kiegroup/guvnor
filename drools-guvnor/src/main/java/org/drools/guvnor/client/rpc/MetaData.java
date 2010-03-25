package org.drools.guvnor.client.rpc;

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

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is the DTO for a versionable asset's meta data.
 * ie basically everything except the payload.
 */
public class MetaData
    implements
    IsSerializable {

    public String   name             = "";
    public String   description      = "";

    public String   title            = "";
    public String   status           = "";

    public Date     lastModifiedDate;
    public String   lastContributor  = "";
    public long     versionNumber;

    public Date     createdDate;

    public String   packageName      = "";
    public String   packageUUID      = "";
    public String[] categories       = new String[0];

    public String   format           = "";
    public String   type             = "";
    public String   creator          = "";
    public String   externalSource   = "";
    public String   subject          = "";
    public String   externalRelation = "";
    public String   rights           = "";
    public String   coverage         = "";
    public String   publisher        = "";
    public String   checkinComment   = "";

    public boolean  disabled         = false;

    public Date     dateEffective;
    public Date     dateExpired;

    /**
     * Remove a category.
     * @param idx The index of the cat to remove.
     */
    public void removeCategory(int idx) {
        String[] newList = new String[categories.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < categories.length; i++ ) {

            if ( i != idx ) {
                newList[newIdx] = categories[i];
                newIdx++;
            }

        }
        this.categories = newList;
    }

    /**
     * Add the given cat to the end of the cat list.
     */
    public void addCategory(String cat) {
        for ( int i = 0; i < this.categories.length; i++ ) {
            if ( categories[i].equals( cat ) ) return;
        }
        String[] list = this.categories;
        String[] newList = new String[list.length + 1];

        for ( int i = 0; i < list.length; i++ ) {
            newList[i] = list[i];
        }
        newList[list.length] = cat;

        this.categories = newList;
    }

}