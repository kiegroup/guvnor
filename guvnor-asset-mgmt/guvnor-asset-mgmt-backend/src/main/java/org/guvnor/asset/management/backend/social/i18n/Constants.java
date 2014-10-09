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

package org.guvnor.asset.management.backend.social.i18n;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Constants {

    private static ResourceBundle messages = ResourceBundle.getBundle("org.guvnor.asset.management.backend.social.i18n.Constants");

    public static final String CONFIGURE_REPOSITORY = "ConfigureRepository";

    public static final String PROMOTE_ASSETS = "PromoteAssets";

    public static final String BUILD_PROJECT = "BuildProject";

    public static final String RELEASE_PROJECT = "ReleaseProject";

    public String getMessage( String key ) {
        return key != null ? messages.getString( key ) : null;
    }

    public String getMessage( String key, Object... params ) {
        final String value = getMessage( key );
        if ( value != null ) {
            return MessageFormat.format( value, params );
        }
        return null;
    }

    public String configure_repository_start( String repo ) {
        return getMessage( "ConfigureRepository_start", repo );
    }

    public String configure_repository_end( String repo ) {
        return getMessage( "ConfigureRepository_end", repo );
    }

    public String configure_repository_failed( String repo ) {
        return getMessage( "ConfigureRepository_failed", repo );
    }

    public String configure_repository_branch_created( String branch, String repo ) {
        return getMessage( "ConfigureRepository_branch_created", branch, repo );
    }

    public String promote_assets_start( String repo ) {
        return getMessage( "PromoteAssets_start", repo );
    }

    public String promote_assets_end( String repo ) {
        return getMessage( "PromoteAssets_end", repo );
    }

    public String promote_assets_failed( String repo, String error ) {
        return getMessage( "PromoteAssets_failed", repo, error );
    }

    public String promote_assets_assets_promoted( String repo, String sourceBranch, String targetBranch ) {
        return getMessage( "PromoteAssets_assets_promoted", repo, sourceBranch, targetBranch );
    }

    public String build_project_start( String project, String branch, String repo ) {
        return getMessage( "BuildProject_start", project, branch, repo );
    }

    public String build_project_build_success( String project ) {
        return getMessage( "BuildProject_build_success", project );
    }

    public String build_project_build_failed( String project ) {
        return getMessage( "BuildProject_build_failed", project );
    }

    public String build_project_deploy_maven_success( String project ) {
        return getMessage( "BuildProject_deploy_maven_success", project );
    }

    public String build_project_deploy_maven_failed( String project ) {
        return getMessage( "BuildProject_deploy_maven_failed", project );
    }

    public String build_project_deploy_runtime_success( String project ) {
        return getMessage( "BuildProject_deploy_runtime_success", project );
    }

    public String build_project_deploy_runtime_skipped( String project ) {
        return getMessage( "BuildProject_deploy_runtime_skipped", project );
    }

    public String build_project_deploy_runtime_failed( String project ) {
        return getMessage( "BuildProject_deploy_runtime_failed", project );
    }

    public String build_project_end_with_errors( String project ) {
        return getMessage( "BuildProject_end_with_errors", project );
    }

    public String build_project_end( String project ) {
        return getMessage( "BuildProject_end", project );
    }

    public String release_project_start( String repo ) {
        return getMessage( "ReleaseProject_start", repo );
    }

    public String release_project_version_change_success( String repo, String version ) {
        return getMessage( "ReleaseProject_version_change_success", repo, version );
    }

    public String release_project_end( String repo ) {
        return getMessage( "ReleaseProject_end" );
    }

}
