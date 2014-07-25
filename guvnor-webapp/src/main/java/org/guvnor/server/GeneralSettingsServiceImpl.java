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
