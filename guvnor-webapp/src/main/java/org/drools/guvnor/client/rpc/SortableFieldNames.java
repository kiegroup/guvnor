/*
 * Copyright 2013 JBoss Inc
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

public enum SortableFieldNames {

    TITLE_PROPERTY_NAME( "drools:title" ),
    DESCRIPTION_PROPERTY_NAME( "drools:description" ),
    LAST_MODIFIED_PROPERTY_NAME( "drools:lastModified" ),
    FORMAT_PROPERTY_NAME( "drools:format" ),
    CHECKIN_COMMENT( "drools:checkinComment" ),
    VERSION_NUMBER_PROPERTY_NAME( "drools:versionNumber" ),
    CONTENT_PROPERTY_ARCHIVE_FLAG( "drools:archive" ),

    LAST_CONTRIBUTOR_PROPERTY_NAME( "drools:lastContributor" ),
    CREATOR_PROPERTY_NAME( "drools:creator" ),
    TYPE_PROPERTY_NAME( "drools:type" ),
    SOURCE_PROPERTY_NAME( "drools:source" ),
    SUBJECT_PROPERTY_NAME( "drools:subject" ),
    RELATION_PROPERTY_NAME( "drools:relation" ),
    RIGHTS_PROPERTY_NAME( "drools:rights" ),
    COVERAGE_PROPERTY_NAME( "drools:coverage" ),
    PUBLISHER_PROPERTY_NAME( "drools:publisher" ),

    STATE_PROPERTY_NAME( "drools:stateReference" ),

    CATEGORY_PROPERTY_NAME( "drools:categoryReference" );

    private String fieldName;

    SortableFieldNames( String fieldName ) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

}
