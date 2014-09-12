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

package org.guvnor.asset.management.client.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * This uses GWT to provide client side compile time resolving of locales. See:
 * http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web- toolkit-doc-1-5&t=DevGuideInternationalization
 * (for more information).
 * <p/>
 * Each method name matches up with a key in Constants.properties (the properties file can still be used on the server). To use
 * this, use <code>GWT.create(Constants.class)</code>.
 */
public interface Constants extends Messages {

    Constants INSTANCE = GWT.create(Constants.class);


    String Release_Branch();

    String Dev_Branch();

    String Configure_Repository();

    String Choose_Repository();

    String Repository_Configuration();

    String Choose_Branch();

    String Choose_Project();

    String User_Name();

    String Password();

    String Server_URL();

    String Build_Project();

    String Deploy_To_Maven();

    String Deploy_To_Runtime();

    String Build_Configuration();

    String Promote_Assets();

    String Choose_Source_Branch();

    String Choose_Destination_Branch();

    String Version();

    String ABuildIsAlreadyRunning();
    
    String Loading();
    
    String ProjectStructureWithName(String string);

    String Save();

    String ProjectStructure();
    
    String VersionHolder();
    
    String ArtifactIdHolder();
    
    String GroupIdHolder();
    
    String InitProjectStructure();
    
    String AddModule();
    
    String Modules();

    String Current_Version();

    String Select_Repository();
    
    String No_Project_Structure_Available();

    String Select_A_Branch();

    String Commits_To_Promote();

    String Requires_Review();

    String Source_Branch();

    String Files_In_The_Branch();

    String Files_To_Promote();

}
