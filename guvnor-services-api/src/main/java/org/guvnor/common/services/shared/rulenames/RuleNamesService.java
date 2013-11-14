package org.guvnor.common.services.shared.rulenames;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface RuleNamesService {

    //Rule names methods - for "extends" use
    Map<String, Collection<String>> getRuleNamesMap();

    List<String> getRuleNames();

    Collection<String> getRuleNamesForPackage( String packageName );
}
