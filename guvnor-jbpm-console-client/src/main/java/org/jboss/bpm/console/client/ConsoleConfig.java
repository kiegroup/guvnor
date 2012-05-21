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
package org.jboss.bpm.console.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;

/**
 * Initialize console config from host page (<code>Application.html</code>) variable:
 * <pre>
 *  var consoleConfig = {
 * consoleServerUrl: "http://localhost:8080/gwt-console-server",
 * reportServerUrl: "http://localhost:8080/report",
 * [...]
 * };
 * </pre>
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 * @see com.google.gwt.i18n.client.Dictionary
 */
public class ConsoleConfig implements Config
{
  private String serverWebContext;

  private String overallReportFile;
  private String processSummaryReportFile;
  private String instanceSummaryReportFile;

  private String profileName;
  private String logo;

  private String consoleServerUrl;

  private String defaultEditor;
  
  public ConsoleConfig(String proxyUrl)
  {
    Dictionary theme = Dictionary.getDictionary("consoleConfig");
    profileName = theme.get("profileName");
    logo = theme.get("logo");

    serverWebContext = theme.get("serverWebContext");

    overallReportFile = theme.get("overallReportFile");
    processSummaryReportFile = theme.get("processSummaryReportFile");
    instanceSummaryReportFile = theme.get("instanceSummaryReportFile");

    defaultEditor = theme.get("defaultEditor");
    
    if(null==proxyUrl)
    {
      // extract host
      String base = GWT.getHostPageBaseURL();
      String protocol = base.substring(0, base.indexOf("//")+2);
      String noProtocol = base.substring(base.indexOf(protocol)+protocol.length(), base.length());
      String host = noProtocol.substring(0, noProtocol.indexOf("/"));

      // default url
      consoleServerUrl = protocol + host + serverWebContext;
    }
    else
    {
      consoleServerUrl = proxyUrl;
    }

    // features

  }

  public String getHost()
  {
    String host = null;
    if(!GWT.isScript()) // development with proxy
    {
      host = consoleServerUrl;
    }
    else
    {
      String baseUrl = GWT.getModuleBaseURL();
      host = baseUrl.substring(
          0, baseUrl.indexOf("app")
      );
    }

    return host;
  }

  public String getProfileName()
  {
    return profileName;
  }

  public String getLogo()
  {
    return logo;
  }

  public String getDefaultEditor()
  {
    return defaultEditor;
  }

  public String getConsoleServerUrl()
  {
    if(consoleServerUrl ==null)
      throw new RuntimeException("Config not properly setup: console server URL is null");
    return consoleServerUrl;
  }

  public void setConsoleServerUrl(String consoleServerUrl)
  {
    this.consoleServerUrl = consoleServerUrl;
  }

  public String getServerWebContext()
  {
    return serverWebContext;
  }

  public void setServerWebContext(String serverWebContext)
  {
    this.serverWebContext = serverWebContext;
  }

  public String getOverallReportFile()
  {
    return overallReportFile;
  }

  public void setOverallReportFile(String overallReportFile)
  {
    this.overallReportFile = overallReportFile;
  }

  public String getProcessSummaryReportFile()
  {
    return processSummaryReportFile;
  }

  public void setProcessSummaryReportFile(String processSummaryReportFile)
  {
    this.processSummaryReportFile = processSummaryReportFile;
  }

  public String getInstanceSummaryReportFile()
  {
    return instanceSummaryReportFile;
  }

  public void setInstanceSummaryReportFile(String instanceSummaryReportFile)
  {
    this.instanceSummaryReportFile = instanceSummaryReportFile;
  }
}
