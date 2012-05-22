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
import org.jboss.bpm.console.client.model.DeploymentRef;
import org.jboss.bpm.console.client.model.DeploymentRefWrapper;
import org.jboss.bpm.console.client.model.JobRef;
import org.jboss.bpm.console.client.model.JobRefWrapper;
import org.jboss.bpm.console.server.gson.GsonFactory;
import org.jboss.bpm.console.server.plugin.ProcessEnginePlugin;
import org.jboss.bpm.console.server.plugin.PluginMgr;
import org.jboss.bpm.console.server.util.ProjectName;
import org.jboss.bpm.console.server.util.RsComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Manage process deployments
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Path("engine")
@RsComment(
    title = "Process Engine",
    description = "Process runtime state"
)
public class EngineFacade
{
  private static final Logger log = LoggerFactory.getLogger(EngineFacade.class);

  private ProcessEnginePlugin processEnginePlugin;

  private ProcessEnginePlugin getDeploymentPlugin()
  {
    if(null==this.processEnginePlugin)
    {
      this.processEnginePlugin = PluginMgr.load(ProcessEnginePlugin.class);
    }

    return this.processEnginePlugin;
  }

  @GET
  @Path("deployments")
  @Produces("application/json")
  public Response getDeployments()
  {

    ProcessEnginePlugin dplPlugin = getDeploymentPlugin();
    if(processEnginePlugin !=null)
    {
      List<DeploymentRef> dpls = dplPlugin.getDeployments();
      return createJsonResponse(
          new DeploymentRefWrapper(dpls)
      );
    }
    else
    {
      log.error("ProcessEnginePlugin not available");
      return Response.serverError().build();
    }

  }

  @POST
  @Path("deployment/{id}/suspend")
  @Produces("application/json")
  @RsComment(project = {ProjectName.JBPM})
  public Response suspendDeployment(
      @PathParam("id")
      String id
  )
  {
    return doSuspend(id, true);
  }

  @POST
  @Path("deployment/{id}/resume")
  @Produces("application/json")
  @RsComment(project = {ProjectName.JBPM})
  public Response resumeDeployment(
      @PathParam("id")
      String id
  )
  {
    return doSuspend(id, false);
  }

  private Response doSuspend(String id, boolean suspended)
  {
    ProcessEnginePlugin dplPlugin = getDeploymentPlugin();
    if(processEnginePlugin !=null)
    {
      processEnginePlugin.suspendDeployment(id, suspended);
      return Response.ok().build();
    }
    else
    {
      log.error("ProcessEnginePlugin not available");
      return Response.serverError().build();
    }
  }

  @POST
  @Path("deployment/{id}/delete")
  @Produces("application/json")
  @RsComment(project = {ProjectName.JBPM})
  public Response deleteDeployment(
      @PathParam("id")
      String id
  )
  {

    ProcessEnginePlugin dplPlugin = getDeploymentPlugin();
    if(processEnginePlugin !=null)
    {
      processEnginePlugin.deleteDeployment(id);
      return Response.ok().build();
    }
    else
    {
      log.error("ProcessEnginePlugin not available");
      return Response.serverError().build();
    }

  }


  @GET
  @Path("jobs")
  @Produces("application/json")
  @RsComment(project = {ProjectName.JBPM})
  public Response getJobs()
  {

    ProcessEnginePlugin dplPlugin = getDeploymentPlugin();
    if(processEnginePlugin !=null)
    {
      List<JobRef> jobs = dplPlugin.getJobs();
      return createJsonResponse(
          new JobRefWrapper(jobs)
      );
    }
    else
    {
      log.error("ProcessEnginePlugin not available");
      return Response.serverError().build();
    }

  }

  @POST
  @Path("job/{id}/execute")
  @Produces("application/json")
  @RsComment(project = {ProjectName.JBPM})
  public Response executeJob(
      @PathParam("id")
      String id
  )
  {

    ProcessEnginePlugin dplPlugin = getDeploymentPlugin();
    if(processEnginePlugin !=null)
    {
      dplPlugin.executeJob(id);
      return Response.ok().build();
    }
    else
    {
      log.error("ProcessEnginePlugin not available");
      return Response.serverError().build();
    }

  }

  private Response createJsonResponse(Object wrapper)
  {
    Gson gson = GsonFactory.createInstance();
    String json = gson.toJson(wrapper);
    return Response.ok(json).type("application/json").build();
  }
}
