package org.guvnor.common.services.backend.file;

import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.shared.file.DeleteService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.base.options.CommentedOption;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;

@Service
public class DeleteServiceImpl implements DeleteService {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Paths paths;

    @Inject
    private Identity identity;

    @Inject
    private SessionInfo sessionInfo;

    @Override
    public void delete( final Path path,
                        final String comment ) {
        try {
            System.out.println( "USER:" + identity.getName() + " DELETING asset [" + path.getFileName() + "]" );

            ioService.delete( paths.convert( path ),
                              new CommentedOption( sessionInfo.getId(), identity.getName(), null, comment ) );

        } catch ( final Exception e ) {
            throw ExceptionUtilities.handleException( e );
        }
    }

}
