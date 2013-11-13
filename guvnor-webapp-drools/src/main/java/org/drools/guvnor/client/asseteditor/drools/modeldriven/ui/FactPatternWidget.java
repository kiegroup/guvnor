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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.HumanReadable;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.factPattern.Connectives;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.factPattern.PopupCreator;
import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DroolsGuvnorImageResources;
import org.drools.guvnor.client.resources.DroolsGuvnorImages;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.HasCEPWindow;
import org.drools.ide.common.client.modeldriven.brl.HasConstraints;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraintEBLeftSide;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the new smart widget that works off the model.
 */
public class FactPatternWidget extends RuleModellerWidget {

    private FactPattern                                             pattern;
    private DirtyableFlexTable                                      layout                 = new DirtyableFlexTable();
    private Connectives                                             connectives;
    private PopupCreator                                            popupCreator;
    private boolean                                                 bindable;
    private boolean                                                 isAll0WithLabel;
    private boolean                                                 readOnly;

    private boolean                                                 isFactTypeKnown;

    private final Map<SingleFieldConstraint, ConstraintValueEditor> constraintValueEditors = new HashMap<SingleFieldConstraint, ConstraintValueEditor>();

    public FactPatternWidget(RuleModeller mod,
                             EventBus eventBus,
                             IPattern p,
                             boolean canBind) {
        this( mod,
              eventBus,
              p,
              false,
              canBind,
              null );
    }

    /**
     * Creates a new FactPatternWidget
     * 
     * @param canBind
     * @param readOnly
     *            if the widget should be in RO mode. If this parameter is null,
     *            the readOnly attribute is calculated.
     */
    public FactPatternWidget(RuleModeller ruleModeller,
                             EventBus eventBus,
                             IPattern pattern,
                             boolean canBind,
                             Boolean readOnly) {
        this( ruleModeller,
              eventBus,
              pattern,
              false,
              canBind,
              readOnly );
    }

    public FactPatternWidget(RuleModeller mod,
                             EventBus eventBus,
                             IPattern p,
                             boolean isAll0WithLabel,
                             boolean canBind,
                             Boolean readOnly) {
        super( mod,
               eventBus );
        this.pattern = (FactPattern) p;
        this.bindable = canBind;

        this.popupCreator = new PopupCreator();
        this.popupCreator.setBindable( bindable );
        this.popupCreator.setCompletions( mod.getSuggestionCompletions() );
        this.popupCreator.setModeller( mod );
        this.popupCreator.setPattern( pattern );

        this.isAll0WithLabel = isAll0WithLabel;

        //if readOnly == null, the RO attribute is calculated.
        this.isFactTypeKnown = mod.getSuggestionCompletions().containsFactType( this.pattern.getFactType() );
        if ( readOnly == null ) {
            this.readOnly = !this.isFactTypeKnown;
        } else {
            this.readOnly = readOnly;
        }

        this.connectives = new Connectives( mod,
                                            eventBus,
                                            pattern,
                                            this.readOnly );

        layout.setWidget( 0,
                          0,
                          getPatternLabel( this.pattern ) );
        FlexCellFormatter formatter = layout.getFlexCellFormatter();
        formatter.setAlignment( 0,
                                0,
                                HasHorizontalAlignment.ALIGN_LEFT,
                                HasVerticalAlignment.ALIGN_BOTTOM );
        formatter.setStyleName( 0,
                                0,
                                "modeller-fact-TypeHeader" );

        List<FieldConstraint> sortedConst = sortConstraints( pattern.getFieldConstraints() );
        pattern.setFieldConstraints( sortedConst );
        
        drawConstraints( sortedConst,
                         pattern );

        //CEP 'window' widget
        int row = layout.getRowCount() + 1;
        layout.setWidget( row,
                          0,
                          createCEPWindowWidget( mod,
                                                 pattern ) );

        if ( this.readOnly ) {
            layout.addStyleName( "editor-disabled-widget" );
        }

        if ( bindable ) {
            layout.addStyleName( "modeller-fact-pattern-Widget" );
        }

        initWidget( layout );

    }

    /**
     * Render a hierarchy of constraints, hierarchy here means constraints that
     * may themselves depend on members of constraint objects. With this code,
     * the GUI enables clicking rules of the form: $result = RoutingResult(
     * NerOption.types contains "arzt" )
     * 
     * @param sortedConst
     *            a sorted list of constraints to display.
     */
    private void drawConstraints(List<FieldConstraint> sortedConst,
                                 HasConstraints hasConstraints) {
        final DirtyableFlexTable table = new DirtyableFlexTable();
        layout.setWidget( 1,
                          0,
                          table );
        List<FieldConstraint> parents = new ArrayList<FieldConstraint>();

        for ( int i = 0; i < sortedConst.size(); i++ ) {
            traverseSingleFieldConstraints( sortedConst,
                                            table,
                                            parents,
                                            hasConstraints,
                                            i );

            //now the clear icon
            final int currentRow = i;
            Image clear = DroolsGuvnorImages.INSTANCE.DeleteItemSmall();
            clear.setTitle( Constants.INSTANCE.RemoveThisWholeRestriction() );
            clear.addClickHandler( createClickHandlerForClearImageButton( currentRow ) );

            if ( !this.readOnly ) {
                //This used to be 5 and Connectives were not rendered
                table.setWidget( currentRow,
                                 6,
                                 clear );
            }

        }
    }

    private ClickHandler createClickHandlerForClearImageButton(final int currentRow) {
        return new ClickHandler() {

            public void onClick(ClickEvent event) {
                if ( Window.confirm( Constants.INSTANCE.RemoveThisItem() ) ) {
                    setModified( true );
                    pattern.removeConstraint( currentRow );
                    getModeller().refreshWidget();
                }
            }
        };
    }

    private void traverseSingleFieldConstraints(List<FieldConstraint> sortedConst,
                                                final DirtyableFlexTable table,
                                                List<FieldConstraint> parents,
                                                HasConstraints hasConstraints,
                                                int i) {
        int tabs = -1;
        FieldConstraint current = sortedConst.get( i );
        if ( current instanceof SingleFieldConstraint ) {
            SingleFieldConstraint single = (SingleFieldConstraint) current;
            FieldConstraint parent = single.getParent();

            for ( int j = 0; j < parents.size(); j++ ) {
                FieldConstraint storedParent = parents.get( j );
                if ( storedParent != null && storedParent.equals( parent ) ) {
                    tabs = j + 1;
                    traverseForRemoval( parents,
                                        j );
                    parents.add( current );
                    break;
                }
            }

            if ( tabs < 0 ) {
                tabs = 0;
                parents.add( current );
            }
        }
        renderFieldConstraint( table,
                               i,
                               current,
                               hasConstraints,
                               true,
                               tabs );
    }

    private void traverseForRemoval(List<FieldConstraint> parents,
                                    int j) {
        for ( int k = j + 1; k < parents.size(); k++ ) {
            parents.remove( j + 1 );
        }
    }

    /**
     * Sort the rule constraints such that parent rules are inserted directly
     * before their child rules.
     * 
     * @param constraints
     *            the list of inheriting constraints to sort.
     * @return a sorted list of constraints ready for display.
     */
    private List<FieldConstraint> sortConstraints(FieldConstraint[] constraints) {
        List<FieldConstraint> sortedConst = new ArrayList<FieldConstraint>( constraints.length );
        for ( int i = 0; i < constraints.length; i++ ) {
            FieldConstraint current = constraints[i];
            if ( current instanceof SingleFieldConstraint ) {
                SingleFieldConstraint single = (SingleFieldConstraint) current;
                int index = sortedConst.indexOf( single.getParent() );
                if ( single.getParent() == null ) {
                    sortedConst.add( single );
                } else if ( index >= 0 ) {
                    sortedConst.add( index + 1,
                                     single );
                } else {
                    insertSingleFieldConstraint( single,
                                                 sortedConst );
                }
            } else {
                sortedConst.add( current );
            }
        }
        return sortedConst;
    }

    /**
     * Recursively add constraints and their parents.
     * 
     * @param sortedConst
     *            the array to fill.
     * @param fieldConst
     *            the constraint to investigate.
     */
    private void insertSingleFieldConstraint(SingleFieldConstraint fieldConst,
                                             List<FieldConstraint> sortedConst) {
        if ( fieldConst.getParent() instanceof SingleFieldConstraint ) {
            insertSingleFieldConstraint( (SingleFieldConstraint) fieldConst.getParent(),
                                         sortedConst );
        }
        sortedConst.add( fieldConst );
    }

    /**
     * This will render a field constraint into the given table. The row is the
     * row number to stick it into.
     */
    private void renderFieldConstraint(final DirtyableFlexTable inner,
                                       int row,
                                       FieldConstraint constraint,
                                       HasConstraints hasConstraints,
                                       boolean showBinding,
                                       int tabs) {
        //if nesting, or predicate, then it will need to span 5 cols.
        if ( constraint instanceof SingleFieldConstraint ) {
            renderSingleFieldConstraint( this.getModeller(),
                                         inner,
                                         row,
                                         (SingleFieldConstraint) constraint,
                                         hasConstraints,
                                         showBinding,
                                         tabs );
        } else if ( constraint instanceof CompositeFieldConstraint ) {
            inner.setWidget( row,
                             1,
                             compositeFieldConstraintEditor( (CompositeFieldConstraint) constraint ) );
            inner.getFlexCellFormatter().setColSpan( row,
                                                     1,
                                                     5 );
            inner.setWidget( row,
                             0,
                             new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) ); //NON-NLS
        }
    }

    /**
     * This will show the constraint editor - allowing field constraints to be
     * nested etc.
     */
    private Widget compositeFieldConstraintEditor(final CompositeFieldConstraint constraint) {
        FlexTable t = new FlexTable();
        String desc = null;

        ClickHandler click = new ClickHandler() {

            public void onClick(ClickEvent event) {
                popupCreator.showPatternPopupForComposite( (Widget) event.getSource(),
                                                           constraint );
            }
        };

        if ( constraint.compositeJunctionType.equals( CompositeFieldConstraint.COMPOSITE_TYPE_AND ) ) {
            desc = Constants.INSTANCE.AllOf() + ":";
        } else {
            desc = Constants.INSTANCE.AnyOf() + ":";
        }

        t.setWidget( 0,
                     0,
                     new ClickableLabel( desc,
                                         click,
                                         !this.readOnly ) );
        t.getFlexCellFormatter().setColSpan( 0,
                                             0,
                                             2 );

        FieldConstraint[] nested = constraint.constraints;
        DirtyableFlexTable inner = new DirtyableFlexTable();
        if ( nested != null ) {
            for ( int i = 0; i < nested.length; i++ ) {
                this.renderFieldConstraint( inner,
                                            i,
                                            nested[i],
                                            constraint,
                                            true,
                                            0 );
                //add in remove icon here...
                final int currentRow = i;
                Image clear = DroolsGuvnorImages.INSTANCE.DeleteItemSmall();
                clear.setTitle( Constants.INSTANCE.RemoveThisNestedRestriction() );
                clear.addClickHandler( new ClickHandler() {

                    public void onClick(ClickEvent event) {
                        if ( Window.confirm( Constants.INSTANCE.RemoveThisItemFromNestedConstraint() ) ) {
                            setModified( true );
                            constraint.removeConstraint( currentRow );
                            getModeller().refreshWidget();
                        }
                    }
                } );
                if ( !this.readOnly ) {
                    //This used to be 5 and Connectives were not rendered
                    inner.setWidget( i,
                                     6,
                                     clear );
                }
            }
        }

        t.setWidget( 1,
                     1,
                     inner );
        t.setWidget( 1,
                     0,
                     new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) );
        return t;
    }

    /**
     * Applies a single field constraint to the given table, and start row.
     */
    private void renderSingleFieldConstraint(final RuleModeller modeller,
                                             final DirtyableFlexTable inner,
                                             final int row,
                                             final SingleFieldConstraint constraint,
                                             final HasConstraints hasConstraints,
                                             boolean showBinding,
                                             final int tabs) {

        final int col = 1; //for offsetting, just a slight indent

        inner.setWidget( row,
                         0,
                         new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) );
        if ( constraint.getConstraintValueType() != SingleFieldConstraint.TYPE_PREDICATE ) {

            HorizontalPanel ebContainer = null;
            if ( constraint instanceof SingleFieldConstraintEBLeftSide ) {
                ebContainer = expressionBuilderLS( (SingleFieldConstraintEBLeftSide) constraint,
                                                   showBinding,
                                                   tabs * 20 );
                inner.setWidget( row,
                                 0 + col,
                                 ebContainer );
            } else {
                inner.setWidget( row,
                                 0 + col,
                                 fieldLabel( constraint,
                                             hasConstraints,
                                             showBinding,
                                             tabs * 20 ) );
            }
            inner.setWidget( row,
                             1 + col,
                             operatorDropDown( constraint,
                                               inner,
                                               row,
                                               2 + col ) );
            //Get first part of constraint.fieldName? #1=Fact1, #2=SubFact1
            inner.setWidget( row,
                             2 + col,
                             valueEditor( constraint ) );
            inner.setWidget( row,
                             3 + col,
                             connectives.connectives( constraint,
                                                      constraint.getFieldType() ) );

            if ( ebContainer != null && ebContainer.getWidgetCount() > 0 ) {
                if ( ebContainer.getWidget( 0 ) instanceof ExpressionBuilder ) {
                    associateExpressionWithChangeHandler( inner,
                                                          row,
                                                          constraint,
                                                          col,
                                                          ebContainer );
                }
            }

            if ( !this.readOnly ) {
                inner.setWidget( row,
                                 4 + col,
                                 createAddConnectiveImageButton( modeller,
                                                                 constraint ) );
            }

        } else if ( constraint.getConstraintValueType() == SingleFieldConstraint.TYPE_PREDICATE ) {
            inner.setWidget( row,
                             1,
                             predicateEditor( constraint ) );
            inner.getFlexCellFormatter().setColSpan( row,
                                                     1,
                                                     5 );
        }
    }

    private Image createAddConnectiveImageButton(final RuleModeller modeller,
                                                 final SingleFieldConstraint constraint) {
        Image addConnective = DroolsGuvnorImages.INSTANCE.AddConnective();
        addConnective.setTitle( Constants.INSTANCE.AddMoreOptionsToThisFieldsValues() );
        addConnective.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                setModified( true );
                constraint.addNewConnective();
                modeller.refreshWidget();
            }
        } );
        return addConnective;
    }

    //Widget for CEP 'windows'
    private Widget createCEPWindowWidget(final RuleModeller modeller,
                                         final HasCEPWindow c) {
        if ( modeller.getSuggestionCompletions().isFactTypeAnEvent( pattern.getFactType() ) ) {
            HorizontalPanel hp = new HorizontalPanel();
            Label lbl = new Label( Constants.INSTANCE.OverCEPWindow() );
            lbl.setStyleName( "paddedLabel" );
            hp.add( lbl );
            CEPWindowOperatorsDropdown cwo = new CEPWindowOperatorsDropdown( c,
                                                                             readOnly );

            if ( !this.isReadOnly() ) {
                cwo.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

                    public void onValueChange(ValueChangeEvent<OperatorSelection> event) {
                        setModified( true );
                        OperatorSelection selection = event.getValue();
                        String selected = selection.getValue();
                        c.getWindow().setOperator( selected );
                        getModeller().makeDirty();
                    }
                } );
            }

            hp.add( cwo );
            return hp;
        }
        return new HTML();
    }

    private void associateExpressionWithChangeHandler(final DirtyableFlexTable inner,
                                                      final int row,
                                                      final SingleFieldConstraint constraint,
                                                      final int col,
                                                      HorizontalPanel ebContainer) {
        ExpressionBuilder eb = (ExpressionBuilder) ebContainer.getWidget( 0 );
        eb.addExpressionTypeChangeHandler( new ExpressionTypeChangeHandler() {

            public void onExpressionTypeChanged(ExpressionTypeChangeEvent event) {
                try {
                    //Change "operator" drop-down as the content depends on data-type
                    constraint.setFieldType( event.getNewType() );
                    inner.setWidget( row,
                                     1 + col,
                                     operatorDropDown( constraint,
                                                       inner,
                                                       row,
                                                       2 + col ) );
                    //Change "value" editor to the pen icon as the applicable Widget depends on data-type
                    constraint.setConstraintValueType( SingleFieldConstraint.TYPE_UNDEFINED );
                    constraint.setValue( "" );
                    inner.setWidget( row,
                                     2 + col,
                                     valueEditor( constraint ) );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    /**
     * This provides an inline formula editor, not unlike a spreadsheet does.
     */
    private Widget predicateEditor(final SingleFieldConstraint c) {

        HorizontalPanel pred = new HorizontalPanel();
        pred.setWidth( "100%" );
        Image img = new Image( DroolsGuvnorImageResources.INSTANCE.functionAssets() );
        img.setTitle( Constants.INSTANCE.FormulaBooleanTip() );

        pred.add( img );
        if ( c.getValue() == null ) {
            c.setValue( "" );
        }

        final TextBox box = new TextBox();
        box.setText( c.getValue() );

        if ( !this.readOnly ) {
            box.addChangeHandler( new ChangeHandler() {

                public void onChange(ChangeEvent event) {
                    setModified( true );
                    c.setValue( box.getText() );
                    getModeller().makeDirty();
                }
            } );
            box.setWidth( "100%" );
            pred.add( box );
        } else {
            pred.add( new SmallLabel( c.getValue() ) );
        }

        return pred;
    }

    /**
     * This returns the pattern label.
     */
    private Widget getPatternLabel(final FactPattern fp) {
        ClickHandler click = new ClickHandler() {

            public void onClick(ClickEvent event) {
                popupCreator.showPatternPopup( (Widget) event.getSource(),
                                               fp,
                                               null,
                                               false );
            }
        };

        String patternName = (pattern.isBound()) ? pattern.getFactType() + " <b>[" + pattern.getBoundName() + "]</b>" : pattern.getFactType();

        String desc;
        if ( isAll0WithLabel ) {
            desc = Constants.INSTANCE.All0with( patternName );
        } else {
            if ( pattern.getNumberOfConstraints() > 0 ) {
                desc = Constants.INSTANCE.ThereIsAAn0With( patternName );
            } else {
                desc = Constants.INSTANCE.ThereIsAAn0( patternName );
            }
            desc = anA( desc,
                        patternName );
        }

        return new ClickableLabel( desc,
                                   click,
                                   !this.readOnly );
    }

    /**
     * Change to an/a depending on context - only for english TODO use GWT
     * support for that:
     * http://code.google.com/intl/nl/webtoolkit/doc/latest/DevGuideI18n.html
     */
    private String anA(String desc,
                       String patternName) {
        if ( desc.startsWith( "There is a/an" ) ) { //NON-NLS
            String vowel = patternName.substring( 0,
                                                  1 );
            if ( vowel.equalsIgnoreCase( "A" ) || vowel.equalsIgnoreCase( "E" ) || vowel.equalsIgnoreCase( "I" ) || vowel.equalsIgnoreCase( "O" ) || vowel.equalsIgnoreCase( "U" ) ) { //NON-NLS
                return desc.replace( "There is a/an",
                                     "There is an" ); //NON-NLS
            } else {
                return desc.replace( "There is a/an",
                                     "There is a" ); //NON-NLS
            }
        } else {
            return desc;
        }
    }

    private Widget valueEditor(final SingleFieldConstraint c) {

        //Create a new ConstraintValueEditor
        ConstraintValueEditor constraintValueEditor = new ConstraintValueEditor( c,
                                                                                 pattern.constraintList,
                                                                                 this.getModeller(),
                                                                                 this.getEventBus(),
                                                                                 this.readOnly );
        constraintValueEditor.setOnValueChangeCommand( new Command() {
            public void execute() {
                setModified( true );
                refreshConstraintValueEditorsDropDownData( c );
            }
        } );

        //Keep a reference to the value editors so they can be refreshed for dependent enums
        constraintValueEditors.put( c,
                                    constraintValueEditor );

        return constraintValueEditor;
    }

    private Widget operatorDropDown(final SingleFieldConstraint c,
                                    final DirtyableFlexTable inner,
                                    final int row,
                                    final int col) {
        if ( !this.readOnly ) {

            String fieldName;
            String factType;
            String fieldType;
            //Connectives Operators are handled in class Connectives
            if ( c instanceof SingleFieldConstraintEBLeftSide ) {
                SingleFieldConstraintEBLeftSide sfexp = (SingleFieldConstraintEBLeftSide) c;
                factType = sfexp.getExpressionLeftSide().getPreviousGenericType();
                //Window.alert("sfexp.getFactType():"+sfexp.getFactType());
                //Window.alert("sfexp.getFieldType():"+sfexp.getFieldType());
                if ( factType == null ) {
                	//Window.alert("sfexp.getExpressionLeftSide().getPreviousGenericType() is null");
                    factType = sfexp.getExpressionLeftSide().getGenericType();
                	//factType = sfexp.getFieldType();
                   // fieldType = sfexp.getFieldType();
                	
                }
                //factType = sfexp.getExpressionLeftSide().getGenericType();
                fieldName = sfexp.getExpressionLeftSide().getFieldName();
                
            } else {
            	//Window.alert("! c instanceof SingleFieldConstraintEBLeftSide");
                factType = c.getFactType();
                //factType = c.getFieldType();
                //Window.alert("c.getFieldType():"+c.getFieldType());
                //Window.alert("c.getFactType():"+c.getFactType());
                
                fieldName = c.getFieldName();
            }
            fieldType = c.getFieldType();
            
            //Window.alert("factType: "  + factType + " field Name" + fieldName + " fieldType " + c.getFieldType());
            String[] operators = connectives.getCompletions().getOperatorCompletions( factType,
                                                                                      fieldName, fieldType );
            CEPOperatorsDropdown w = new CEPOperatorsDropdown( operators,
                                                               c );

            w.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

                public void onValueChange(ValueChangeEvent<OperatorSelection> event) {
                    setModified( true );
                    final OperatorSelection selection = event.getValue();
                    final String selected = selection.getValue();
                    final String selectedText = selection.getDisplayText();

                    //Prevent recursion once operator change has been applied
                    if ( selectedText.equals( c.getOperator() ) ) {
                        return;
                    }

                    final boolean newOperatorRequiresExplicitList = SuggestionCompletionEngine.operatorRequiresList( selected );
                    final boolean oldOperatorRequiresExplicitList = SuggestionCompletionEngine.operatorRequiresList( c.getOperator() );
                    c.setOperator( selected );
                    if ( c.getOperator().equals( "" ) ) {
                        c.setOperator( null );
                    }
                    if ( selectedText.equals( Constants.INSTANCE.isEqualToNull() ) || selectedText.equals( Constants.INSTANCE.isNotEqualToNull() ) ) {
                        if ( inner != null ) {
                            inner.getWidget( row,
                                             col ).setVisible( false );
                        }
                    } else {
                        if ( inner != null ) {
                            inner.getWidget( row,
                                             col ).setVisible( true );
                        }
                    }

                    //If new operator requires a comma separated list and old did not, or vice-versa
                    //we need to redraw the ConstraintValueEditor for the constraint
                    if ( newOperatorRequiresExplicitList != oldOperatorRequiresExplicitList ) {
                        if ( newOperatorRequiresExplicitList == false ) {
                            final String[] oldValueList = c.getValue().split( "," );
                            if ( oldValueList.length > 0 ) {
                                c.setValue( oldValueList[0] );
                            }
                        }

                        //Redraw ConstraintValueEditor
                        inner.setWidget( row,
                                         col,
                                         valueEditor( c ) );
                    }

                    getModeller().makeDirty();
                }
            } );

            return w;
        } else {
            SmallLabel sl = new SmallLabel( "<b>" + (c.getOperator() == null ? Constants.INSTANCE.pleaseChoose() : HumanReadable.getOperatorDisplayName( c.getOperator() )) + "</b>" );
            return sl;
        }

    }

    private HorizontalPanel expressionBuilderLS(final SingleFieldConstraintEBLeftSide con,
                                                boolean showBinding,
                                                int padding) {
        HorizontalPanel ab = new HorizontalPanel();
        ab.setStyleName( "modeller-field-Label" );

        if ( !con.isBound() ) {
            if ( bindable && showBinding && !this.readOnly ) {
                ab.add( new ExpressionBuilder( getModeller(),
                                               getEventBus(),
                                               con.getExpressionLeftSide() ) );
            } else {
                ab.add( new SmallLabel( con.getExpressionLeftSide().getText() ) );
            }
        } else {
            ab.add( new ExpressionBuilder( getModeller(),
                                           getEventBus(),
                                           con.getExpressionLeftSide() ) );
        }
        return ab;
    }

    /**
     * get the field widget. This may be a simple label, or it may be bound (and
     * show the var name) or a icon to create a binding. It will only show the
     * binding option of showBinding is true.
     */
    private Widget fieldLabel(final SingleFieldConstraint con,
                              final HasConstraints hasConstraints,
                              boolean showBinding,
                              int padding) {
        HorizontalPanel ab = new HorizontalPanel();
        ab.setStyleName( "modeller-field-Label" );

        StringBuilder bindingLabel = new StringBuilder();
        if ( con.isBound() ) {
            bindingLabel.append( "<b>[" );
            bindingLabel.append( con.getFieldBinding() );
            bindingLabel.append( "]</b>&nbsp;" );
        }

        String fieldName = con.getFieldName();
        bindingLabel.append( fieldName );

        if ( bindable && showBinding && !this.readOnly ) {
            ClickHandler click = new ClickHandler() {

                public void onClick(ClickEvent event) {
                    String[] fields = new String[0];
                    //If field name is "this" use parent FactPattern type otherwise we can use the Constraint's field type
                    String fieldName = con.getFieldName();
                    if ( SuggestionCompletionEngine.TYPE_THIS.equals( fieldName ) ) {
                        fields = connectives.getCompletions().getFieldCompletions( pattern.getFactType() );
                    } else {
                        fields = connectives.getCompletions().getFieldCompletions( con.getFieldType() );
                    }
                    popupCreator.showBindFieldPopup( (Widget) event.getSource(),
                                                     pattern,
                                                     con,
                                                     fields,
                                                     popupCreator );
                }
            };
            ClickableLabel cl = new ClickableLabel( bindingLabel.toString(),
                                                    click,
                                                    !this.readOnly );
            DOM.setStyleAttribute( cl.getElement(),
                                   "marginLeft",
                                   "" + padding + "pt" );
            ab.add( cl );
        } else {
            ab.add( new SmallLabel( bindingLabel.toString() ) );
        }

        return ab;
    }

    public boolean isDirty() {
        return layout.hasDirty();
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public boolean isFactTypeKnown() {
        return this.isFactTypeKnown;
    }

    private void refreshConstraintValueEditorsDropDownData(final SingleFieldConstraint modifiedConstraint) {
        for ( Map.Entry<SingleFieldConstraint, ConstraintValueEditor> e : constraintValueEditors.entrySet() ) {
            if ( e.getKey() != modifiedConstraint ) {
                e.getValue().refreshDropDownData();
            }
        }
    }

}
