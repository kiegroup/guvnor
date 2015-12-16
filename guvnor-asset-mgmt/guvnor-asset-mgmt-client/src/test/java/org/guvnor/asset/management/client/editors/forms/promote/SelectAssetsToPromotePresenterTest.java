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

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import junit.framework.TestCase;
import org.guvnor.asset.management.client.i18n.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.forms.GetFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.RequestFormParamsEvent;
import org.uberfire.ext.widgets.common.client.forms.SetFormParamsEvent;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SelectAssetsToPromotePresenterTest extends TestCase {

    protected List<FileCommitsLog> commitsLog;

    protected FileCommitsLog employeeLog;
    protected FileCommitsLog addressLog;
    protected FileCommitsLog departmentLog;
    protected FileCommitsLog officeLog;
    protected FileCommitsLog projectLog;

    private String branch = "master";

    private String filesInBranch;

    @GwtMock
    protected Constants constants;

    @Mock
    protected PlaceRequest placeRequest;

    @Mock
    protected SelectAssetsToPromotePresenter.SelectAssetsToPromoteView view;

    @Mock
    protected EventSourceMock<GetFormParamsEvent> eventMock;

    protected SelectAssetsToPromotePresenter presenter;

    @Before
    public void init() {
        presenter = new SelectAssetsToPromotePresenter(view, eventMock, new SelectAssetsToPromotePresenter.CommitsReader() {
            @Override
            public Map<String, String> getCommitsPerFile(String commitsPerFileString) {
                Map<String, String> commitsPerFile = new HashMap<String, String>();
                for( FileCommitsLog log : commitsLog ) {
                    StringBuffer commits = new StringBuffer();

                    for ( String commit : log.getCommits() ) {
                        if ( commits.length() > 0 ) commits.append( "," );
                        commits.append( commit );
                    }

                    commitsPerFile.put( log.getPath(), commits.toString() );
                }
                return commitsPerFile;
            }
        });
        initLogInfo();
    }

    @Test
    public void testBasicMethods() {
        presenter.onStartup( placeRequest );

        assertEquals( "Presenter has wrong view.", view, presenter.getView() );

        presenter.getTitle();

        verify( constants ).Promote_Assets();

        presenter.setRequiresReview( Boolean.TRUE );
    }

    @Test
    public void testAssetSelection() {

        startTest();

        int promotedFiles = 1;

        for ( FileCommitsLog log : commitsLog ) {
            presenter.addFileToPromotedList( log.getPath() );
            verify( view ).addFileToPromote( log.getPath() );
            checkPromotedFiles( promotedFiles );
            promotedFiles ++;
        }

        promotedFiles = commitsLog.size();

        for ( FileCommitsLog log : commitsLog ) {
            promotedFiles --;
            presenter.removeFileFromPromotedList( log.getPath() );
            verify( view, atLeastOnce() ).addFieldInBranch( log.getPath() );
            checkPromotedFiles( promotedFiles );
        }
    }

    @Test
    public void testSelectAssetsFormResult() {

        startTest();

        presenter.addFileToPromotedList( employeeLog.getPath() );
        presenter.addFileToPromotedList( departmentLog.getPath() );
        presenter.addFileToPromotedList( projectLog.getPath() );

        presenter.getOutputMap(new RequestFormParamsEvent());

        ArgumentCaptor<GetFormParamsEvent> getFormParamsCaptor = ArgumentCaptor.forClass( GetFormParamsEvent.class );

        verify( eventMock ).fire( getFormParamsCaptor.capture() );

        assertNotNull( "No GetFormParamsEvent generated.", getFormParamsCaptor.getValue() );

        Map<String, Object> resultParams = getFormParamsCaptor.getValue().getParams();

        String commits = (String) resultParams.get( "out_commits" );

        checkFileCommits( commits, employeeLog.getCommits() );
        checkFileCommits( commits, departmentLog.getCommits() );
        checkFileCommits( commits, projectLog.getCommits() );

        Boolean requiresReview = (Boolean) resultParams.get( "out_requires_review" );
        assertEquals("Result contains wrong value for 'out_requires_review'.", presenter.getRequiresReview(), requiresReview);
    }

    protected void startTest() {

        Map<String, String> params = new HashMap<String, String>();
        params.put( "in_source_branch_name", branch );
        params.put( "in_list_of_files", filesInBranch );
        params.put( "in_commits_per_file", "" ); // no need to send commits per file json, they will be mocked later.

        SetFormParamsEvent setFormParamsEvent = new SetFormParamsEvent( params, false );

        presenter.setInputMap( setFormParamsEvent );

        verify( view ).setSourceBranch( branch );
        verify( view, times( 5 ) ).addFieldInBranch( anyString() );
        verify( view ).setReadOnly( Boolean.FALSE );

        assertEquals( "Presenter must contain " + commitsLog.size() + " files in branch. ", commitsLog.size(), presenter.getCommitsPerFile().size() );
    }

    protected void checkFileCommits( String commits, String[] fileCommits ) {
        for ( String commit : fileCommits ) {
            assertTrue( "Commits list '" + commits + "' must contain commit '" + commit + "'.", commits.contains( commit ) );
        }
    }

    protected void checkPromotedFiles( int filesToPromote ) {
        assertEquals( "Presenter must contain " + filesToPromote + " files to promote. ", filesToPromote, presenter.getFilesToPromote().size() );
    }

    protected void initLogInfo() {
        // Mock asset commits
        employeeLog = new FileCommitsLog(
                "src/main/java/org/guvnor/asset/management/test/Employee.java",
                new String[] { "d7b6f69712291cd94a16800ac6a01e12c6119e3d",
                        "70eb7440daeff2b62506d5a45b5016807e6752a3" });

        addressLog = new FileCommitsLog(
                "src/main/java/org/guvnor/asset/management/test/Address.java",
                new String[] { "d7b6f69712291cd94a16800ac6a01e12c6119e3d",
                        "ec3859477dcdf86a69bd70955ebc9188cfa9dc36" });

        departmentLog = new FileCommitsLog(
                "src/main/java/org/guvnor/asset/management/test/Department.java",
                new String[] { "d7b6f69712291cd94a16800ac6a01e12c6119e3d",
                        "ec3859477dcdf86a69bd70955ebc9188cfa9dc36",
                        "90a4e33d4a53cdbaa7258021cd0f22f853e6eebd" });

        officeLog = new FileCommitsLog(
                "src/main/java/org/guvnor/asset/management/test/Office.java",
                new String[] { "d7b6f69712291cd94a16800ac6a01e12c6119e3d" });

        projectLog = new FileCommitsLog(
                "src/main/java/org/guvnor/asset/management/test/Project.java",
                new String[] { "70eb7440daeff2b62506d5a45b5016807e6752a3" });

        commitsLog = new ArrayList<FileCommitsLog>();
        commitsLog.add( employeeLog );
        commitsLog.add( addressLog );
        commitsLog.add( departmentLog );
        commitsLog.add( officeLog );
        commitsLog.add( projectLog );

        StringBuffer files = new StringBuffer();

        for ( FileCommitsLog log : commitsLog ) {
            // Build files List
            if ( files.length() > 0 ) files.append( ",");
            files.append(log.getPath());
        }

        filesInBranch = files.toString();
    }

    private class FileCommitsLog {
        private String path;
        private String[] commits;

        public FileCommitsLog( String path, String[] commits ) {
            this.path = path;
            this.commits = commits;
        }

        public String getPath() {
            return path;
        }

        public void setPath( String path ) {
            this.path = path;
        }

        public String[] getCommits() {
            return commits;
        }

        public void setCommits( String[] commits ) {
            this.commits = commits;
        }
    }
}
