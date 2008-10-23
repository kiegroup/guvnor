package org.drools.guvnor.server.contenthandler;
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



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * All content handlers must implement this, and be registered in content_types.properties
 * @author Michael Neale
 *
 */
public abstract class ContentHandler {

    /**
     * When loading asset content.
     * @param asset The target.
     * @param item The source.
     * @throws SerializableException
     */
    public abstract void retrieveAssetContent(RuleAsset asset,
                                              PackageItem pkg,
                                              AssetItem item) throws SerializableException;

    /**
     * For storing the asset content back into the repo node (any changes).
     * @param asset
     * @param repoAsset
     * @throws SerializableException
     */
    public abstract void storeAssetContent(RuleAsset asset,
                                           AssetItem repoAsset) throws SerializableException;

    /**
     * @return true if the current content type is for a rule asset.
     * If it is a rule asset, then it can be assembled into a package.
     * If its not, then it is there, nominally to support compiling or
     * validation/testing of the package (eg a model, or a dsl file).
     */
    public boolean isRuleAsset() {
        return this instanceof IRuleAsset;
    }
    private String findParentCategory(AssetItem asset, String currentCat){
    	//Start your search at the top
    	CategoryItem item = asset.getRulesRepository().loadCategory("/");
    	return findCategoryInChild(item, currentCat);
    }
    private String findCategoryInChild(CategoryItem item, String currentCat){
    	List children = item.getChildTags();
		for (int i = 0; i < children.size(); i++) {
			if(((CategoryItem) children.get(i)).getName().equals(currentCat)){
				return item.getName();
			}else{
				String check = findCategoryInChild((CategoryItem)children.get(i), currentCat);
				if(check!=null && check.length() > 0){
					return check;
				}
			}
			
		}
		return "";
    }
    private String findKeyforValue(HashMap<String,String> catRules, String catToFind){
    	for (Iterator i = catRules.entrySet().iterator(); i.hasNext();) {
	        Map.Entry entry = (Map.Entry)i.next();
	        //Found rule name that should be used to extend current rule as defined in the Category Rule	
	        if(entry.getValue().equals(catToFind)){
	        	return(String)entry.getKey();
	        }
	    }
    	return "";
    }
    /**
     * Search Categories in a package against the current rule to see if the current rule should be extended,
     * via another rule. IE rule rule1 extends rule2
     * This is an implementation of that DRL feature, via Category to Rule mappings in Guvnor
     * @param asset
     * @return rule that should be extended, based on categories
     */
    protected String parentNameFromCategory(AssetItem asset, String currentParent){
    	 
    	
    	List<CategoryItem> cats = asset.getCategories();
        String catName = null;
        String parentCat = null;
        if(cats.size() > 0){       	
//        	for(int i=0;i< cats.size(); i++){
//        		System.out.println(i+" Cat: "+((CategoryItem)(cats.get(i))).getName());
//        		System.out.println(i+" Path: "+((CategoryItem)(cats.get(i))).getFullPath());     
//        		
//        	}
        	catName = cats.get(0).getName();
        }
        //get all Category Rules for Package
        HashMap<String,String> catRules = asset.getPackage().getCategoryRules();
        
        String newParent = currentParent;
        if(null != catRules && null != catName){
        	//Asset or Rule is actually used in the Category Rule, so ignore the category of the normal rule
        	//Either extend from the parent category rule or none at all
        	String ruleName = asset.getName();
        	if(catRules.containsKey(ruleName)){
        		//find Cat for your rule		
        		parentCat = findParentCategory(asset,catRules.get(ruleName));
//        		System.out.println("Found rule: " + ruleName + " in categoryRuleHash, Parent Cat: " + parentCat);
        		//This rule name is in our Category Rules
        		//See if there is a Parent and it has a rule defined, if so extend that rule, to create a chain
        		if(parentCat != null && parentCat.length() > 0 && catRules.containsValue(parentCat)){
//        			System.out.println("Should have rule in Category to use for my Parent");
        			newParent = findKeyforValue(catRules,parentCat);
        			
        		}else{
        			//Must be blank to avoid circular reference
        			newParent = "";
        		}
        		//else make sure parent is ALWAYS blank, to avoid circle references
        	
        	//If the rule is not defined in the Category Rule, check to make sure currentParent isnt already set
        	//If you wanted to override the Category Rule, with a extends on the rule manually, honor it
        	}else if(currentParent != null && currentParent.length() > 0){
        		newParent = currentParent;
        	//Normal use case
        	//Category of the current asset has been defined in Category Rules for the current package
        	}else if(catRules.containsValue(catName)){
        		newParent = findKeyforValue(catRules,catName);
        	}
        }
        return newParent;
    }

}