package org.drools.guvnor.client.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.testing.Scenario;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nheron
 * Date: 7 nov. 2009
 * Time: 19:33:37
 * To change this template use File | Settings | File Templates.
 */
public class ConfigWidget extends Composite {
    private final Constants constants = ((Constants) GWT.create(Constants.class));

    public ConfigWidget(final Scenario sc, final String packageName, final ScenarioWidget scWidget) {

        final ListBox box = new ListBox(true);

        for (int i = 0; i < sc.rules.size(); i++) {
            box.addItem((String)sc.rules.get(i));
        }
        HorizontalPanel filter = new HorizontalPanel();


        final Image add = new ImageButton("images/new_item.gif", constants.AddANewRule());
        add.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                showRulePopup(w, box, packageName, sc.rules, scWidget);
            }
        });

        final Image remove = new ImageButton("images/trash.gif", constants.RemoveSelectedRule());
        remove.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                if (box.getSelectedIndex() == -1) {
                    Window.alert(constants.PleaseChooseARuleToRemove());
                } else {
                    String r = box.getItemText(box.getSelectedIndex());
                    sc.rules.remove(r);
                    box.removeItem(box.getSelectedIndex());
                }
            }
        });
        VerticalPanel actions = new VerticalPanel();
        actions.add(add); actions.add(remove);




        final ListBox drop = new ListBox();
        drop.addItem(constants.AllowTheseRulesToFire(), "inc"); //NON-NLS
        drop.addItem(constants.PreventTheseRulesFromFiring(), "exc");    //NON-NLS
        drop.addItem(constants.AllRulesMayFire());
        drop.addChangeListener(new ChangeListener() {
            public void onChange(Widget w) {
                String s = drop.getValue(drop.getSelectedIndex());
                if (s.equals("inc")) {   //NON-NLS
                    sc.inclusive = true;
                    add.setVisible(true); remove.setVisible(true); box.setVisible(true);
                } else if (s.equals("exc")) {     //NON-NLS
                    sc.inclusive = false;
                    add.setVisible(true); remove.setVisible(true); box.setVisible(true);
                } else {
                    sc.rules.clear();
                    box.clear();
                    box.setVisible(false); add.setVisible(false); remove.setVisible(false);
                }
            }
        });

        if (sc.rules.size() > 0) {
        	drop.setSelectedIndex((sc.inclusive) ? 0 : 1);
        } else {
        	drop.setSelectedIndex(2);
        	box.setVisible(false); add.setVisible(false); remove.setVisible(false);
        }


        filter.add(drop);
        filter.add(box);
        filter.add(actions);

        initWidget(filter);
    }

    private void showRulePopup(Widget w, final ListBox box, String packageName, final List filterList, ScenarioWidget scw) {
        final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", constants.SelectRule()); //NON-NLS

        Widget ruleSelector = scw.getRuleSelectionWidget(packageName, new RuleSelectionEvent() {
			public void ruleSelected(String r) {
                filterList.add(r);
                box.addItem(r);
                pop.hide();

			}
        });

        pop.addRow(ruleSelector);

        pop.show();

    }


}

