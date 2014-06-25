package org.guvnor.structure.backend.config.watch;

public interface AsyncConfigWatchService {

    void execute( final ConfigServiceWatchServiceExecutor wsExecutor );

    String getDescription();
}
