package org.drools.guvnor.server.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.drools.guvnor.shared.LoadModuleAction;
import org.drools.guvnor.shared.LoadModuleResult;
import org.drools.guvnor.shared.Module;

/**
 * @author Philippe Beaudoin
 */
public class LoadModuleHandler implements
    ActionHandler<LoadModuleAction, LoadModuleResult> {

  private Provider<HttpServletRequest> requestProvider;
  private ServletContext servletContext;

  @Inject
  LoadModuleHandler(ServletContext servletContext,
      Provider<HttpServletRequest> requestProvider) {
    this.servletContext = servletContext;
    this.requestProvider = requestProvider;
  }

  @Override
  public LoadModuleResult execute(LoadModuleAction action,
      ExecutionContext context) throws ActionException {
      Module m = new Module();
      return new LoadModuleResult(m);
  }

  @Override
  public Class<LoadModuleAction> getActionType() {
    return LoadModuleAction.class;
  }

  @Override
  public void undo(LoadModuleAction action, LoadModuleResult result,
      ExecutionContext context) throws ActionException {
    // Not undoable
  }

}
