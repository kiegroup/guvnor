package org.guvnor.common.services.shared.rulenames;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.Path;

@Remote
public interface RuleNamesService {

    //Rule names methods - for "extends" use
    Map<String, Collection<String>> getRuleNamesMap( final Path path );

    List<String> getRuleNames( final Path path );

    Collection<String> getRuleNamesForPackage( final Path path,
                                               final String packageName );
}
