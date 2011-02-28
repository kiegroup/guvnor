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
package org.drools.guvnor.client.ruleeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.toolbar.ActionToolbarButtonsConfigurationProvider;
import org.drools.guvnor.client.rulelist.OpenItemCommand;
import org.drools.guvnor.client.util.LazyStackPanel;
import org.drools.guvnor.client.util.LoadContentCommand;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MultiViewEditor extends GuvnorEditor {

    private Constants                                 constants       = GWT.create( Constants.class );

    private VerticalPanel                             viewsPanel      = new VerticalPanel();
    private boolean                                   showMetadata    = false;
    private boolean                                   showDescription = false;
    private Command                                   closeCommand;
    private final Set<MultiViewRow>                   rows            = new HashSet<MultiViewRow>();
    private Map<String, RuleViewer>                   ruleViews       = new HashMap<String, RuleViewer>();
    private final OpenItemCommand                       editItemEvent;
    private ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider;

    private Map<String, RuleAsset>                    assets          = new HashMap<String, RuleAsset>();

    private MultiViewEditorMenuBarCreator             menuBarCreator;

    public MultiViewEditor(MultiViewRow[] rows,
                           OpenItemCommand editItemEvent) {
        this( rows,
              editItemEvent,
              null );
    }

    public MultiViewEditor(MultiViewRow[] rows,
                           OpenItemCommand editItemEvent,
                           ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider) {
        this( Arrays.asList( rows ),
              editItemEvent,
              individualActionToolbarButtonsConfigurationProvider );
    }

    public MultiViewEditor(List<MultiViewRow> rows,
                           OpenItemCommand editItemEvent,
                           ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider) {
        this.rows.addAll( rows );
        this.editItemEvent = editItemEvent;
        this.individualActionToolbarButtonsConfigurationProvider = individualActionToolbarButtonsConfigurationProvider;

        init();
    }

    public MultiViewEditor(RuleAsset[] assets,
                           OpenItemCommand editItemEvent,
                           ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider) {
        this( assets,
              editItemEvent,
              individualActionToolbarButtonsConfigurationProvider,
              null );
    }

    public MultiViewEditor(RuleAsset[] assets,
            OpenItemCommand editItemEvent,
            ActionToolbarButtonsConfigurationProvider individualActionToolbarButtonsConfigurationProvider,
            MultiViewEditorMenuBarCreator menuBarCreator) {
        this.rows.addAll( createRows( assets ) );
        this.editItemEvent = editItemEvent;
        this.individualActionToolbarButtonsConfigurationProvider = individualActionToolbarButtonsConfigurationProvider;
        this.menuBarCreator = menuBarCreator;
        addAssets( assets );
        init();
}

    private void addAssets(RuleAsset[] assets) {
        for ( RuleAsset ruleAsset : assets ) {
            this.assets.put( ruleAsset.uuid,
                             ruleAsset );
        }
    }

    private static List<MultiViewRow> createRows(RuleAsset[] assets) {
        List<MultiViewRow> rows = new ArrayList<MultiViewRow>();
        for ( RuleAsset ruleAsset : assets ) {
            MultiViewRow row = new MultiViewRow();
            row.uuid = ruleAsset.uuid;
            row.name = ruleAsset.metaData.name;
            row.format = AssetFormats.BUSINESS_RULE;
            rows.add( row );
        }
        return rows;
    }

    private void init() {
        VerticalPanel rootPanel = new VerticalPanel();

        rootPanel.setWidth( "100%" );

        rootPanel.add( createToolbar() );

        viewsPanel.setWidth( "100%" );
        rootPanel.add( viewsPanel );

        doViews();

        initWidget( rootPanel );
    }

    //    MenuBar layoutMenu = new MenuBar( true );
    //    layoutMenu.addItem( new MenuItem( showMetadataText(),
    //                                      new Command() {
    //                                          public void execute() {
    //                                              doViews();
    //                                          }
    //                                      } ) );
    //    layoutMenu.addItem( new MenuItem( showDescriptionAndDiscussionText(),
    //                                      new Command() {
    //                                          public void execute() {
    //                                              doViews();
    //                                          }
    //                                      } ) );
    //
    //    toolbar.addItem( constants.Show(),
    //                     layoutMenu );
    private MenuBar createToolbar() {

        //if no MultiViewEditorMenuBarCreator is set, then use the Default
        //implementation.
        if ( this.menuBarCreator == null ) {
            this.menuBarCreator = new DefaultMultiViewEditorMenuBarCreator();
        }

        return this.menuBarCreator.createMenuBar( this );
    }

    private void doViews() {

        viewsPanel.clear();
        ruleViews.clear();
        final LazyStackPanel panel = new LazyStackPanel();

        //the first row will be expanded
        int rowNumber = 1;
        for ( final MultiViewRow row : rows ) {
            //            panel.add( row.name );
            //            panel.setIconCls( EditorLauncher.getAssetFormatBGStyle( row.format ) ); //NON-NLS
            //            panel.setCollapsible( true );
            //            panel.setTitleCollapse( true );
            //            panel.setCollapsed( true );
            //            panel.setWidth( "100%" );

            //            panel.addListener( new PanelListenerAdapter() {
            //                public void onExpand(final Panel panel) {

            panel.add( row.name,
                       new LoadContentCommand() {

                           public Widget load() {
                               final SimplePanel content = new SimplePanel();

                               if ( assets.containsKey( row.uuid ) ) {
                                   addRuleViewInToSimplePanel( row,
                                                               content,
                                                               assets.get( row.uuid )  );
                               } else {
                                   RepositoryServiceFactory.getAssetService().loadRuleAsset( row.uuid,
                                                                                        new GenericCallback<RuleAsset>() {

                                                                                            public void onSuccess(final RuleAsset asset) {
                                                                                                assets.put( asset.uuid,
                                                                                                            asset );

                                                                                                addRuleViewInToSimplePanel( row,
                                                                                                                            content,
                                                                                                                            asset );
                                                                                            }

                                                                                        } );

                               }
                               return content;
                           }
                       },
                       rowNumber == 1 );

            // Only load if it doesn't exist yet.
            //            if ( ruleViews.get( row.uuid ) == null ) {
            //
            //            } else {
            //                panel.add( ruleViews.get( row.uuid ) );
            //                //                        panel.doLayout();
            //            }
            //            //                }
            //
            //            //            } );
            //
            rowNumber++;
        }

        viewsPanel.add( panel );

    }

    private void addRuleViewInToSimplePanel(final MultiViewRow row,
                                            final SimplePanel content,
                                            final RuleAsset asset) {
        SuggestionCompletionCache.getInstance().doAction( asset.metaData.packageName,
                                                          new Command() {

                                                              public void execute() {

                                                                  RuleViewerSettings ruleViewerSettings = new RuleViewerSettings();
                                                                  ruleViewerSettings.setDocoVisible( false );
                                                                  ruleViewerSettings.setMetaVisible( false );
                                                                  ruleViewerSettings.setStandalone(true);

                                                                  final RuleViewer ruleViewer = new RuleViewer( asset,
                                                                                                                editItemEvent,
                                                                                                                false,
                                                                                                                individualActionToolbarButtonsConfigurationProvider,
                                                                                                                ruleViewerSettings );
                                                                  ruleViewer.setDocoVisible( showDescription );
                                                                  ruleViewer.setMetaVisible( showMetadata );

                                                                  content.add( ruleViewer );
                                                                  ruleViewer.setWidth( "100%" );
                                                                  ruleViewer.setHeight( "100%" );

                                                                  ruleViewer.setCloseCommand( new Command() {

                                                                      public void execute() {
                                                                          ruleViews.remove( row.uuid );
                                                                          rows.remove( row );
                                                                          doViews();
                                                                      }
                                                                  } );

                                                                  ruleViews.put( row.uuid,
                                                                                 ruleViewer );

                                                              }
                                                          } );
    }

    public void checkin(final boolean closeAfter) {
        final CheckinPopup pop = new CheckinPopup( constants.CheckInChanges() );
        pop.setCommand( new Command() {

            public void execute() {
                String comment = pop.getCheckinComment();
                for ( RuleViewer ruleViewer : ruleViews.values() ) {
                    ruleViewer.doCheckin( comment );
                }
                if ( closeAfter ) {
                    close();
                }
            }
        } );
        pop.show();

    }

    public void close() {
        closeCommand.execute();
    }

    @Override
    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void makeDirty() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resetDirty() {
        // TODO Auto-generated method stub
    }

    public void setCloseCommand(Command command) {
        closeCommand = command;
    }
}
