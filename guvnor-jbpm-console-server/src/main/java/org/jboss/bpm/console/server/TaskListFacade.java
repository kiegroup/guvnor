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
package org.jboss.bpm.console.server;

import com.google.gson.Gson;
import org.jboss.bpm.console.client.model.TaskRef;
import org.jboss.bpm.console.client.model.TaskRefWrapper;
import org.jboss.bpm.console.server.gson.GsonFactory;
import org.jboss.bpm.console.server.integration.ManagementFactory;
import org.jboss.bpm.console.server.integration.TaskManagement;
import org.jboss.bpm.console.server.plugin.PluginMgr;
import org.jboss.bpm.console.server.plugin.FormAuthorityRef;
import org.jboss.bpm.console.server.plugin.FormDispatcherPlugin;
import org.jboss.bpm.console.server.util.ProjectName;
import org.jboss.bpm.console.server.util.RsComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.List;

/**
 * REST server module for accessing task related data.
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Path("tasks")
@RsComment(
    title = "Task Lists",
    description = "Access task lists",
    project = {ProjectName.JBPM}
)
public class TaskListFacade
{
  private static final Logger log = LoggerFactory.getLogger(TaskMgmtFacade.class);

  private TaskManagement taskManagement;
  private FormDispatcherPlugin formPlugin;

  /**
   * Lazy load the {@link org.jboss.bpm.console.server.integration.TaskManagement}
   */
  private TaskManagement getTaskManagement()
  {
    if(null==this.taskManagement)
    {
      ManagementFactory factory = ManagementFactory.newInstance();
      this.taskManagement = factory.createTaskManagement();
      log.debug("Using ManagementFactory impl:" + factory.getClass().getName());
    }

    return this.taskManagement;
  }

  /**
   * Lazy load the {@link org.jboss.bpm.console.server.plugin.FormDispatcherPlugin}.
   * Can be null if the plugin is not available.
   */
  private FormDispatcherPlugin getFormDispatcherPlugin()
  {
    if(null==this.formPlugin)
    {
      this.formPlugin = PluginMgr.load(FormDispatcherPlugin.class);
    }

    return this.formPlugin;
  }

  @GET
  @Path("{idRef}")
  @Produces("application/json")
  public Response getTasksForIdRef(
      @PathParam("idRef")
      String idRef
  )
  {
    List<TaskRef> assignedTasks = getTaskManagement().getAssignedTasks(idRef);
    return processTaskListResponse(assignedTasks);
  }

  @GET
  @Path("{idRef}/participation")
  @Produces("application/json")
  public Response getTasksForIdRefParticipation(
      @PathParam("idRef")
      String idRef
  )
  {
    List<TaskRef> taskParticipation = getTaskManagement().getUnassignedTasks(idRef, null);
    return processTaskListResponse(taskParticipation);
  }

  private Response processTaskListResponse(List<TaskRef> taskList)
  {
    // decorate task form URL if plugin available
    FormDispatcherPlugin formPlugin = getFormDispatcherPlugin();
    if(formPlugin!=null)
    {
      for(TaskRef task : taskList)
      {
        URL taskFormURL = formPlugin.getDispatchUrl(
            new FormAuthorityRef(String.valueOf(task.getId()))
        );
        if(taskFormURL!=null)
        {
          task.setUrl(taskFormURL.toExternalForm());
        }
      }
    }

    TaskRefWrapper wrapper = new TaskRefWrapper(taskList);
    return createJsonResponse(wrapper);
  }

  private Response createJsonResponse(Object wrapper)
  {
    Gson gson = GsonFactory.createInstance();
    String json = gson.toJson(wrapper);
    return Response.ok(json).type("application/json").build();
  }
}
