package org.drools.guvnor.client.ruleeditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.factconstraints.client.ConstraintConfiguration;
import org.drools.factconstraints.client.helper.ConstraintsContainer;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.WorkingSetConfigData;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

/**
 *
 * @author esteban
 */
public class FactsConstraintsEditorPanel extends Composite {
    private static int idGenerator = 0;
    private Constants constants = GWT.create(Constants.class);
    private ListBox factsCombo = new ListBox(false);
    private ListBox fieldsCombo = new ListBox(false);
    private ListBox constraintsCombo = new ListBox(false);
    private VerticalPanel vpConstraintConf = new VerticalPanel();
    private boolean validFactsChanged = true;
    private Map<String, ConstraintConfiguration> contraintsMap = new HashMap<String, ConstraintConfiguration>();
    private final RuleAsset workingSet;
    private final WorkingSetEditor workingSetEditor;

    public FactsConstraintsEditorPanel(WorkingSetEditor workingSetEditor) {

        this.workingSetEditor = workingSetEditor;

        this.workingSet = workingSetEditor.getWorkingSet();

        factsCombo.setVisibleItemCount(1);
        fieldsCombo.setVisibleItemCount(1);
        constraintsCombo.setVisibleItemCount(5);

        factsCombo.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                fillSelectedFactFields();
            }
        });

        fieldsCombo.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                fillFieldConstrains();
            }
        });

        Image addNewConstraint = new ImageButton("images/new_item.gif"); // NON-NLS
        addNewConstraint.setTitle(constants.AddNewConstraint());

        addNewConstraint.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showNewConstrainPop();
            }
        });

        Image removeConstraint = new Image("images/trash.gif"); //NON-NLS
        removeConstraint.setTitle(constants.removeConstraint());
        removeConstraint.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                removeConstraint();
            }
        });

        final FlexTable table = new FlexTable();

        VerticalPanel vp = new VerticalPanel();
        vp.add(new SmallLabel(constants.FactTypes()));
        vp.add(factsCombo);
        table.setWidget(0, 0, vp);

        vp = new VerticalPanel();
        vp.add(new SmallLabel(constants.Field()));
        vp.add(fieldsCombo);
        table.setWidget(1, 0, vp);

        vp = new VerticalPanel();
        HorizontalPanel hp = new HorizontalPanel();
        vp.add(new SmallLabel("Constraints")); //TODO i18n
        hp.add(constraintsCombo);

        VerticalPanel btnPanel = new VerticalPanel();
        btnPanel.add(addNewConstraint);
        btnPanel.add(removeConstraint);
        hp.add(btnPanel);
        vp.add(hp);
        table.setWidget(2, 0, vp);
        table.getFlexCellFormatter().setRowSpan(2, 0, 3);
        constraintsCombo.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                showConstraintConfig();
            }
        });

        vpConstraintConf.add(new SmallLabel("Contraints Parameters")); //TODO i18n
        vpConstraintConf.add(new SmallLabel(""));
        table.setWidget(0, 1, vpConstraintConf);
        table.getFlexCellFormatter().setRowSpan(0, 1, 5);

        fillSelectedFacts();
        fillSelectedFactFields();
        fillFieldConstrains();
        showConstraintConfig();

        this.initWidget(table);
    }

    protected final void fillSelectedFacts() {
        if (validFactsChanged) {
            String s = factsCombo.getSelectedIndex() != -1 ? factsCombo.getItemText(factsCombo.getSelectedIndex()) : "";
            factsCombo.clear();
            validFactsChanged = false;
            for (int i = 0; i < workingSetEditor.getValidFactsListBox().getItemCount(); i++) {
                String itemText = workingSetEditor.getValidFactsListBox().getItemText(i);
                factsCombo.addItem(itemText);
                if (s.equals(itemText)) {
                    factsCombo.setSelectedIndex(i);
                }
            }
            if (factsCombo.getSelectedIndex() == -1 && factsCombo.getItemCount() > 0) {
                factsCombo.setSelectedIndex(0);
            }
            fillSelectedFactFields();
        }
    }

    private void fillSelectedFactFields() {
        if (factsCombo.getSelectedIndex() != -1) {
            String fact = factsCombo.getItemText(factsCombo.getSelectedIndex());
            fieldsCombo.clear();
            for (String field : getCompletionEngine().getFieldCompletions(fact)) {
                fieldsCombo.addItem(field);
            }
        }
        if (fieldsCombo.getSelectedIndex() == -1 && fieldsCombo.getItemCount() > 0) {
            fieldsCombo.setSelectedIndex(0);
        }
        fillFieldConstrains();
    }

    private void fillFieldConstrains() {
        if (fieldsCombo.getSelectedIndex() != -1) {
            String fieldName = fieldsCombo.getItemText(fieldsCombo.getSelectedIndex());
            String factField = factsCombo.getItemText(factsCombo.getSelectedIndex());
            constraintsCombo.clear();
            contraintsMap.clear();
            for (ConstraintConfiguration c : this.workingSetEditor.getConstraintsConstrainer().getConstraints(factField, fieldName)) {
                constraintsCombo.addItem(c.getConstraintName(), addContrainsMap(c));
            }
            vpConstraintConf.remove(vpConstraintConf.getWidgetCount() - 1);
            vpConstraintConf.add(new SmallLabel());
        }
        showConstraintConfig();
    }

    synchronized private String addContrainsMap(ConstraintConfiguration c) {
        String constraintId = String.valueOf(idGenerator++);
        contraintsMap.put(constraintId, c);
        return constraintId;
    }

    protected void removeConstraint() {
        if (constraintsCombo.getSelectedIndex() != -1) {
            ConstraintConfiguration c = contraintsMap.get(constraintsCombo.getValue(constraintsCombo.getSelectedIndex()));
            ((WorkingSetConfigData) workingSet.content).constraints = this.workingSetEditor.getConstraintsConstrainer().removeConstraint(c);
        }
        fillFieldConstrains();
    }

    private void showConstraintConfig() {
        if (constraintsCombo.getItemCount() == 0) {
            vpConstraintConf.remove(vpConstraintConf.getWidgetCount() - 1);
            vpConstraintConf.add(new SmallLabel());
            return;
        }
        if (constraintsCombo.getSelectedIndex() != -1) {
            ConstraintConfiguration c = contraintsMap.get(constraintsCombo.getValue(constraintsCombo.getSelectedIndex()));
            ConstraintEditor editor = new ConstraintEditor(c);
            vpConstraintConf.remove(vpConstraintConf.getWidgetCount() - 1);
            vpConstraintConf.add(editor);
        }
    }

    private void showNewConstrainPop() {
        final FormStylePopup pop = new FormStylePopup("images/config.png", constants.AddNewConstraint()); //NON-NLS
        final Button addbutton = new Button(constants.OK());
        final ListBox consDefsCombo = new ListBox(false);

        consDefsCombo.setVisibleItemCount(5);

        addbutton.setTitle(constants.AddNewConstraint());

        List<String> names = new ArrayList<String>(ConstraintsContainer.getAllConfigurations().keySet());
        Collections.sort(names);
        for (String name : names) {
            consDefsCombo.addItem(name);
        }

        addbutton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                String name = consDefsCombo.getItemText(consDefsCombo.getSelectedIndex());
                ConstraintConfiguration config = ConstraintsContainer.getEmptyConfiguration(name);
                if (config != null) {

                    String factName = factsCombo.getItemText(factsCombo.getSelectedIndex());
                    String fieldName = fieldsCombo.getItemText(fieldsCombo.getSelectedIndex());
                    config.setFactType(factName);
                    config.setFieldName(fieldName);
                    if (((WorkingSetConfigData) workingSet.content).constraints == null) {
                        ((WorkingSetConfigData) workingSet.content).constraints = new ArrayList<ConstraintConfiguration>();
                    }
                    ((WorkingSetConfigData) workingSet.content).constraints.add(config);
                    constraintsCombo.addItem(config.getConstraintName(), addContrainsMap(config));
                    workingSetEditor.getConstraintsConstrainer().addConstraint(config);

                }
                pop.hide();
            }
        });

        pop.addAttribute(constants.WillExtendTheFollowingRuleCalled(), consDefsCombo);
        pop.addAttribute("", addbutton);

        pop.show();
    }

    private SuggestionCompletionEngine getCompletionEngine() {
        return SuggestionCompletionCache.getInstance().getEngineFromCache(workingSet.metaData.packageName);
    }

    public void notifyValidFactsChanged(){
        this.validFactsChanged = true;
    }
}
