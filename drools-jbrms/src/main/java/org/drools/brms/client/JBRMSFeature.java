package org.drools.brms.client;

import com.google.gwt.user.client.ui.Composite;

/**
 * A 'feature' is a single panel of the JBRMS console. They are meant to be lazily
 * instantiated so that the application doesn't pay for all of them on startup.
 */
public abstract class JBRMSFeature extends Composite {

  /**
   * Encapsulated information about a JBRMS Feature. Each component is expected to have a
   * static <code>init()</code> method that will be called by the layout on startup.
   */
  public abstract static class ComponentInfo {
    private JBRMSFeature instance;
    private String name, description;

    public ComponentInfo(String name, String desc) {
      this.name = name;
      description = desc;
    }

    public abstract JBRMSFeature createInstance();

    public String getDescription() {
      return description;
    }

    public final JBRMSFeature getInstance() {
      if (instance != null)
        return instance;
      return (instance = createInstance());
    }

    public String getName() {
      return name;
    }
  }

  /**
   * Called just before this sink is hidden.
   */
  public void onHide() {
  }

  /**
   * Called just after this sink is shown.
   */
  public void onShow() {
  }
}
