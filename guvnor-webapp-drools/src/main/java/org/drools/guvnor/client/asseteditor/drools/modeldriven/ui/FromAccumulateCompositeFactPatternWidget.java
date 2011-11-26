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

package org.drools.guvnor.client.asseteditor.drools.modeldriven.ui;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import org.drools.guvnor.client.asseteditor.drools.modeldriven.HumanReadable;
import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.*;

public class FromAccumulateCompositeFactPatternWidget extends FromCompositeFactPatternWidget {

    public FromAccumulateCompositeFactPatternWidget(RuleModeller modeller,
                                                    FromAccumulateCompositeFactPattern pattern, Boolean readOnly) {
        super(modeller, pattern, readOnly);
    }

    public FromAccumulateCompositeFactPatternWidget(RuleModeller modeller,
                                                    FromAccumulateCompositeFactPattern pattern) {
        super(modeller, pattern);
    }

    @Override
    protected Widget getCompositeLabel() {
        ClickHandler leftPatternclick = new ClickHandler() {

            public void onClick(ClickEvent event) {
                Widget w = (Widget) event.getSource();
                showFactTypeSelector(w);
            }
        };
        ClickHandler sourcePatternClick = new ClickHandler() {

            public void onClick(ClickEvent event) {
                Widget w = (Widget) event.getSource();
                showSourcePatternSelector(w);
            }
        };

        String lbl = "<div class='form-field'>" + HumanReadable.getCEDisplayName("from accumulate") + "</div>";

        DirtyableFlexTable panel = new DirtyableFlexTable();


        int r = 0;

        if (pattern.getFactPattern() == null) {
            panel.setWidget(r++, 0, new ClickableLabel("<br> <font color='red'>" + constants.clickToAddPattern() + "</font>", leftPatternclick, !this.readOnly));
        }


        panel.setWidget(r++, 0, new HTML(lbl));

        if (this.getFromAccumulatePattern().getSourcePattern() == null) {
            panel.setWidget(r++, 0, new ClickableLabel("<br> <font color='red'>" + constants.clickToAddPattern() + "</font>", sourcePatternClick, !this.readOnly));
        } else {
            IPattern rPattern = this.getFromAccumulatePattern()
                    .getSourcePattern();

            RuleModellerWidget sourcePatternWidget;
            if (rPattern instanceof FactPattern) {
                sourcePatternWidget = new FactPatternWidget(
                        this.getModeller(), rPattern, true,
                        true, this.readOnly);
            } else if (rPattern instanceof FromAccumulateCompositeFactPattern) {
                sourcePatternWidget = new FromAccumulateCompositeFactPatternWidget(
                        this.getModeller(),
                        (FromAccumulateCompositeFactPattern) rPattern,
                        this.readOnly);
            } else if (rPattern instanceof FromCollectCompositeFactPattern) {
                sourcePatternWidget = new FromCollectCompositeFactPatternWidget(
                        this.getModeller(),
                        (FromCollectCompositeFactPattern) rPattern,
                        this.readOnly);
            } else if (rPattern instanceof FromCompositeFactPattern) {
                sourcePatternWidget = new FromCompositeFactPatternWidget(
                        this.getModeller(),
                        (FromCompositeFactPattern) rPattern, this.readOnly);
            } else {
                throw new IllegalArgumentException("Unsupported pattern "
                        + rPattern + " for right side of FROM ACCUMULATE");
            }

            sourcePatternWidget.addOnModifiedCommand(new Command() {
                public void execute() {
                    setModified(true);
                }
            });

            panel.setWidget(
                    r++,
                    0,
                    addRemoveButton(sourcePatternWidget,
                            new ClickHandler() {

                                public void onClick(ClickEvent event) {
                                    if (Window.confirm(constants
                                            .RemoveThisBlockOfData())) {
                                        setModified(true);
                                        getFromAccumulatePattern()
                                                .setSourcePattern(null);
                                        getModeller().refreshWidget();
                                    }

                                }
                            }));
        }

        //REVISIT: Nested TabLayoutPanel does not work, its content is truncated. 
        //TabLayoutPanel tPanel = new TabLayoutPanel(2, Unit.EM);
        TabPanel tPanel = new TabPanel();

        DirtyableFlexTable codeTable = new DirtyableFlexTable();
        int codeTableRow = 0;
        int codeTableCol = 0;

        codeTable.setWidget(codeTableRow, codeTableCol++, new HTML("<div class='form-field'>Init:</div>"));

        final TextBox initField = new TextBox();
        initField.setTitle("init code");
        initField.setText(getFromAccumulatePattern().getInitCode());
        initField.setEnabled(!this.readOnly);
        codeTable.setWidget(codeTableRow++, codeTableCol--, initField);

        codeTable.setWidget(codeTableRow, codeTableCol++, new HTML("<div class='form-field'>Action:</div>"));
        final TextBox actionField = new TextBox();
        actionField.setTitle("action code");
        actionField.setText(getFromAccumulatePattern().getActionCode());
        actionField.setEnabled(!this.readOnly);
        codeTable.setWidget(codeTableRow++, codeTableCol--, actionField);

        codeTable.setWidget(codeTableRow, codeTableCol++, new HTML("<div class='form-field'>Reverse:</div>"));
        final TextBox reverseField = new TextBox();
        reverseField.setTitle("reverse code.");
        reverseField.setText(getFromAccumulatePattern().getReverseCode());
        reverseField.setEnabled(!this.readOnly);
        codeTable.setWidget(codeTableRow++, codeTableCol--, reverseField);

        codeTable.setWidget(codeTableRow, codeTableCol++, new HTML("<div class='form-field'>Result:</div>"));
        final TextBox resultField = new TextBox();
        resultField.setTitle("result code");
        resultField.setText(getFromAccumulatePattern().getResultCode());
        resultField.setEnabled(!this.readOnly);
        codeTable.setWidget(codeTableRow++, codeTableCol--, resultField);


        //panel.setWidget(r++, 0, codeTable);
        ScrollPanel codePanel = new ScrollPanel();
        codePanel.add(codeTable);

        tPanel.add(codePanel, "Custom Code");

        DirtyableFlexTable functionTable = new DirtyableFlexTable();

        functionTable.setWidget(0, 0, new HTML("<div class='form-field'>Function:</div>"));
        final TextBox functionField = new TextBox();
        functionField.setTitle("function code");
        functionField.setText(getFromAccumulatePattern().getFunction());
        functionField.setEnabled(!this.readOnly);
        functionTable.setWidget(0, 1, functionField);

//        panel.setWidget(r++, 0, functionTable);

        ScrollPanel functionPanel = new ScrollPanel();
        functionPanel.add(functionTable);


        tPanel.add(functionPanel, "Function");
        ChangeHandler changehandler = new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                Widget sender = (Widget) event.getSource();
                TextBox senderTB = (TextBox) event.getSource();
                String code = senderTB.getText();
                setModified(true);
                if (sender == initField) {
                    getFromAccumulatePattern().setFunction(null);
                    functionField.setText("");
                    getFromAccumulatePattern().setInitCode(code);
                } else if (sender == actionField) {
                    getFromAccumulatePattern().setFunction(null);
                    functionField.setText("");
                    getFromAccumulatePattern().setActionCode(code);
                } else if (sender == reverseField) {
                    getFromAccumulatePattern().setFunction(null);
                    functionField.setText("");
                    getFromAccumulatePattern().setReverseCode(code);
                } else if (sender == resultField) {
                    getFromAccumulatePattern().setFunction(null);
                    functionField.setText("");
                    getFromAccumulatePattern().setResultCode(code);
                } else if (sender == functionField) {
                    getFromAccumulatePattern().clearCodeFields();
                    initField.setText("");
                    actionField.setText("");
                    reverseField.setText("");
                    resultField.setText("");
                    getFromAccumulatePattern().setFunction(code);
                }
            }
        };

        initField.addChangeHandler(changehandler);
        actionField.addChangeHandler(changehandler);
        reverseField.addChangeHandler(changehandler);
        resultField.addChangeHandler(changehandler);
        functionField.addChangeHandler(changehandler);


        boolean useFunction = getFromAccumulatePattern().useFunctionOrCode().equals(FromAccumulateCompositeFactPattern.USE_FUNCTION);

        tPanel.selectTab(useFunction ? 1 : 0);

        panel.setWidget(r++, 0, tPanel);

        return panel;
    }

    /**
     * Pops up the fact selector.
     */
    @Override
    protected void showFactTypeSelector(final Widget w) {
        final ListBox box = new ListBox();
        SuggestionCompletionEngine completions = this.getModeller().getSuggestionCompletions();
        String[] facts = completions.getFactTypes();

        box.addItem(constants.Choose());

        for (int i = 0; i < facts.length; i++) {
            box.addItem(facts[i]);
        }
        box.setSelectedIndex(0);

        final FormStylePopup popup = new FormStylePopup();
        popup.setTitle(constants.NewFactPattern());
        popup.addAttribute(constants.chooseFactType(),
                box);
        box.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                pattern.setFactPattern(new FactPattern(box.getItemText(box.getSelectedIndex())));
                setModified(true);
                getModeller().refreshWidget();
                popup.hide();
            }
        });
        popup.show();
    }

    /**
     * Pops up the fact selector.
     */
    protected void showSourcePatternSelector(final Widget w) {
        final ListBox box = new ListBox();
        SuggestionCompletionEngine completions = this.getModeller().getSuggestionCompletions();
        String[] facts = completions.getFactTypes();

        box.addItem(constants.Choose());
        for (int i = 0; i < facts.length; i++) {
            box.addItem(facts[i]);
        }
        box.setSelectedIndex(0);

        final FormStylePopup popup = new FormStylePopup();
        popup.setTitle(constants.NewFactPattern());
        popup.addAttribute(constants.chooseFactType(),
                box);
        box.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                getFromAccumulatePattern().setSourcePattern(new FactPattern(box.getItemText(box.getSelectedIndex())));
                setModified(true);
                getModeller().refreshWidget();
                popup.hide();
            }
        });

        final Button fromBtn = new Button(constants.From());
        final Button fromAccumulateBtn = new Button(constants.FromAccumulate());
        final Button fromCollectBtn = new Button(constants.FromCollect());
        ClickHandler btnsClickHandler = new ClickHandler() {

            public void onClick(ClickEvent event) {
                Widget sender = (Widget) event.getSource();
                if (sender == fromBtn) {
                    getFromAccumulatePattern().setSourcePattern(
                            new FromCompositeFactPattern());
                } else if (sender == fromAccumulateBtn) {
                    getFromAccumulatePattern().setSourcePattern(
                            new FromAccumulateCompositeFactPattern());
                } else if (sender == fromCollectBtn) {
                    getFromAccumulatePattern().setSourcePattern(
                            new FromCollectCompositeFactPattern());
                } else {
                    throw new IllegalArgumentException("Unknown sender: "
                            + sender);
                }

                setModified(true);
                getModeller().refreshWidget();
                popup.hide();

            }
        };

        fromBtn.addClickHandler(btnsClickHandler);
        fromAccumulateBtn.addClickHandler(btnsClickHandler);
        fromCollectBtn.addClickHandler(btnsClickHandler);
        popup.addAttribute("", fromBtn);
        popup.addAttribute("", fromAccumulateBtn);
        popup.addAttribute("", fromCollectBtn);

        popup.show();
    }

    private FromAccumulateCompositeFactPattern getFromAccumulatePattern() {
        return (FromAccumulateCompositeFactPattern) this.pattern;
    }


}
