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

import org.jboss.bpm.console.client.model.ProcessInstanceRef;
import org.jboss.bpm.console.server.integration.ManagementFactory;
import org.jboss.bpm.console.server.integration.ProcessManagement;
import org.jboss.bpm.console.server.integration.TaskManagement;
import org.jboss.bpm.console.server.plugin.FormAuthorityRef;
import org.jboss.bpm.console.server.plugin.FormDispatcherPlugin;
import org.jboss.bpm.console.server.plugin.PluginMgr;
import org.jboss.bpm.console.server.util.ProjectName;
import org.jboss.bpm.console.server.util.RsComment;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Path("form")
@RsComment(
    title = "Form Processing",
    description = "Web based form processing",
    project = {ProjectName.JBPM}
)
public class FormProcessingFacade
{
  private static final Logger log = LoggerFactory.getLogger(FormProcessingFacade.class);

  private FormDispatcherPlugin formPlugin;

  private ProcessManagement processManagement;

  private TaskManagement taskManagement;
  private static final String SUCCESSFULLY_PROCESSED_INPUT =
      "<div style='font-family:sans-serif; padding:10px;'>" +
          "<h3>Successfully processed input</h3><p/>" +
          "You can now close this window." +
          "</div>";

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

  private ProcessManagement getProcessManagement()
  {
    if(null==this.processManagement)
    {
      ManagementFactory factory = ManagementFactory.newInstance();
      this.processManagement = factory.createProcessManagement();
      log.debug("Using ManagementFactory impl:" + factory.getClass().getName());
    }

    return this.processManagement;
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


  @GET
  @Path("task/{id}/render")
  @Produces("text/html")
  public Response renderTaskUI(
      @PathParam("id")
      String taskId
  )
  {
    return provideForm(new FormAuthorityRef(taskId));
  }

  @GET
  @Path("process/{id}/render")
  @Produces("text/html")
  public Response renderProcessUI(
      @PathParam("id")
      String definitionId
  )
  {
    return provideForm(new FormAuthorityRef(definitionId, FormAuthorityRef.Type.PROCESS));
  }

  @POST
  @Path("task/{id}/complete")
  @Produces("text/html")
  @Consumes("multipart/form-data")
  public Response closeTaskWithUI(
      @Context
      HttpServletRequest request,
      @PathParam("id")
      String taskId,
      MultipartFormDataInput payload
  )
  {
    FieldMapping mapping = createFieldMapping(payload);

    // complete task
    String username = request.getUserPrincipal() != null ?
        request.getUserPrincipal().getName() : null;

    String outcomeDirective = mapping.directives.get("outcome");

    if(outcomeDirective!=null)
    {
      getTaskManagement().completeTask(
          Long.valueOf(taskId), // TODO: change to string id's
          outcomeDirective, // actually a plugin implementation detail
          mapping.processVars,
          username
      );
    }
    else
    {
      getTaskManagement().completeTask(
          Long.valueOf(taskId),
          mapping.processVars,
          username
      );
    }

    return Response.ok(SUCCESSFULLY_PROCESSED_INPUT).build();
  }

  @POST
  @Path("process/{id}/complete")
  @Produces("text/html")
  @Consumes("multipart/form-data")
  public Response startProcessWithUI(
      @Context
      HttpServletRequest request,
      @PathParam("id")
      String definitionId,
      MultipartFormDataInput payload
  )
  {
    FieldMapping mapping = createFieldMapping(payload);

    // start process
    ProcessInstanceRef instance =
        getProcessManagement().newInstance(definitionId, mapping.processVars);

    return Response.ok(SUCCESSFULLY_PROCESSED_INPUT).build();
  }

  private Response provideForm(FormAuthorityRef authorityRef)
  {
    DataHandler dh = getFormDispatcherPlugin().provideForm(
        authorityRef
    );

    if(null==dh)
    {
      throw new RuntimeException("No UI associated with "+authorityRef.getType()+" " + authorityRef.getReferenceId());
    }

    return Response.ok(dh.getDataSource()).type("text/html").build();
  }

  private FieldMapping createFieldMapping(MultipartFormDataInput payload)
  {
    FieldMapping mapping = new FieldMapping();

    Map<String, InputPart> formData = payload.getFormData();
    Iterator<String> partNames = formData.keySet().iterator();

    while(partNames.hasNext())
    {
      final String partName = partNames.next();
      final InputPart part = formData.get(partName);
      final MediaType mediaType = part.getMediaType();

      String mType = mediaType.getType();
      String mSubtype = mediaType.getSubtype();

      try{
          if("text".equals(mType) && "plain".equals(mSubtype))
          {
            // RFC2045: Each part has an optional "Content-Type" header
            // that defaults to "text/plain".
            // Can go into process without conversion
            if(mapping.isReserved(partName))
              mapping.directives.put(partName, part.getBodyAsString());
            else
              mapping.processVars.put(partName, part.getBodyAsString());
          }
          else
          {
            // anything else turns into a DataHandler
            final byte[] data = part.getBodyAsString().getBytes();
            DataHandler dh = new DataHandler(
                new DataSource()
                {
                  public InputStream getInputStream() throws IOException
                  {
                    return new ByteArrayInputStream(data);
                  }

                  public OutputStream getOutputStream() throws IOException
                  {
                    throw new RuntimeException("This is a readonly DataHandler");
                  }

                  public String getContentType()
                  {
                    return mediaType.getType();
                  }

                  public String getName()
                  {
                    return partName;
                  }
                }
            );
            mapping.processVars.put(partName, dh);
          }
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
    }

    return mapping;
  }

  private class FieldMapping
  {
    final String[] reservedNames = {"outcome", "form"};   // TODO: implementation detail of the form plugin

    Map<String,Object> processVars = new HashMap<String,Object>();
    Map<String,String> directives = new HashMap<String,String>();

    public boolean isReserved(String name)
    {
      boolean result = false;
      for(String s : reservedNames)
      {
        if(s.equals(name))
        {
          result = true;
          break;
        }
      }
      return result;
    }
  }
}
