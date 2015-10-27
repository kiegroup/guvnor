/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.client.screens;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectWizard;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

@Dependent
public class NewProjectWizard
        extends AbstractWizard
        implements ProjectWizard {

    @Override
    public String getTitle() {
        return "New project";
    }

    @Override
    public List<WizardPage> getPages() {
        return new ArrayList<WizardPage>();
    }

    @Override
    public Widget getPageWidget( int pageNumber ) {
        return null;
    }

    @Override
    public int getPreferredHeight() {
        return 550;
    }

    @Override
    public int getPreferredWidth() {
        return 800;
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
    }

    @Override
    public void complete() {
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void initialise() {

    }

    @Override
    public void initialise( final POM pom ) {

    }

    @Override
    public void start( final Callback<Project> callback,
                       final boolean openEditor ) {
        super.start();
    }

}