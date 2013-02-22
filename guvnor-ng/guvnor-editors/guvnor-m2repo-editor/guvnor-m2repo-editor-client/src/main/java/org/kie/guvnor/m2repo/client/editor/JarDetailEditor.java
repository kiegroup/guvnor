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

package org.kie.guvnor.m2repo.client.editor;

import org.kie.guvnor.m2repo.client.resources.ImageResources;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.common.ResizableTextArea;

//Select JAR from list to see information on it:-
//                maven info,
//                artifact information,
//                maven dependency tree
public class JarDetailEditor extends FormStylePopup {

    public JarDetailEditor(String pomInfo) {
        super(ImageResources.INSTANCE.modelLarge(),
               "Jar details" );

        ResizableTextArea pomInfoTextArea = new ResizableTextArea();
        pomInfoTextArea.setText(pomInfo);
        pomInfoTextArea.setEnabled(false);
        pomInfoTextArea.setSize("700px", "500px");
        
        addAttribute( "", pomInfoTextArea);
/*        addAttribute( "Artifact info:", new HTML(""));
        addAttribute( "Dependency info:", new HTML(""));*/

    }

 }
