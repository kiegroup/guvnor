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

    String UnmanagedRepository(String repository);

    String Save();

    String Saving();

    String Deleting();

    String ProjectStructure();
    
    String VersionHolder();
    
    String ArtifactIdHolder();
    
    String GroupIdHolder();

    String CreatingProjectStructure();

    String ConvertingToMultiModuleProject();
    
    String AddModule();

    String DeleteModule();

    String EditModule();
    
    String Modules();

    String Module();

    String NewProject();

    String Projects();

    String Project();

    String RepositoryNotSelected();

    String ConfirmModuleDeletion(String module);

    String ConfirmProjectDeletion(String project);

    String ConfirmSaveProjectStructure();

    String ConfirmConvertToMultiModuleStructure();


    //Project structure data widget constants

    String InitProjectStructure();

    String EditProject();

    String SaveChanges();

    String ConvertToMultiModule();

    //create
    String Project_structure_view_create_projectTypeLabel();
    String Project_structure_view_create_isSingleModuleRadioButton();
    String Project_structure_view_create_isSingleModuleRadioButtonHelpInline();
    String Project_structure_view_create_isMultiModuleRadioButton();
    String Project_structure_view_create_isMultiModuleRadioButtonHelpInline();
    String Project_structure_view_create_groupIdTextBoxHelpInline();
    String Project_structure_view_create_artifactIdTextBoxHelpInline();
    String Project_structure_view_create_versionTextBoxHelpInline();

    //single module
    String Project_structure_view_edit_single_projectTypeLabel();
    String Project_structure_view_edit_single_isSingleModuleRadioButton();
    String Project_structure_view_edit_single_isSingleModuleRadioButtonHelpInline();
    String Project_structure_view_edit_single_isMultiModuleRadioButton();
    String Project_structure_view_edit_single_isMultiModuleRadioButtonHelpInline();
    String Project_structure_view_edit_single_groupIdTextBoxHelpInline();
    String Project_structure_view_edit_single_artifactIdTextBoxHelpInline();
    String Project_structure_view_edit_single_versionTextBoxHelpInline();

    //multi module
    String Project_structure_view_edit_multi_projectTypeLabel();
    String Project_structure_view_edit_multi_isMultiModuleRadioButton();
    String Project_structure_view_edit_multi_isMultiModuleRadioButtonHelpInline();
    String Project_structure_view_edit_multi_groupIdTextBoxHelpInline();
    String Project_structure_view_edit_multi_artifactIdTextBoxHelpInline();
    String Project_structure_view_edit_multi_versionTextBoxHelpInline();

    //unmanaged repo
    String Project_structure_view_edit_unmanaged_projectTypeLabel();

    //End of Project structure data widget constants

    String Current_Version();

    String Select_Repository();
    
    String No_Project_Structure_Available();

    String Select_A_Branch();

    String Commits_To_Promote();

    String Requires_Review();

    String Source_Branch();

    String Files_In_The_Branch();

    String Files_To_Promote();
    
    String Promote_All();
    
    String Promote_Selected();
    
    String Release_Project();
    
    String Release_Configuration();

    String AssetManagementLog();

}
