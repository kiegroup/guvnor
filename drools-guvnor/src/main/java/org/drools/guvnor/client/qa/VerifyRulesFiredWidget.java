package org.drools.guvnor.client.qa;

import java.util.List;

import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.VerifyRuleFired;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.util.Format;

/**
 * Created by IntelliJ IDEA.
 * User: nheron
 * Date: 7 nov. 2009
 * Time: 19:30:04
 * To change this template use File | Settings | File Templates.
 */
public class VerifyRulesFiredWidget extends Composite {
    private Grid outer;
    private boolean showResults;
    private Constants constants = ((Constants) GWT.create(Constants.class));

    /**
     * @param rfl List<VeryfyRuleFired>
     * @param scenario = the scenario to add/remove from
     */
    public VerifyRulesFiredWidget(final List rfl, final Scenario scenario, boolean showResults) {
        outer = new Grid(2, 1);
        this.showResults = showResults;
        outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader"); //NON-NLS
        outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName("modeller-fact-pattern-Widget");    //NON-NLS

        outer.setWidget(0, 0, new SmallLabel(constants.ExpectRules()));
        initWidget(outer);

        FlexTable data = render(rfl, scenario);
        outer.setWidget(1, 0, data);
    }



	private FlexTable render(final List rfl, final Scenario sc) {
		FlexTable data = new DirtyableFlexTable();


        for (int i = 0; i < rfl.size(); i++) {
            final VerifyRuleFired v = (VerifyRuleFired) rfl.get(i);

            if (showResults && v.successResult != null) {
            	if (!v.successResult.booleanValue()) {
            		data.setWidget(i, 0, new Image("images/warning.gif")); //NON-NLS
                    data.setWidget(i, 4, new HTML(Format.format(constants.ActualResult(), v.actualResult)));

            		data.getCellFormatter().addStyleName(i, 4, "testErrorValue");   //NON-NLS

            	} else {
            		data.setWidget(i, 0, new Image("images/test_passed.png"));     //NON-NLS
            	}

            }
            data.setWidget(i, 1, new SmallLabel(v.ruleName + ":"));
            data.getFlexCellFormatter().setAlignment(i, 1, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);


            final ListBox b = new ListBox();
            b.addItem(constants.firedAtLeastOnce(), "y");
            b.addItem(constants.didNotFire(), "n");
            b.addItem(constants.firedThisManyTimes(), "e");
            final TextBox num = new TextBox();
            num.setVisibleLength(5);

            if (v.expectedFire != null ) {
                b.setSelectedIndex((v.expectedFire.booleanValue()) ? 0 : 1);
                num.setVisible(false);
            } else {
                b.setSelectedIndex(2);
                String xc = (v.expectedCount != null)? "" + v.expectedCount.intValue() : "0";
                num.setText(xc);
            }

            b.addChangeListener(new ChangeListener() {
                public void onChange(Widget w) {
                    String s = b.getValue(b.getSelectedIndex());
                    if (s.equals("y") || s.equals("n")) {
                        num.setVisible(false);
                        v.expectedFire = (s.equals("y")) ? Boolean.TRUE : Boolean.FALSE;
                        v.expectedCount = null;
                    } else {
                        num.setVisible(true);
                        v.expectedFire = null;
                        num.setText("1"); v.expectedCount = new Integer(1);
                    }
                }
            });

            b.addItem(constants.ChooseDotDotDot());

            num.addChangeListener(new ChangeListener() {
                public void onChange(Widget w) {
                    v.expectedCount = new Integer(num.getText());
                }
            });

            HorizontalPanel h = new HorizontalPanel();
            h.add(b); h.add(num);
            data.setWidget(i, 2, h);

            Image del = new ImageButton("images/delete_item_small.gif", constants.RemoveThisRuleExpectation(), new ClickListener() {
				public void onClick(Widget w) {
					if (Window.confirm(constants.AreYouSureYouWantToRemoveThisRuleExpectation())) {
						rfl.remove(v);
						sc.removeFixture(v);
						outer.setWidget(1, 0, render(rfl, sc));
					}
				}
			});

            data.setWidget(i, 3, del);



            //we only want numbers here...
            num.addKeyboardListener(new KeyboardListener() {
                    public void onKeyDown(Widget arg0, char arg1, int arg2) {}
                    public void onKeyPress(Widget w, char c, int i) {
                        if (Character.isLetter( c ) ) {
                            ((TextBox) w).cancelKey();
                        }
                    }
                    public void onKeyUp(Widget arg0, char arg1, int arg2) {}
                } );
        }
		return data;
	}
}

