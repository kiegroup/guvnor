package org.drools.guvnor.client.qa;

import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.drools.guvnor.client.common.DropDownValueChanged;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.ui.ActionValueEditor;
import org.drools.guvnor.client.modeldriven.ui.EnumDropDown;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.util.Format;

/**
 * Constraint editor for the FieldData in the Given Section
 *
 * @author Nicolas Heron
 */

public class FieldDataConstraintEditor extends DirtyableComposite {

    private String factType;
    private FieldData field;
    private FactData givenFact;
    private final Panel panel;
    private Scenario scenario;
    private ExecutionTrace executionTrace;
    private SuggestionCompletionEngine sce;
    private ValueChanged callback;
    private Constants constants = ((Constants) GWT.create(Constants.class));

    public FieldDataConstraintEditor(String factType,
                                     ValueChanged callback,
                                     FieldData field,
                                     FactData givenFact,
                                     SuggestionCompletionEngine sce,
                                     Scenario scenario,
                                     ExecutionTrace exec) {
        this.field = field;
        this.sce = sce;
        this.factType = factType;
        this.callback = callback;
        this.scenario = scenario;
        this.executionTrace = exec;
        this.givenFact = givenFact;
        panel = new SimplePanel();
        refreshEditor();
        initWidget(panel);
    }

    private void refreshEditor() {
        String key = factType + "." + field.name;
        String flType = sce.getFieldType(key);
        panel.clear();
        if (flType != null && flType.equals(SuggestionCompletionEngine.TYPE_NUMERIC)) {
            final TextBox box = editableTextBox(callback,
                    field.name,
                    field.value);
            box.addKeyboardListener(ActionValueEditor.getNumericFilter(box));
            panel.add(box);
        } else if (flType != null && flType.equals(SuggestionCompletionEngine.TYPE_BOOLEAN)) {
            String[] c = new String[]{"true", "false"};
            panel.add(new EnumDropDown(field.value,
                    new DropDownValueChanged() {
                        public void valueChanged(String newText,
                                                 String newValue) {
                            callback.valueChanged(newValue);
                        }
                    },
                    DropDownData.create(c)));
        } else {
            String[] enums = sce.getDataEnumList(key);
            if (enums != null) {
                panel.add(new EnumDropDown(field.value,
                        new DropDownValueChanged() {
                            public void valueChanged(String newText,
                                                     String newValue) {
                                callback.valueChanged(newValue);
                            }
                        },
                        DropDownData.create(enums)));

            } else {
                if (field.value != null && field.value.length() > 0 && field.nature == FieldData.TYPE_UNDEFINED) {
                    //  GUVNOR-337
                    if (field.value.length() > 1 && field.value.charAt(1) == '[' && field.value.charAt(0) == '=') {
                        field.nature = FieldData.TYPE_LITERAL;
                    } else if (field.value.charAt(0) == '=') {
                        field.nature = FieldData.TYPE_VARIABLE;
                    } else {
                        field.nature = FieldData.TYPE_LITERAL;
                    }
                }
                if (field.nature == FieldData.TYPE_UNDEFINED &&
                        (isThereABoundVariableToSet() == true || isItAList() == true)) {
                    Image clickme = new Image("images/edit.gif"); // NON-NLS
                    clickme.addClickListener(new ClickListener() {
                        public void onClick(Widget w) {
                            showTypeChoice(w,
                                    field);
                        }
                    });
                    panel.add(clickme);
                } else if (field.nature == FieldData.TYPE_VARIABLE) {
                    panel.add(variableEditor(callback));
                } else if (field.nature == FieldData.TYPE_COLLECTION) {
                    panel.add(listEditor(callback));
                } else {
                    panel.add(editableTextBox(callback,
                            field.name,
                            field.value));
                }
            }
        }

    }

    private static TextBox editableTextBox(final ValueChanged changed,
                                           String fieldName,
                                           String initialValue) {
        // Fixme nheron
        final TextBox tb = new TextBox();
        tb.setText(initialValue);
        String m = Format.format(((Constants) GWT.create(Constants.class)).ValueFor0(),
                fieldName);
        tb.setTitle(m);
        tb.addChangeListener(new ChangeListener() {
            public void onChange(Widget w) {
                changed.valueChanged(tb.getText());
            }
        });

        return tb;
    }

    private Widget variableEditor(final ValueChanged changed) {
        // sce.
        List vars = this.scenario.getFactNamesInScope(this.executionTrace,
                true);

        final ListBox box = new ListBox();

        if (this.field.value == null) {
            box.addItem(constants.Choose());
        }
        int j = 0;
        for (int i = 0; i < vars.size(); i++) {
            String var = (String) vars.get(i);
            Map m = this.scenario.getVariableTypes();
            FactData f = (FactData) this.scenario.getFactTypes().get(var);
            String fieldType = null;
            if (field.collectionType == null) {
                fieldType = sce.getFieldType(this.factType,
                        field.name);
            } else {
                fieldType = field.collectionType;
            }

            if (f.type.equals(fieldType)) {
                if (box.getItemCount() == 0) {
                    box.addItem("...");
                    j++;
                }
                box.addItem("=" + var);
                if (this.field.value != null && this.field.value.equals("=" + var)) {
                    box.setSelectedIndex(j);

                }
                j++;
            }
        }

        box.addChangeListener(new ChangeListener() {
            public void onChange(Widget w) {
                field.value = box.getItemText(box.getSelectedIndex());
                changed.valueChanged(field.value);
            }
        });

        return box;
    }

    private Widget listEditor(final ValueChanged changed) {
        Panel panel = new VerticalPanel();
        //nheron
        int i = 0;
        for (final FieldData f : this.field.collectionFieldList) {

            DirtyableHorizontalPane hpanel = new DirtyableHorizontalPane();

            FieldDataConstraintEditor fieldElement = new FieldDataConstraintEditor(f.collectionType, new ValueChanged() {
                public void valueChanged(String newValue) {
                    f.value = newValue ;
                    calculateValueFromList();
                    makeDirty();
                }
            }, f, givenFact, sce, scenario, executionTrace);
            hpanel.add(fieldElement);
            final int index = i;
            Image del = new ImageButton("images/delete_item_small.gif", Format.format(constants.AElementToDelInCollectionList(), "tt"), new ClickListener() {
                public void onClick(Widget w) {
                    field.collectionFieldList.remove(index);
                    calculateValueFromList();
                    refreshEditor();
                }
            });

            hpanel.add(del);

            Image addPattern = new ImageButton("images/new_item_below.png");
            addPattern.setTitle(constants.AddElementBelow());


            addPattern.addClickListener(new ClickListener(){
                public void onClick(Widget sender) {
                   FieldData newFieldData = new FieldData();
                   newFieldData.name = field.name;
                   newFieldData.collectionType = field.collectionType;
                   field.collectionFieldList.add(index+1,newFieldData);
                   calculateValueFromList();
                   refreshEditor();
                }
            });
            hpanel.add(addPattern);
            Image moveDown = new ImageButton("images/shuffle_down.gif");
            moveDown.setTitle(constants.MoveDownListMove());
            moveDown.addClickListener(new ClickListener(){
                public void onClick(Widget sender) {
                   if (index <field.collectionFieldList.size()-1){
                          FieldData onMyLine =field.collectionFieldList.get(index);
                          FieldData onDown =field.collectionFieldList.get(index+1);
                          field.collectionFieldList.set(index+1,onMyLine);
                          field.collectionFieldList.set(index,onDown);
                          calculateValueFromList();
                          refreshEditor();
                   }
                }
            });
            hpanel.add(moveDown);

            Image moveUp = new ImageButton("images/shuffle_up.gif");
            moveUp.setTitle(constants.MoveUpList());
            moveUp.addClickListener(new ClickListener(){
                public void onClick(Widget sender) {
                   if (index >0){
                          FieldData oneUp =field.collectionFieldList.get(index-1);
                          FieldData onMyLine =field.collectionFieldList.get(index);
                          field.collectionFieldList.set(index,oneUp);
                          field.collectionFieldList.set(index-1,onMyLine);
                          calculateValueFromList();
                          refreshEditor();
                   }
                }
            });
            hpanel.add(moveUp);
            panel.add(hpanel);
             i++;
        }
    

        if (this.field.collectionFieldList.size()==0){
            Image add = new ImageButton("images/new_item.gif", Format.format(constants.AElementToAddInCollectionList(), "tt"), new ClickListener() {
                public void onClick(Widget w) {
                    FieldData newFieldData = new FieldData();
                    newFieldData.name = field.name;
                    newFieldData.collectionType = field.collectionType;
                    field.collectionFieldList.add(newFieldData);
                    calculateValueFromList();
                    refreshEditor();
                }
            });
            panel.add(add);
        }
        return panel;
    }

    private void calculateValueFromList() {
        if (this.field.collectionFieldList == null || this.field.collectionFieldList.isEmpty()) {
            this.field.value = "=[]";
            return;
        }
        StringBuffer listContent = new StringBuffer();
        for (final FieldData f : this.field.collectionFieldList) {
            listContent.append(',');
            if (f.value != null) {
                listContent.append(f.value.substring(1));
            }
        }
        this.field.value = "=[" + listContent.substring(1) + "]";
    }

    private void showTypeChoice(Widget w,
                                final FieldData con) {
        final FormStylePopup form = new FormStylePopup("images/newex_wiz.gif",
                constants.FieldValue());

        Button lit = new Button(constants.LiteralValue());
        lit.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                con.nature = FieldData.TYPE_LITERAL;
                doTypeChosen(form);
            }

        });
        form.addAttribute(constants.LiteralValue() + ":",
                widgets(lit,
                        new InfoPopup(constants.LiteralValue(),
                                constants.LiteralValTip())));

        form.addRow(new HTML("<hr/>"));
        form.addRow(new SmallLabel(constants.AdvancedOptions()));

        // If we are here, then there must be a bound variable compatible with
        // me
        if (isThereABoundVariableToSet() == true) {
            Button variable = new Button(constants.BoundVariable());
            variable.addClickListener(new ClickListener() {
                public void onClick(Widget w) {
                    con.nature = FieldData.TYPE_VARIABLE;
                    doTypeChosen(form);
                }
            });
            form.addAttribute(constants.AVariable(),
                    widgets(variable,
                            new InfoPopup(constants.ABoundVariable(),
                                    constants.BoundVariableTip())));
        }
        if (isItAList() == true) {
            Button variable = new Button(constants.GuidedList());
            variable.addClickListener(new ClickListener() {
                public void onClick(Widget w) {
                    String factCollectionType = sce.getParametricFieldType(factType, field.name);
                    con.setNature(FieldData.TYPE_COLLECTION, factCollectionType);
                    doTypeChosen(form);
                }
            });
            form.addAttribute(constants.AVariable(),
                    widgets(variable,
                            new InfoPopup(constants.AGuidedList(),
                                    constants.AGuidedListTip())));
        }
        form.show();
    }

    private boolean isThereABoundVariableToSet() {
        boolean retour = false;
        List vars = scenario.getFactNamesInScope(this.executionTrace,
                true);
        if (vars.size() > 0) {
            for (int i = 0; i < vars.size(); i++) {
                String var = (String) vars.get(i);
                Map m = this.scenario.getVariableTypes();
                FactData f = (FactData) scenario.getFactTypes().get(var);
                String fieldType = null;
                if (field.collectionType == null) {
                    fieldType = sce.getFieldType(this.factType,
                            field.name);
                } else {
                    fieldType = field.collectionType;
                }
                if (f.type.equals(fieldType)) {
                    retour = true;
                    break;
                }
            }
        }
        return retour;
    }

    private boolean isItAList() {
        boolean retour = false;
        String fieldType = sce.getFieldType(this.factType, field.name);
        if (fieldType != null && fieldType.equals("Collection")) {
            retour = true;
        }
        return retour;
    }

    private void doTypeChosen(final FormStylePopup form) {
        refreshEditor();
        form.hide();
    }

    private Panel widgets(Widget left,
                          Widget right) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(left);
        panel.add(right);
        panel.setWidth("100%");
        return panel;
    }

}
