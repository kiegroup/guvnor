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

package org.drools.ide.common.server.util;

import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionRetractFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.TypeSafeGuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.TypeSafeGuidedDecisionTable.DTCellValue;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GuidedDTXMLPersistence {

    private XStream                       xt;
    private static GuidedDTXMLPersistence INSTANCE = new GuidedDTXMLPersistence();

    private GuidedDTXMLPersistence() {
        xt = new XStream( new DomDriver() );
        xt.alias( "decision-table",
                  GuidedDecisionTable.class );
        xt.alias( "metadata-column",
                  MetadataCol.class );
        xt.alias( "attribute-column",
                  AttributeCol.class );
        xt.alias( "condition-column",
                  ConditionCol.class );
        xt.alias( "set-field-col",
                  ActionSetFieldCol.class );
        xt.alias( "retract-fact-column",
                  ActionRetractFactCol.class );
        xt.alias( "insert-fact-column",
                  ActionInsertFactCol.class );

        //See https://issues.jboss.org/browse/GUVNOR-1115
        xt.aliasField( "attr",
                       AttributeCol.class,
                       "attribute" );
        xt.aliasPackage( "org.drools.guvnor.client",
                         "org.drools.ide.common.client" );

        xt.alias( "dtable",
                  TypeSafeGuidedDecisionTable.class );
        xt.alias( "value",
                  DTCellValue.class );
    }

    public static GuidedDTXMLPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal(TypeSafeGuidedDecisionTable dt) {
        return xt.toXML( dt );
    }

    public TypeSafeGuidedDecisionTable unmarshal(String xml) {
        if ( xml == null || xml.trim().equals( "" ) ) {
            return new TypeSafeGuidedDecisionTable();
        }

        //Upgrade DTModel to new class
        Object model = xt.fromXML( xml );
        TypeSafeGuidedDecisionTable newDTModel;
        if ( model instanceof GuidedDecisionTable ) {
            GuidedDecisionTable legacyDTModel = (GuidedDecisionTable) model;
            newDTModel = RepositoryUpgradeHelper.convertGuidedDTModel( legacyDTModel );
        } else {
            newDTModel = (TypeSafeGuidedDecisionTable) model;
        }
        return newDTModel;
    }

}
