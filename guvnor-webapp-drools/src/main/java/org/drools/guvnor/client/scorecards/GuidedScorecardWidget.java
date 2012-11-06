/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.client.scorecards;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.SaveCommand;
import org.drools.guvnor.client.asseteditor.SaveEventListener;
import org.drools.guvnor.client.asseteditor.drools.modeldriven.ui.EnumDropDown;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.DropDownValueChanged;
import org.drools.guvnor.client.common.TextBoxFactory;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.util.DecoratedDisclosurePanel;
import org.drools.guvnor.client.widgets.CustomEditTextCell;
import org.drools.guvnor.client.widgets.DynamicSelectionCell;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.ModelField;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.scorecards.Attribute;
import org.drools.ide.common.client.modeldriven.scorecards.Characteristic;
import org.drools.ide.common.client.modeldriven.scorecards.ScorecardModel;

import java.util.*;

public class GuidedScorecardWidget extends Composite
        implements
        SaveEventListener,
        EditorWidget {

    private static final String[] REASON_CODE_ALGORITHMS = new String[]{"none", "pointsAbove", "pointsBelow"};
    final private String[] typesForAttributes = new String[]{"String", "int", "double", "boolean"};
    final private String[] typesForScore = new String[]{"double"};
    final private String[] typesForRC = new String[]{"List"};

    final private String[] stringOperators = new String[]{"=", "in"};
    final private String[] booleanOperators = new String[]{"true", "false"};
    final private String[] numericOperators = new String[]{"=", ">", "<", ">=", "<=", ">..<", ">=..<", ">=..<=", ">..<="};

    private VerticalPanel layout;
    private Asset asset;
    private ClientFactory clientFactory;
    private EventBus globalEventBus;
    private SimplePanel dtableContainer = new SimplePanel();
    private VerticalPanel characteristicsPanel;
    private Button btnAddCharacteristic;
    private List<DirtyableFlexTable> characteristicsTables = new ArrayList<DirtyableFlexTable>();

    private Map<DirtyableFlexTable, ListDataProvider<Attribute>> characteristicsAttrMap = new HashMap<DirtyableFlexTable, ListDataProvider<Attribute>>();
    private SuggestionCompletionEngine sce;
    private Map<String, ModelField[]> sceModelFields;

    private EnumDropDown ddUseReasonCode;
    private EnumDropDown ddReasonCodeAlgo;
    private EnumDropDown ddReasonCodeField;
    private TextBox tbBaselineScore;
    private TextBox tbInitialScore;
    private Grid scorecardPropertiesGrid;

    public void onSave(SaveCommand saveCommand) {
        try {
            ScorecardModel scorecardModel = (ScorecardModel) asset.getContent();
            scorecardModel.setName(asset.getName());
            scorecardModel.setPackageName(asset.getMetaData().getModuleName());
            scorecardModel.setBaselineScore(Double.parseDouble(tbBaselineScore.getValue()));
            scorecardModel.setInitialScore(Double.parseDouble(tbInitialScore.getValue()));
            scorecardModel.setReasonCodesAlgorithm(ddReasonCodeAlgo.getValue(ddReasonCodeAlgo.getSelectedIndex()));
            scorecardModel.setUseReasonCodes(ddUseReasonCode.getSelectedIndex() == 1);

            EnumDropDown enumDropDown = (EnumDropDown) scorecardPropertiesGrid.getWidget(1, 0);
            String factName = enumDropDown.getValue(enumDropDown.getSelectedIndex());
            scorecardModel.setFactName(factName);
            if (sce.getModelFields().get(factName) != null){
                for (ModelField mf : sce.getModelFields().get(factName)) {
                    if (mf.getType().equals(factName)){
                        scorecardModel.setFactName(mf.getClassName());
                        break;
                    }
                }
            }

            enumDropDown = (EnumDropDown) scorecardPropertiesGrid.getWidget(1, 1);
            if (enumDropDown.getSelectedIndex() > -1) {
                String fieldName = enumDropDown.getValue(enumDropDown.getSelectedIndex());
                fieldName = fieldName.substring(0, fieldName.indexOf(":")).trim();
                scorecardModel.setFieldName(fieldName);
            } else {
                scorecardModel.setFieldName("");
            }

            if (ddReasonCodeField.getSelectedIndex() > -1) {
                String rcField = ddReasonCodeField.getValue(ddReasonCodeField.getSelectedIndex());
                rcField = rcField.substring(0, rcField.indexOf(":")).trim();
                scorecardModel.setReasonCodeField(rcField);
            }

            scorecardModel.getCharacteristics().clear();
            for (DirtyableFlexTable flexTable : characteristicsTables) {
                Characteristic characteristic = new Characteristic();
                characteristic.setName(((TextBox) flexTable.getWidget(0, 1)).getValue());

                enumDropDown = (EnumDropDown) flexTable.getWidget(2, 0);
                String simpleFactName = enumDropDown.getValue(enumDropDown.getSelectedIndex());
                if (sce.getModelFields().get(simpleFactName) != null){
                    for (ModelField mf : sce.getModelFields().get(simpleFactName)) {
                        ////System.out.println(">>>> "+mf.getType()+"  "+mf.getClassName()+"  "+mf.getName());
                        if (mf.getType().equals(simpleFactName)){
                            characteristic.setFact(mf.getClassName());
                            break;
                        }
                    }
                }
                enumDropDown = (EnumDropDown) flexTable.getWidget(2, 1);
                if (enumDropDown.getSelectedIndex() > -1) {
                    String fieldName = enumDropDown.getValue(enumDropDown.getSelectedIndex());
                    fieldName = fieldName.substring(0, fieldName.indexOf(":")).trim();
                    characteristic.setField(fieldName);
                } else {
                    characteristic.setField("");
                }

                characteristic.setReasonCode(((TextBox) flexTable.getWidget(2, 3)).getValue());

                String baselineScore = ((TextBox) flexTable.getWidget(2, 2)).getValue();
                try {
                    characteristic.setBaselineScore(Double.parseDouble(baselineScore));
                } catch (Exception e) {
                    characteristic.setBaselineScore(0.0d);
                }

                scorecardModel.getCharacteristics().add(characteristic);
                characteristic.setDataType(getDataTypeForField(simpleFactName, characteristic.getField()));
                System.out.println(">>>SetDataType -->"+simpleFactName+"<-->"+characteristic.getField()+"<-->"+characteristic.getDataType()+"<--");
                characteristic.getAttributes().clear();
                characteristic.getAttributes().addAll(characteristicsAttrMap.get(flexTable).getList());
            }
            saveCommand.save();
        } catch (Throwable t ) {
            t.printStackTrace();
        }
    }

    public void onAfterSave() {

    }

    public GuidedScorecardWidget(final Asset asset,
                                 final RuleViewer viewer,
                                 final ClientFactory clientFactory,
                                 final EventBus globalEventBus) {
        try {
            this.asset = asset;
            this.clientFactory = clientFactory;
            this.globalEventBus = globalEventBus;
            layout = new VerticalPanel();
            ScorecardModel scorecardModel = (ScorecardModel) asset.getContent();

            DecoratedDisclosurePanel disclosurePanel = new DecoratedDisclosurePanel("Scorecard " + " ( " + asset.getName() + " )");
            disclosurePanel.setWidth("100%");
            disclosurePanel.setTitle(Constants.INSTANCE.Scorecard());
            disclosurePanel.setOpen(true);

            DecoratedDisclosurePanel configPanel = new DecoratedDisclosurePanel("Setup Parameters");
            configPanel.setWidth("95%");
            configPanel.setOpen(true);
            configPanel.add(getScorecardProperties());
            VerticalPanel config = new VerticalPanel();

            DecoratedDisclosurePanel characteristicsPanel = new DecoratedDisclosurePanel("Characteristics");
            characteristicsPanel.setOpen(scorecardModel.getCharacteristics().size() > 0);
            characteristicsPanel.setWidth("95%");
            characteristicsPanel.add(getCharacteristics());

            config.setWidth("100%");
            config.add(configPanel);
            config.add(characteristicsPanel);

            disclosurePanel.add(config);
            layout.add(disclosurePanel);
            dtableContainer.setPixelSize(1000, 200);
            layout.add(dtableContainer);

            for (Characteristic characteristic : scorecardModel.getCharacteristics()) {
                DirtyableFlexTable flexTable = addCharacteristic(characteristic);
                for (Attribute attribute : characteristic.getAttributes()) {
                    addAttribute(flexTable, attribute);
                }
            }
            initWidget(layout);
        } catch(Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private Widget getScorecardProperties() {

        if (sce == null) {
            sce = SuggestionCompletionCache.getInstance().getEngineFromCache(asset.getMetaData().getModuleName());
            sceModelFields = sce.getModelFields();
        }

        final ScorecardModel scorecardModel = (ScorecardModel) asset.getContent();

        scorecardPropertiesGrid = new Grid(4, 4);
        scorecardPropertiesGrid.setCellSpacing(5);
        scorecardPropertiesGrid.setCellPadding(5);

        tbInitialScore = TextBoxFactory.getTextBox(SuggestionCompletionEngine.TYPE_NUMERIC_DOUBLE);
        tbInitialScore.setText(Double.toString(scorecardModel.getInitialScore()));

        String factName = scorecardModel.getFactName();
        if (factName.lastIndexOf(".") > -1){
            // if fact is a fully qualified className, strip off the packageName
            factName = factName.substring(factName.lastIndexOf(".")+1);
        }
        final EnumDropDown dropDownFields = new EnumDropDown("",
                new DropDownValueChanged() {
                    public void valueChanged(String newText,
                                             String newValue) {
                        //do nothing
                    }
                }, DropDownData.create(new String[]{}));

        EnumDropDown dropDownFacts = new EnumDropDown(factName,
                new DropDownValueChanged() {
                    public void valueChanged(String newText, String newValue) {
                        String selectedField = scorecardModel.getFieldName();
                        selectedField = selectedField+" : double";
                        dropDownFields.setDropDownData(selectedField, DropDownData.create(getEligibleFields(newValue, typesForScore)));
                    }
                }, DropDownData.create(sce.getFactTypes()));

        ddReasonCodeField = new EnumDropDown("",
                new DropDownValueChanged() {
                    public void valueChanged(String newText,
                                             String newValue) {
                        //do nothing
                    }
                }, DropDownData.create(new String[]{}));

        String rcField = scorecardModel.getReasonCodeField() +" : List";
        ddReasonCodeField.setDropDownData(rcField,DropDownData.create(getEligibleFields(factName,typesForRC)));

        boolean useReasonCodes = scorecardModel.isUseReasonCodes();
        String reasonCodesAlgo = scorecardModel.getReasonCodesAlgorithm();
        if (reasonCodesAlgo == null || reasonCodesAlgo.trim().length() == 0) {
            reasonCodesAlgo = "none";
        }

        ddUseReasonCode = booleanEditor(Boolean.toString(useReasonCodes));
        ddReasonCodeAlgo = dropDownEditor(DropDownData.create(REASON_CODE_ALGORITHMS), reasonCodesAlgo);
        tbBaselineScore = TextBoxFactory.getTextBox(SuggestionCompletionEngine.TYPE_NUMERIC_DOUBLE);

        scorecardPropertiesGrid.setText(0, 0, "Facts");
        scorecardPropertiesGrid.setText(0, 1, "Resultant Score Field");
        scorecardPropertiesGrid.setText(0, 2, "Initial Score");

        scorecardPropertiesGrid.setWidget(1, 0, dropDownFacts);
        scorecardPropertiesGrid.setWidget(1, 1, dropDownFields);
        scorecardPropertiesGrid.setWidget(1, 2, tbInitialScore);

        scorecardPropertiesGrid.setText(2, 0, "Use Reason Codes");
        scorecardPropertiesGrid.setText(2, 1, "Resultant Reason Codes Field");
        scorecardPropertiesGrid.setText(2, 2, "Reason Codes Algorithm");
        scorecardPropertiesGrid.setText(2, 3, "Baseline Score");

        scorecardPropertiesGrid.setWidget(3, 0, ddUseReasonCode);
        scorecardPropertiesGrid.setWidget(3, 1, ddReasonCodeField);
        scorecardPropertiesGrid.setWidget(3, 2, ddReasonCodeAlgo);
        scorecardPropertiesGrid.setWidget(3, 3, tbBaselineScore);

        /* TODO : Remove this explicitly Disabled Reasoncode support field*/
        ddUseReasonCode.setEnabled(false);

        tbBaselineScore.setText(Double.toString(scorecardModel.getBaselineScore()));

        scorecardPropertiesGrid.getCellFormatter().setWidth(0, 0, "200px");
        scorecardPropertiesGrid.getCellFormatter().setWidth(0, 1, "250px");
        scorecardPropertiesGrid.getCellFormatter().setWidth(0, 2, "200px");
        scorecardPropertiesGrid.getCellFormatter().setWidth(0, 3, "200px");

        int index = Arrays.asList(sce.getFactTypes()).indexOf(factName);
        dropDownFacts.setSelectedIndex(index);
        dropDownFields.setDropDownData(scorecardModel.getFieldName()+" : double", DropDownData.create(getEligibleFields(factName, typesForScore)));

        return scorecardPropertiesGrid;
    }

    private Widget getCharacteristics() {
        characteristicsPanel = new VerticalPanel();
        HorizontalPanel toolbar = new HorizontalPanel();
        btnAddCharacteristic = new Button("New Characteristic", new ClickHandler() {
            public void onClick(ClickEvent event) {
                addCharacteristic(null);
            }
        });
        toolbar.add(btnAddCharacteristic);

        toolbar.setHeight("24");
        characteristicsPanel.add(toolbar);
        SimplePanel gapPanel = new SimplePanel();
        gapPanel.add(new HTML("<br/>"));
        characteristicsPanel.add(gapPanel);
        return characteristicsPanel;
    }

    private void removeCharacteristic(DirtyableFlexTable selectedTable) {
        if (selectedTable != null) {
            TextBox tbName = (TextBox) selectedTable.getWidget(0, 1);
            String name = tbName.getValue();
            if (name == null || name.trim().length() == 0) {
                name = "Untitled";
            }
            String msg = "Are you sure you want to delete '" + (name) + "' Characteristic?";
            if (Window.confirm(msg)) {
                characteristicsTables.remove(selectedTable);
                characteristicsAttrMap.remove(selectedTable);
                Widget parent = selectedTable.getParent().getParent();
                int i = characteristicsPanel.getWidgetIndex(parent);
                characteristicsPanel.remove(parent);
                characteristicsPanel.remove(i);
            }
        }
    }

    private void addAttribute(final DirtyableFlexTable selectedTable, Attribute attribute) {
        Attribute newAttribute = null;
        if (attribute != null) {
            characteristicsAttrMap.get(selectedTable).getList().add(attribute);
        } else {
            newAttribute = new Attribute();
            characteristicsAttrMap.get(selectedTable).getList().add(newAttribute);
        }
        characteristicsAttrMap.get(selectedTable).refresh();

        //disable the fact & field dropdowns
        ((EnumDropDown) selectedTable.getWidget(2, 0)).setEnabled(false);
        ((EnumDropDown) selectedTable.getWidget(2, 1)).setEnabled(false);
        EnumDropDown edd = ((EnumDropDown) selectedTable.getWidget(2, 1));
        if (edd.getSelectedIndex() > -1) {
            String field = edd.getValue(edd.getSelectedIndex());
            field = field.substring(field.indexOf(":")+1).trim();
            CellTable<Attribute> cellTable = (CellTable<Attribute>) characteristicsAttrMap.get(selectedTable).getDataDisplays().iterator().next();
            DynamicSelectionCell dynamicSelectionCell = (DynamicSelectionCell) cellTable.getColumn(0).getCell();
            List<String> newOptions = null;
            if ("double".equalsIgnoreCase(field) || "int".equalsIgnoreCase(field)) {
                newOptions = Arrays.asList(numericOperators);
            } else if ("boolean".equalsIgnoreCase(field)) {
                newOptions = Arrays.asList(booleanOperators);
                CustomEditTextCell etc = (CustomEditTextCell)cellTable.getColumn(1).getCell();
                etc.setEnabled(false);
                ((Button)selectedTable.getWidget(0, 3)).setEnabled(characteristicsAttrMap.get(selectedTable).getList().size() != 2);
                if (newAttribute != null) {
                    newAttribute.setValue("N/A");
                }
            } else if ("String".equalsIgnoreCase(field)) {
                newOptions = Arrays.asList(stringOperators);
            }
            dynamicSelectionCell.setOptions(newOptions);
            if ( newAttribute != null) {
                if (newOptions != null) {
                    newAttribute.setOperator(newOptions.get(0));
                }
            }
        }
    }

    private DirtyableFlexTable addCharacteristic(final Characteristic characteristic) {
        final DirtyableFlexTable cGrid = new DirtyableFlexTable();
        cGrid.setBorderWidth(0);
        cGrid.setCellPadding(1);
        cGrid.setCellSpacing(1);

        cGrid.setStyleName("rule-ListHeader");

        Button btnAddAttribute = new Button("Add Attribute", new ClickHandler() {
            public void onClick(ClickEvent event) {
                addAttribute(cGrid, null);
            }
        });

        Button btnRemoveCharacteristic = new Button("Remove Characteristic", new ClickHandler() {
            public void onClick(ClickEvent event) {
                removeCharacteristic(cGrid);
            }
        });

        String selectedFact = "";
        if (characteristic != null) {
            selectedFact = characteristic.getFact();
            if (selectedFact.lastIndexOf(".") > -1) {
                selectedFact = selectedFact.substring(selectedFact.lastIndexOf(".")+1);
            }
        }
        final EnumDropDown dropDownFields = new EnumDropDown("",
                new DropDownValueChanged() {
                    public void valueChanged(String newText,
                                             String newValue) {
                        //do nothing
                    }
                }, DropDownData.create(new String[]{}));

        EnumDropDown dropDownFacts = new EnumDropDown(selectedFact,
                new DropDownValueChanged() {
                    public void valueChanged(String newText, String newValue) {
                        String selectedField = "";
                        if (characteristic != null) {
                            selectedField = characteristic.getField();
                            selectedField = selectedField+" : "+characteristic.getDataType();
                        }
                        dropDownFields.setDropDownData(selectedField, DropDownData.create(getEligibleFields(newValue, typesForAttributes)));
                        //dropDownFields.setSelectedIndex(0);
                    }
                }, DropDownData.create(sce.getFactTypes()));

        DropDownData dropDownData = DropDownData.create(getEligibleFields(selectedFact, typesForAttributes));
        dropDownFields.setDropDownData("", dropDownData);

        cGrid.setWidget(0, 0, new Label("Name"));
        final TextBox tbName = TextBoxFactory.getTextBox(SuggestionCompletionEngine.TYPE_STRING);
        cGrid.setWidget(0, 1, tbName);
        cGrid.setWidget(0, 2, btnRemoveCharacteristic);
        cGrid.setWidget(0, 3, btnAddAttribute);

        cGrid.setWidget(1, 0, new Label("Fact"));
        cGrid.setWidget(1, 1, new Label("Characteristic"));
        cGrid.setWidget(1, 2, new Label("Baseline Score"));
        cGrid.setWidget(1, 3, new Label("Reason Code"));

        cGrid.setWidget(2, 0, dropDownFacts);
        cGrid.setWidget(2, 1, dropDownFields);

        TextBox tbBaseline = TextBoxFactory.getTextBox(SuggestionCompletionEngine.TYPE_NUMERIC_DOUBLE);
        boolean useReasonCodesValue = "true".equalsIgnoreCase(ddUseReasonCode.getValue(ddUseReasonCode.getSelectedIndex()));
        tbBaseline.setEnabled(useReasonCodesValue);
        cGrid.setWidget(2, 2, tbBaseline);

        TextBox tbReasonCode = TextBoxFactory.getTextBox(SuggestionCompletionEngine.TYPE_STRING);
        tbReasonCode.setEnabled(useReasonCodesValue);
        cGrid.setWidget(2, 3, tbReasonCode);

        SimplePanel gapPanel = new SimplePanel();
        gapPanel.add(new HTML("<br/>"));

        VerticalPanel panel = new VerticalPanel();
        panel.add(cGrid);
        panel.add(addAttributeCellTable(cGrid, characteristic));
        panel.setWidth("100%");
        DecoratorPanel decoratorPanel = new DecoratorPanel();
        decoratorPanel.add(panel);
        characteristicsPanel.add(decoratorPanel);
        characteristicsPanel.add(gapPanel);

        characteristicsTables.add(cGrid);

        cGrid.getColumnFormatter().setWidth(0, "150px");
        cGrid.getColumnFormatter().setWidth(1, "250px");
        cGrid.getColumnFormatter().setWidth(2, "150px");
        cGrid.getColumnFormatter().setWidth(3, "150px");

        if (characteristic != null) {
            tbReasonCode.setValue(characteristic.getReasonCode());
            tbBaseline.setValue("" + characteristic.getBaselineScore());

            int index = Arrays.asList(sce.getFactTypes()).indexOf(selectedFact);
            dropDownFacts.setSelectedIndex(index);
            String modifiedFieldName = characteristic.getField()+" : "+characteristic.getDataType();
            dropDownFields.setSelectedIndex(Arrays.asList(getEligibleFields(selectedFact, typesForAttributes)).indexOf(modifiedFieldName));
            tbName.setValue(characteristic.getName());
        }

        return cGrid;
    }

    private Widget addAttributeCellTable(final DirtyableFlexTable cGrid, final Characteristic characteristic) {
        final CellTable<Attribute> attributeCellTable = new CellTable<Attribute>();

        List<String> operators = new ArrayList<String>();
        String dataType;
        if (characteristic == null) {
            dataType = "String";
        } else {
            dataType = characteristic.getDataType();
        }

        if ("String".equalsIgnoreCase(dataType)) {
            operators.addAll(Arrays.asList(stringOperators));
        } else if ("boolean".equalsIgnoreCase(dataType)) {
            operators.addAll(Arrays.asList(booleanOperators));
        } else {
            operators.addAll(Arrays.asList(numericOperators));
        }
        DynamicSelectionCell categoryCell = new DynamicSelectionCell(operators);
        Column<Attribute, String> operatorColumn = new Column<Attribute, String>(categoryCell) {
            public String getValue(Attribute object) {
                return object.getOperator();
            }
        };

        Column<Attribute, String> valueColumn = new Column<Attribute, String>(new CustomEditTextCell()) {
            public String getValue(Attribute attribute) {
                return attribute.getValue();
            }
        };
        final EditTextCell partialScoreCell = new EditTextCell();
        Column<Attribute, String> partialScoreColumn = new Column<Attribute, String>(partialScoreCell) {
            public String getValue(Attribute attribute) {
                return "" + attribute.getPartialScore();
            }
        };

        Column<Attribute, String> reasonCodeColumn = new Column<Attribute, String>(new EditTextCell()) {
            public String getValue(Attribute attribute) {
                return attribute.getReasonCode();
            }
        };

        ActionCell.Delegate<Attribute> delegate = new ActionCell.Delegate<Attribute>() {
            public void execute(Attribute attribute) {
                if (Window.confirm("Remove this attribute?")) {
                    List<Attribute> list = characteristicsAttrMap.get(cGrid).getList();
                    list.remove(attribute);
                    ((EnumDropDown) cGrid.getWidget(2, 0)).setEnabled(list.size() == 0);
                    ((EnumDropDown) cGrid.getWidget(2, 1)).setEnabled(list.size() == 0);
                    ((Button)cGrid.getWidget(0, 3)).setEnabled(list.size() != 2);
                    attributeCellTable.redraw();
                }
            }
        };
        Cell<Attribute> actionCell = new ActionCell<Attribute>("Remove",delegate);
        Column<Attribute, String> actionColumn = new IdentityColumn(actionCell);

        reasonCodeColumn.setFieldUpdater(new FieldUpdater<Attribute, String>() {
            public void update(int index, Attribute object, String value) {
                object.setReasonCode(value);
                attributeCellTable.redraw();
            }
        });
        operatorColumn.setFieldUpdater(new FieldUpdater<Attribute, String>() {
            public void update(int index, Attribute object, String value) {
                object.setOperator(value);
                attributeCellTable.redraw();
            }
        });
        valueColumn.setFieldUpdater(new FieldUpdater<Attribute, String>() {
            public void update(int index, Attribute object, String value) {
                object.setValue(value);
                attributeCellTable.redraw();
            }
        });
        partialScoreColumn.setFieldUpdater(new FieldUpdater<Attribute, String>() {
            public void update(int index, Attribute object, String value) {
                try {
                    double d = Double.parseDouble(value);
                    object.setPartialScore(d);
                } catch (Exception e1) {
                    partialScoreCell.clearViewData(object);
                }
                attributeCellTable.redraw();
            }
        });
        // Add the columns.
        attributeCellTable.addColumn(operatorColumn, "Operator");
        attributeCellTable.addColumn(valueColumn, "Value");
        attributeCellTable.addColumn(partialScoreColumn, "Partial Score");
        attributeCellTable.addColumn(reasonCodeColumn, "Reason Code");
        attributeCellTable.addColumn(actionColumn, "Actions");
        attributeCellTable.setWidth("100%", true);

        attributeCellTable.setColumnWidth(operatorColumn, 5.0, Style.Unit.PCT);
        attributeCellTable.setColumnWidth(valueColumn, 10.0, Style.Unit.PCT);
        attributeCellTable.setColumnWidth(partialScoreColumn, 10.0, Style.Unit.PCT);
        attributeCellTable.setColumnWidth(reasonCodeColumn, 10.0, Style.Unit.PCT);
        attributeCellTable.setColumnWidth(actionColumn, 5.0, Style.Unit.PCT);

        ListDataProvider<Attribute> dataProvider = new ListDataProvider<Attribute>();
        dataProvider.addDataDisplay(attributeCellTable);
        characteristicsAttrMap.put(cGrid, dataProvider);
        return (attributeCellTable);
    }

    private String[] getEligibleFields(String factName, String[] types) {
        List<String> fields = new ArrayList<String>();
        for (String clazz : sceModelFields.keySet()) {
            if (clazz.equalsIgnoreCase(factName)) {
                for (ModelField field : sceModelFields.get(clazz)) {
                    String type = field.getClassName();
                    if (type.lastIndexOf(".") > -1) {
                        type = type.substring(type.lastIndexOf(".") + 1);
                    }
                    for (String t : types) {
                        if (type.equalsIgnoreCase(t)) {
                            fields.add(field.getName() + " : " + type);
                            break;
                        }
                    }
                }
            }
        }
        return fields.toArray(new String[]{});
    }

    private String getDataTypeForField(String factName, String fieldName) {
        for (String clazz : sceModelFields.keySet()) {
            if (clazz.equalsIgnoreCase(factName)) {
                for (ModelField field : sceModelFields.get(clazz)) {
                    if (fieldName.equalsIgnoreCase(field.getName())) {
                        String type = field.getClassName();
                        if (type.endsWith("String")) {
                            type = "String";
                        } else if ( type.endsWith("Double")) {
                            type = "Double";
                        } else if (type.endsWith("Integer")) {
                            type = "int";
                        }
                        return type;
                    }
                }
            }
        }
        return null;
    }

    private EnumDropDown booleanEditor(String currentValue) {
        return new EnumDropDown(currentValue,
                new DropDownValueChanged() {
                    public void valueChanged(String newText,
                                             String newValue) {
                        boolean enabled = "true".equalsIgnoreCase(newValue);
                        ddReasonCodeAlgo.setEnabled(enabled);
                        tbBaselineScore.setEnabled(enabled);
                        ddReasonCodeField.setEnabled(enabled);
                        for (DirtyableFlexTable cGrid : characteristicsTables) {
                            //baseline score for each characteristic
                            ((TextBox) cGrid.getWidget(2, 2)).setEnabled(enabled);
                            //reason code for each characteristic
                            ((TextBox) cGrid.getWidget(2, 3)).setEnabled(enabled);
                        }
                    }
                },
                DropDownData.create(new String[]{"false", "true"}));
    }

    private EnumDropDown dropDownEditor(final DropDownData dropDownData, String currentValue) {
        return new EnumDropDown(currentValue,
                new DropDownValueChanged() {
                    public void valueChanged(String newText,
                                             String newValue) {
                        //valueHasChanged(newValue);
                    }
                },
                dropDownData);
    }
}
