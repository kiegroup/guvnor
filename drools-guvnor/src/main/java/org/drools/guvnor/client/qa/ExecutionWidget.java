package org.drools.guvnor.client.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import com.gwtext.client.util.Format;
import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.testing.ExecutionTrace;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: nheron
 * Date: 7 nov. 2009
 * Time: 19:32:35
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionWidget extends Composite {
    private Constants constants = ((Constants) GWT.create(Constants.class));

    public ExecutionWidget(final ExecutionTrace ext, boolean showResults) {


    	final Widget dt = simulDate(ext);
    	dt.setVisible(ext.scenarioSimulatedDate != null);

    	final ListBox choice = new ListBox();

        choice.addItem(constants.UseRealDateAndTime());
    	choice.addItem(constants.UseASimulatedDateAndTime());
    	choice.setSelectedIndex((ext.scenarioSimulatedDate == null) ? 0 : 1);
    	choice.addChangeListener(new ChangeListener() {
			public void onChange(Widget w) {
				if (choice.getSelectedIndex() == 0) {
					dt.setVisible( false );
					ext.scenarioSimulatedDate = null;
				} else {
					dt.setVisible(true);
				}
			}
		});

    	HorizontalPanel p = new HorizontalPanel();
    	p.add(new Image("images/execution_trace.gif"));   //NON-NLS
    	p.add(choice);
    	p.add(dt);

    	VerticalPanel vert = new VerticalPanel();
    	if (showResults && ext.executionTimeResult != null
    			&& ext.numberOfRulesFired != null) {
            HTML rep = new HTML("<i><small>" + Format.format(constants.property0RulesFiredIn1Ms(), ext.numberOfRulesFired.toString(), ext.executionTimeResult.toString()) + "</small></i>");


    		final HorizontalPanel h = new HorizontalPanel();
    		h.add(rep);
    		vert.add(h);

    		final Button show = new Button(constants.ShowRulesFired());
    		show.addClickListener(new ClickListener() {
				public void onClick(Widget w) {
					ListBox rules = new ListBox(true);
					for (int i = 0; i < ext.rulesFired.length; i++) {
						rules.addItem(ext.rulesFired[i]);
					}
					h.add(new SmallLabel("&nbsp:" + constants.RulesFired()));
					h.add(rules);
					show.setVisible(false);
				}
    		});
    		h.add(show);


    		vert.add(p);
    		initWidget(vert);
    	} else {
    		initWidget(p);
    	}
    }



    private Widget simulDate(final ExecutionTrace ext) {
    	HorizontalPanel ab = new HorizontalPanel();
        final String fmt = "dd-MMM-YYYY"; //NON-NLS
        final TextBox dt = new TextBox();
        if (ext.scenarioSimulatedDate == null) {
            dt.setText("<" + fmt + ">");
        } else {
            dt.setText(ext.scenarioSimulatedDate.toLocaleString());
        }
        final SmallLabel dateHint = new SmallLabel();
        dt.addKeyboardListener(new KeyboardListener() {
			public void onKeyDown(Widget arg0, char arg1, int arg2) {}
			public void onKeyPress(Widget arg0, char arg1, int arg2) {}
			public void onKeyUp(Widget w, char arg1, int arg2) {
				try {
					Date d = new Date(dt.getText());
					dateHint.setText(d.toLocaleString());
				} catch (Exception e) {
					dateHint.setText("...");
				}
			}
        });

        dt.addChangeListener(new ChangeListener() {
            public void onChange(Widget w) {
                if (dt.getText().trim().equals("")) {
                    dt.setText(constants.currentDateAndTime());
                } else {
                    try {
                        Date d = new Date(dt.getText());
                        ext.scenarioSimulatedDate = d;
                        dt.setText(d.toLocaleString());
                        dateHint.setText("");
                    } catch (Exception e) {
                        ErrorPopup.showMessage(Format.format(constants.BadDateFormatPleaseTryAgainTryTheFormatOf0(), fmt));
                    }
                }
            }
        });
        ab.add(dt);
        ab.add(dateHint);
        return ab;
    }


}

