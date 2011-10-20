/*
 * Copyright 2011 JBoss Inc
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
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.PageResponse;
import org.drools.guvnor.client.rpc.QueryPageRequest;
import org.drools.guvnor.client.rpc.QueryPageRow;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.widgets.tables.QueryPagedTable;

import java.util.ArrayList;
import java.util.List;

/**
 * This is for quickly finding an asset by name. Partial completion is allowed.
 * This also uses some auto completion magic.
 */
public class QuickFindWidget extends Composite {

    private static Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );

    private SuggestBox searchBox;
    private CheckBox archiveBox;
    private CheckBox caseSensitiveBox;

    public QuickFindWidget( final ClientFactory clientFactory ) {

        VerticalPanel container = new VerticalPanel();
        VerticalPanel criteria = new VerticalPanel();

        FormStyleLayout layout = new FormStyleLayout( images.systemSearch(),
                "" );

        searchBox = new SuggestBox( new SuggestOracle() {
            public void requestSuggestions( Request r,
                                            Callback cb ) {
                loadShortList( r.getQuery(),
                        archiveBox.getValue(),
                        caseSensitiveBox.getValue(),
                        r,
                        cb );

            }
        } );

        HorizontalPanel srch = new HorizontalPanel();

        final SimplePanel resultsP = new SimplePanel();
        final ClickHandler cl = new ClickHandler() {
            public void onClick( ClickEvent event ) {
                resultsP.clear();
                QueryPagedTable table = new QueryPagedTable(
                        searchBox.getValue(),
                        archiveBox.getValue(),
                        caseSensitiveBox.getValue(),
                        clientFactory );
                resultsP.add( table );
            }
        };
        searchBox.addKeyUpHandler( new KeyUpHandler() {
            public void onKeyUp( KeyUpEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    cl.onClick( null );
                }
            }
        } );
        srch.add( searchBox );
        layout.addAttribute( constants.FindItemsWithANameMatching(),
                srch );

        archiveBox = new CheckBox();
        archiveBox.setValue( false );
        layout.addAttribute( constants.IncludeArchivedAssetsInResults(),
                archiveBox );

        caseSensitiveBox = new CheckBox();
        caseSensitiveBox.setValue( false );
        layout.addAttribute( constants.IsSearchCaseSensitive(),
                caseSensitiveBox );

        Button go = new Button( constants.Search() );
        go.addClickHandler( cl );
        layout.addAttribute( "",
                go );

        HorizontalPanel searchTitle = new HorizontalPanel();
        searchTitle.add( new Image( images.information() ) );
        searchTitle.add( new Label( constants.EnterSearchString() ) );
        FlexTable listPanel = new FlexTable();
        listPanel.setWidget( 0,
                0,
                searchTitle );

        PrettyFormLayout pfl = new PrettyFormLayout();
        pfl.startSection();
        pfl.addRow( listPanel );
        pfl.endSection();

        criteria.add( pfl );
        criteria.add( layout );
        container.add( criteria );
        container.add( resultsP );

        initWidget( container );
    }

    /**
     * This will load a list of items as they are typing.
     */
    protected void loadShortList( String searchText,
                                  Boolean searchArchived,
                                  Boolean isCaseSensitive,
                                  final Request r,
                                  final Callback cb ) {
        final QueryPageRequest queryRequest = new QueryPageRequest( searchText,
                searchArchived,
                isCaseSensitive,
                0,
                5 );
        RepositoryServiceFactory.getAssetService().quickFindAsset( queryRequest,
                new GenericCallback<PageResponse<QueryPageRow>>() {

                    public void onSuccess( PageResponse<QueryPageRow> result ) {
                        List<SuggestOracle.Suggestion> items = new ArrayList<SuggestOracle.Suggestion>();
                        for (QueryPageRow row : result.getPageRowList()) {
                            final String name = row.getName();
                            items.add( new SuggestOracle.Suggestion() {

                                public String getDisplayString() {
                                    return name;
                                }

                                public String getReplacementString() {
                                    return name;
                                }

                            } );
                        }
                        cb.onSuggestionsReady( r,
                                new SuggestOracle.Response( items ) );
                    }

                } );

    }

}
