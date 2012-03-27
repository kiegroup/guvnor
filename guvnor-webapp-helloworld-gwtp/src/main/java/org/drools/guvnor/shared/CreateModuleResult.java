package org.drools.guvnor.shared;

import com.gwtplatform.dispatch.shared.Result;

/**
 * The result of a {@link CreateModuleAction} action.
 */
public class CreateModuleResult implements Result {

  private String uuid;

  public CreateModuleResult(final String uuid) {
    this.uuid = uuid;
  }

  /**
   * For serialization only.
   */
  @SuppressWarnings("unused")
  private CreateModuleResult() {
  }

  public String getUUID() {
    return uuid;
  }

}

