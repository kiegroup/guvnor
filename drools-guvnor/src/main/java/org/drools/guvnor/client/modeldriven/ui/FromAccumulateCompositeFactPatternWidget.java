package org.drools.guvnor.client.modeldriven.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromAccumulateCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromCollectCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.FromCompositeFactPattern;
import org.drools.ide.common.client.modeldriven.brl.IPattern;

/**
 *
 * @author esteban
 */
public class FromAccumulateCompositeFactPatternWidget extends FromCompositeFactPatternWidget {

    private RuleModellerWidget sourcePatternWidget;

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

        ClickListener leftPatternclick = new ClickListener() {

            public void onClick(Widget w) {
                showFactTypeSelector(w);
            }
        };

        ClickListener sourcePatternClick = new ClickListener() {

            public void onClick(Widget w) {
                showSourcePatternSelector(w);
            }
        };


        String lbl = "<div class='x-form-field'>" + HumanReadable.getCEDisplayName("from accumulate") + "</div>";

        DirtyableFlexTable panel = new DirtyableFlexTable();


        int r = 0;

        if (pattern.getFactPattern() == null) {
            panel.setWidget(r++, 0, new ClickableLabel("<br> <font color='red'>" + constants.clickToAddPattern() + "</font>", leftPatternclick, !this.readOnly));
        }


        panel.setWidget(r++, 0, new HTML(lbl));

        if (this.getFromAccumulatePattern().getSourcePattern() == null) {
            panel.setWidget(r++, 0, new ClickableLabel("<br> <font color='red'>" + constants.clickToAddPattern() + "</font>", sourcePatternClick, !this.readOnly));
        } else {
            IPattern rPattern = this.getFromAccumulatePattern().getSourcePattern();

            if (rPattern instanceof FactPattern) {
                this.sourcePatternWidget = new FactPatternWidget(this.getModeller(), rPattern, constants.All0with(), true,this.readOnly);
            } else if (rPattern instanceof FromAccumulateCompositeFactPattern) {
                this.sourcePatternWidget = new FromAccumulateCompositeFactPatternWidget(this.getModeller(), (FromAccumulateCompositeFactPattern) rPattern,this.readOnly);
            } else if (rPattern instanceof FromCollectCompositeFactPattern) {
                this.sourcePatternWidget = new FromCollectCompositeFactPatternWidget(this.getModeller(), (FromCollectCompositeFactPattern) rPattern,this.readOnly);
            } else if (rPattern instanceof FromCompositeFactPattern) {
                this.sourcePatternWidget = new FromCompositeFactPatternWidget(this.getModeller(), (FromCompositeFactPattern) rPattern,this.readOnly);
            } else {
                throw new IllegalArgumentException("Unsuported pattern " + rPattern + " for right side of FROM ACCUMULATE");
            }

            this.sourcePatternWidget.addOnModifiedCommand(new Command() {
                public void execute() {
                    setModified(true);
                }
            });

            panel.setWidget(r++,
                    0,
                    addRemoveButton(this.sourcePatternWidget, new ClickListener() {

                public void onClick(Widget sender) {
                    if (Window.confirm(constants.RemoveThisBlockOfData())) {
                        setModified(true);
                        getFromAccumulatePattern().setSourcePattern(null);
                        getModeller().refreshWidget();
                    }
                }
            }));
        }

        TabPanel tPanel = new TabPanel();

        DirtyableFlexTable codeTable = new DirtyableFlexTable();
        int codeTableRow = 0;
        int codeTableCol = 0;

        codeTable.setWidget(codeTableRow, codeTableCol++, new HTML("<div class='x-form-field'>Init:</div>"));
        final TextBox initField = new TextBox();
        initField.setTitle("init code");
        initField.setText(getFromAccumulatePattern().getInitCode());
        codeTable.setWidget(codeTableRow++, codeTableCol--, initField);

        codeTable.setWidget(codeTableRow, codeTableCol++, new HTML("<div class='x-form-field'>Action:</div>"));
        final TextBox actionField = new TextBox();
        actionField.setTitle("action code");
        actionField.setText(getFromAccumulatePattern().getActionCode());
        codeTable.setWidget(codeTableRow++, codeTableCol--, actionField);

        codeTable.setWidget(codeTableRow, codeTableCol++, new HTML("<div class='x-form-field'>Reverse:</div>"));
        final TextBox reverseField = new TextBox();
        reverseField.setTitle("reverse code.");
        reverseField.setText(getFromAccumulatePattern().getReverseCode());
        codeTable.setWidget(codeTableRow++, codeTableCol--, reverseField);

        codeTable.setWidget(codeTableRow, codeTableCol++, new HTML("<div class='x-form-field'>Result:</div>"));
        final TextBox resultField = new TextBox();
        resultField.setTitle("result code");
        resultField.setText(getFromAccumulatePattern().getResultCode());
        codeTable.setWidget(codeTableRow++, codeTableCol--, resultField);


        //panel.setWidget(r++, 0, codeTable);
        Panel codePanel = new Panel();
        codePanel.setAutoWidth(true);
        codePanel.setClosable(false);
        codePanel.setTitle("Custom Code");
        codePanel.setAutoHeight(true);
        codePanel.add(codeTable);
        codePanel.setDisabled(this.readOnly);
        tPanel.add(codePanel);

        DirtyableFlexTable functionTable = new DirtyableFlexTable();

        functionTable.setWidget(0, 0, new HTML("<div class='x-form-field'>Function:</div>"));
        final TextBox functionField = new TextBox();
        functionField.setTitle("function code");
        functionField.setText(getFromAccumulatePattern().getFunction());
        functionTable.setWidget(0, 1, functionField);

//        panel.setWidget(r++, 0, functionTable);

        Panel functionPanel = new Panel();
        functionPanel.setAutoWidth(true);
        functionPanel.setClosable(false);
        functionPanel.setTitle("Function");
        functionPanel.setAutoHeight(true);
        functionPanel.add(functionTable);
        functionPanel.setDisabled(this.readOnly);
        tPanel.add(functionPanel);

        ChangeListener changeListener = new ChangeListener() {

            public void onChange(Widget sender) {

                TextBox senderTB = (TextBox) sender;
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

        initField.addChangeListener(changeListener);
        actionField.addChangeListener(changeListener);
        reverseField.addChangeListener(changeListener);
        resultField.addChangeListener(changeListener);
        functionField.addChangeListener(changeListener);


        boolean useFunction = getFromAccumulatePattern().useFunctionOrCode().equals(FromAccumulateCompositeFactPattern.USE_FUNCTION);
        
        tPanel.setActiveTab(useFunction?1:0);

        tPanel.setBorder(false);
        tPanel.setBodyBorder(false);
        tPanel.setWidth(200);

        tPanel.setDisabled(this.readOnly);

//        functionTable.setVisible(useFunction);
//        codeTable.setVisible(!useFunction);

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

        box.addChangeListener(new ChangeListener() {

            public void onChange(Widget w) {
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

        box.addChangeListener(new ChangeListener() {

            public void onChange(Widget w) {
                getFromAccumulatePattern().setSourcePattern(new FactPattern(box.getItemText(box.getSelectedIndex())));
                setModified(true);
                getModeller().refreshWidget();
                popup.hide();
            }
        });




        final Button fromBtn = new Button(constants.From());
        final Button fromAccumulateBtn = new Button(constants.FromAccumulate());
        final Button fromCollectBtn = new Button(constants.FromCollect());

        ClickListener btnsClickListener = new ClickListener() {

            public void onClick(Widget sender) {

                if (sender == fromBtn) {
                    getFromAccumulatePattern().setSourcePattern(new FromCompositeFactPattern());
                } else if (sender == fromAccumulateBtn) {
                    getFromAccumulatePattern().setSourcePattern(new FromAccumulateCompositeFactPattern());
                } else if (sender == fromCollectBtn) {
                    getFromAccumulatePattern().setSourcePattern(new FromCollectCompositeFactPattern());
                } else {
                    throw new IllegalArgumentException("Unknown sender: " + sender);
                }

                setModified(true);
                getModeller().refreshWidget();
                popup.hide();
            }
        };

        fromBtn.addClickListener(btnsClickListener);
        fromAccumulateBtn.addClickListener(btnsClickListener);
        fromCollectBtn.addClickListener(btnsClickListener);

        popup.addAttribute("", fromBtn);
        popup.addAttribute("", fromAccumulateBtn);
        popup.addAttribute("", fromCollectBtn);

        popup.show();
    }

    private FromAccumulateCompositeFactPattern getFromAccumulatePattern() {
        return (FromAccumulateCompositeFactPattern) this.pattern;
    }


}
