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

import java.util.List;
import java.net.URL;

import org.jboss.bpm.console.client.model.ActiveNodeInfo;
import org.jboss.bpm.console.client.model.DiagramInfo;

/**
 * Access process graph image and coordinates.
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public interface GraphViewerPlugin
{
  /**
   * Check {@link #getDiagramURL(String)} != null before invoking.   
   */
  byte[] getProcessImage(String processId);

  DiagramInfo getDiagramInfo(String processId);

  List<ActiveNodeInfo> getActiveNodeInfo(String instanceId);

  /**
   * Can be null, in case no diagram is associated with the process   
   */
  URL getDiagramURL(String id);
  
  /**
   * Collects node information (such as coordinates) for given processDefinitionId and selected activities.
   * Both arguments must be specified.
   * @param processDefinitionId process definition id which nodes information should be retrieved for
   * @param activities list of activity names treated as a filter
   * @return list of found node information. Can be empty list if no definition was found
   */
  List<ActiveNodeInfo> getNodeInfoForActivities(String processDefinitionId, List<String> activities);
}
