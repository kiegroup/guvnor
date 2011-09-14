/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.client.widgets.wizards;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.Popup;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The generic Wizard view implementation
 */
public class WizardActivityViewImpl extends Popup
        implements
        WizardActivityView {

    @UiField
    protected VerticalPanel              sideBar;

    @UiField
    protected SimplePanel                sideBarContainer;

    @UiField
    ScrollPanel                          bodyContainer;

    @UiField
    protected SimplePanel                body;

    @UiField
    protected Button                     btnNext;

    @UiField
    protected Button                     btnPrevious;

    @UiField
    protected Button                     btnFinish;

    private Widget                       content;
    private List<WizardPageTitle>        pageTitleWidgets = new ArrayList<WizardPageTitle>();

    private int                          pageNumber;
    private int                          pageNumberTotal;

    private WizardActivityView.Presenter presenter;
    private EventBus                     eventBus;

    interface WizardActivityViewImplBinder
        extends
        UiBinder<Widget, WizardActivityViewImpl> {
    }

    private static WizardActivityViewImplBinder uiBinder  = GWT.create( WizardActivityViewImplBinder.class );

    private static Constants                    constants = GWT.create( Constants.class );

    public WizardActivityViewImpl(EventBus eventBus) {
        this.eventBus = eventBus;
        content = uiBinder.createAndBindUi( this );
    }

    @Override
    public Widget getContent() {
        return this.content;
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setPageTitles(List<WizardPage> pages) {
        this.pageNumberTotal = pages.size() - 1;
        for ( WizardPage page : pages ) {
            WizardPageTitle wpt = new WizardPageTitle( eventBus,
                                                       page );
            pageTitleWidgets.add( wpt );
            sideBar.add( wpt );
        }
    }

    @UiHandler(value = "btnCancel")
    public void btnCancelClick(ClickEvent event) {
        this.hide();
    }

    @UiHandler(value = "btnFinish")
    public void btnFinishClick(ClickEvent event) {
        presenter.complete();
    }

    @UiHandler(value = "btnNext")
    public void btnNextClick(ClickEvent event) {
        if ( pageNumber == pageNumberTotal ) {
            return;
        }
        selectPage( pageNumber + 1 );
        btnNext.setFocus( false );
    }

    @UiHandler(value = "btnPrevious")
    public void btnPreviousClick(ClickEvent event) {
        if ( pageNumber == 0 ) {
            return;
        }
        selectPage( pageNumber - 1 );
        btnPrevious.setFocus( false );
    }

    public void selectPage(int pageNumber) {
        if ( pageNumber < 0 || pageNumber > this.pageTitleWidgets.size() - 1 ) {
            return;
        }
        this.pageNumber = pageNumber;
        for ( int i = 0; i < this.pageTitleWidgets.size(); i++ ) {
            WizardPageTitle wpt = this.pageTitleWidgets.get( i );
            wpt.setPageSelected( i == pageNumber );
        }
        btnNext.setEnabled( pageNumber < pageNumberTotal );
        btnPrevious.setEnabled( pageNumber > 0 );
        presenter.pageSelected( pageNumber );
    }

    public void setBodyWidget(Widget w) {
        body.setWidget( w );
        center();
    }

    public void setPreferredHeight(int height) {
        bodyContainer.setHeight( height + "px" );
        sideBarContainer.setHeight( height + "px" );
    }

    public void setPreferredWidth(int width) {
        bodyContainer.setWidth( width + "px" );
    }

    public void setPageCompletionState(int pageIndex,
                                       boolean isComplete) {
        WizardPageTitle wpt = this.pageTitleWidgets.get( pageIndex );
        wpt.setComplete( isComplete );
    }

    public void setCompletionStatus(boolean isComplete) {
        btnFinish.setEnabled( isComplete );
    }

    public void showSavingIndicator() {
        LoadingPopup.showMessage( constants.SavingPleaseWait() );
    }

    public void hideSavingIndicator() {
        LoadingPopup.close();
    }

    public void showDuplicateAssetNameError() {
        Window.alert( constants.AssetNameAlreadyExistsPickAnother() );
    }

    public void showUnspecifiedCheckinError() {
        ErrorPopup.showMessage( constants.FailedToCheckInTheItemPleaseContactYourSystemAdministrator() );
    }

    public void showCheckinError(String message) {
        ErrorPopup.showMessage( message );
    }

}
