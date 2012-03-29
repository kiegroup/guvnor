package org.drools.guvnor.shared;

import com.gwtplatform.dispatch.shared.Result;

/**
 * The result of a {@link LoadModuleAction} action.
 */
public class LoadModuleResult implements Result {

  private Module module;

  public LoadModuleResult(final Module module) {
    this.module = module;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private LoadModuleResult() {
  }

  public Module getModule() {
    return module;
  }

}

