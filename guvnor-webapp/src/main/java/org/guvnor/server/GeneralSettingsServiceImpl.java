/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.server;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.shared.GeneralSettings;
import org.guvnor.shared.GeneralSettingsService;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class GeneralSettingsServiceImpl
        implements GeneralSettingsService {

    private GeneralSettings generalSettings = new GeneralSettings();

    @Override
    public GeneralSettings load() {
        // Some mock data.
        generalSettings.setName("demo");
        generalSettings.setEnabled(true);
        generalSettings.setSocialLogin(true);
        generalSettings.setUserRegistration(true);
        generalSettings.setResetPassword(false);
        generalSettings.setVerifyEmail(false);
        generalSettings.setUserAccountManagement(true);
        generalSettings.setRequireSSL(false);
        generalSettings.setCookieLoginAllowed(true);

        return generalSettings;
    }

    @Override
    public void save(GeneralSettings settings) {
        this.generalSettings = settings;
    }
}
