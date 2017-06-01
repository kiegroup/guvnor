/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.client.util;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class PopupHelper {

    public void showInformationPopup(final String message) {
        showOkButtonPopup(CommonConstants.INSTANCE.Information(),
                          message);
    }

    public void showErrorPopup(final String message) {
        showOkButtonPopup(CommonConstants.INSTANCE.Error(),
                          message);
    }

    public void showYesNoPopup(final String title,
                               final String message,
                               final Command yesCommand,
                               final Command noCommand) {
        YesNoCancelPopup popup = YesNoCancelPopup.newYesNoCancelPopup(title,
                                                                      message,
                                                                      yesCommand,
                                                                      noCommand,
                                                                      null);
        popup.setClosable(false);
        popup.clearScrollHeight();
        popup.show();
    }

    private static void showOkButtonPopup(final String title,
                                          final String message) {
        YesNoCancelPopup popup = YesNoCancelPopup.newYesNoCancelPopup(title,
                                                                      message,
                                                                      () -> {
                                                                      },
                                                                      CommonConstants.INSTANCE.OK(),
                                                                      null,
                                                                      null,
                                                                      null,
                                                                      null);

        popup.setClosable(false);
        popup.clearScrollHeight();
        popup.show();
    }
}
