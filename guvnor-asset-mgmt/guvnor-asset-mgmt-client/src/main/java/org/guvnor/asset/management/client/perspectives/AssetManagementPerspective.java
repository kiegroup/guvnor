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
package org.guvnor.asset.management.client.perspectives;

import javax.enterprise.context.ApplicationScoped;
import org.uberfire.lifecycle.OnStartup;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * A Perspective
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "Asset Management", isDefault = false)
public class AssetManagementPerspective {


    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl( PanelType.ROOT_LIST );
        p.setName( "Asset Management" );
        p.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "Repository Configuration" ) ) );
        
        final PanelDefinition south = new PanelDefinitionImpl( PanelType.SIMPLE );
        south.setHeight(300);
        south.setMinHeight(200);
        south.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "Build Configuration" ) ) );

        p.getRoot().insertChild( Position.SOUTH, south );
        
        final PanelDefinition east = new PanelDefinitionImpl( PanelType.SIMPLE );
        east.setHeight(400);
        east.setWidth(400);
        east.setMinWidth(200);
        east.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "Promote Changes" ) ) );

        p.getRoot().insertChild( Position.EAST, east );
        
        
        p.setTransient( true );
        return p;
    }
    
    @OnStartup
    public void init() {
       
        
    }

}
