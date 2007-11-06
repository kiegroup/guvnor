package org.drools.brms.client.qa;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.brms.client.common.DirtyableFlexTable;
import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.modeldriven.testing.ExecutionTrace;
import org.drools.brms.client.modeldriven.testing.FactData;
import org.drools.brms.client.modeldriven.testing.FieldData;
import org.drools.brms.client.modeldriven.testing.RetractFact;
import org.drools.brms.client.modeldriven.testing.Scenario;
import org.drools.brms.client.modeldriven.testing.VerifyFact;
import org.drools.brms.client.modeldriven.testing.VerifyField;
import org.drools.brms.client.modeldriven.testing.VerifyRuleFired;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScenarioWidget extends Composite {

    public ScenarioWidget() {
        DirtyableFlexTable layout  = new DirtyableFlexTable();


        //Sample data
        FactData d1 = new FactData("Driver", "d1", new FieldData[] {new FieldData("age", "42"), new FieldData("name", "david")}, false);
        FactData d2 = new FactData("Driver", "d2", new FieldData[] {new FieldData("name", "michael")}, false);
        FactData d3 = new FactData("Driver", "d3", new FieldData[] {new FieldData("name", "michael2")}, false);
        FactData d4 = new FactData("Accident", "a1", new FieldData[] {new FieldData("name", "michael2")}, false);
        Scenario sc = new Scenario();
        sc.fixtures.add(d1);
        sc.fixtures.add(d2);
        sc.globals.add(d3);
        sc.globals.add(d4);
        sc.rules.add("rule1");
        sc.rules.add("rule2");

        VerifyFact vf = new VerifyFact("d1", new VerifyField[] {
            new VerifyField("age", "42", "=="),
            new VerifyField("name", "michael", "!=")

        });

        VerifyRuleFired vf1 = new VerifyRuleFired("xxx fdsfds", new Integer(42), null);
        VerifyRuleFired vf2 = new VerifyRuleFired("yyyyy fdsfdsfds fds", null, new Boolean(true));
        List ruleFires = new ArrayList(); ruleFires.add(vf1); ruleFires.add(vf2);


        //now have to sort this out for the view.
        //we want data grouped by type, for the purposes of the grid
        Map facts = breakUpFactData(sc.fixtures);
        Map globals = breakUpFactData(sc.globals);


        //now we have them grouped by type and global/fact, so we can render them appropriately.
        //maps are a map of Type => List of FactData
        VerticalPanel factPanel = new VerticalPanel();
        for (Iterator iterator = facts.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry e = (Map.Entry) iterator.next();
            factPanel.add(new DataInputWidget((String)e.getKey(), facts, false, false));
        }
        VerticalPanel globalPanel = new VerticalPanel();
        for (Iterator iterator = globals.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry e = (Map.Entry) iterator.next();
            globalPanel.add(new DataInputWidget((String)e.getKey(), globals, true, false));
        }

        ExecutionTrace ex = new ExecutionTrace();
        ExecutionWidget exw = new ExecutionWidget(ex, false);


        ConfigWidget conf = new ConfigWidget(sc, new String[] {"rule1", "rule2", "rule3"});

        layout.setWidget(0, 0, conf);
        layout.setWidget(1, 0, globalPanel);
        layout.setWidget(2, 0, new HTML("<hr/>"));




        layout.setWidget(3, 0, factPanel);
        layout.setWidget(4, 0, exw);
        layout.setWidget(5, 0, new VerifyFactWidget(vf));
        layout.setWidget(6, 0, new VerifyRulesFiredWidget( ruleFires ));
        layout.setWidget(7, 0, new RetractWidget(new RetractFact("f1")));

//        layout.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
//        layout.getFlexCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
//        layout.getFlexCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
//        layout.getFlexCellFormatter().setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_CENTER);
//        layout.getFlexCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER);
//        layout.getFlexCellFormatter().setHorizontalAlignment(5, 0, HasHorizontalAlignment.ALIGN_CENTER);
//        layout.getFlexCellFormatter().setHorizontalAlignment(6, 0, HasHorizontalAlignment.ALIGN_CENTER);

        layout.setStyleName("model-builder-Background");
        initWidget(layout);

    }

    /**
     * Separate out the elements into the appropriate types.
     * Will be keyed on the type name.
     */
    static Map breakUpFactData(List factData) {
        Map facts = new HashMap();
        for (Iterator iterator = factData.iterator(); iterator.hasNext();) {
            Object f = (Object) iterator.next();
            if (f instanceof FactData) {
                FactData fd = (FactData) f;
                addToMap(facts, fd);
            }
        }
        return facts;
    }

    private static void addToMap(Map m, FactData fd) {
        if (!m.containsKey(fd.type)) {
            m.put(fd.type, new ArrayList());
        }
        List l = (List) m.get(fd.type);
        l.add(fd);
    }

}


/**
 * For capturing input for all the facts of a given type.
 * @author Michael Neale
 */
class DataInputWidget extends Composite {


    public DataInputWidget(String factType, Map facts, boolean isGlobal, boolean isModify) {

        Grid outer = new Grid(2, 1);
        outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader");
        outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName("modeller-fact-pattern-Widget");


        if (isGlobal) {
            outer.setWidget(0, 0, new Label("Global: " + factType));
        } else if (isModify) {
            outer.setWidget(0, 0, new Label("Modify: " + factType));
        } else  {
            outer.setWidget(0, 0, new Label("Insert: " + factType));
        }

        final FlexTable t = new FlexTable();

        //This will work out what row is for what field
        Map fields = new HashMap();
        int col = 0;
        List defList = (List) facts.get(factType);
        for (Iterator iterator = defList.iterator(); iterator.hasNext();) {
            FactData d = (FactData) iterator.next();
            for (int i = 0; i < d.fieldData.length; i++) {
                FieldData fd = d.fieldData[i];
                if (!fields.containsKey(fd.name)) {
                    int idx = fields.size() + 1;
                    fields.put(fd.name, new Integer(idx));
                    t.setWidget(idx, 0, new Label(fd.name + ":"));
                    t.getCellFormatter().setHorizontalAlignment(idx, 0, HasHorizontalAlignment.ALIGN_RIGHT);
                }
            }
        }

        //now we go through the facts and the fields, adding them to the grid
        //if a fact is missing a FieldData, we will add it in (so people can enter data later on)
          col = 0;
        for (Iterator iterator = defList.iterator(); iterator.hasNext();) {
            FactData d = (FactData) iterator.next();
            t.setWidget(0, ++col, new Label(d.name));
            Map presentFields = new HashMap(fields);
            for (int i = 0; i < d.fieldData.length; i++) {
                FieldData fd = d.fieldData[i];
                int fldRow = ((Integer) fields.get(fd.name)).intValue();
                t.setWidget(fldRow, col, editableCell(fd));
                presentFields.remove(fd.name);
            }

            for (Iterator missing = presentFields.entrySet().iterator(); missing.hasNext();) {
                Map.Entry e = (Map.Entry) missing.next();
                int fldRow = ((Integer) e.getValue()).intValue();
                FieldData fd = new FieldData((String) e.getKey(), "");
                d.addFieldData(fd);
                t.setWidget(fldRow, col, editableCell(fd));
            }
        }

        outer.setWidget(1, 0, t);
        initWidget(outer);
    }

    private Widget editableCell(final FieldData fd) {
        final TextBox tb = new TextBox();
        tb.setText(fd.value);
        tb.setTitle("Value for: " + fd.name);
        tb.addChangeListener(new ChangeListener() {
            public void onChange(Widget w) {
                fd.value = tb.getText();
            }
        });
        return tb;
    }




}

class ConfigWidget extends Composite {
    public ConfigWidget(final Scenario sc, final String[] fullRuleList) {

        final ListBox box = new ListBox(true);

        for (int i = 0; i < sc.rules.size(); i++) {
            box.addItem((String)sc.rules.get(i));
        }
        HorizontalPanel filter = new HorizontalPanel();

        final Image add = new ImageButton("images/new_item.gif", "Add a new rule.");
        add.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                showRulePopup(w, box, fullRuleList, sc.rules);
            }
        });

        final Image remove = new ImageButton("images/trash.gif", "Remove selected rule.");
        remove.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                if (box.getSelectedIndex() == -1) {
                    Window.alert("Please choose a rule to remove.");
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
        drop.addItem("Allow these rules to fire:", "inc");
        drop.addItem("Prevent these rules from firing:", "exc");
        drop.addItem("All rules may fire");
        drop.addChangeListener(new ChangeListener() {
            public void onChange(Widget w) {
                String s = drop.getValue(drop.getSelectedIndex());
                if (s.equals("inc")) {
                    sc.inclusive = true;
                    add.setVisible(true); remove.setVisible(true); box.setVisible(true);
                } else if (s.equals("exc")) {
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
        }


        filter.add(drop);
        filter.add(box);
        filter.add(actions);

        initWidget(filter);
    }

    private void showRulePopup(Widget w, final ListBox box, String[] fullRuleList, final List filterList) {
        final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", "Select rule");
        final ListBox rules = new ListBox();
        for (int i = 0; i < fullRuleList.length; i++) {
            rules.addItem(fullRuleList[i]);
        }
        pop.addRow(rules);
        Button ok = new Button("Add");
        pop.addRow(ok);
        ok.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                String r = rules.getItemText(rules.getSelectedIndex());
                filterList.add(r);
                box.addItem(r);
                pop.hide();
            }
        });
        pop.setPopupPosition(w.getAbsoluteLeft(), w.getAbsoluteTop());
        pop.show();

    }


}

class ExecutionWidget extends Composite {
    public ExecutionWidget(ExecutionTrace ext, boolean showResults) {
        final SimplePanel p = new SimplePanel();
        render(ext, showResults, p);
        initWidget(p);
    }

    private void render(final ExecutionTrace ext, boolean showResults,
            final SimplePanel p) {
        FormStyleLayout layout = new FormStyleLayout("images/execution_trace.gif", "Run the rules");
        p.add(layout);
        if (showResults) {
            layout.addAttribute("Execution time:", new Label(ext.executionTimeResult + " ms"));
            layout.addAttribute("Number of rules fired:", new Label(ext.numberOfRulesFired + ""));
        }
        layout.addAttribute("Simulation date:", simulDate(ext));


    }

    private Widget simulDate(final ExecutionTrace ext) {
        final String fmt = "dd-MMM-YYYY";
        final TextBox dt = new TextBox();
        if (ext.scenarioSimulatedDate == null) {
            dt.setText("<current date and time>");
        } else {
            dt.setText(ext.scenarioSimulatedDate.toLocaleString());
        }
        dt.addChangeListener(new ChangeListener() {
            public void onChange(Widget w) {
                if (dt.getText().trim().equals("")) {
                    dt.setText("<current date and time>");
                } else {
                    try {
                        Date d = new Date(dt.getText());
                        ext.scenarioSimulatedDate = d;
                        dt.setText(d.toLocaleString());
                    } catch (Exception e) {
                        ErrorPopup.showMessage("Bad date format - please try again (try the format of " + fmt + ").");
                    }
                }
            }
        });
        return dt;
    }


}

class VerifyFactWidget extends Composite {
    public VerifyFactWidget(VerifyFact vf) {
        Grid outer = new Grid(2, 1);
        outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader");
        outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName("modeller-fact-pattern-Widget");
        outer.setWidget(0, 0, new Label("Expect [" + vf.factName + "]"));
        initWidget(outer);

        FlexTable data = new FlexTable();
        for (int i = 0; i < vf.fieldValues.length; i++) {
            final VerifyField fld = vf.fieldValues[i];
            data.setWidget(i, 0, new Label(fld.fieldName + ":"));
            data.getFlexCellFormatter().setHorizontalAlignment(i, 0, HasHorizontalAlignment.ALIGN_RIGHT);

            final ListBox opr = new ListBox();
            opr.addItem("equals", "==");
            opr.addItem("does not equal", "!=");
            if (fld.operator.equals("==")) {
                opr.setSelectedIndex(0);
            } else {
                opr.setSelectedIndex(1);
            }
            opr.addChangeListener(new ChangeListener() {
                public void onChange(Widget w) {
                    fld.operator = opr.getValue(opr.getSelectedIndex());
                }
            });

            data.setWidget(i, 1, opr);

            final TextBox input = new TextBox();
            input.setText(fld.expected);
            input.addChangeListener(new ChangeListener() {
                public void onChange(Widget w) {
                    fld.expected = input.getText();
                }
            });
            data.setWidget(i, 2, input);

        }
        outer.setWidget(1, 0, data);

    }
}

class VerifyRulesFiredWidget extends Composite {
    /**
     * @param rfl List<VeryfyRuleFired>
     */
    public VerifyRulesFiredWidget(List rfl) {
        Grid outer = new Grid(2, 1);
        outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader");
        outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName("modeller-fact-pattern-Widget");
        outer.setWidget(0, 0, new Label("Expect rules"));
        initWidget(outer);

        FlexTable data = new FlexTable();


        for (int i = 0; i < rfl.size(); i++) {
            final VerifyRuleFired v = (VerifyRuleFired) rfl.get(i);
            data.setWidget(i, 0, new Label(v.ruleName + ":"));
            data.getFlexCellFormatter().setAlignment(i, 0, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_MIDDLE);


            final ListBox b = new ListBox();
            b.addItem("fired at least once", "y");
            b.addItem("did not fire", "n");
            b.addItem("fired this many times: ", "e");
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
                        v.expectedCount = null;
                    } else {
                        num.setVisible(true);
                        v.expectedFire = null;
                        num.setText("1"); v.expectedCount = new Integer(1);
                    }
                }
            });

            num.addChangeListener(new ChangeListener() {
                public void onChange(Widget w) {
                    v.expectedCount = new Integer(num.getText());
                }
            });

            HorizontalPanel h = new HorizontalPanel();
            h.add(b); h.add(num);
            data.setWidget(i, 1, h);

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
        outer.setWidget(1, 0, data);

    }
}

class RetractWidget extends Composite {
	public RetractWidget(RetractFact ret) {
        Grid outer = new Grid(1, 1);
        outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader");
        outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName("modeller-fact-pattern-Widget");
        outer.setWidget(0, 0, new Label("Retract [" + ret.name + "]"));
        initWidget(outer);
	}
}
