package org.kie.guvnor.commons.ui.client.callbacks;

import com.google.gwt.user.client.Window;
import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.Message;

/**
 * Default Error handler for all Portable Exceptions
 */
public class DefaultErrorCallback implements ErrorCallback {

    @Override
    public boolean error( final Message message,
                          final Throwable throwable ) {
        Window.alert( message.toString() );
        Window.alert( throwable.getMessage() );
        return false;
    }

}
