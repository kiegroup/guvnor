/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.repository;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jboss.weld.environment.servlet.Listener;

/**
 * Workaround for https://issues.jboss.org/browse/WELD-983
 */
public class SafeWeldListener implements ServletContextListener, HttpSessionListener, ServletRequestListener {

    private Boolean onJEE6Server = null;
    private ServletContextListener servletContextListener;
    private HttpSessionListener httpSessionListener;
    private ServletRequestListener servletRequestListener;

    private void checkOnJEE6Server(String serverInfo) {
        onJEE6Server = (serverInfo.startsWith("JBoss Web/7.")) ||
                       (serverInfo.startsWith("WebLogic") && serverInfo.contains("WebLogic Server 12.")) || //WebLogic 12
                       (serverInfo.startsWith("IBM WebSphere Application Server/8.")); //WebSphere 8.5
        if (!onJEE6Server) {
            // Note that weldListener is not a global variable to avoid a ClassNotFoundException on JBoss 7
            Listener weldListener = new Listener();
            servletContextListener = weldListener;
            httpSessionListener = weldListener;
            servletRequestListener = weldListener;
        }
    }

    public void contextInitialized(ServletContextEvent sce) {
        checkOnJEE6Server(sce.getServletContext().getServerInfo());
        if (onJEE6Server) {
            return;
        }
        servletContextListener.contextInitialized(sce);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        if (onJEE6Server) {
            return;
        }
        servletContextListener.contextDestroyed(sce);
    }

    public void sessionCreated(HttpSessionEvent se) {
        if (onJEE6Server) {
            return;
        }
        httpSessionListener.sessionCreated(se);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        if (onJEE6Server) {
            return;
        }
        httpSessionListener.sessionDestroyed(event);
    }

    public void requestDestroyed(ServletRequestEvent event) {
        if (onJEE6Server) {
            return;
        }
        servletRequestListener.requestDestroyed(event);
    }

    public void requestInitialized(ServletRequestEvent event) {
        if (onJEE6Server) {
            return;
        }
        servletRequestListener.requestInitialized(event);
    }

}
