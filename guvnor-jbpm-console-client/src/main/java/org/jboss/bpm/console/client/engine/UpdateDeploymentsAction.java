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
package org.jboss.bpm.console.client.engine;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.mvc4g.client.Controller;
import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.URLBuilder;
import org.jboss.bpm.console.client.common.*;
import org.jboss.bpm.console.client.model.DTOParser;
import org.jboss.bpm.console.client.model.DeploymentRef;
import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.base.MessageBuilder;

import java.util.List;

/**
 * Updates a list of deployment and can optionally select a particular one.
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class UpdateDeploymentsAction extends AbstractRESTAction
{

  public final static String ID = UpdateDeploymentsAction.class.getName();

    protected UpdateDeploymentsAction(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public String getId()
  {
    return ID;
  }

  public String getUrl(Object event)
  {
    return URLBuilder.getInstance().getDeploymentsUrl();
  }

  public RequestBuilder.Method getRequestMethod()
  {
    return RequestBuilder.GET;
  }

  @Override
  protected DataDriven getDataDriven(Controller controller)
  {
    return (DeploymentListView)controller.getView(DeploymentListView.ID);
  }

  public void handleSuccessfulResponse(
      final Controller controller, final Object event, Response response)
  {
    DeploymentListView view = (DeploymentListView)controller.getView(DeploymentListView.ID);

    // deployment could be disabled
    if(view!=null)
    {
      JSONValue json = JSONParser.parse(response.getText());
      List<DeploymentRef> deployments = DTOParser.parseDeploymentRefList(json);

      if(null==view)
        throw new RuntimeException("View not initialized: " + DeploymentListView.ID);

      view.update(deployments);

      // optional
      String deploymentId = (String)event;
      if(deploymentId!=null)              
        view.select(deploymentId);

      // refresh process definitions
      MessageBuilder.createMessage()
          .toSubject(Model.SUBJECT)
          .command(ModelCommands.HAS_BEEN_UPDATED)
          .with(ModelParts.CLASS, Model.DEPLOYMENT_MODEL)
          .noErrorHandling()
          .sendNowWith(ErraiBus.get()
          );
    }
  }
}
