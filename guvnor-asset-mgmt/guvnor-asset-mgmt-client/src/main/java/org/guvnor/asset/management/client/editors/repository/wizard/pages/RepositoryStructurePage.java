/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.asset.management.client.editors.repository.wizard.pages;

import javax.inject.Inject;

import org.guvnor.asset.management.client.i18n.Constants;
import org.uberfire.client.callbacks.Callback;

public class RepositoryStructurePage extends RepositoryWizardPage
        implements
        RepositoryStructurePageView.Presenter {

    @Inject
    private RepositoryStructurePageView view;

    private boolean isComplete = false;

    @Override
    public String getTitle() {
        return Constants.INSTANCE.RepositoryStructurePage();
    }

    @Override
    public void isComplete( Callback<Boolean> callback ) {
        callback.callback( isComplete );
    }

    @Override
    public void initialise() {
        view.init( this );
        content.setWidget( view );
    }

    @Override
    public void prepareView() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void stateChanged() {

        //TODO complete this validation.
        isComplete = isValid( view.getProjectName() );
        isComplete = isComplete && isValid( view.getGroupId() );
        isComplete = isComplete && isValid( view.getArtifactId() );
        isComplete = isComplete && isValid( view.getVersion() );

        model.setProjectName( view.getProjectName() );
        if ( view.getProjectName() != null ) {
            model.setProjectDescription( view.getProjectDescription().trim() );
        }
        model.setGroupId( view.getGroupId() );
        model.setArtifactId( view.getArtifactId() );
        model.setVersion( view.getVersion() );
        model.setMultiModule( view.isMultiModule() );
        model.setConfigureRepository( view.isConfigureRepository() );

        fireEvent();
    }

    private boolean isValid( String value ) {
        return value != null && !"".equals( value.trim() );
    }
}
