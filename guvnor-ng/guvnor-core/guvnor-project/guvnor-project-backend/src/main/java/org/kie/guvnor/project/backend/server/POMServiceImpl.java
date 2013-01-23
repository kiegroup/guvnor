package org.kie.guvnor.project.backend.server;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.kie.commons.io.IOService;
import org.kie.guvnor.datamodel.events.InvalidateDMOProjectCacheEvent;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.project.service.POMService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;

public class POMServiceImpl
        implements POMService {


    private Event<InvalidateDMOProjectCacheEvent> invalidateDMOProjectCache;
    private IOService ioService;
    private Paths paths;
    private POMContentHandler pomContentHandler;

    public POMServiceImpl() {
        // For Weld
    }

    @Inject
    public POMServiceImpl(Event<InvalidateDMOProjectCacheEvent> invalidateDMOProjectCache,
                          @Named("ioStrategy") IOService ioService,
                          Paths paths,
                          POMContentHandler pomContentHandler) {
        this.invalidateDMOProjectCache = invalidateDMOProjectCache;
        this.ioService = ioService;
        this.paths = paths;
        this.pomContentHandler = pomContentHandler;
    }


    @Override
    public POM loadPOM(final Path path) {
        try {
            org.kie.commons.java.nio.file.Path convert = paths.convert(path);
            String propertiesString = ioService.readAllString(convert);
            return pomContentHandler.toModel(propertiesString);
        } catch (IOException e) {
            e.printStackTrace();  //TODO Need to use the Problems screen for these -Rikkola-
        } catch (XmlPullParserException e) {
            e.printStackTrace();  //TODO Need to use the Problems screen for these -Rikkola-
        }
        return null;

    }


    @Override
    public Path savePOM(final Path pathToPOM,
                        final POM pomModel) {
        try {
            Path result = paths.convert(ioService.write(paths.convert(pathToPOM), pomContentHandler.toString(pomModel)));

            invalidateDMOProjectCache.fire(new InvalidateDMOProjectCacheEvent(result));

            return result;

        } catch (IOException e) {
            e.printStackTrace();  //TODO Notify this in the Problems screen -Rikkola-
        }
        return null;
    }

}
