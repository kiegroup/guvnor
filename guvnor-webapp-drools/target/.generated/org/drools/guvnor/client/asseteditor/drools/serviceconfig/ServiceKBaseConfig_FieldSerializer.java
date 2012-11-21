package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ServiceKBaseConfig_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption getAssertBehavior(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::assertBehavior;
  }-*/;
  
  private static native void setAssertBehavior(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance, org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::assertBehavior = value;
  }-*/;
  
  private static native java.lang.String getAssetsPassword(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::assetsPassword;
  }-*/;
  
  private static native void setAssetsPassword(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::assetsPassword = value;
  }-*/;
  
  private static native java.lang.String getAssetsUser(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::assetsUser;
  }-*/;
  
  private static native void setAssetsUser(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::assetsUser = value;
  }-*/;
  
  private static native org.drools.guvnor.client.asseteditor.drools.serviceconfig.EventProcessingOption getEventProcessingMode(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::eventProcessingMode;
  }-*/;
  
  private static native void setEventProcessingMode(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance, org.drools.guvnor.client.asseteditor.drools.serviceconfig.EventProcessingOption value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::eventProcessingMode = value;
  }-*/;
  
  private static native java.util.Map getKagents(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::kagents;
  }-*/;
  
  private static native void setKagents(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance, java.util.Map value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::kagents = value;
  }-*/;
  
  private static native java.util.Map getKsessions(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::ksessions;
  }-*/;
  
  private static native void setKsessions(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance, java.util.Map value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::ksessions = value;
  }-*/;
  
  private static native java.lang.Integer getMaxThreads(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::maxThreads;
  }-*/;
  
  private static native void setMaxThreads(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance, java.lang.Integer value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::maxThreads = value;
  }-*/;
  
  private static native java.lang.Boolean getMbeans(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::mbeans;
  }-*/;
  
  private static native void setMbeans(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance, java.lang.Boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::mbeans = value;
  }-*/;
  
  private static native java.util.Set getModels(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::models;
  }-*/;
  
  private static native void setModels(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance, java.util.Set value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::models = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::name;
  }-*/;
  
  private static native void setName(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::name = value;
  }-*/;
  
  private static native java.util.Set getResources(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::resources;
  }-*/;
  
  private static native void setResources(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance, java.util.Set value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig::resources = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance) throws SerializationException {
    setAssertBehavior(instance, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssertBehaviorOption) streamReader.readObject());
    setAssetsPassword(instance, streamReader.readString());
    setAssetsUser(instance, streamReader.readString());
    setEventProcessingMode(instance, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.EventProcessingOption) streamReader.readObject());
    setKagents(instance, (java.util.Map) streamReader.readObject());
    setKsessions(instance, (java.util.Map) streamReader.readObject());
    setMaxThreads(instance, (java.lang.Integer) streamReader.readObject());
    setMbeans(instance, (java.lang.Boolean) streamReader.readObject());
    setModels(instance, (java.util.Set) streamReader.readObject());
    setName(instance, streamReader.readString());
    setResources(instance, (java.util.Set) streamReader.readObject());
    
  }
  
  public static org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig instance) throws SerializationException {
    streamWriter.writeObject(getAssertBehavior(instance));
    streamWriter.writeString(getAssetsPassword(instance));
    streamWriter.writeString(getAssetsUser(instance));
    streamWriter.writeObject(getEventProcessingMode(instance));
    streamWriter.writeObject(getKagents(instance));
    streamWriter.writeObject(getKsessions(instance));
    streamWriter.writeObject(getMaxThreads(instance));
    streamWriter.writeObject(getMbeans(instance));
    streamWriter.writeObject(getModels(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeObject(getResources(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKBaseConfig)object);
  }
  
}
