package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.HumanReadable;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.MethodInfo;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionCallMethod;
import org.drools.ide.common.client.modeldriven.testing.CallFieldValue;
import org.drools.ide.common.client.modeldriven.testing.CallMethod;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CallMethodWidget extends DirtyableComposite {

    protected static Constants               constants   = GWT.create( Constants.class );
    private static Images                    images      = GWT.create( Images.class );

    protected final ScenarioWidget           parent;
    protected final Scenario                 scenario;
    protected final CallMethod               mCall;
    protected final String                   factName;
    private final ExecutionTrace             executionTrace;

    final private DirtyableFlexTable         layout;
    private boolean                          isBoundFact = false;

    private String[]                         fieldCompletionTexts;
    private String[]                         fieldCompletionValues;
    private String                           variableClass;

    private final SuggestionCompletionEngine suggestionCompletionEngine;

    public CallMethodWidget(String factName,
                            ScenarioWidget parent,
                            Scenario scenario,
                            CallMethod mCall,
                            ExecutionTrace executionTrace) {
        super();
        this.factName = factName;
        this.parent = parent;
        this.scenario = scenario;
        this.mCall = mCall;
        this.executionTrace = executionTrace;
        this.suggestionCompletionEngine = parent.suggestionCompletionEngine;

        this.layout = new DirtyableFlexTable();

        layout.setStyleName( "model-builderInner-Background" ); // NON-NLS

        if ( suggestionCompletionEngine.isGlobalVariable( mCall.getVariable() ) ) {

            List<MethodInfo> infos = suggestionCompletionEngine.getMethodInfosForGlobalVariable( mCall.getVariable() );
            this.fieldCompletionTexts = new String[infos.size()];
            this.fieldCompletionValues = new String[infos.size()];
            int i = 0;
            for ( MethodInfo info : infos ) {
                this.fieldCompletionTexts[i] = info.getName();
                this.fieldCompletionValues[i] = info.getNameWithParameters();
                i++;
            }

            this.variableClass = (String) suggestionCompletionEngine.getGlobalVariable( mCall.getVariable() );
        } else {
            FactData pattern = (FactData) scenario.getFactTypes().get( mCall.getVariable() );
            if ( pattern != null ) {
                List<String> methodList = suggestionCompletionEngine.getMethodNames( pattern.getType() );
                fieldCompletionTexts = new String[methodList.size()];
                fieldCompletionValues = new String[methodList.size()];
                int i = 0;
                for ( String methodName : methodList ) {
                    fieldCompletionTexts[i] = methodName;
                    fieldCompletionValues[i] = methodName;
                    i++;
                }
                this.variableClass = pattern.getType();
                this.isBoundFact = true;
            }
        }

        doLayout();
        initWidget( this.layout );
    }

    private void doLayout() {
        layout.clear();
        layout.setWidget( 0,
                          0,
                          getSetterLabel() );
        DirtyableFlexTable inner = new DirtyableFlexTable();
        for ( int i = 0; i < mCall.getCallFieldValues().length; i++ ) {
            CallFieldValue val = mCall.getCallFieldValues()[i];

            inner.setWidget( i,
                             0,
                             fieldSelector( val ) );
            inner.setWidget( i,
                             1,
                             valueEditor( val ) );

        }
        layout.setWidget( 0,
                          1,
                          inner );
        layout.setWidget( 0,
                          2,
                          new DeleteButton() );
    }

    private Widget getSetterLabel() {
        HorizontalPanel horiz = new HorizontalPanel();

        if ( mCall.getState() == ActionCallMethod.TYPE_UNDEFINED ) {
            Image edit = new ImageButton( images.addFieldToFact() );
            edit.setTitle( constants.AddAnotherFieldToThisSoYouCanSetItsValue() );

            edit.addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    Image w = (Image) event.getSource();
                    showAddFieldPopup( w );

                }
            } );

            horiz.add( new SmallLabel( HumanReadable.getActionDisplayName( "call" ) + " [" + mCall.getVariable() + "]" ) ); // NON-NLS
            horiz.add( edit );
        } else {
            horiz.add( new SmallLabel( HumanReadable.getActionDisplayName( "call" ) + " [" + mCall.getVariable() + "." + mCall.getMethodName() + "]" ) ); // NON-NLS
        }

        return horiz;
    }

    protected void showAddFieldPopup(Widget w) {

        final FormStylePopup popup = new FormStylePopup( images.newexWiz(),
                                                         constants.ChooseAMethodToInvoke() );
        ListBox box = new ListBox();
        box.addItem( "..." );

        for ( int i = 0; i < fieldCompletionTexts.length; i++ ) {
            box.addItem( fieldCompletionTexts[i],
                         fieldCompletionValues[i] );
        }

        box.setSelectedIndex( 0 );

        popup.addAttribute( constants.ChooseAMethodToInvoke(),
                            box );
        box.addChangeHandler( new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                mCall.setState( ActionCallMethod.TYPE_DEFINED );
                ListBox sourceW = (ListBox) event.getSource();
                String methodName = sourceW.getItemText( sourceW.getSelectedIndex() );
                String methodNameWithParams = sourceW.getValue( sourceW.getSelectedIndex() );

                mCall.setMethodName( methodName );
                List<String> fieldList = new ArrayList<String>();

                fieldList.addAll( suggestionCompletionEngine.getMethodParams( variableClass,
                                                                              methodNameWithParams ) );

                // String fieldType = completions.getFieldType( variableClass,
                // fieldName );
                int i = 0;
                for ( String fieldParameter : fieldList ) {
                    mCall.addFieldValue( new CallFieldValue( methodName,
                                                             String.valueOf( i ),
                                                             fieldParameter ) );
                    i++;
                }

                parent.renderEditor();
                popup.hide();

            }
        } );

        popup.setPopupPosition( w.getAbsoluteLeft(),
                                w.getAbsoluteTop() );
        popup.show();

    }

    private Widget valueEditor(final CallFieldValue val) {

        String type = "";
        if ( suggestionCompletionEngine.isGlobalVariable( this.mCall.getVariable() ) ) {
            type = suggestionCompletionEngine.getGlobalVariable( this.mCall.getVariable() );
        } else {
            Map<String, String> mFactTypes = scenario.getVariableTypes();
            type = mFactTypes.get( this.mCall.getVariable() );
        }

        DropDownData enums = suggestionCompletionEngine.getEnums( type,
                val.field, this.mCall.getCallFieldValues()
        );
        return new MethodParameterCallValueEditor( val,
                                                   enums,
                                                   executionTrace,
                                                   scenario,
                                                   val.type,
                                                   new Command() {

                                                       public void execute() {
                                                           makeDirty();
                                                       }
                                                   } );
    }

    /**
     * This will return a keyboard listener for field setters, which will obey
     * numeric conventions - it will also allow formulas (a formula is when the
     * first value is a "=" which means it is meant to be taken as the user
     * typed)
     */
    public static KeyPressHandler getNumericFilter(final TextBox box) {
        return new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {
                TextBox w = (TextBox) event.getSource();
                char c = event.getCharCode();
                if ( Character.isLetter( c ) && c != '=' && !(box.getText().startsWith( "=" )) ) {
                    ((TextBox) w).cancelKey();
                }

            }
        };
    }

    private Widget fieldSelector(final CallFieldValue val) {
        return new SmallLabel( val.type );
    }

    /**
     * This returns true if the values being set are on a fact.
     */
    public boolean isBoundFact() {
        return isBoundFact;
    }

    public boolean isDirty() {
        return layout.hasDirty();
    }

    protected void onDelete() {
        if ( Window.confirm( constants.AreYouSureToRemoveCallMethod() ) ) {
            scenario.removeFixture( mCall );
            parent.renderEditor();
        }
    }

    class DeleteButton extends ImageButton {
        public DeleteButton() {
            super( images.deleteItemSmall(),
                   constants.RemoveCallMethod() );

            addClickHandler( new ClickHandler() {

                public void onClick(ClickEvent event) {
                    onDelete();
                }
            } );
        }
    }

}
