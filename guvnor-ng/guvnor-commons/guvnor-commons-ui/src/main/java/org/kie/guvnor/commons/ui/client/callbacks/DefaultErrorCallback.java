package org.kie.guvnor.commons.ui.client.callbacks;

import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.Message;
import org.kie.guvnor.commons.ui.client.popups.errors.ErrorPopup;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.services.exceptions.FileAlreadyExistsPortableException;
import org.kie.guvnor.services.exceptions.GenericPortableException;
import org.kie.guvnor.services.exceptions.InvalidPathPortableException;
import org.kie.guvnor.services.exceptions.NoSuchFilePortableException;
import org.kie.guvnor.services.exceptions.SecurityPortableException;

/**
 * Default Error handler for all Portable Exceptions
 */
public class DefaultErrorCallback implements ErrorCallback {

    @Override
    public boolean error( final Message message,
                          final Throwable throwable ) {
        try {
            throw throwable;
        } catch ( InvalidPathPortableException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionInvalidPath0( e.getPath() ) );

        } catch ( FileAlreadyExistsPortableException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionFileAlreadyExists0( e.getPath() ) );

        } catch ( NoSuchFilePortableException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionNoSuchFile0( e.getPath() ) );

        } catch ( SecurityPortableException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionSecurity0( e.getPath() ) );

        } catch ( GenericPortableException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( Throwable e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        }
        return false;
    }

}
