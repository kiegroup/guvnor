package org.guvnor.shared;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface GeneralSettingsService {

    GeneralSettings load();

    void save(GeneralSettings settings);

}
