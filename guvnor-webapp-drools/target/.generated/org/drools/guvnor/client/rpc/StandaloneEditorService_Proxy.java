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

public class StandaloneEditorService_Proxy extends RemoteServiceProxy implements org.drools.guvnor.client.rpc.StandaloneEditorServiceAsync {
  private static final String REMOTE_SERVICE_INTERFACE_NAME = "org.drools.guvnor.client.rpc.StandaloneEditorService";
  private static final String SERIALIZATION_POLICY ="34D679DC3DD6A4C7138DF0716B50EC23";
  private static final org.drools.guvnor.client.rpc.StandaloneEditorService_TypeSerializer SERIALIZER = new org.drools.guvnor.client.rpc.StandaloneEditorService_TypeSerializer();
  
  public StandaloneEditorService_Proxy() {
    super(GWT.getModuleBaseURL(),
      "standaloneEditorService", 
      SERIALIZATION_POLICY, 
      SERIALIZER);
  }
  
  public void getAsstesBRL(org.drools.guvnor.client.rpc.Asset[] assets, com.google.gwt.user.client.rpc.AsyncCallback asyncCallback) {
    RpcStatsContext statsContext = new RpcStatsContext();
    boolean toss = statsContext.isStatsAvailable() && statsContext.stats(statsContext.timeStat("StandaloneEditorService_Proxy.getAsstesBRL", "begin"));
    SerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    try {
      if (getRpcToken() != null) {
        streamWriter.writeObject(getRpcToken());
      }
      streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
      streamWriter.writeString("getAsstesBRL");
      streamWriter.writeInt(1);
      streamWriter.writeString("[Lorg.drools.guvnor.client.rpc.Asset;/228832219");
      streamWriter.writeObject(assets);
      String payload = streamWriter.toString();
      toss = statsContext.isStatsAvailable() && statsContext.stats(statsContext.timeStat("StandaloneEditorService_Proxy.getAsstesBRL",  "requestSerialized"));
      doInvoke(ResponseReader.OBJECT, "StandaloneEditorService_Proxy.getAsstesBRL", statsContext, payload, asyncCallback);
    } catch (SerializationException ex) {
      asyncCallback.onFailure(ex);
    }
  }
  
  public void getAsstesDRL(org.drools.guvnor.client.rpc.Asset[] assets, com.google.gwt.user.client.rpc.AsyncCallback asyncCallback) {
    RpcStatsContext statsContext = new RpcStatsContext();
    boolean toss = statsContext.isStatsAvailable() && statsContext.stats(statsContext.timeStat("StandaloneEditorService_Proxy.getAsstesDRL", "begin"));
    SerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    try {
      if (getRpcToken() != null) {
        streamWriter.writeObject(getRpcToken());
      }
      streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
      streamWriter.writeString("getAsstesDRL");
      streamWriter.writeInt(1);
      streamWriter.writeString("[Lorg.drools.guvnor.client.rpc.Asset;/228832219");
      streamWriter.writeObject(assets);
      String payload = streamWriter.toString();
      toss = statsContext.isStatsAvailable() && statsContext.stats(statsContext.timeStat("StandaloneEditorService_Proxy.getAsstesDRL",  "requestSerialized"));
      doInvoke(ResponseReader.OBJECT, "StandaloneEditorService_Proxy.getAsstesDRL", statsContext, payload, asyncCallback);
    } catch (SerializationException ex) {
      asyncCallback.onFailure(ex);
    }
  }
  
  public void getInvocationParameters(java.lang.String parametersUUID, com.google.gwt.user.client.rpc.AsyncCallback asyncCallback) {
    RpcStatsContext statsContext = new RpcStatsContext();
    boolean toss = statsContext.isStatsAvailable() && statsContext.stats(statsContext.timeStat("StandaloneEditorService_Proxy.getInvocationParameters", "begin"));
    SerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    try {
      if (getRpcToken() != null) {
        streamWriter.writeObject(getRpcToken());
      }
      streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
      streamWriter.writeString("getInvocationParameters");
      streamWriter.writeInt(1);
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString(parametersUUID);
      String payload = streamWriter.toString();
      toss = statsContext.isStatsAvailable() && statsContext.stats(statsContext.timeStat("StandaloneEditorService_Proxy.getInvocationParameters",  "requestSerialized"));
      doInvoke(ResponseReader.OBJECT, "StandaloneEditorService_Proxy.getInvocationParameters", statsContext, payload, asyncCallback);
    } catch (SerializationException ex) {
      asyncCallback.onFailure(ex);
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
