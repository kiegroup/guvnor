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

public class VerificationService_Proxy extends RemoteServiceProxy implements org.drools.guvnor.client.rpc.VerificationServiceAsync {
  private static final String REMOTE_SERVICE_INTERFACE_NAME = "org.drools.guvnor.client.rpc.VerificationService";
  private static final String SERIALIZATION_POLICY ="793C55D2235A526EF67C7D2FFAA995DC";
  private static final org.drools.guvnor.client.rpc.VerificationService_TypeSerializer SERIALIZER = new org.drools.guvnor.client.rpc.VerificationService_TypeSerializer();
  
  public VerificationService_Proxy() {
    super(GWT.getModuleBaseURL(),
      "verificationService", 
      SERIALIZATION_POLICY, 
      SERIALIZER);
  }
  
  public void analysePackage(java.lang.String packageUUID, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    RpcStatsContext statsContext = new RpcStatsContext();
    boolean toss = statsContext.isStatsAvailable() && statsContext.stats(statsContext.timeStat("VerificationService_Proxy.analysePackage", "begin"));
    SerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    try {
      if (getRpcToken() != null) {
        streamWriter.writeObject(getRpcToken());
      }
      streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
      streamWriter.writeString("analysePackage");
      streamWriter.writeInt(1);
      streamWriter.writeString("java.lang.String/2004016611");
      streamWriter.writeString(packageUUID);
      String payload = streamWriter.toString();
      toss = statsContext.isStatsAvailable() && statsContext.stats(statsContext.timeStat("VerificationService_Proxy.analysePackage",  "requestSerialized"));
      doInvoke(ResponseReader.OBJECT, "VerificationService_Proxy.analysePackage", statsContext, payload, callback);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void verifyAsset(org.drools.guvnor.client.rpc.Asset asset, java.util.Set sactiveWorkingSets, com.google.gwt.user.client.rpc.AsyncCallback arg2) {
    RpcStatsContext statsContext = new RpcStatsContext();
    boolean toss = statsContext.isStatsAvailable() && statsContext.stats(statsContext.timeStat("VerificationService_Proxy.verifyAsset", "begin"));
    SerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    try {
      if (getRpcToken() != null) {
        streamWriter.writeObject(getRpcToken());
      }
      streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
      streamWriter.writeString("verifyAsset");
      streamWriter.writeInt(2);
      streamWriter.writeString("org.drools.guvnor.client.rpc.Asset/2594588063");
      streamWriter.writeString("java.util.Set");
      streamWriter.writeObject(asset);
      streamWriter.writeObject(sactiveWorkingSets);
      String payload = streamWriter.toString();
      toss = statsContext.isStatsAvailable() && statsContext.stats(statsContext.timeStat("VerificationService_Proxy.verifyAsset",  "requestSerialized"));
      doInvoke(ResponseReader.OBJECT, "VerificationService_Proxy.verifyAsset", statsContext, payload, arg2);
    } catch (SerializationException ex) {
      arg2.onFailure(ex);
    }
  }
  
  public void verifyAssetWithoutVerifiersRules(org.drools.guvnor.client.rpc.Asset asset, java.util.Set activeWorkingSets, com.google.gwt.user.client.rpc.AsyncCallback arg2) {
    RpcStatsContext statsContext = new RpcStatsContext();
    boolean toss = statsContext.isStatsAvailable() && statsContext.stats(statsContext.timeStat("VerificationService_Proxy.verifyAssetWithoutVerifiersRules", "begin"));
    SerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    try {
      if (getRpcToken() != null) {
        streamWriter.writeObject(getRpcToken());
      }
      streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
      streamWriter.writeString("verifyAssetWithoutVerifiersRules");
      streamWriter.writeInt(2);
      streamWriter.writeString("org.drools.guvnor.client.rpc.Asset/2594588063");
      streamWriter.writeString("java.util.Set");
      streamWriter.writeObject(asset);
      streamWriter.writeObject(activeWorkingSets);
      String payload = streamWriter.toString();
      toss = statsContext.isStatsAvailable() && statsContext.stats(statsContext.timeStat("VerificationService_Proxy.verifyAssetWithoutVerifiersRules",  "requestSerialized"));
      doInvoke(ResponseReader.OBJECT, "VerificationService_Proxy.verifyAssetWithoutVerifiersRules", statsContext, payload, arg2);
    } catch (SerializationException ex) {
      arg2.onFailure(ex);
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
