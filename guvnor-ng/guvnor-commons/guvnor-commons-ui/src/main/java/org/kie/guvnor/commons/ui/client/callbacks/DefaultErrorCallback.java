package org.kie.guvnor.commons.ui.client.callbacks;

import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.Message;

/**
 * Default Error handler for all Portable Exceptions
 */
public class DefaultErrorCallback implements ErrorCallback {

    @Override
    public boolean error( final Message message,
                          final Throwable throwable ) {
        //TODO Do something useful with the error!
        return true;
    }

}
