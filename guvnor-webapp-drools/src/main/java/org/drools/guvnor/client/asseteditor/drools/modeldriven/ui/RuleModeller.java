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

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.drools.guvnor.client.common.DirtyableVerticalPane;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.moduleeditor.drools.WorkingSetManager;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.AnalysisReportLine;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.VerificationService;
import org.drools.guvnor.client.rpc.VerificationServiceAsync;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleMetadata;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.rpc.*;

/**
 * This is the parent widget that contains the model based rule builder.
 */
public class RuleModeller extends DirtyableComposite
    implements
    RuleModelEditor {

    private static final Constants    constants               = GWT.create( Constants.class );
    private static final Images       images                  = GWT.create( Images.class );

    private DirtyableFlexTable        layout;
    private RuleModel                 model;
    private RuleModellerConfiguration configuration           = RuleModellerConfiguration.getInstance();
    private boolean                   showingOptions          = false;
    private int                       currentLayoutRow        = 0;
    private String                    packageName;
    private RuleAsset                 asset;
    private ModellerWidgetFactory     widgetFactory;

    private List<RuleModellerWidget>  lhsWidgets              = new ArrayList<RuleModellerWidget>();
    private List<RuleModellerWidget>  rhsWidgets              = new ArrayList<RuleModellerWidget>();

    private boolean                   hasModifiedWidgets;

    private final Command             onWidgetModifiedCommand = new Command() {

                                                                  public void execute() {
                                                                      hasModifiedWidgets = true;
                                                                      verifyRule(null);
                                                                  }
                                                              };

    public RuleModeller(RuleAsset asset,
                        RuleViewer viewer,
                        ClientFactory clientFactory,
                        EventBus eventBus) {
        this( asset,
              null,
              new RuleModellerWidgetFactory() );
    }

    public RuleModeller(RuleAsset asset,
                        RuleViewer viewer,
                        ModellerWidgetFactory widgetFactory) {
        this.asset = asset;
        this.model = (RuleModel) asset.getContent();
        this.packageName = asset.getMetaData().getPackageName();

        this.widgetFactory = widgetFactory;

        layout = new DirtyableFlexTable();

        initWidget();

        layout.setStyleName( "model-builder-Background" );
        initWidget( layout );
        setWidth( "100%" );
        setHeight( "100%" );
    }

    /**
     * This updates the widget to reflect the state of the model.
     */
    public void initWidget() {
        layout.clear();
        this.currentLayoutRow = 0;

        Image addPattern = new ImageButton( images.newItem() );
        addPattern.setTitle( constants.AddAConditionToThisRule() );
        addPattern.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                showConditionSelector( null );
            }
        } );

        layout.getColumnFormatter().setWidth( 0,
                                              "8%" );
        layout.getColumnFormatter().setWidth( 1,
                                              "87%" );
        layout.getColumnFormatter().setWidth( 2,
                                              "5%" );

        if ( this.showLHS() ) {
            layout.setWidget( currentLayoutRow,
                              0,
                              new SmallLabel( "<b>" + constants.WHEN() + "</b>" ) );

            if ( !lockLHS() ) {
                layout.setWidget( currentLayoutRow,
                                  2,
                                  addPattern );
            }
            currentLayoutRow++;

            renderLhs( this.model );
        }

        if ( this.showRHS() ) {
            layout.setWidget( currentLayoutRow,
                              0,
                              new SmallLabel( "<b>" + constants.THEN() + "</b>" ) );

            Image addAction = new ImageButton( images.newItem() );
            addAction.setTitle( constants.AddAnActionToThisRule() );
            addAction.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    showActionSelector( (Widget) event.getSource(),
                                        null );
                }
            } );
            if ( !lockRHS() ) {
                layout.setWidget( currentLayoutRow,
                                  2,
                                  addAction );
            }
            currentLayoutRow++;

            renderRhs( this.model );
        }

        if ( showAttributes() ) {

            final int tmp1 = currentLayoutRow;
            final int tmp2 = currentLayoutRow + 1;

            final RuleModeller self = this;
            if ( !this.showingOptions ) {
                ClickableLabel showMoreOptions = new ClickableLabel( "(show options...)",
                                                                     new ClickHandler() {

                                                                         public void onClick(ClickEvent event) {
                                                                             showingOptions = true;
                                                                             layout.setWidget( tmp1,
                                                                                               0,
                                                                                               new SmallLabel( constants.optionsRuleModeller() ) );
                                                                             layout.setWidget( tmp1,
                                                                                               2,
                                                                                               getAddAttribute() );
                                                                             layout.setWidget( tmp2,
                                                                                               1,
                                                                                               new RuleAttributeWidget( self,
                                                                                                                        self.model ) );
                                                                         }
                                                                     } );
                layout.setWidget( tmp1,
                                  0,
                                  showMoreOptions );
            } else {
                layout.setWidget( tmp1,
                                  0,
                                  new SmallLabel( constants.optionsRuleModeller() ) );
                layout.setWidget( tmp1,
                                  2,
                                  getAddAttribute() );
                layout.setWidget( tmp2,
                                  1,
                                  new RuleAttributeWidget( self,
                                                           self.model ) );

            }

        }

        currentLayoutRow++;
        layout.setWidget( currentLayoutRow + 1,
                          1,
                          spacerWidget() );
        layout.getCellFormatter().setHeight( currentLayoutRow + 1,
                                             1,
                                             "100%" );

        this.verifyRule( null );
    }

    private boolean isLock(String attr) {

        if ( this.asset.isReadonly() ) {
            return true;
        }

        if ( this.model.metadataList.length == 0 ) {
            return false;
        }

        for ( RuleMetadata at : this.model.metadataList ) {
            if ( at.attributeName.equals( attr ) ) {
                return true;
            }
        }
        return false;
    }

    public boolean showRHS() {
        return !this.configuration.isHideRHS();
    }

    /** return true if we should not allow unfrozen editing of the RHS */
    public boolean lockRHS() {
        return isLock( RuleAttributeWidget.LOCK_RHS );
    }

    public boolean showLHS() {
        return !this.configuration.isHideLHS();
    }

    /** return true if we should not allow unfrozen editing of the LHS */
    public boolean lockLHS() {
        return isLock( RuleAttributeWidget.LOCK_LHS );
    }

    private boolean showAttributes() {
        if ( !UserCapabilities.INSTANCE.hasCapability( Capability.SHOW_KNOWLEDGE_BASES_VIEW ) ) {
            return false;
        }

        return !this.configuration.isHideAttributes();
    }

    public void refreshWidget() {
        initWidget();
        showWarningsAndErrors();
        makeDirty();
    }

    private Widget getAddAttribute() {
        Image add = new ImageButton( images.newItem() );
        add.setTitle( constants.AddAnOptionToTheRuleToModifyItsBehaviorWhenEvaluatedOrExecuted() );

        add.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                showAttributeSelector();
            }
        } );
        return add;
    }

    protected void showAttributeSelector() {
        AttributeSelectorPopup pop = new AttributeSelectorPopup( model,
                                                                 lockLHS(),
                                                                 lockRHS(),
                                                                 new Command() {

                                                                     public void execute() {
                                                                         refreshWidget();
                                                                     }
                                                                 } );

        pop.show();
    }

    /**
     * Do all the widgets for the RHS.
     */
    private void renderRhs(final RuleModel model) {

        for ( int i = 0; i < model.rhs.length; i++ ) {
            DirtyableVerticalPane widget = new DirtyableVerticalPane();
            widget.setWidth( "100%" );
            IAction action = model.rhs[i];

            //if lockRHS() set the widget RO, otherwise let them decide.
            Boolean readOnly = this.lockRHS() ? true : null;

            RuleModellerWidget w = getWidgetFactory().getWidget( this,
                                                                 action,
                                                                 readOnly );
            w.addOnModifiedCommand( this.onWidgetModifiedCommand );

            w.setWidth( "100%" );
            widget.add( spacerWidget() );

            DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();
            horiz.setWidth( "100%" );
            //horiz.setBorderWidth(2);

            Image remove = new ImageButton( images.deleteItemSmall() );
            remove.setTitle( constants.RemoveThisAction() );
            final int idx = i;
            remove.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    if ( Window.confirm( constants.RemoveThisItem() ) ) {
                        model.removeRhsItem( idx );
                        refreshWidget();
                    }
                }
            } );
            horiz.add( w );
            if ( !(w instanceof ActionRetractFactWidget) ) {
                w.setWidth( "100%" ); //NON-NLS
                horiz.setWidth( "100%" );
            }

            if ( !(this.lockRHS() || w.isReadOnly()) ) {
                horiz.add( remove );
            }

            widget.add( horiz );

            layout.setHTML( currentLayoutRow,
                            0,
                            "<div class='form-field'>" + (i + 1) + ".</div>" );
            layout.getFlexCellFormatter().setHorizontalAlignment( currentLayoutRow,
                                                                  0,
                                                                  HasHorizontalAlignment.ALIGN_CENTER );
            layout.getFlexCellFormatter().setVerticalAlignment( currentLayoutRow,
                                                                0,
                                                                HasVerticalAlignment.ALIGN_MIDDLE );

            layout.setWidget( currentLayoutRow,
                              1,
                              widget );
            layout.getFlexCellFormatter().setHorizontalAlignment( currentLayoutRow,
                                                                  1,
                                                                  HasHorizontalAlignment.ALIGN_LEFT );
            layout.getFlexCellFormatter().setVerticalAlignment( currentLayoutRow,
                                                                1,
                                                                HasVerticalAlignment.ALIGN_TOP );
            layout.getFlexCellFormatter().setWidth( currentLayoutRow,
                                                    1,
                                                    "100%" );

            final int index = i;
            if ( !(this.lockRHS() || w.isReadOnly()) ) {
                this.addActionsButtonsToLayout( constants.AddAnActionBelow(),
                                                new ClickHandler() {

                                                    public void onClick(ClickEvent event) {
                                                        showActionSelector( (Widget) event.getSource(),
                                                                            index + 1 );
                                                    }
                                                },
                                                new ClickHandler() {

                                                    public void onClick(ClickEvent event) {
                                                        model.moveRhsItemDown( index );
                                                        refreshWidget();
                                                    }
                                                },
                                                new ClickHandler() {

                                                    public void onClick(ClickEvent event) {
                                                        model.moveRhsItemUp( index );
                                                        refreshWidget();
                                                    }
                                                } );
            }

            this.rhsWidgets.add( w );
            currentLayoutRow++;

        }

    }

    /**
     * Pops up the fact selector.
     */
    protected void showConditionSelector(Integer position) {
        RuleModellerConditionSelectorPopup popup = new RuleModellerConditionSelectorPopup( model,
                                                                                           this,
                                                                                           packageName,
                                                                                           position );
        popup.show();
    }

    protected void showActionSelector(Widget w,
                                      Integer position) {
        RuleModellerActionSelectorPopup popup = new RuleModellerActionSelectorPopup( model, this, packageName, position );
        popup.show();
    }

    /**
     * Builds all the condition widgets.
     */
    private void renderLhs(final RuleModel model) {

        for ( int i = 0; i < model.lhs.length; i++ ) {
            DirtyableVerticalPane vert = new DirtyableVerticalPane();
            vert.setWidth( "100%" );

            //if lockLHS() set the widget RO, otherwise let them decide.
            Boolean readOnly = this.lockLHS() ? true : null;

            IPattern pattern = model.lhs[i];

            RuleModellerWidget w = getWidgetFactory().getWidget( this,
                                                                 pattern,
                                                                 readOnly );
            w.addOnModifiedCommand( this.onWidgetModifiedCommand );

            vert.add( wrapLHSWidget( model,
                                     i,
                                     w ) );
            vert.add( spacerWidget() );

            layout.setWidget( currentLayoutRow,
                              0,
                              this.wrapLineNumber( i + 1,
                                                   true ) );
            layout.getFlexCellFormatter().setHorizontalAlignment( currentLayoutRow,
                                                                  0,
                                                                  HasHorizontalAlignment.ALIGN_CENTER );
            layout.getFlexCellFormatter().setVerticalAlignment( currentLayoutRow,
                                                                0,
                                                                HasVerticalAlignment.ALIGN_MIDDLE );

            layout.setWidget( currentLayoutRow,
                              1,
                              vert );
            layout.getFlexCellFormatter().setHorizontalAlignment( currentLayoutRow,
                                                                  1,
                                                                  HasHorizontalAlignment.ALIGN_LEFT );
            layout.getFlexCellFormatter().setVerticalAlignment( currentLayoutRow,
                                                                1,
                                                                HasVerticalAlignment.ALIGN_TOP );
            layout.getFlexCellFormatter().setWidth( currentLayoutRow,
                                                    1,
                                                    "100%" );

            final int index = i;
            if ( !(this.lockLHS() || w.isReadOnly()) ) {
                this.addActionsButtonsToLayout( constants.AddAConditionBelow(),
                                                new ClickHandler() {

                                                    public void onClick(ClickEvent event) {
                                                        showConditionSelector( index + 1 );
                                                    }
                                                },
                                                new ClickHandler() {

                                                    public void onClick(ClickEvent event) {
                                                        model.moveLhsItemDown( index );
                                                        refreshWidget();
                                                    }
                                                },
                                                new ClickHandler() {

                                                    public void onClick(ClickEvent event) {
                                                        model.moveLhsItemUp( index );
                                                        refreshWidget();
                                                    }
                                                } );
            }

            this.lhsWidgets.add( w );
            currentLayoutRow++;
        }

    }

    private HTML spacerWidget() {
        HTML h = new HTML( "&nbsp;" ); //NON-NLS
        h.setHeight( "2px" ); //NON-NLS
        return h;
    }

    /**
     * This adds the widget to the UI, also adding the remove icon.
     */
    private Widget wrapLHSWidget(final RuleModel model,
                                 int i,
                                 RuleModellerWidget w) {
        DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();

        final Image remove = new ImageButton( images.deleteItemSmall() );
        remove.setTitle( constants.RemoveThisENTIREConditionAndAllTheFieldConstraintsThatBelongToIt() );
        final int idx = i;
        remove.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                if ( Window.confirm( constants.RemoveThisEntireConditionQ() ) ) {
                    if ( model.removeLhsItem( idx ) ) {
                        refreshWidget();
                    } else {
                        ErrorPopup.showMessage( constants.CanTRemoveThatItemAsItIsUsedInTheActionPartOfTheRule() );
                    }
                }
            }
        } );

        horiz.setWidth( "100%" );
        w.setWidth( "100%" );

        horiz.add( w );
        if ( !(this.lockLHS() || w.isReadOnly()) ) {
            horiz.add( remove );
        }

        return horiz;
    }

    private Widget wrapLineNumber(int number,
                                  boolean isLHSLine) {
        String id = "rhsLine";
        if ( isLHSLine ) {
            id = "lhsLine";
        }
        id += number;

        DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();
        horiz.add( new HTML( "<div class='form-field' id='" + id + "'>" + number + ".</div>" ) );

        return horiz;
    }

    private void addLineIcon(int row,
                             ImageResource img,
                             String title) {
        Widget widget = layout.getWidget( row,
                                          0 );
        if ( widget instanceof DirtyableHorizontalPane ) {
            DirtyableHorizontalPane horiz = (DirtyableHorizontalPane) widget;
            final Image icon = new ImageButton( img );
            icon.setTitle( title );
            horiz.add( icon );
        }
    }

    private void clearLineIcons(int row) {
        if ( layout.getCellCount( row ) <= 0 ) {
            return;
        }
        Widget widget = layout.getWidget( row,
                                          0 );
        if ( widget instanceof DirtyableHorizontalPane ) {
            DirtyableHorizontalPane horiz = (DirtyableHorizontalPane) widget;
            while ( horiz.getWidgetCount() > 1 ) {
                horiz.remove( horiz.getWidgetCount() - 1 );
            }
        }
    }

    private void clearLinesIcons() {
        for ( int i = 0; i < layout.getRowCount(); i++ ) {
            this.clearLineIcons( i );
        }
    }

    private void addActionsButtonsToLayout(String title,
                                           ClickHandler addBelowListener,
                                           ClickHandler moveDownListener,
                                           ClickHandler moveUpListener) {

        final DirtyableHorizontalPane hp = new DirtyableHorizontalPane();

        Image addPattern = new ImageButton( images.newItemBelow() );
        addPattern.setTitle( title );
        addPattern.addClickHandler( addBelowListener );

        Image moveDown = new ImageButton( images.shuffleDown() );
        moveDown.setTitle( constants.MoveDown() );
        moveDown.addClickHandler( moveDownListener );

        Image moveUp = new ImageButton( images.shuffleUp() );
        moveUp.setTitle( constants.MoveUp() );
        moveUp.addClickHandler( moveUpListener );

        hp.add( addPattern );
        hp.add( moveDown );
        hp.add( moveUp );

        layout.setWidget( currentLayoutRow,
                          2,
                          hp );
        layout.getFlexCellFormatter().setHorizontalAlignment( currentLayoutRow,
                                                              2,
                                                              HasHorizontalAlignment.ALIGN_CENTER );
        layout.getFlexCellFormatter().setVerticalAlignment( currentLayoutRow,
                                                            2,
                                                            HasVerticalAlignment.ALIGN_MIDDLE );
    }

    public RuleModel getModel() {
        return model;
    }

    /**
     * Returns true is a var name has already been used either by the rule, or
     * as a global.
     */
    public boolean isVariableNameUsed(String name) {
        SuggestionCompletionEngine completions = SuggestionCompletionCache.getInstance().getEngineFromCache( packageName );
        return model.isVariableNameUsed( name ) || completions.isGlobalVariable( name );
    }

    @Override
    public boolean isDirty() {
        return (layout.hasDirty() || dirtyflag);
    }

    public SuggestionCompletionEngine getSuggestionCompletions() {
        return SuggestionCompletionCache.getInstance().getEngineFromCache( packageName );
    }

    private List<AnalysisReportLine> errors;
    private List<AnalysisReportLine> warnings;

    public void verifyRule(final Command cmd) {
        this.verifyRule( cmd,
                         false );
    }

    public void verifyRule(final Command cmd,
                           boolean forceVerification) {
        errors = new ArrayList<AnalysisReportLine>();
        warnings = new ArrayList<AnalysisReportLine>();

        //if AutoVerifierEnabled is off or there are not modified widgets,
        //just execute cmd and return.
        if ( !forceVerification && (!WorkingSetManager.getInstance().isAutoVerifierEnabled() || !this.hasModifiedWidgets) ) {
            if ( cmd != null ) {
                cmd.execute();
            }
            return;
        }

        LoadingPopup.showMessage( constants.VerifyingItemPleaseWait() );
        Set<WorkingSetConfigData> activeWorkingSets = WorkingSetManager.getInstance().getActiveWorkingSets(asset.getMetaData().getPackageName());

        VerificationServiceAsync verificationService = GWT.create( VerificationService.class );

        verificationService.verifyAssetWithoutVerifiersRules( this.asset,
                                                              activeWorkingSets,
                                                              new AsyncCallback<AnalysisReport>() {

                                                                  public void onSuccess(AnalysisReport report) {
                                                                      LoadingPopup.close();

                                                                      errors = Arrays.asList( report.errors );
                                                                      warnings = Arrays.asList( report.warnings );

                                                                      processWarningsAndErrors();

                                                                      hasModifiedWidgets = false;
                                                                      if ( cmd != null ) {
                                                                          cmd.execute();
                                                                      }
                                                                  }

                                                                  public void onFailure(Throwable arg0) {
                                                                      LoadingPopup.close();
                                                                  }
                                                              } );

    }

    private void processWarningsAndErrors() {

        if ( this.warnings.isEmpty() && this.errors.isEmpty() ) {
            for ( RuleModellerWidget ruleModellerWidget : this.lhsWidgets ) {
                ruleModellerWidget.setModified( false );
            }
            for ( RuleModellerWidget ruleModellerWidget : this.rhsWidgets ) {
                ruleModellerWidget.setModified( false );
            }
        }
        showWarningsAndErrors();
    }

    private void showWarningsAndErrors() {
        this.clearLinesIcons();
        if ( this.warnings != null ) {
            for ( AnalysisReportLine warning : this.warnings ) {
                if ( warning.patternOrderNumber != null ) {
                    this.addLineIcon( warning.patternOrderNumber + 1,
                                      images.warning(),
                                      warning.description );
                }
            }
        }
        if ( this.errors != null ) {
            for ( AnalysisReportLine error : this.errors ) {
                if ( error.patternOrderNumber != null ) {
                    this.addLineIcon( error.patternOrderNumber + 1,
                                      images.error(),
                                      error.description );
                }
            }
        }
    }

    public boolean hasVerifierErrors() {
        return this.errors != null && this.errors.size() > 0;
    }

    public boolean hasVerifierWarnings() {
        return this.warnings != null && this.warnings.size() > 0;
    }

    public ModellerWidgetFactory getWidgetFactory() {
        return widgetFactory;
    }

    public void setWidgetFactory(ModellerWidgetFactory widgetFactory) {
        this.widgetFactory = widgetFactory;
    }

    public RuleModeller getRuleModeller() {
        return this;
    }

    public boolean isTemplate() {
        return widgetFactory.isTemplate();
    }

    public RuleAsset getAsset() {
        return asset;
    }

}
