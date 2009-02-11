package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;

/**
 * To be shown when the user opens repo for the first time.
 * @author Michael Neale
 *
 */
public class NewRepoDialog extends FormStylePopup {
    private Constants constants;

    public NewRepoDialog() {
		//super("images/new_wiz.gif", "Welcome to Guvnor !");
		setTitle(((Constants) GWT.create(Constants.class)).WelcomeToGuvnor());
		setWidth(300);

        constants = ((Constants) GWT.create(Constants.class));
        addAttribute("", new HTML("<div class='highlight'>" + constants.BrandNewRepositoryNote() + "</div>"));  //NON-NLS

		HorizontalPanel hp = new HorizontalPanel();

		Button ins = new Button(constants.YesPleaseInstallSamples());
		hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		hp.add(ins);
		Button no = new Button(constants.NoThanks());
		hp.add(no);

		addAttribute("", hp);
		ins.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				if (!Window.confirm(constants.AboutToInstallSampleRepositoryAreYouSure())) return;
				LoadingPopup.showMessage(constants.ImportingAndProcessing());
				RepositoryServiceFactory.getService().installSampleRepository(new GenericCallback<Object>() {
					public void onSuccess(Object a) {
						Window.alert(constants.RepositoryInstalledSuccessfully());
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
