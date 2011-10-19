/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.asseteditor.drools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.factconstraints.helper.ConstraintsContainer;
import org.drools.ide.common.client.factconstraints.helper.CustomFormsContainer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;

public class WorkingSetEditor extends Composite
    implements
    EditorWidget {

    private RuleAsset                   workingSet;
    private ListBox                     availFacts = new ListBox( true );
    private ListBox                     validFacts = new ListBox( true );
    private ConstraintsContainer        cc;
    private CustomFormsContainer        cfc;

    private FactsConstraintsEditorPanel factsConstraintsgEditorPanel;
    private CustomFormsEditorPanel      customFormsEditorPanel;
    
	public WorkingSetEditor(RuleAsset asset,
                            RuleViewer viewer,
                        ClientFactory clientFactory,
                        EventBus eventBus) {
		this(asset);
	}
	
    public WorkingSetEditor(RuleAsset asset) {
        if ( !AssetFormats.WORKING_SET.equals( asset.getFormat() ) ) {
            throw new IllegalArgumentException( "asset must a be a workingset not a: " + asset.getFormat() );
        }
        workingSet = asset;
        WorkingSetConfigData wsData = (WorkingSetConfigData) workingSet.getContent();
        cc = new ConstraintsContainer( wsData.constraints );
        cfc = new CustomFormsContainer( wsData.customForms );
        refreshWidgets();
        setWidth( "100%" );

    }

    private void refreshWidgets() {
        WorkingSetConfigData wsData = (WorkingSetConfigData) workingSet.getContent();

        TabPanel tPanel = new TabPanel();
        //tPanel.setWidth(800);
        ScrollPanel pnl = new ScrollPanel();
        //        pnl.setAutoWidth(true);
        //pnl.setClosable(false);
        pnl.setTitle( "WS Definition" ); //TODO {bauna} i18n
        //        pnl.setAutoHeight(true);
        pnl.add( buildDoubleList( wsData ) );
        tPanel.add( pnl,
                    "WS Definition" );

        pnl = new ScrollPanel();
        //        pnl.setAutoWidth(true);
        //pnl.setClosable(false);
        //pnl.setTitle("WS Constraints"); //TODO {bauna} i18n
        //        pnl.setAutoHeight(true);
        this.factsConstraintsgEditorPanel = new FactsConstraintsEditorPanel( this );
        pnl.add( this.factsConstraintsgEditorPanel );
        tPanel.add( pnl,
                    "WS Constraints" );

        pnl = new ScrollPanel();
        //        pnl.setAutoWidth(true);
        //pnl.setClosable(false);
        pnl.setTitle( "WS Custom Forms" ); //TODO {bauna} i18n
        //        pnl.setAutoHeight(true);
        this.customFormsEditorPanel = new CustomFormsEditorPanel( this );
        pnl.add( this.customFormsEditorPanel );
        tPanel.add( pnl,
                    "WS Custom Forms" );
        tPanel.addBeforeSelectionHandler( new BeforeSelectionHandler<java.lang.Integer>() {

            public void onBeforeSelection(BeforeSelectionEvent<java.lang.Integer> arg0) {
                factsConstraintsgEditorPanel.fillSelectedFacts();
                customFormsEditorPanel.fillSelectedFacts();
            }

        } );

        tPanel.selectTab( 0 );
        initWidget( tPanel );
    }

    private Grid buildDoubleList(WorkingSetConfigData wsData) {
        Grid grid = new Grid( 2,
                              3 );

        SuggestionCompletionEngine sce = SuggestionCompletionCache.getInstance().getEngineFromCache( workingSet.getMetaData().getPackageName() );
        boolean filteringFact = sce.isFilteringFacts();
        sce.setFilteringFacts( false );

        try {
            Set<String> elem = new HashSet<String>();

            availFacts.setVisibleItemCount( 10 );
            validFacts.setVisibleItemCount( 10 );

            if ( wsData.validFacts != null ) {
                elem.addAll( Arrays.asList( wsData.validFacts ) );
                for ( String factName : wsData.validFacts ) {
                    validFacts.addItem( factName );
                }
            }

            for ( String factName : sce.getFactTypes() ) {
                if ( !elem.contains( factName ) ) {
                    availFacts.addItem( factName );
                }
            }

            Grid btnsPanel = new Grid( 2,
                                       1 );

            btnsPanel.setWidget( 0,
                                 0,
                                 new Button( ">",
                                             new ClickHandler() {

                                                 public void onClick(ClickEvent sender) {
                                                     copySelected( availFacts,
                                                                   validFacts );
                                                     updateAsset( validFacts );
                                                     factsConstraintsgEditorPanel.fillSelectedFacts();
                                                     customFormsEditorPanel.fillSelectedFacts();
                                                 }
                                             } ) );

            btnsPanel.setWidget( 1,
                                 0,
                                 new Button( "&lt;",
                                             new ClickHandler() {

                                                 public void onClick(ClickEvent sender) {
                                                     copySelected( validFacts,
                                                                   availFacts );
                                                     updateAsset( validFacts );
                                                     factsConstraintsgEditorPanel.fillSelectedFacts();
                                                     customFormsEditorPanel.fillSelectedFacts();
                                                 }
                                             } ) );

            grid.setWidget( 0,
                            0,
                            new SmallLabel( "Available Facts" ) ); //TODO i18n
            grid.setWidget( 0,
                            1,
                            new SmallLabel( "" ) );
            grid.setWidget( 0,
                            2,
                            new SmallLabel( "WorkingSet Facts" ) ); //TODO i18n
            grid.setWidget( 1,
                            0,
                            availFacts );
            grid.setWidget( 1,
                            1,
                            btnsPanel );
            grid.setWidget( 1,
                            2,
                            validFacts );

            grid.getColumnFormatter().setWidth( 0,
                                                "45%" );
            grid.getColumnFormatter().setWidth( 0,
                                                "10%" );
            grid.getColumnFormatter().setWidth( 0,
                                                "45%" );
            return grid;
        } finally {
            sce.setFilteringFacts( filteringFact );
        }
    }

    /**
     * This will get the save widgets.
     */
    private void updateAsset(ListBox availFacts) {
        List<String> l = new ArrayList<String>( availFacts.getItemCount() );
        for ( int i = 0; i < availFacts.getItemCount(); i++ ) {
            l.add( availFacts.getItemText( i ) );
        }
        ((WorkingSetConfigData) workingSet.getContent()).validFacts = l.toArray( new String[l.size()] );
    }

    private void copySelected(final ListBox from,
                              final ListBox to) {
        int selected;
        while ( (selected = from.getSelectedIndex()) != -1 ) {
            to.addItem( from.getItemText( selected ) );
            from.removeItem( selected );
            factsConstraintsgEditorPanel.notifyValidFactsChanged();
            customFormsEditorPanel.notifyValidFactsChanged();
        }
    }

    public ConstraintsContainer getConstraintsConstrainer() {
        return cc;
    }

    public CustomFormsContainer getCustomFormsContainer() {
        return cfc;
    }

    protected RuleAsset getWorkingSet() {
        return workingSet;
    }

    protected ListBox getValidFactsListBox() {
        return this.validFacts;
    }
}
