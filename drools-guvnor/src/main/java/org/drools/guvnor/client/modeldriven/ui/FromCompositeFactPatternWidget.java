package org.drools.guvnor.client.modeldriven.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.FromCompositeFactPattern;
import org.drools.guvnor.client.messages.Constants;

/**
 *
 * @author esteban
 */
public class FromCompositeFactPatternWidget extends RuleModellerWidget {

    protected FromCompositeFactPattern pattern;
    protected DirtyableFlexTable layout;
    protected Constants constants = ((Constants) GWT.create(Constants.class));
    protected boolean readOnly;

    public FromCompositeFactPatternWidget(RuleModeller modeller,
            FromCompositeFactPattern pattern) {
        this(modeller, pattern, null);
    }

    public FromCompositeFactPatternWidget(RuleModeller modeller,
            FromCompositeFactPattern pattern, Boolean readOnly) {
        super(modeller);
        this.pattern = pattern;

        //if readOnly is null, the readOnly attribute is calculated.
        if (readOnly == null) {
            this.calculateReadOnly();
        }else{
            this.readOnly = readOnly;
        }


        this.layout = new DirtyableFlexTable();
        if (this.readOnly) {
            this.layout.addStyleName("editor-disabled-widget");
        }
        this.layout.addStyleName("model-builderInner-Background");

        doLayout();
        initWidget(layout);
    }

    protected void doLayout() {

        int r = 0;

        if (pattern.getFactPattern() != null) {
            FactPattern fact = pattern.getFactPattern();
            if (fact != null) {


                if (this.readOnly) {
                    //creates a new read-only FactPatternWidget
                    FactPatternWidget factPatternWidget = new FactPatternWidget(this.getModeller(), fact, false, true);
                    this.layout.setWidget(r,
                            0, factPatternWidget);
                } else {
                    FactPatternWidget factPatternWidget = new FactPatternWidget(this.getModeller(), fact, true,false);
                    this.layout.setWidget(r,
                            0,
                            addRemoveButton(factPatternWidget, new ClickListener() {

                        public void onClick(Widget w) {
                            if (Window.confirm(constants.RemoveThisEntireConditionQ())) {
                                pattern.setFactPattern(null);
                                getModeller().refreshWidget();
                            }
                        }
                    }));
                }
                r++;
            }
        }

        this.layout.setWidget(r,
                0,
                getCompositeLabel());

    }

    protected Widget getCompositeLabel() {

        ClickListener click = new ClickListener() {

            public void onClick(Widget w) {
                showFactTypeSelector(w);
            }
        };
        String lbl = "<div class='x-form-field'>" + HumanReadable.getCEDisplayName("from") + "</div>";

        DirtyableFlexTable panel = new DirtyableFlexTable();

        int r = 0;

        if (pattern.getFactPattern() == null) {
            panel.setWidget(r, 0, new ClickableLabel("<br> <font color='red'>" + constants.clickToAddPatterns() + "</font>", click, !this.readOnly));
            r++;
        }


        panel.setWidget(r, 0, new HTML(lbl));
        panel.setWidget(r, 1, new ExpressionBuilder(this.getModeller(), this.pattern.getExpression(), this.readOnly));


        return panel;
    }

    /**
     * Pops up the fact selector.
     */
    protected void showFactTypeSelector(final Widget w) {
        SuggestionCompletionEngine completions = this.getModeller().getSuggestionCompletions();
        final ListBox box = new ListBox();
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
                getModeller().refreshWidget();
                popup.hide();
            }
        });

        popup.show();
    }

    protected Widget addRemoveButton(Widget w, ClickListener listener) {
        DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();

        final Image remove = new ImageButton("images/delete_faded.gif"); //NON-NLS
        remove.setTitle(constants.RemoveThisBlockOfData());
        remove.addClickListener(listener);


        horiz.setWidth("100%");
        w.setWidth("100%");

        horiz.add(w);
        if (!this.readOnly) {
            horiz.add(remove);
        }
        return horiz;
    }

    public boolean isDirty() {
        return layout.hasDirty();
    }

    protected void calculateReadOnly() {
        if (this.pattern.factPattern != null) {
            this.readOnly = !this.getModeller().getSuggestionCompletions().containsFactType(this.pattern.factPattern.factType);
        }
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }
}
