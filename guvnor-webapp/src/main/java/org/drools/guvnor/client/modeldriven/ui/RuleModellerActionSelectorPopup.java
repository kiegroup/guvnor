/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.modeldriven.ui;

import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.configurations.Capability;
import org.drools.guvnor.client.configurations.UserCapabilities;
import org.drools.ide.common.client.modeldriven.brl.ActionCallMethod;
import org.drools.ide.common.client.modeldriven.brl.ActionGlobalCollectionAdd;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact;
import org.drools.ide.common.client.modeldriven.brl.ActionRetractFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.ActionUpdateField;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;
import org.drools.ide.common.client.modeldriven.brl.FreeFormLine;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Pop-up for adding Actions to the (RuleModeller) guided editor
 */
public class RuleModellerActionSelectorPopup extends AbstractRuleModellerSelectorPopup {

    public RuleModellerActionSelectorPopup(RuleModel model,
                                           RuleModeller ruleModeller,
                                           String packageName,
                                           Integer position) {
        super( model,
               ruleModeller,
               packageName,
               position );
    }

    @Override
    protected String getPopupTitle() {
        return constants.AddANewAction();
    }

    @Override
    public Widget getContent() {
        if ( position == null ) {
            positionCbo.addItem( constants.Bottom(),
                                 String.valueOf( this.model.rhs.length ) );
            positionCbo.addItem( constants.Top(),
                                 "0" );
            for ( int i = 1; i < model.rhs.length; i++ ) {
                positionCbo.addItem( constants.Line0( i ),
                                     String.valueOf( i ) );
            }
        } else {
            //if position is fixed, we just add one element to the drop down.
            positionCbo.addItem( String.valueOf( position ) );
            positionCbo.setSelectedIndex( 0 );
        }

        if ( completions.getDSLConditions().length == 0 && completions.getFactTypes().length == 0 ) {
            layoutPanel.addRow( new HTML( "<div class='highlight'>" + constants.NoModelTip() + "</div>" ) ); 
        }
        
        //only show the drop down if we are not using fixed position.
        if ( position == null ) {
            HorizontalPanel hp0 = new HorizontalPanel();
            hp0.add( new HTML( constants.PositionColon() ) );
            hp0.add( positionCbo );
            hp0.add( new InfoPopup( constants.PositionColon(),
                                    constants.ActionPositionExplanation() ) );
            layoutPanel.addRow( hp0 );
        }

        choices = makeChoicesListBox();
        choicesPanel.add( choices );
        layoutPanel.addRow( choicesPanel );

        HorizontalPanel hp = new HorizontalPanel();
        Button ok = new Button( constants.OK() );
        hp.add( ok );
        ok.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                selectSomething();
            }
        } );

        Button cancel = new Button( constants.Cancel() );
        hp.add( cancel );
        cancel.addClickHandler( new ClickHandler() {

            public void onClick(ClickEvent event) {
                hide();
            }
        } );

        CheckBox chkOnlyDisplayDSLConditions = new CheckBox();
        chkOnlyDisplayDSLConditions.setText( constants.OnlyDisplayDSLActions() );
        chkOnlyDisplayDSLConditions.setValue( bOnlyShowDSLConditions );
        chkOnlyDisplayDSLConditions.addValueChangeHandler( new ValueChangeHandler<Boolean>() {

            public void onValueChange(ValueChangeEvent<Boolean> event) {
                bOnlyShowDSLConditions = event.getValue();
                choicesPanel.setWidget( makeChoicesListBox() );
            }

        } );

        layoutPanel.addRow( chkOnlyDisplayDSLConditions );

        layoutPanel.addRow( hp );

        this.setAfterShow( new Command() {

            public void execute() {
                choices.setFocus( true );
            }
        } );

        return layoutPanel;
    }

    private ListBox makeChoicesListBox() {
        choices = new ListBox( true );
        choices.setPixelSize( getChoicesWidth(),
                              getChoicesHeight() );

        choices.addKeyUpHandler( new KeyUpHandler() {
            public void onKeyUp(com.google.gwt.event.dom.client.KeyUpEvent event) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    selectSomething();
                }
            }
        } );

        addDSLSentences();
        if ( !bOnlyShowDSLConditions ) {
            addUpdateNotModify();
            addGlobals();
            addRetractions();
            addModifies();
            addInsertions();
            addLogicalInsertions();
            addGlobalCollections();
            addFreeFormDRL();
        }

        return choices;
    }

    // Add DSL sentences
    private void addDSLSentences() {
        if ( completions.getDSLActions().length > 0 ) {

            for ( int i = 0; i < completions.getDSLActions().length; i++ ) {
                final DSLSentence sen = completions.getDSLActions()[i];
                if ( sen != null ) {
                    String sentence = sen.toString();
                    choices.addItem( sentence,
                                     "DSL" + sentence );
                    cmds.put( "DSL" + sentence,
                              new Command() {

                                  public void execute() {
                                      addNewDSLRhs( sen,
                                                    Integer.parseInt( positionCbo.getValue( positionCbo.getSelectedIndex() ) ) );
                                      hide();
                                  }
                              } );
                }
            }

        }
    }

    //Add update, not modify
    private void addUpdateNotModify() {
        List<String> vars = model.getLHSBoundFacts();
        if ( vars.size() == 0 ) {
            return;
        }

        choices.addItem( SECTION_SEPARATOR );
        for ( Iterator<String> iter = vars.iterator(); iter.hasNext(); ) {
            final String v = iter.next();

            choices.addItem( constants.ChangeFieldValuesOf0( v ),
                             "VAR" + v );
            cmds.put( "VAR" + v,
                      new Command() {

                          public void execute() {
                              addActionSetField( v,
                                                 Integer.parseInt( positionCbo.getValue( positionCbo.getSelectedIndex() ) ) );
                              hide();
                          }
                      } );

        }
    }

    //Add Globals
    private void addGlobals() {
        String[] globals = completions.getGlobalVariables();
        if ( globals.length == 0 ) {
            return;
        }
        choices.addItem( SECTION_SEPARATOR );
        for ( int i = 0; i < globals.length; i++ ) {
            final String v = globals[i];
            choices.addItem( constants.ChangeFieldValuesOf0( v ),
                             "GLOBVAR" + v );
            cmds.put( "GLOBVAR" + v,
                      new Command() {

                          public void execute() {
                              addActionSetField( v,
                                                 Integer.parseInt( positionCbo.getValue( positionCbo.getSelectedIndex() ) ) );
                              hide();
                          }
                      } );
        }
    }

    //Add Retractions
    private void addRetractions() {
        List<String> vars = model.getLHSBoundFacts();
        if ( vars.size() == 0 ) {
            return;
        }
        choices.addItem( SECTION_SEPARATOR );
        for ( Iterator<String> iter = vars.iterator(); iter.hasNext(); ) {
            final String v = iter.next();
            choices.addItem( constants.Retract0( v ),
                             "RET" + v );
            cmds.put( "RET" + v,
                      new Command() {

                          public void execute() {
                              addRetract( v,
                                          Integer.parseInt( positionCbo.getValue( positionCbo.getSelectedIndex() ) ) );
                              hide();
                          }
                      } );
        }
    }

    //Add Modifies
    private void addModifies() {
        List<String> vars = model.getLHSBoundFacts();
        if ( vars.size() == 0 ) {
            return;
        }
        choices.addItem( SECTION_SEPARATOR );
        for ( Iterator<String> iter = vars.iterator(); iter.hasNext(); ) {
            final String v = iter.next();

            choices.addItem( constants.Modify0( v ),
                             "MOD" + v );
            cmds.put( "MOD" + v,
                      new Command() {

                          public void execute() {
                              addModify( v,
                                         Integer.parseInt( positionCbo.getValue( positionCbo.getSelectedIndex() ) ) );
                              hide();
                          }
                      } );
        }
    }

    //Add insertions
    private void addInsertions() {
        if ( completions.getFactTypes().length == 0 ) {
            return;
        }
        choices.addItem( SECTION_SEPARATOR );
        for ( int i = 0; i < completions.getFactTypes().length; i++ ) {
            final String item = completions.getFactTypes()[i];
            choices.addItem( constants.InsertFact0( item ),
                             "INS" + item );
            cmds.put( "INS" + item,
                      new Command() {

                          public void execute() {
                              model.addRhsItem( new ActionInsertFact( item ),
                                                Integer.parseInt( positionCbo.getValue( positionCbo.getSelectedIndex() ) ) );
                              hide();
                          }
                      } );
        }
    }

    //Add logical insertions
    private void addLogicalInsertions() {
        if ( completions.getFactTypes().length == 0 ) {
            return;
        }
        choices.addItem( SECTION_SEPARATOR );
        for ( int i = 0; i < completions.getFactTypes().length; i++ ) {
            final String item = completions.getFactTypes()[i];
            choices.addItem( constants.LogicallyInsertFact0( item ),
                             "LINS" + item );
            cmds.put( "LINS" + item,
                      new Command() {

                          public void execute() {
                              model.addRhsItem( new ActionInsertLogicalFact( item ),
                                                Integer.parseInt( positionCbo.getValue( positionCbo.getSelectedIndex() ) ) );
                              hide();
                          }
                      } );
        }
    }

    //Add global collections
    private void addGlobalCollections() {
        List<String> vars = model.getLHSBoundFacts();
        if ( vars.size() == 0 ) {
            return;
        }
        if ( completions.getGlobalCollections().length == 0 ) {
            return;
        }
        choices.addItem( SECTION_SEPARATOR );
        for ( String bf : vars ) {
            for ( int i = 0; i < completions.getGlobalCollections().length; i++ ) {
                final String glob = completions.getGlobalCollections()[i];
                final String var = bf;
                choices.addItem( constants.Append0ToList1( var,
                                                           glob ),
                                 "GLOBCOL" + glob + var );
                cmds.put( "GLOBCOL" + glob + var,
                          new Command() {

                              public void execute() {
                                  ActionGlobalCollectionAdd gca = new ActionGlobalCollectionAdd();
                                  gca.globalName = glob;
                                  gca.factName = var;
                                  model.addRhsItem( gca,
                                                    Integer.parseInt( positionCbo.getValue( positionCbo.getSelectedIndex() ) ) );
                                  hide();
                              }
                          } );
            }
        }
    }

    //Add free-form DRL
    private void addFreeFormDRL() {
        List<String> vars = model.getLHSBoundFacts();
        List<String> vars2 = model.getRHSBoundFacts();
        String[] globals = completions.getGlobalVariables();

        if ( UserCapabilities.INSTANCE.hasCapability( Capability.SHOW_KNOWLEDGE_BASES_VIEW ) ) {
            choices.addItem( SECTION_SEPARATOR );
            choices.addItem( constants.AddFreeFormDrl(),
                             "FF" );
            cmds.put( "FF",
                      new Command() {

                          public void execute() {
                              model.addRhsItem( new FreeFormLine(),
                                                Integer.parseInt( positionCbo.getValue( positionCbo.getSelectedIndex() ) ) );
                              hide();
                          }
                      } );

            //Add globals
            if ( globals.length > 0 ) {
                choices.addItem( SECTION_SEPARATOR );
            }
            for ( int i = 0; i < globals.length; i++ ) {
                final String v = globals[i];
                choices.addItem( constants.CallMethodOn0( v ),
                                 "GLOBCALL" + v );
                cmds.put( "GLOBCALL" + v,
                          new Command() {

                              public void execute() {
                                  addCallMethod( v,
                                                 Integer.parseInt( positionCbo.getValue( positionCbo.getSelectedIndex() ) ) );
                                  hide();
                              }
                          } );

            }

            //Method calls
            if ( vars.size() > 0 ) {
                choices.addItem( SECTION_SEPARATOR );
            }
            for ( Iterator<String> iter = vars.iterator(); iter.hasNext(); ) {
                final String v = iter.next();

                choices.addItem( constants.CallMethodOn0( v ),
                                 "CALL" + v );
                cmds.put( "CALL" + v,
                          new Command() {

                              public void execute() {
                                  addCallMethod( v,
                                                 Integer.parseInt( positionCbo.getValue( positionCbo.getSelectedIndex() ) ) );
                                  hide();
                              }
                          } );
            }

            //Update, not modify
            if ( vars2.size() > 0 ) {
                choices.addItem( SECTION_SEPARATOR );
            }
            for ( Iterator<String> iter = vars2.iterator(); iter.hasNext(); ) {
                final String v = iter.next();

                choices.addItem( constants.CallMethodOn0( v ),
                                 "CALL" + v );
                cmds.put( "CALL" + v,
                          new Command() {

                              public void execute() {
                                  addCallMethod( v,
                                                 Integer.parseInt( positionCbo.getValue( positionCbo.getSelectedIndex() ) ) );
                                  hide();
                              }
                          } );
            }
        }
    }

    private void addNewDSLRhs(DSLSentence sentence,
                                int position) {
        this.model.addRhsItem( sentence.copy(),
                               position );
    }

    private void addRetract(String var,
                              int position) {
        this.model.addRhsItem( new ActionRetractFact( var ),
                               position );
    }

    private void addActionSetField(String itemText,
                                     int position) {
        this.model.addRhsItem( new ActionSetField( itemText ),
                               position );
    }

    private void addCallMethod(String itemText,
                                 int position) {
        this.model.addRhsItem( new ActionCallMethod( itemText ),
                               position );
    }

    private void addModify(String itemText,
                             int position) {
        this.model.addRhsItem( new ActionUpdateField( itemText ),
                               position );
    }

}
