package org.drools.guvnor.client.modeldriven.ui;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.FromAccumulateCompositeFactPattern;
import org.drools.guvnor.client.modeldriven.brl.FromCollectCompositeFactPattern;
import org.drools.guvnor.client.modeldriven.brl.FromCompositeFactPattern;
import org.drools.guvnor.client.modeldriven.brl.IPattern;

/**
 *
 * @author esteban
 */
public class FromCollectCompositeFactPatternWidget extends FromCompositeFactPatternWidget {

    public FromCollectCompositeFactPatternWidget(RuleModeller modeller,
            FromCollectCompositeFactPattern pattern) {
        super(modeller, pattern);
    }

    public FromCollectCompositeFactPatternWidget(RuleModeller modeller,
            FromCollectCompositeFactPattern pattern,Boolean readOnly) {
        super(modeller, pattern, readOnly);
    }

    @Override
    protected Widget getCompositeLabel() {

        ClickListener leftPatternclick = new ClickListener() {

            public void onClick(Widget w) {
                showFactTypeSelector(w);
            }
        };

        ClickListener rightPatternclick = new ClickListener() {

            public void onClick(Widget w) {
                showRightPatternSelector(w);
            }
        };


        String lbl = "<div class='x-form-field'>" + HumanReadable.getCEDisplayName("from collect") + "</div>";

        DirtyableFlexTable panel = new DirtyableFlexTable();

        int r = 0;

        if (pattern.getFactPattern() == null) {
            panel.setWidget(r++, 0, new ClickableLabel("<br> <font color='red'>" + constants.clickToAddPatterns() + "</font>", leftPatternclick, !this.readOnly));
        }


        panel.setWidget(r++, 0, new HTML(lbl));

        if (this.getFromCollectPattern().getRightPattern() == null) {
            panel.setWidget(r++, 0, new ClickableLabel("<br> <font color='red'>" + constants.clickToAddPatterns() + "</font>", rightPatternclick, !this.readOnly));
        } else {
            IPattern rPattern = this.getFromCollectPattern().getRightPattern();

            Widget patternWidget = null;
            if (rPattern instanceof FactPattern) {
                patternWidget = new FactPatternWidget(modeller, rPattern, constants.All0with(), true, this.readOnly);
            } else if (rPattern instanceof FromAccumulateCompositeFactPattern) {
                patternWidget = new FromAccumulateCompositeFactPatternWidget(modeller, (FromAccumulateCompositeFactPattern) rPattern,this.readOnly);
            } else if (rPattern instanceof FromCollectCompositeFactPattern) {
                patternWidget = new FromCollectCompositeFactPatternWidget(modeller, (FromCollectCompositeFactPattern) rPattern,this.readOnly);
            } else if (rPattern instanceof FromCompositeFactPattern) {
                patternWidget = new FromCompositeFactPatternWidget(modeller, (FromCompositeFactPattern) rPattern,this.readOnly);
            } else {
                throw new IllegalArgumentException("Unsuported pattern " + rPattern + " for right side of FROM COLLECT");
            }


            panel.setWidget(r++,
                    0,
                    addRemoveButton(patternWidget, new ClickListener() {

                public void onClick(Widget sender) {
                    if (Window.confirm(constants.RemoveThisBlockOfData())) {
                        getFromCollectPattern().setRightPattern(null);
                        modeller.refreshWidget();
                    }
                }
            }));
        }

        return panel;
    }

    @Override
    protected void showFactTypeSelector(final Widget w) {

        final FormStylePopup popup = new FormStylePopup();
        popup.setTitle(constants.NewFactPattern());

        final ListBox box = new ListBox();

        box.addItem(constants.Choose());

        box.addItem("java.util.ArrayList", "java.util.ArrayList");
        box.addItem("java.util.LinkedList", "java.util.LinkedArrayList");
        box.addItem("java.util.HashSet", "java.util.HashSet");
        box.addItem("java.util.LinkedHashSet", "java.util.LinkedHashSet");
        
        //TODO: Add Facts that extedns Collection
//        box.addItem("...");
//        box.addItem("TODO: Add Facts that extedns Collection");

        box.setSelectedIndex(0);

        box.addChangeListener(new ChangeListener() {

            public void onChange(Widget w) {
                pattern.setFactPattern(new FactPattern(box.getItemText(box.getSelectedIndex())));
                modeller.refreshWidget();
                popup.hide();
            }
        });

        popup.addAttribute(constants.chooseFactType(),
                box);

        popup.show();
    }

    /**
     * Pops up the fact selector.
     */
    protected void showRightPatternSelector(final Widget w) {
        final ListBox box = new ListBox();
        SuggestionCompletionEngine completions = modeller.getSuggestionCompletions();
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
                getFromCollectPattern().setRightPattern(new FactPattern(box.getItemText(box.getSelectedIndex())));
                modeller.refreshWidget();
                popup.hide();
            }
        });


        final Button fromBtn = new Button(constants.From());
        final Button fromAccumulateBtn = new Button(constants.FromAccumulate());
        final Button fromCollectBtn = new Button(constants.FromCollect());

        ClickListener btnsClickListener = new ClickListener() {

            public void onClick(Widget sender) {

                if (sender == fromBtn) {
                    getFromCollectPattern().setRightPattern(new FromCompositeFactPattern());
                } else if (sender == fromAccumulateBtn) {
                    getFromCollectPattern().setRightPattern(new FromAccumulateCompositeFactPattern());
                } else if (sender == fromCollectBtn) {
                    getFromCollectPattern().setRightPattern(new FromCollectCompositeFactPattern());
                } else {
                    throw new IllegalArgumentException("Unknown sender: " + sender);
                }

                modeller.refreshWidget();
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

    private FromCollectCompositeFactPattern getFromCollectPattern() {
        return (FromCollectCompositeFactPattern) this.pattern;
    }
}
