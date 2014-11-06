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

import org.uberfire.client.mvp.UberView;

public interface RepositoryStructurePageView
        extends
        UberView<RepositoryStructurePageView.Presenter> {

    interface Presenter {

        void stateChanged();

        void setProjectName( String projectName );

        void setProjectDescription( String projectDescription );

        void setGroupId( String groupId );

        void setArtifactId( String artifactId );

        void setConfigureRepository( boolean configureRepository );

        void setVersion( String version );

    }

    String getProjectName();

    void setProjectName( String projectName );

    String getProjectDescription();

    void setProjectDescription( String projectDescription );

    String getGroupId();

    void setGroupId( String groupId );

    String getArtifactId();

    void setArtifactId( String artifactId );

    String getVersion();

    void setVersion( String version );

    boolean isMultiModule();

    boolean isConfigureRepository();

    void setConfigureRepository( boolean configureRepository );

}
