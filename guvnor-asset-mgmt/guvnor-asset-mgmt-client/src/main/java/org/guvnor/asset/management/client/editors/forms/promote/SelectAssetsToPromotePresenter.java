/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.asset.management.client.editors.forms.promote;

import com.google.gwt.core.client.GWT;
import org.guvnor.asset.management.client.i18n.Constants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.forms.GetFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.RequestFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.SetFormParamsEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Dependent
@WorkbenchScreen( identifier = "SelectAssetsToPromote Form" )
public class SelectAssetsToPromotePresenter {

    private Constants constants = GWT.create( Constants.class );

    public interface SelectAssetsToPromoteView extends UberView<SelectAssetsToPromotePresenter> {

        void setSourceBranch( String branch );

        void clearFilesToPromote();

        void addFileToPromote( String file );

        void clearFilesInBranch();

        void addFieldInBranch( String file );

        void setReadOnly( boolean readOnly );
    }

    public interface CommitsReader {
        Map<String, String> getCommitsPerFile(String commitsPerFileString);
    }

    private SelectAssetsToPromoteView view;

    private Event<GetFormParamsEvent> getFormParamsEvent;

    private CommitsReader commitsReader;

    private PlaceRequest place;

    protected Map<String, String> commitsPerFile;
    protected List<String> filesToPromote;
    protected Boolean requiresReview = Boolean.FALSE;

    @Inject
    public SelectAssetsToPromotePresenter( SelectAssetsToPromoteView view,
                                           Event<GetFormParamsEvent> getFormParamsEvent,
                                           CommitsReader commitsReader ) {
        this.view = view;
        this.getFormParamsEvent = getFormParamsEvent;
        this.commitsReader = commitsReader;
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
    }

    public void setInputMap( @Observes SetFormParamsEvent event ) {

        Map<String, String> params = event.getParams();
        String files = params.get( "in_list_of_files" );

        commitsPerFile = new HashMap<String, String>();
        filesToPromote = new ArrayList<String>();

        commitsPerFile = commitsReader.getCommitsPerFile( params.get( "in_commits_per_file" ) );

        String[] filesArray = files.split( "," );

        view.setSourceBranch( params.get( "in_source_branch_name" ) );

        view.clearFilesToPromote();
        view.clearFilesInBranch();
        for ( String file : filesArray ) {
            view.addFieldInBranch( file );
        }
        view.setReadOnly( event.isReadOnly() );
    }

    public void getOutputMap( @Observes RequestFormParamsEvent event ) {

        Map<String, Object> outputMap = new HashMap<String, Object>();

        String out_commits = "";

        for ( String selectedFile : filesToPromote ) {
            String commits = commitsPerFile.get( selectedFile );
            if ( commits == null || commits.length() == 0 || out_commits.contains(commits) ) {
                continue;
            }
            if ( out_commits.length() > 0 ) {
                out_commits += ",";
            }
            out_commits += commits;
        }

        outputMap.put( "out_commits", out_commits );
        outputMap.put( "out_requires_review", requiresReview );
        getFormParamsEvent.fire( new GetFormParamsEvent( event.getAction(), outputMap ) );
    }

    public void addFileToPromotedList( String file ) {
        filesToPromote.add( file );
        view.addFileToPromote( file );
    }

    public void removeFileFromPromotedList( String file ) {
        filesToPromote.remove( file );
        view.addFieldInBranch( file );
    }

    public void setRequiresReview( Boolean requiresReview ) {
        this.requiresReview = requiresReview;
    }

    public Boolean getRequiresReview() {
        return requiresReview;
    }

    public Map<String, String> getCommitsPerFile() {
        return commitsPerFile;
    }

    public List<String> getFilesToPromote() {
        return filesToPromote;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Promote_Assets();
    }

    @WorkbenchPartView
    public UberView<SelectAssetsToPromotePresenter> getView() {
        return view;
    }

}
