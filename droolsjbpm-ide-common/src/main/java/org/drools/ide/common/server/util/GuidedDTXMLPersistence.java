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
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionRetractFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.MetadataCol52;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("deprecation")
public class GuidedDTXMLPersistence {

    private XStream                               xt;
    private GuidedDecisionTableModelUpgradeHelper upgrader = new GuidedDecisionTableModelUpgradeHelper();
    private static GuidedDTXMLPersistence         INSTANCE = new GuidedDTXMLPersistence();

    private GuidedDTXMLPersistence() {
        xt = new XStream( new DomDriver() );

        //Legacy model
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

        //Post 5.2 model
        xt.alias( "decision-table52",
                  GuidedDecisionTable52.class );
        xt.alias( "metadata-column52",
                  MetadataCol52.class );
        xt.alias( "attribute-column52",
                  AttributeCol52.class );
        xt.alias( "condition-column52",
                  ConditionCol52.class );
        xt.alias( "set-field-col52",
                  ActionSetFieldCol52.class );
        xt.alias( "retract-fact-column52",
                  ActionRetractFactCol52.class );
        xt.alias( "insert-fact-column52",
                  ActionInsertFactCol52.class );
        xt.alias( "value",
                  DTCellValue52.class );

        //See https://issues.jboss.org/browse/GUVNOR-1115
        xt.aliasPackage( "org.drools.guvnor.client",
                         "org.drools.ide.common.client" );
    }

    public static GuidedDTXMLPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal(GuidedDecisionTable52 dt) {
        return xt.toXML( dt );
    }

    public GuidedDecisionTable52 unmarshal(String xml) {
        if ( xml == null || xml.trim().equals( "" ) ) {
            return new GuidedDecisionTable52();
        }

        //Upgrade DTModel to new class
        Object model = xt.fromXML( xml );
        GuidedDecisionTable52 newDTModel;
        if ( model instanceof GuidedDecisionTable ) {
            GuidedDecisionTable legacyDTModel = (GuidedDecisionTable) model;
            newDTModel = upgrader.upgrade( legacyDTModel );
        } else {
            newDTModel = (GuidedDecisionTable52) model;
        }
        return newDTModel;
    }

}
