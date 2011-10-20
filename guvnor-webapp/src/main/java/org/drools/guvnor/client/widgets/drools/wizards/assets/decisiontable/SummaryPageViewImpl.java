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

package org.drools.guvnor.client.widgets.drools.wizards.assets.decisiontable;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.WizardResources;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * An implementation of the Summary page
 */
public class SummaryPageViewImpl extends Composite
    implements
    SummaryPageView {

    private Presenter presenter;

    @UiField
    HorizontalPanel   messages;

    @UiField
    TextBox           txtAssetName;

    @UiField
    HorizontalPanel   assetNameContainer;

    @UiField
    Label             lblAssetDescription;

    @UiField
    Label             lblPackageName;

    @UiField
    Label             lblTableFormat;

    private String    assetName;

    interface SummaryPageWidgetBinder
        extends
        UiBinder<Widget, SummaryPageViewImpl> {
    }

    private static SummaryPageWidgetBinder uiBinder  = GWT.create( SummaryPageWidgetBinder.class );

    private static Constants               constants = GWT.create( Constants.class );

    public SummaryPageViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
        initialiseAssetName();
    }

    private void initialiseAssetName() {
        txtAssetName.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange(ValueChangeEvent<String> event) {
                assetName = txtAssetName.getText();
                presenter.stateChanged();
                validateAssetName();
            }

        } );
    }

    private void validateAssetName() {
        if ( assetName != null && !assetName.equals( "" ) ) {
            assetNameContainer.setStyleName( WizardResources.INSTANCE.style().wizardDTableFieldContainerValid() );
        } else {
            assetNameContainer.setStyleName( WizardResources.INSTANCE.style().wizardDTableFieldContainerInvalid() );
        }
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
        txtAssetName.setText( assetName );
        validateAssetName();
    }

    public void setAssetDescription(String assetDescription) {
        lblAssetDescription.setText( assetDescription );
    }

    public void setPackageName(String packageName) {
        lblPackageName.setText( packageName );
    }

    public void setTableFormat(TableFormat tableFormat) {
        switch ( tableFormat ) {
            case EXTENDED_ENTRY :
                lblTableFormat.setText( constants.TableFormatExtendedEntry() );
                break;
            case LIMITED_ENTRY :
                lblTableFormat.setText( constants.TableFormatLimitedEntry() );
                break;
        }
    }

    public String getAssetName() {
        return this.assetName;
    }

    public void setHasInvalidAssetName(boolean isInvalid) {
        messages.setVisible( isInvalid );
    }

}
