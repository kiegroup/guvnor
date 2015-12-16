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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;

import javax.enterprise.context.Dependent;

@Dependent
public class SelectAssetsToPromoteViewImpl extends Composite implements SelectAssetsToPromotePresenter.SelectAssetsToPromoteView {

    interface Binder
            extends UiBinder<Widget, SelectAssetsToPromoteViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    private SelectAssetsToPromotePresenter presenter;

    private boolean isReadOnly = false;

    @UiField
    public TextBox sourceBranchBox;

    @UiField
    public CheckBox requiresReviewCheckBox;

    @UiField
    public ListBox filesInTheBranchList;

    @UiField
    public ListBox filesToPromoteList;

    @UiField
    public Button promoteAllFilesButton;

    @UiField
    public Button promoteSelectedFilesButton;

    public SelectAssetsToPromoteViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final SelectAssetsToPromotePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void clearFilesInBranch() {
        filesInTheBranchList.clear();
    }

    @Override
    public void addFieldInBranch( String file ) {
        filesInTheBranchList.addItem( file );
    }

    @Override
    public void clearFilesToPromote() {
        filesToPromoteList.clear();
    }

    @Override
    public void addFileToPromote( String file ) {
        filesToPromoteList.addItem(file);
    }

    @Override
    public void setSourceBranch( String branch ) {
        sourceBranchBox.setText( branch );
    }

    @UiHandler("promoteSelectedFilesButton")
    public void promoteSelectedFilesButton( ClickEvent e ) {
        for (int i = 0; i < filesInTheBranchList.getItemCount(); i++) {
            if (filesInTheBranchList.isItemSelected( i )) {
                String file = filesInTheBranchList.getValue( i );
                presenter.addFileToPromotedList( file );
                filesInTheBranchList.removeItem( i );
                i--;
            }
        }
    }

    @UiHandler("promoteAllFilesButton")
    public void promoteAllFilesButton( ClickEvent e ) {
        for (int i = 0; i < filesInTheBranchList.getItemCount(); i++) {
            String file = filesInTheBranchList.getValue( i );
            presenter.addFileToPromotedList( file );
        }
        filesInTheBranchList.clear();
    }

    @UiHandler("filesToPromoteList")
    public void removeFileFromPromotedList( ClickEvent e ) {
        int selectedIndex = filesToPromoteList.getSelectedIndex();
        String file = filesToPromoteList.getValue(selectedIndex);
        filesToPromoteList.removeItem(selectedIndex);
        presenter.removeFileFromPromotedList(file);
    }

    @UiHandler("requiresReviewCheckBox")
    public void checkRequiredReviewCheckBox( ClickEvent e ) {
        presenter.setRequiresReview( requiresReviewCheckBox.getValue() );
    }

    @Override
    public void setReadOnly( boolean readOnly ) {
        this.isReadOnly = readOnly;
        filesInTheBranchList.setEnabled( !readOnly );
        filesToPromoteList.setEnabled( !readOnly );
        promoteSelectedFilesButton.setEnabled( !readOnly );
        promoteAllFilesButton.setEnabled( !readOnly );
        requiresReviewCheckBox.setEnabled( !readOnly );
        sourceBranchBox.setEnabled( !readOnly );
    }
}
