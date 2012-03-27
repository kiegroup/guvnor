package org.drools.guvnor.server.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.drools.guvnor.shared.CreateModuleAction;
import org.drools.guvnor.shared.CreateModuleResult;

/**
 * @author Philippe Beaudoin
 */
public class CreateModuleHandler implements
    ActionHandler<CreateModuleAction, CreateModuleResult> {

  private Provider<HttpServletRequest> requestProvider;
  private ServletContext servletContext;

  @Inject
  CreateModuleHandler(ServletContext servletContext,
      Provider<HttpServletRequest> requestProvider) {
    this.servletContext = servletContext;
    this.requestProvider = requestProvider;
  }

  @Override
  public CreateModuleResult execute(CreateModuleAction action,
      ExecutionContext context) throws ActionException {

    return new CreateModuleResult("testUUID");
  }

  @Override
  public Class<CreateModuleAction> getActionType() {
    return CreateModuleAction.class;
  }

  @Override
  public void undo(CreateModuleAction action, CreateModuleResult result,
      ExecutionContext context) throws ActionException {
    // Not undoable
  }

}
