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

import org.jboss.bpm.console.server.integration.ManagementFactory;
import org.jboss.bpm.console.server.integration.TaskManagement;
import org.jboss.bpm.console.server.plugin.FormDispatcherPlugin;
import org.jboss.bpm.console.server.plugin.PluginMgr;
import org.jboss.bpm.console.server.util.ProjectName;
import org.jboss.bpm.console.server.util.RsComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * REST server module for accessing task related data.
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Path("task")
@RsComment(
    title = "Task Management",
    description = "Manage task instances",
    project = {ProjectName.JBPM}
)
public class TaskMgmtFacade
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
   * Lazy load the {@link org.jboss.bpm.console.server.integration.TaskManagement}
   */
  private FormDispatcherPlugin getFormDispatcherPlugin()
  {
    if(null==this.formPlugin)
    {
      this.formPlugin = PluginMgr.load(FormDispatcherPlugin.class);
      log.debug("Using FormDispatcherPlugin impl:" + this.formPlugin);
    }

    return this.formPlugin;
  }

  @POST
  @Path("{taskId}/assign/{ifRef}")
  @Produces("application/json")
  public Response assignTask(
      @Context
      HttpServletRequest request,
      @PathParam("taskId")
      long taskId,
      @PathParam("ifRef")
      String idRef
  )
  {
    log.debug("Assign task " + taskId + " to '" + idRef +"'");
    getTaskManagement().assignTask(taskId, idRef, request.getUserPrincipal().getName());
    return Response.ok().build();
  }

  @POST
  @Path("{taskId}/release")
  @Produces("application/json")
  public Response releaseTask(
      @Context
      HttpServletRequest request,
      @PathParam("taskId")
      long taskId
  )
  {
    log.debug("Release task " + taskId);
    getTaskManagement().assignTask(taskId, null, request.getUserPrincipal().getName());
    return Response.ok().build();
  }

  @POST
  @Path("{taskId}/close")
  @Produces("application/json")
  public Response closeTask(
      @Context
      HttpServletRequest request,
      @PathParam("taskId")
      long taskId
  )
  {
    log.debug("Close task " + taskId );
    getTaskManagement().completeTask(taskId, null, request.getUserPrincipal().getName());
    return Response.ok().build();
  }

  @POST
  @Path("{taskId}/close/{outcome}")
  @Produces("application/json")
  public Response closeTaskWithSignal(
      @Context
      HttpServletRequest request,
      @PathParam("taskId")
      long taskId,
      @QueryParam("outcome")
      String outcome
  )
  {
    log.debug("Close task " + taskId + " outcome " + outcome);
    getTaskManagement().completeTask(taskId, outcome, null, request.getUserPrincipal().getName());
    return Response.ok().build();
  }

}
