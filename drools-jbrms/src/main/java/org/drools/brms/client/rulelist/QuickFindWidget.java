package org.drools.brms.client.rulelist;
/*
 * Copyright 2005 JBoss Inc
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



import java.util.ArrayList;
import java.util.List;

import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.common.PrettyFormLayout;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.gwtext.client.widgets.form.FormPanel;

/**
 * This is for quickly finding an asset by name. Partial completion is allowed.
 * This also uses some auto completion magic.
 * @author Michael Neale
 */
public class QuickFindWidget extends Composite {

    private final FormStyleLayout layout;
    private final FlexTable listPanel;
    private SuggestBox searchBox;
    private CheckBox archiveBox;
    private EditItemEvent editEvent;



    public QuickFindWidget(EditItemEvent editEvent) {
        layout = new FormStyleLayout("images/system_search.png", "");

        searchBox = new SuggestBox(new SuggestOracle() {
			public void requestSuggestions(Request r, Callback cb) {
				loadShortList(r.getQuery(), r, cb);

			}
        });


        this.editEvent = editEvent;
        HorizontalPanel srch = new HorizontalPanel();
        Button go = new Button("Go");
        go.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
               updateList();
            }
        } );

        srch.add( searchBox );
        srch.add( go );

        archiveBox = new CheckBox();

        archiveBox.setChecked(false);

        layout.addAttribute( "Find items with a name matching:", srch );
        layout.addAttribute("Include archived items in list:", archiveBox);


        listPanel = new FlexTable();
        listPanel.setWidget( 0, 0, new HTML("<img src='images/information.gif'/>&nbsp;Enter the name or part of a name. Alternatively, use the categories to browse.") );

        PrettyFormLayout pfl = new PrettyFormLayout();
        pfl.startSection();
        pfl.addRow(listPanel);




        pfl.endSection();
        layout.addRow(pfl);


        initWidget( layout );
    }

    /**
     * This will load a list of items as they are typing.
     */
    protected void loadShortList(String match, final Request r, final Callback cb) {
        RepositoryServiceFactory.getService().quickFindAsset( match, 5, archiveBox.isChecked() ,new GenericCallback() {


            public void onSuccess(Object data) {
                final TableDataResult result = (TableDataResult) data;
                List items = new ArrayList();
                for ( int i = 0; i < result.data.length; i++ ) {
                    if (!result.data[i].id.equals( "MORE" )) {
                    	final String str = result.data[i].values[0];
                    	items.add( new SuggestOracle.Suggestion() {

							public String getDisplayString() {
								return str;
							}

							public String getReplacementString() {
								return str;
							}

                    	});

                    }
                }
                cb.onSuggestionsReady(r, new SuggestOracle.Response(items));
            }

        });

    }

    protected void updateList() {

        LoadingPopup.showMessage( "Searching..." );
        RepositoryServiceFactory.getService().quickFindAsset( searchBox.getText(), 15, archiveBox.isChecked() , new GenericCallback() {
            public void onSuccess(Object data) {
                TableDataResult result = (TableDataResult) data;
                populateList(result);

            }
        });

    }

    protected void populateList(TableDataResult result) {


        FlexTable data = new FlexTable();

        //if its only one, just open it...
        if (result.data.length == 1) {
            editEvent.open( result.data[0].id );
        }

        for ( int i = 0; i < result.data.length; i++ ) {

            final TableDataRow row = result.data[i];
            if (row.id.equals( "MORE" )) {
                data.setWidget( i, 0, new HTML("<i>There are more items... try narrowing the search terms..</i>") );
                data.getFlexCellFormatter().setColSpan( i, 0, 3 );
            } else {
                data.setWidget( i, 0, new Label(row.values[0]) );
                data.setWidget( i, 1, new Label(row.values[1]) );
                Button open = new Button("Open");
                open.addClickListener( new ClickListener() {
                    public void onClick(Widget w) {
                        editEvent.open( row.id );
                    }
                } );

                data.setWidget( i, 2, open );
            }


        }

        data.setWidth( "100%" );
        listPanel.setWidget( 0, 0, data);

        LoadingPopup.close();

    }


}