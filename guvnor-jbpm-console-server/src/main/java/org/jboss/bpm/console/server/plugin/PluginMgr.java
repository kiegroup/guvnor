/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.bpm.console.server.plugin;

import org.jboss.bpm.console.server.util.ServiceLoader;
import org.jboss.bpm.console.server.util.InvocationProxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Loads plugins through the {@link org.jboss.bpm.console.server.util.ServiceLoader}.
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class PluginMgr
{

  private static final Log log = LogFactory.getLog(PluginMgr.class);
  private static List<String> failedToResolve = new CopyOnWriteArrayList<String>();
  /**
   * Load a plugin through the {@link org.jboss.bpm.console.server.util.ServiceLoader}.
   * The plugin interface name acts as the service key.
   *
   * @param type plugin interface
   * @return a plugin implementation of type T or null if the plugin is not available.
   */
  public static <T> T load(Class<T> type)
  {
    boolean failedBefore = failedToResolve.contains(type.getName());
    if(failedBefore) return null;

    T pluginImpl = (T) ServiceLoader.loadService(
        type.getName(), null
    );


    if(pluginImpl!=null)
    {
      log.info("Successfully loaded plugin '" +type.getName()+ "': "+pluginImpl.getClass());
      //return (T)InvocationProxy.newInstance(pluginImpl);
      return pluginImpl;
    }
    else
    {
      //log.warn("Unable to load plugin: '" + type.getName() + "'");
      failedToResolve.add(type.getName());
      return null;
    }
  }
}
