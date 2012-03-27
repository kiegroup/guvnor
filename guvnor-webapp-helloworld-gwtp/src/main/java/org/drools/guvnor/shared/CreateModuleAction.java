package org.drools.guvnor.shared;

import com.gwtplatform.dispatch.shared.UnsecuredActionImpl;

/**
 * An action that can be sent using an {@link DispatchAsync} (client-side)
 * corresponding to a {@link com.gwtplatform.dispatch.server.Dispatch}
 * (server-side).
 */
public class CreateModuleAction extends
    UnsecuredActionImpl<CreateModuleResult> {

  private String moduleName;

  public CreateModuleAction(final String moduleName) {
    this.moduleName = moduleName;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private CreateModuleAction() {
  }

  public String getModuleName() {
    return moduleName;
  }
}
