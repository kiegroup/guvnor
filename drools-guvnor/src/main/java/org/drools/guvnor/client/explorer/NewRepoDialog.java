/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
				RepositoryServiceFactory.getService().installSampleRepository(new GenericCallback() {
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
