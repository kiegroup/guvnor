/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.server.builder.pagerow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.rpc.PageRequest;
import org.drools.guvnor.client.rpc.PermissionsPageRow;

public class PermissionPageRowBuilder {
    public List<PermissionsPageRow> createRows(final PageRequest pageRequest,
                                               final Map<String, List<String>> permissions) {
        int rowNumber = 0;
        int rowMinNumber = pageRequest.getStartRowIndex();
        int rowMaxNumber = pageRequest.getPageSize() == null ? permissions.size() : rowMinNumber + pageRequest.getPageSize();
        int resultsSize = (pageRequest.getPageSize() == null ? permissions.size() : pageRequest.getPageSize());
        List<PermissionsPageRow> rowList = new ArrayList<PermissionsPageRow>( resultsSize );
        Iterator<String> mapItr = permissions.keySet().iterator();
        while ( mapItr.hasNext() && rowNumber < rowMaxNumber ) {
            String userName = mapItr.next();
            if ( rowNumber >= rowMinNumber ) {
                List<String> userPermissions = permissions.get( userName );
                PermissionsPageRow row = new PermissionsPageRow();
                row.setUserName( userName );
                row.setUserPermissions( userPermissions );
                rowList.add( row );
            }
            rowNumber++;
        }

        return rowList;
    }

}
