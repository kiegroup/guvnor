package org.kie.guvnor.commons.ui.client.popups.file;

/**
 * A command that can have a payload when executed
 */
public interface CommandWithPayload<T> {

    public void execute( final T payload );

}
