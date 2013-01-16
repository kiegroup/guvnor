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

package org.drools.guvnor.server.selector;

import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;

import java.util.ArrayList;
import java.util.List;

public class BuiltInSelector implements AssetSelector {
    private String  status;
    private String  statusOperator;
    private String  category;
    private String  categoryOperator;
    private boolean enableStatusSelector;
    private boolean enableCategorySelector;
    private List<String> searchStatus;

    public BuiltInSelector() {
    }

    public boolean isEnableStatusSelector() {
        return enableStatusSelector;
    }

    public void setEnableStatusSelector(boolean enableStatusSelector) {
        this.enableStatusSelector = enableStatusSelector;
    }

    public boolean isEnableCategorySelector() {
        return enableCategorySelector;
    }

    public void setEnableCategorySelector(boolean enableCategorySelector) {
        this.enableCategorySelector = enableCategorySelector;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryOperator() {
        return categoryOperator;
    }

    public void setCategoryOperator(String categoryOperator) {
        this.categoryOperator = categoryOperator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.searchStatus = extractListFromComaSeparated(this.status);
    }

    public String getStatusOperator() {
        return statusOperator;
    }

    public void setStatusOperator(String statusOperator) {
        this.statusOperator = statusOperator;
    }

    public boolean isAssetAllowed(AssetItem item) {
        if ( enableStatusSelector && enableCategorySelector ) {
            return (isStatusAllowed( item ) && isCategoryAllowed( item ));
        } else if ( enableStatusSelector ) {
            return isStatusAllowed( item );
        } else if ( enableCategorySelector ) {
            return isCategoryAllowed( item );
        }

        //allow everything if none enabled.
        return true;
    }

    private boolean isStatusAllowed(AssetItem item) {
           if ( "=".equals( statusOperator ) ) {
               if (searchStatus.contains(item.getStateDescription())){
                   return true;
               }
           } else if ( "!=".equals( statusOperator ) ) {
               if (!searchStatus.contains(item.getStateDescription())){
                   return true;
               }
           }

           return false;
       }
     public List<String> extractListFromComaSeparated(String status){
       List<String> extractedList = new ArrayList<String>();
       if (status!= null && status.length()> 0 ){

             int firstIndex=0;
             int lastIndex= status.indexOf(",", firstIndex);
             while (lastIndex != -1){

               String newStatus = status.substring(firstIndex, lastIndex);
               extractedList.add(newStatus);
               firstIndex = lastIndex+1;
               lastIndex = status.indexOf(",", firstIndex);
             }
             if (firstIndex > 0){
                 String newStatus = status.substring(firstIndex,status.length());
                 extractedList.add(newStatus);
             }else {
                 extractedList.add(status);
             }
       }
       return extractedList;
     }

    private boolean isCategoryAllowed(AssetItem item) {
        if ( "=".equals( categoryOperator ) ) {
            for ( CategoryItem cat : item.getCategories() ) {
                if ( cat.getFullPath().equals( category ) ) {
                    return true;
                }
            }
        } else if ( "!=".equals( categoryOperator ) ) {
            boolean categoryFound = false;
            for ( CategoryItem cat : item.getCategories() ) {
                if ( cat.getFullPath().equals( category ) ) {
                    categoryFound = true;
                }
            }
            return !categoryFound;
        }

        return false;
    }
}
