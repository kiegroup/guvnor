/*
 * Copyright 2010 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.widgets.query;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import org.drools.guvnor.client.common.DatePickerTextBox;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.configurations.ApplicationPreferences;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.rpc.MetaDataQuery;
import org.drools.guvnor.client.util.DecoratedDisclosurePanel;
import org.drools.guvnor.client.widgets.tables.QueryPagedTable;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.*;

@Dependent
@WorkbenchScreen(identifier = "search")
public class QueryWidget extends Composite {

    private ConstantsCore       constants = GWT.create( ConstantsCore.class );

    private VerticalPanel       layout;
    private final ClientFactory clientFactory;

    @Inject
    public QueryWidget(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
        layout = new VerticalPanel();
        doQuickFind();
        doTextSearch();
        doMetaSearch();
        layout.setWidth( "100%" );
        initWidget( layout );
        setWidth( "100%" );
    }

    private void doQuickFind() {
        DecoratedDisclosurePanel advancedDisclosure = new DecoratedDisclosurePanel( constants.NameSearch() );
        advancedDisclosure.ensureDebugId( "cwDisclosurePanel" );
        advancedDisclosure.setWidth( "100%" );
        advancedDisclosure.setContent( new QuickFindWidget( clientFactory ) );
        advancedDisclosure.setOpen( true );

        layout.add( advancedDisclosure );
    }

    private void doTextSearch() {
        DecoratedDisclosurePanel advancedDisclosure = new DecoratedDisclosurePanel( constants.TextSearch() );
        advancedDisclosure.setWidth( "100%" );
        advancedDisclosure.setOpen( true );

        VerticalPanel container = new VerticalPanel();
        VerticalPanel criteria = new VerticalPanel();

        FormStyleLayout ts = new FormStyleLayout();
        final TextBox tx = new TextBox();
        ts.addAttribute( constants.SearchFor(),
                         tx );

        final CheckBox archiveBox = new CheckBox();
        archiveBox.setValue( false );
        ts.addAttribute( constants.IncludeArchivedAssetsInResults(),
                         archiveBox );

        Button go = new Button();
        go.setText( constants.Search1() );
        ts.addAttribute( "",
                         go );
        ts.setWidth( "100%" );

        final SimplePanel resultsP = new SimplePanel();
        final ClickHandler cl = new ClickHandler() {

            public void onClick(ClickEvent arg0) {
                if ( tx.getText().equals( "" ) ) {
                    Window.alert( constants.PleaseEnterSomeSearchText() );
                    return;
                }
                resultsP.clear();
                QueryPagedTable table = new QueryPagedTable(
                                                             tx.getText(),
                                                             archiveBox.getValue(),
                                                             clientFactory );
                resultsP.add( table );
            }

        };

        go.addClickHandler( cl );
        tx.addKeyPressHandler( new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if ( event.getCharCode() == KeyCodes.KEY_ENTER ) {
                    cl.onClick( null );
                }
            }
        } );

        criteria.add( ts );
        container.add( criteria );
        container.add( resultsP );
        advancedDisclosure.setContent( container );
        layout.add( advancedDisclosure );
    }

    private void doMetaSearch() {
        DecoratedDisclosurePanel advancedDisclosure = new DecoratedDisclosurePanel( constants.AttributeSearch() );
        advancedDisclosure.setWidth( "100%" );
        advancedDisclosure.setOpen( true );

        VerticalPanel container = new VerticalPanel();
        VerticalPanel criteria = new VerticalPanel();

        final Map<String, MetaDataQuery> atts = new HashMap<String, MetaDataQuery>() {
            private static final long serialVersionUID = 510l;

            {
                put( constants.CreatedBy(),
                        new MetaDataQuery( "drools:creator" ) ); // NON-NLS
                put( constants.Format1(),
                        new MetaDataQuery( "drools:format" ) ); // NON-NLS
                put( constants.Subject(),
                        new MetaDataQuery( "drools:subject" ) ); // NON-NLS
                put( constants.Type1(),
                        new MetaDataQuery( "drools:type" ) ); // NON-NLS
                put( constants.ExternalLink(),
                        new MetaDataQuery( "drools:relation" ) ); // NON-NLS
                put( constants.Source(),
                        new MetaDataQuery( "drools:source" ) ); // NON-NLS
                put( constants.Description1(),
                        new MetaDataQuery( "drools:description" ) ); // NON-NLS
                put( constants.LastModifiedBy(),
                        new MetaDataQuery( "drools:lastContributor" ) ); // NON-NLS
                put( constants.CheckinComment(),
                        new MetaDataQuery( "drools:checkinComment" ) ); // NON-NLS
            }
        };

        FormStyleLayout fm = new FormStyleLayout();
        for ( String fieldName : atts.keySet() ) {
            final MetaDataQuery q = atts.get( fieldName );
            final TextBox box = new TextBox();
            box.setTitle( constants.WildCardsSearchTip() );
            fm.addAttribute( fieldName
                                     + ":",
                             box );
            box.addChangeHandler( new ChangeHandler() {
                public void onChange(ChangeEvent arg0) {
                    q.valueList = box.getText();
                }
            } );
        }

        HorizontalPanel created = new HorizontalPanel();
        created.add( new SmallLabel( constants.AfterColon() ) );
        final DatePickerTextBox createdAfter = new DatePickerTextBox( "" );
        created.add( createdAfter );

        created.add( new SmallLabel( "&nbsp;" ) ); // NON-NLS

        created.add( new SmallLabel( constants.BeforeColon() ) );
        final DatePickerTextBox createdBefore = new DatePickerTextBox( "" );
        created.add( createdBefore );

        fm.addAttribute( constants.DateCreated1(),
                         created );

        HorizontalPanel lastMod = new HorizontalPanel();
        lastMod.add( new SmallLabel( constants.AfterColon() ) );
        final DatePickerTextBox lastModAfter = new DatePickerTextBox( "" );
        lastMod.add( lastModAfter );

        lastMod.add( new SmallLabel( "&nbsp;" ) ); // NON-NLS

        lastMod.add( new SmallLabel( constants.BeforeColon() ) );
        final DatePickerTextBox lastModBefore = new DatePickerTextBox( "" );
        lastMod.add( lastModBefore );

        fm.addAttribute( constants.LastModified1(),
                         lastMod );

        final SimplePanel resultsP = new SimplePanel();
        Button search = new Button( constants.Search() );
        fm.addAttribute( "",
                         search );
        search.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                resultsP.clear();
                try {
                    List<MetaDataQuery> metadata = new ArrayList<MetaDataQuery>();
                    metadata.addAll( atts.values() );
                    QueryPagedTable table = new QueryPagedTable( metadata,
                                                                 getDate( createdAfter ),
                                                                 getDate( createdBefore ),
                                                                 getDate( lastModAfter ),
                                                                 getDate( lastModBefore ),
                                                                 false,
                                                                 clientFactory );
                    resultsP.add( table );
                } catch ( IllegalArgumentException e ) {
                    ErrorPopup.showMessage( constants.BadDateFormatPleaseTryAgainTryTheFormatOf0(
                            ApplicationPreferences.getDroolsDateFormat() ) );
                }
            }

            private Date getDate(final DatePickerTextBox datePicker) {
                try {
                    return datePicker.getDate();
                } catch ( IllegalArgumentException e ) {
                    datePicker.clear();
                    throw e;
                }
            }
        } );

        criteria.add( fm );
        container.add( criteria );
        container.add( resultsP );
        advancedDisclosure.setContent( container );

        layout.add( advancedDisclosure );
    }


    @WorkbenchPartTitle
    public String getTitle() {
        return constants.Find();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return this;
    }
}
