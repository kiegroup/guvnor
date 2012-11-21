package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.impl.RemoteServiceProxy;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamWriter;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.impl.RequestCallbackAdapter.ResponseReader;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.RpcToken;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.core.client.impl.Impl;
import com.google.gwt.user.client.rpc.impl.RpcStatsContext;

public class ConfigurationService_Proxy extends RemoteServiceProxy implements org.drools.guvnor.client.rpc.ConfigurationServiceAsync {
  private static final String REMOTE_SERVICE_INTERFACE_NAME = "org.drools.guvnor.client.rpc.ConfigurationService";
  private static final String SERIALIZATION_POLICY ="C80F6FBD70FE21AC21B414B306880EBA";
  private static final org.drools.guvnor.client.rpc.ConfigurationService_TypeSerializer SERIALIZER = new org.drools.guvnor.client.rpc.ConfigurationService_TypeSerializer();
  
  public ConfigurationService_Proxy() {
    super(GWT.getModuleBaseURL(),
      "configurationService", 
      SERIALIZATION_POLICY, 
      SERIALIZER);
  }
  
  public void loadPreferences(com.google.gwt.user.client.rpc.AsyncCallback preferences) {
    RpcStatsContext statsContext = new RpcStatsContext();
    boolean toss = statsContext.isStatsAvailable() && statsContext.stats(statsContext.timeStat("ConfigurationService_Proxy.loadPreferences", "begin"));
    SerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    try {
      if (getRpcToken() != null) {
        streamWriter.writeObject(getRpcToken());
      }
      streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
      streamWriter.writeString("loadPreferences");
      streamWriter.writeInt(0);
      String payload = streamWriter.toString();
      toss = statsContext.isStatsAvailable() && statsContext.stats(statsContext.timeStat("ConfigurationService_Proxy.loadPreferences",  "requestSerialized"));
      doInvoke(ResponseReader.OBJECT, "ConfigurationService_Proxy.loadPreferences", statsContext, payload, preferences);
    } catch (SerializationException ex) {
      preferences.onFailure(ex);
    }
  }
  @Override
  public SerializationStreamWriter createStreamWriter() {
    ClientSerializationStreamWriter toReturn =
      (ClientSerializationStreamWriter) super.createStreamWriter();
    if (getRpcToken() != null) {
      toReturn.addFlags(ClientSerializationStreamWriter.FLAG_RPC_TOKEN_INCLUDED);
    }
    return toReturn;
  }
  @Override
  protected void checkRpcTokenType(RpcToken token) {
    if (!(token instanceof com.google.gwt.user.client.rpc.XsrfToken)) {
      throw new RpcTokenException("Invalid RpcToken type: expected 'com.google.gwt.user.client.rpc.XsrfToken' but got '" + token.getClass() + "'");
    }
  }
}
