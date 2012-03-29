package org.drools.guvnor.shared;

import com.gwtplatform.dispatch.shared.UnsecuredActionImpl;

/**
 * An action that can be sent using an {@link DispatchAsync} (client-side)
 * corresponding to a {@link com.gwtplatform.dispatch.server.Dispatch}
 * (server-side).
 */
public class LoadModuleAction extends
    UnsecuredActionImpl<LoadModuleResult> {

  private String moduleName;

  public LoadModuleAction(final String moduleName) {
    this.moduleName = moduleName;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private LoadModuleAction() {
  }

  public String getModuleName() {
    return moduleName;
  }
}
