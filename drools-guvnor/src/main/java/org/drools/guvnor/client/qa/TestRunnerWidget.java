package org.drools.guvnor.client.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.gwtext.client.util.Format;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.testing.*;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.BuilderResultLine;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.ScenarioRunResult;
import org.drools.guvnor.client.rpc.SingleScenarioResult;

import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nheron
 * Date: 7 nov. 2009
 * Time: 19:27:09
 * To change this template use File | Settings | File Templates.
 */
public /**
 * Runs the test, plus shows a summary view of the results.
 */
class TestRunnerWidget extends Composite {

	FlexTable results = new FlexTable();
	//Grid layout = new Grid(2, 1);
	VerticalPanel layout = new VerticalPanel();

	//private HorizontalPanel busy = new HorizontalPanel();
	private SimplePanel actions = new SimplePanel();
    private Constants constants = ((Constants) GWT.create(Constants.class));

    public TestRunnerWidget(final ScenarioWidget parent, final String packageName) {

		final Button run = new Button(constants.RunScenario());
		run.setTitle(constants.RunScenarioTip());
		run.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				LoadingPopup.showMessage(constants.BuildingAndRunningScenario());
				RepositoryServiceFactory.getService().runScenario(parent.asset.metaData.packageName, (Scenario) parent.asset.content, new GenericCallback<SingleScenarioResult>() {
					public void onSuccess(SingleScenarioResult data) {
						LoadingPopup.close();
						layout.clear();
						layout.add(actions);
						layout.add(results);
						actions.setVisible(true);
						ScenarioRunResult result = data.result;
						if (result.errors != null) {
							showErrors(result.errors);
						} else {
							showResults(parent, data);
						}
					}
				});
			}
		});

		actions.add(run);
		layout.add(actions);
		initWidget(layout);
	}


	private void showErrors(BuilderResultLine[] rs) {
		results.clear();
		results.setVisible(true);

        FlexTable errTable = new FlexTable();
        errTable.setStyleName( "build-Results" );
        for ( int i = 0; i < rs.length; i++ ) {
            int row = i;
            final BuilderResultLine res = rs[i];
            errTable.setWidget( row, 0, new Image("images/error.gif"));
            if( res.assetFormat.equals( "package" )) {
                errTable.setText( row, 1, constants.packageConfigurationProblem1() + res.message );
            } else {
                errTable.setText( row, 1, "[" + res.assetName + "] " + res.message );
            }

        }
        ScrollPanel scroll = new ScrollPanel(errTable);

        scroll.setWidth( "100%" );
        results.setWidget(0, 0, scroll);

	}

	private void showResults(final ScenarioWidget parent,
			final SingleScenarioResult data) {
		results.clear();
		results.setVisible(true);

		parent.asset.content = data.result.scenario;
		parent.showResults = true;
		parent.renderEditor();

		int failures = 0;
		int total = 0;
		VerticalPanel resultsDetail = new VerticalPanel();


		for (Iterator iterator = data.result.scenario.fixtures.iterator(); iterator.hasNext();) {
			Fixture f = (Fixture) iterator.next();
			if (f instanceof VerifyRuleFired) {

				VerifyRuleFired vr = (VerifyRuleFired)f;
				HorizontalPanel h = new HorizontalPanel();
				if (!vr.successResult.booleanValue()) {
					h.add(new Image("images/warning.gif"));
					failures++;
				} else {
					h.add(new Image("images/test_passed.png"));
				}
				h.add(new SmallLabel(vr.explanation));
				resultsDetail.add(h);
				total++;
			} else if (f instanceof VerifyFact) {
				VerifyFact vf = (VerifyFact)f;
				for (Iterator it = vf.fieldValues.iterator(); it.hasNext();) {
					total++;
					VerifyField vfl = (VerifyField) it.next();
					HorizontalPanel h = new HorizontalPanel();
					if (!vfl.successResult.booleanValue()) {
						h.add(new Image("images/warning.gif"));
						failures++;
					} else {
						h.add(new Image("images/test_passed.png"));
					}
					h.add(new SmallLabel(vfl.explanation));
					resultsDetail.add(h);
				}


			} else if (f instanceof ExecutionTrace) {
				ExecutionTrace ex = (ExecutionTrace) f;
				if (ex.numberOfRulesFired == data.result.scenario.maxRuleFirings) {
                    Window.alert(Format.format(constants.MaxRuleFiringsReachedWarning(), data.result.scenario.maxRuleFirings));
				}
			}


		}

		results.setWidget(0, 0, new SmallLabel(constants.Results()));
		results.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		if (failures > 0) {
			results.setWidget(0, 1, ScenarioWidget.getBar("#CC0000" , 150, failures, total));
		} else {
			results.setWidget(0, 1, ScenarioWidget.getBar("GREEN" , 150, failures, total));
		}

		results.setWidget(1, 0, new SmallLabel(constants.SummaryColon()));
		results.getFlexCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		results.setWidget(1, 1, resultsDetail);
		results.setWidget(2, 0, new SmallLabel(constants.AuditLogColon()));

		final Button showExp = new Button(constants.ShowEventsButton());
		results.setWidget(2, 1, showExp);
		showExp.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				showExp.setVisible(false);
				results.setWidget(2, 1, doAuditView(data.auditLog));
			}
		});



	}



	private Widget doAuditView(List<String[]> auditLog) {
		VerticalPanel vp = new VerticalPanel();
		vp.add(new HTML("<hr/>"));
		FlexTable g = new FlexTable();
		String indent = "";
		int row = 0;
		boolean firing = false;
		for (int i = 0; i < auditLog.size(); i++) {
			String[] lg = auditLog.get(i);

			int id = Integer.parseInt(lg[0]);
			if (id <= 7) {
				if (id <= 3) {
					if (!firing) {
						g.setWidget(row, 0, new Image("images/audit_events/" + lg[0] + ".gif"));
						g.setWidget(row, 1, new SmallLabel(lg[1]));
					} else {
						g.setWidget(row, 1, hz(new Image("images/audit_events/" + lg[0] + ".gif"), new SmallLabel(lg[1])));
					}
					row++;
				} else	if (id == 6) {
					firing = true;
					g.setWidget(row, 0, new Image("images/audit_events/" + lg[0] + ".gif"));
					g.setWidget(row, 1, new SmallLabel("<b>" + lg[1] + "</b>"));
					row++;
				} else if (id == 7) {
					firing = false;
				} else {
					g.setWidget(row, 0, new Image("images/audit_events/" + lg[0] + ".gif"));
					g.setWidget(row, 1, new SmallLabel("<font color='grey'>" +   lg[1] + "</font>"));
					row++;
				}
			} else {
				g.setWidget(row, 0, new Image("images/audit_events/misc_event.gif"));
				g.setWidget(row, 1, new SmallLabel("<font color='grey'>" + lg[1] + "</font>"));
				row++;
			}
		}
		vp.add(g);
		vp.add(new HTML("<hr/>"));
		return vp;
	}


	private Widget hz(Image image, SmallLabel smallLabel) {
		HorizontalPanel h = new HorizontalPanel();
		h.add(image);
		h.add(smallLabel);
		return h;
	}
}
