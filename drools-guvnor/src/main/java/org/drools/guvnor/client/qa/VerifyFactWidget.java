package org.drools.guvnor.client.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.gwtext.client.util.Format;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.common.ValueChanged;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.drools.ide.common.client.modeldriven.testing.VerifyFact;
import org.drools.ide.common.client.modeldriven.testing.VerifyField;

/**
 * Created by IntelliJ IDEA.
 * User: nheron
 * Date: 7 nov. 2009
 * Time: 19:31:47
 * To change this template use File | Settings | File Templates.
 */
public class VerifyFactWidget extends Composite {
    private Grid outer;
	private boolean showResults;
	private String type;
	private SuggestionCompletionEngine sce;
    private Scenario scenario;
    private ExecutionTrace executionTrace;
    private Constants constants = ((Constants) GWT.create(Constants.class));

    public VerifyFactWidget(final VerifyFact vf, final Scenario sc, final SuggestionCompletionEngine sce, ExecutionTrace executionTrace,boolean showResults) {
        outer = new Grid(2, 1);
        outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader");  //NON-NLS
        outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName("modeller-fact-pattern-Widget");                //NON-NLS
        this.sce = sce;
        this.scenario = sc;
        this.executionTrace =executionTrace;
        HorizontalPanel ab = new HorizontalPanel();
        if (!vf.anonymous) {
	        type = (String) sc.getVariableTypes().get(vf.name);
            ab.add(new SmallLabel(Format.format(constants.scenarioFactTypeHasValues(), type, vf.name)));
        } else {
        	type = vf.name;
            ab.add(new SmallLabel(Format.format(constants.AFactOfType0HasValues(), vf.name)));
        }
        this.showResults = showResults;

        Image add = new ImageButton("images/add_field_to_fact.gif", constants.AddAFieldToThisExpectation(), new ClickListener() { //NON-NLS
			public void onClick(Widget w) {

				String[] fields = (String[]) sce.getModelFields(type);
				final FormStylePopup pop = new FormStylePopup("images/rule_asset.gif", constants.ChooseAFieldToAdd()); //NON-NLS
				final ListBox b = new ListBox();
				for (int i = 0; i < fields.length; i++) {
					b.addItem(fields[i]);
				}
				pop.addRow(b);
				Button ok = new Button(constants.OK());
				ok.addClickListener(new ClickListener() {
									public void onClick(Widget w) {
										String f = b.getItemText(b.getSelectedIndex());
										vf.fieldValues.add(new VerifyField(f, "", "=="));
								        FlexTable data = render(vf);
								        outer.setWidget(1, 0, data);
								        pop.hide();
									}
								});
				pop.addRow(ok);
				pop.show();

			}
		});

        ab.add(add);
        outer.setWidget(0, 0, ab);
        initWidget(outer);

        FlexTable data = render(vf);
        outer.setWidget(1, 0, data);

    }

	private FlexTable render(final VerifyFact vf) {
		FlexTable data = new FlexTable();
        for (int i = 0; i < vf.fieldValues.size(); i++) {
            final VerifyField fld = (VerifyField) vf.fieldValues.get(i);
            data.setWidget(i, 1, new SmallLabel(fld.fieldName + ":"));
            data.getFlexCellFormatter().setHorizontalAlignment(i, 1, HasHorizontalAlignment.ALIGN_RIGHT);

            final ListBox opr = new ListBox();
            opr.addItem(constants.equalsScenario(), "==");
            opr.addItem(constants.doesNotEqualScenario(), "!=");
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

            data.setWidget(i, 2, opr);
            //fix nheron
            Widget cellEditor = new VerifyFieldConstraintEditor(type,
					new ValueChanged() {
						public void valueChanged(String newValue) {
							fld.expected = newValue;
						}

					}, fld, sce,this.scenario,this.executionTrace);
 
            data.setWidget(i, 3, cellEditor);

            Image del = new ImageButton("images/delete_item_small.gif", constants.RemoveThisFieldExpectation(), new ClickListener() {
				public void onClick(Widget w) {
					if (Window.confirm(constants.AreYouSureYouWantToRemoveThisFieldExpectation())) {
						vf.fieldValues.remove(fld);
				        FlexTable data = render(vf);
				        outer.setWidget(1, 0, data);
					}
				}
			});
            data.setWidget(i, 4, del);

            if (showResults && fld.successResult != null) {
            	if (!fld.successResult.booleanValue()) {
            		data.setWidget(i, 0, new Image("images/warning.gif"));        //NON-NLS
                    data.setWidget(i, 5, new HTML(Format.format(constants.ActualResult(), fld.actualResult )));

            		data.getCellFormatter().addStyleName(i, 5, "testErrorValue"); //NON-NLS

            	} else {
            		data.setWidget(i, 0, new Image("images/test_passed.png")); //NON-NLS
            	}
            }



        }
		return data;
	}

}

