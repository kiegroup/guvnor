package org.drools.guvnor.client.ruleeditor;

import org.drools.guvnor.client.common.DefaultContentUploadEditor;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.AssetAttachmentFileWidget;
import org.drools.guvnor.client.rpc.RuleAsset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;

public class BPELWrapper extends Composite {

	private Constants constants = GWT.create(Constants.class);

	public BPELWrapper(RuleAsset asset, RuleViewer viewer) {

		final String uuid = asset.uuid;
		final String fileName = asset.metaData.name;
		final String dirName = asset.metaData.packageName;
		final String servletName = "workflowmanager";
		final String isNew = (asset.content == null ? "true" : "false");

		AssetAttachmentFileWidget uploadWidget = new DefaultContentUploadEditor(
				asset, viewer);

		VerticalPanel panel = new VerticalPanel();
		panel.add(uploadWidget);

		Toolbar tb = new Toolbar();

		ToolbarButton viewSource = new ToolbarButton();
		viewSource.setText(constants.OpenEditorInNewWindow());

		final String url = Format
				.format(
						"bpeleditor/BPELEditor.html?uuid={0}&fileName={1}&dirName={2}&servletName={3}&isNew={4}",
						new String[] { uuid, fileName, dirName, servletName,
								isNew });

		viewSource.addListener(new ButtonListenerAdapter() {
			public void onClick(com.gwtext.client.widgets.Button button,
					EventObject e) {

				Window.open(url, "_" + fileName, null);

			}
		});

		tb.addButton(viewSource);
		panel.add(tb);

		initWidget(panel);

		this.setStyleName(getOverallStyleName());
	}

	public String getOverallStyleName() {
		return "decision-Table-upload"; // NON-NLS
	}
}
