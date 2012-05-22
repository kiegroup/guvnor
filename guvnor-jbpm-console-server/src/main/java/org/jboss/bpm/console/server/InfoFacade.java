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

import org.jboss.bpm.console.server.plugin.FormDispatcherPlugin;
import org.jboss.bpm.console.server.plugin.PluginMgr;
import org.jboss.bpm.console.server.plugin.GraphViewerPlugin;
import org.jboss.bpm.console.server.plugin.ProcessEnginePlugin;
import org.jboss.bpm.console.server.gson.GsonFactory;
import org.jboss.bpm.console.server.util.RsDocBuilder;
import org.jboss.bpm.console.server.util.RsComment;
import org.jboss.bpm.console.client.model.ServerStatus;
import org.jboss.bpm.console.client.model.PluginInfo;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Path("server")
@RsComment(
    title = "Server Info",
    description = "General REST server information"
)
public class InfoFacade
{

  private Class[] pluginInterfaces = {
      FormDispatcherPlugin.class,
      GraphViewerPlugin.class,
      ProcessEnginePlugin.class
  };

  private ServerStatus status = null;

  @GET
  @Path("status")
  @Produces("application/json")
  @RsComment(
      title = "Plugins",
      description = "Plugin availability"
  )
  public Response getServerInfo()
  {
    ServerStatus status = getServerStatus();
    return createJsonResponse(status);
  }

  private ServerStatus getServerStatus()
  {
    if(null==this.status) // expensive to create
    {
      this.status = new ServerStatus();
      for(Class type : pluginInterfaces)
      {
        Object impl = PluginMgr.load(type);
        boolean isAvailable = (impl!=null);

        status.getPlugins().add(new PluginInfo(type.getName(), isAvailable));
      }
    }
    return status;
  }

  @GET
  @Path("resources/{project}")
  @Produces("text/html")
  public Response getPublishedUrls(
      @Context
      HttpServletRequest request,
      @PathParam("project")
      String projectName
  )
  {
    final Class[] rootResources = getRSResources();

    String rsServer = request.getContextPath();
    if (request.getServletPath() != null && !"".equals(request.getServletPath())) {
    	rsServer = request.getContextPath() + request.getServletPath();
    }
    
    RsDocBuilder rsDocBuilder = new RsDocBuilder(rsServer,rootResources);
    StringBuffer sb = rsDocBuilder.build2HTML(projectName);
    return Response.ok(sb.toString()).build();
  }

  private Response createJsonResponse(Object wrapper)
  {
    Gson gson = GsonFactory.createInstance();
    String json = gson.toJson(wrapper);
    return Response.ok(json).type("application/json").build();
  }


  public static Class[] getRSResources() {
    return new Class[]
        {
            InfoFacade.class,
            ProcessMgmtFacade.class,
            TaskListFacade.class,
            TaskMgmtFacade.class,
            UserMgmtFacade.class,
            EngineFacade.class,
            FormProcessingFacade.class,
            ProcessHistoryFacade.class
        };
  }
}
