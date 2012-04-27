/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.server.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.repository.AssetItem;

/**
 * Utility class to sort Declarative Model Asset DRL. An individual Declarative
 * Model can contain multiple Declarative Types. A Declarative Type can extend
 * another Declarative Type in either the same model or another. The DRL for a
 * Declarative Type needs to be added to the PackageBuilder before any those
 * that extend it.
 */
public class DeclaredTypesSorter {

    private static final Pattern typeFinder      = Pattern.compile( "(declare\\s+((\\w+)).*?\\s+end+?)",
                                                                    Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE );

    private static final Pattern superTypeFinder = Pattern.compile( "(extends\\s+((\\w+)))",
                                                                    Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE );

    /**
     * Utility method to split and sort all Declared Types into an ordered list
     * based upon dependencies. Declarative Types that do not extend are listed
     * first then subsequent dependent children.
     * 
     * @param assets
     * @return
     */
    public List<DeclaredTypeAssetInheritanceInformation> sort(final List<AssetItem> assets) {

        final List<DeclaredTypeAssetInheritanceInformation> sortedTypes = new ArrayList<DeclaredTypeAssetInheritanceInformation>();

        //Parse out individual declared types
        for ( AssetItem assetItem : assets ) {
            List<DeclaredTypeInheritanceInformation> individualTypes = parseIndividualTypes( assetItem.getContent() );
            for ( DeclaredTypeInheritanceInformation individualType : individualTypes ) {
                sortedTypes.add( new DeclaredTypeAssetInheritanceInformation( assetItem,
                                                                              individualType.getType(),
                                                                              individualType.getSuperType(),
                                                                              individualType.getDrl() ) );
            }
        }

        //Lookup for SuperTypes to determine the dependency score of a Type
        Map<String, DeclaredTypeAssetInheritanceInformation> lookup = new HashMap<String, DeclaredTypeAssetInheritanceInformation>();
        for ( DeclaredTypeAssetInheritanceInformation type : sortedTypes ) {
            lookup.put( type.getType(),
                        type );
        }

        //Calculate dependency score for Types
        for ( DeclaredTypeAssetInheritanceInformation type : sortedTypes ) {
            String superType = type.getSuperType();
            while ( superType != null ) {
                type.increaseDependencyScore();
                if ( lookup.containsKey( superType ) ) {
                    superType = lookup.get( superType ).getSuperType();
                }
            }
        }

        //Sort based upon dependency score
        Collections.sort( sortedTypes,
                          new Comparator<DeclaredTypeAssetInheritanceInformation>() {

                              public int compare(DeclaredTypeAssetInheritanceInformation o1,
                                                 DeclaredTypeAssetInheritanceInformation o2) {
                                  return o1.getDependencyScore() - o2.getDependencyScore();
                              }

                          } );

        return sortedTypes;
    }

    List<DeclaredTypeInheritanceInformation> parseIndividualTypes(final String typesDrl) {
        final List<DeclaredTypeInheritanceInformation> individualTypes = new ArrayList<DeclaredTypeInheritanceInformation>();

        Matcher tm = typeFinder.matcher( typesDrl );
        while ( tm.find() ) {
            final String type = tm.group( 2 );
            final String typeDrl = tm.group( 1 );
            final String superType = getSuperType( typeDrl );
            individualTypes.add( new DeclaredTypeInheritanceInformation( type,
                                                                         superType,
                                                                         typeDrl ) );
        }

        return individualTypes;
    }

    private String getSuperType(final String typeDrl) {
        Matcher stm = superTypeFinder.matcher( typeDrl );
        if ( stm.find() ) {
            return stm.group( 2 );
        }
        return null;
    }

    static class DeclaredTypeInheritanceInformation {

        private final String type;
        private final String superType;
        private final String drl;

        private int          dependencyScore = 0;

        private DeclaredTypeInheritanceInformation(final String type,
                                                   final String superType,
                                                   final String drl) {
            this.type = type;
            this.superType = superType;
            this.drl = drl;
        }

        public String getType() {
            return type;
        }

        public String getSuperType() {
            return superType;
        }

        public String getDrl() {
            return drl;
        }

        void increaseDependencyScore() {
            this.dependencyScore++;
        }

        int getDependencyScore() {
            return this.dependencyScore;
        }

    }

    static class DeclaredTypeAssetInheritanceInformation extends DeclaredTypeInheritanceInformation {

        private final AssetItem owningAssetItem;

        private DeclaredTypeAssetInheritanceInformation(final AssetItem owningAssetItem,
                                                        final String type,
                                                        final String superType,
                                                        final String drl) {
            super( type,
                   superType,
                   drl );
            this.owningAssetItem = owningAssetItem;
        }

        public AssetItem getOwningAssetItem() {
            return owningAssetItem;
        }

    }
}
