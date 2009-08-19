package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleFlowContentModel;
import org.drools.guvnor.client.rulefloweditor.RuleFlowViewer;
import org.drools.guvnor.client.explorer.Preferences;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;

/**
 * 
 * 
 * @author Toni Rikkola
 * 
 */
public class RuleFlowWrapper extends Composite implements SaveEventListener {

	private RuleViewer viewer;
	private RuleAsset asset;

	private RuleFlowViewer ruleFlowViewer;
	private Panel parameterPanel;
	private Constants constants = ((Constants) GWT.create(Constants.class));

	public RuleFlowWrapper(final RuleAsset asset, final RuleViewer viewer) {
		this.viewer = viewer;
		this.asset = asset;
		initWidgets(asset.uuid, asset.metaData.name);
	}

	protected void initWidgets(final String uuid, String formName) {

		RuleFlowUploadWidget uploadWidget = new RuleFlowUploadWidget(asset,
				viewer);

		VerticalPanel panel = new VerticalPanel();
		panel.add(uploadWidget);

		if (Preferences.getBooleanPref("visual-ruleflow")) {
			initRuleflowViewer();

			if (ruleFlowViewer != null && parameterPanel != null) {
				Toolbar tb = new Toolbar();

				ToolbarButton viewSource = new ToolbarButton();
				viewSource.setText(constants.OpenEditorInNewWindow());
				viewSource.addListener(new ButtonListenerAdapter() {
					public void onClick(
							com.gwtext.client.widgets.Button button,
							EventObject e) {
						doViewDiagram();

						ruleFlowViewer.update();
					}
				});

				tb.addButton(viewSource);
				panel.add(tb);

			}
		}

		initWidget(panel);

		this.setStyleName(getOverallStyleName());
	}

	private void doViewDiagram() {
		LoadingPopup.showMessage(constants.CalculatingSource());

		try {
			FormStylePopup pop = new FormStylePopup("images/view_source.gif", // NON-NLS
					constants.ViewingDiagram(), new Integer(800), Boolean.FALSE);

			pop.addRow(new ScrollPanel(ruleFlowViewer));
			pop.addRow(parameterPanel);

			pop.show();
		} catch (Exception e) {
			ErrorPopup
					.showMessage(constants
							.CouldNotCreateTheRuleflowDiagramItIsPossibleThatTheRuleflowFileIsInvalid());
		}

		LoadingPopup.close();
	}

	private void initRuleflowViewer() {
		RuleFlowContentModel rfcm = (RuleFlowContentModel) asset.content;

		if (rfcm != null && rfcm.getXml() != null && rfcm.getNodes() != null) {
			try {

				parameterPanel = new Panel();
				parameterPanel.setCollapsible(true);
				parameterPanel.setTitle(constants.Parameters());

				FormStyleLayout parametersForm = new FormStyleLayout();
				parametersForm.setHeight("120px"); // NON-NLS
				parameterPanel.add(parametersForm);

				ruleFlowViewer = new RuleFlowViewer(rfcm, parametersForm);

			} catch (Exception e) {
				Window.alert(e.toString());
			}
		} else if (rfcm != null && rfcm.getXml() == null) {

			// If the XML is not set there was some problem when the diagram was
			// created.
			Window
					.alert(constants
							.CouldNotCreateTheRuleflowDiagramItIsPossibleThatTheRuleflowFileIsInvalid());

		}
	}

	public String getIcon() {
		return "images/ruleflow_large.png"; // NON-NLS
	}

	public String getOverallStyleName() {
		return "decision-Table-upload"; // NON-NLS
	}

	public void onAfterSave() {

	}

	public void onSave() {

		RuleFlowContentModel rfcm = (RuleFlowContentModel) asset.content;

		rfcm.setNodes(ruleFlowViewer.getTransferNodes());

	}

	public RuleFlowViewer getRuleFlowViewer() {
		return ruleFlowViewer;
	}
}
