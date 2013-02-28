package org.kie.guvnor.commons.ui.client.menu;

import com.google.inject.Inject;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.popups.file.CommandWithCommitMessage;
import org.kie.guvnor.commons.ui.client.popups.file.SaveOperationService;
import org.kie.guvnor.services.version.VersionService;
import org.kie.guvnor.services.version.events.RestoreEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Command;

import javax.enterprise.event.Event;

public class RestoreVersionCommandProvider {

    @Inject
    private Caller<VersionService> versionService;

    @javax.inject.Inject
    private Event<RestoreEvent> restoreEvent;

    Command getCommand(final Path path) {
        return new Command() {
            @Override
            public void execute() {
                new SaveOperationService().save(path, new CommandWithCommitMessage() {
                    @Override
                    public void execute(final String comment) {
                        versionService.call(new RemoteCallback<Path>() {
                            @Override
                            public void callback(final Path restored) {
                                //TODO {porcelli} close current?
                                restoreEvent.fire(new RestoreEvent(restored));
                            }
                        }).restore(path, comment);
                    }
                });
            }
        };
    }


}
