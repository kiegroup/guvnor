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
import org.jboss.bpm.console.client.model.RoleAssignmentRef;
import org.jboss.bpm.console.client.model.RoleAssignmentRefWrapper;
import org.jboss.bpm.console.server.gson.GsonFactory;
import org.jboss.bpm.console.server.integration.ManagementFactory;
import org.jboss.bpm.console.server.integration.UserManagement;
import org.jboss.bpm.console.server.util.ProjectName;
import org.jboss.bpm.console.server.util.RsComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * REST server module for accessing user related data.
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Path("identity")
@RsComment(
    title = "User management",
    description = "Manage user and groups",
    project = {ProjectName.JBPM}
)
public class UserMgmtFacade
{

  private static final Logger log = LoggerFactory.getLogger(UserMgmtFacade.class);

  private UserManagement userManagement;

  private UserManagement getUserManagement()
  {
    if(null==userManagement)
    {
      ManagementFactory factory = ManagementFactory.newInstance();
      this.userManagement = factory.createUserManagement();
    }

    return this.userManagement;
  }

  @GET
  @Path("sid")
  @Produces("text/plain")
  public Response getSessionID(
      @Context
      HttpServletRequest request
  )
  {
    return Response.ok(request.getSession().getId()).build();
  }

  @POST
  @Path("sid/invalidate")
  @Produces("text/plain")
  public Response destroySession(
      @Context
      HttpServletRequest request
  )
  {
    request.getSession().invalidate();
    return Response.ok().build();
  }

  @GET
  @Path("secure/sid")
  @Produces("text/plain")
  public Response getSessionIDSecure(
      @Context
      HttpServletRequest request
  )
  {
    return Response.ok(request.getSession().getId()).build();
  }

  @GET
  @Path("user/roles")
  @Produces("application/json")
  public Response getRolesForJAASPrincipal(
      @Context
      HttpServletRequest request,
      @QueryParam("roleCheck")
      String roleCheck
  )
  {
    // TODO: Why not use the identity DB right away?
    // These roles are security roles
    if(null==roleCheck)
      throw new WebApplicationException( new IllegalArgumentException("Missing parameter 'roleCheck'") );

    log.debug("Role check user: " + request.getUserPrincipal().getName() + ", actualRoles requested: " + roleCheck);

    List<RoleAssignmentRef> actualRoles = new ArrayList<RoleAssignmentRef>();

    StringTokenizer tok = new StringTokenizer(roleCheck, ",");
    while(tok.hasMoreTokens())
    {
      String possibleRole = tok.nextToken();
      actualRoles.add( new RoleAssignmentRef(possibleRole, request.isUserInRole(possibleRole)));
    }
    return createJsonResponse( new RoleAssignmentRefWrapper(actualRoles));
  }

  @GET
  @Path("user/{actorId}/groups/")
  @Produces("application/json")
  public Response getGroupsForActor(
      @PathParam("actorId")
      String actorId
  )
  {
    List<String> groups = getUserManagement().getGroupsForActor(actorId);
    return createJsonResponse(groups);
  }

  @GET
  @Path("group/{groupName}/members")
  @Produces("application/json")
  public Response getActorsForGroup(
      @PathParam("groupName")
      String groupName
  )
  {
    List<String> groups = getUserManagement().getActorsForGroup(groupName);
    return createJsonResponse(groups);
  }

  @GET
  @Path("user/{actorId}/actors")
  @Produces("application/json")
  public Response getAvailableActors(
      @PathParam("actorId")
      String actorId
  )
  {
    Set<String> users = new HashSet<String>();
    List<String> groups = getUserManagement().getGroupsForActor(actorId);
    for(String group : groups)
    {
      List<String> actors = getUserManagement().getActorsForGroup(group);
      users.addAll(actors);
    }

    List<String> availableActors = new ArrayList<String>();
    availableActors.addAll(users);
    availableActors.addAll(groups);
    return createJsonResponse(availableActors);
  }

  private Response createJsonResponse(Object wrapper)
  {
    Gson gson = GsonFactory.createInstance();
    String json = gson.toJson(wrapper);
    return Response.ok(json).type("application/json").build();
  }
}