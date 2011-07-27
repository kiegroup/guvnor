/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.server.repository;

import javax.jcr.RepositoryException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.RulesRepositoryConfigurator;

/**
 * Utility class to handle shutting down the JCR Transient Repository in a
 * Servlet container
 */
public class RepositoryShutdownService
    implements
    ServletContextListener {

    private static final LoggingHelper log = LoggingHelper.getLogger( RepositoryShutdownService.class );

    /**
     * Force the JCR Repository to shutdown when the application is unloaded.
     * Users may not "logout" of the application and therefore there is a
     * possibility that the Repository holds the repository lock open.
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        try {
            log.info( "Removing listeners...." );
            RepositoryStartupService.removeListeners();

            log.info( "Shutting down repository...." );
            RulesRepositoryConfigurator.getInstance( null ).shutdown();
        } catch ( RepositoryException re ) {
            log.error("Shutting down repository failed.", re);
        }
    }

    public void contextInitialized(ServletContextEvent arg0) {
        //Do nothing; repositories aren't created here
    }

}
