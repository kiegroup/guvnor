package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * To be shown when the user opens repo for the first time.
 * @author Michael Neale
 *
 */
public class NewRepoDialog extends FormStylePopup {

	public NewRepoDialog() {
		//super("images/new_wiz.gif", "Welcome to Guvnor !");
		setTitle("Welcome to Guvnor");
		setWidth(300);

		addAttribute("", new SmallLabel("<b>This looks like a brand new repository.</b>"));
		addAttribute("", new SmallLabel("<b>Would you like to Install a sample repository?<b>"));

		HorizontalPanel hp = new HorizontalPanel();

		Button ins = new Button("Yes, please install Samples !");
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		hp.add(ins);
		Button no = new Button("No thanks !");
		hp.add(no);

		addAttribute("", hp);
		ins.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				if (!Window.confirm("About to install sample repository. Are you sure?")) return;
				LoadingPopup.showMessage("Importing and processing...");
				RepositoryServiceFactory.getService().installSampleRepository(new GenericCallback<Object>() {
					public void onSuccess(Object a) {
						Window.alert("Repository installed successfully.");
						hide();
						Window.Location.reload();
					}
				});
			}
		});
		no.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				hide();
			}
		});

	}

}
