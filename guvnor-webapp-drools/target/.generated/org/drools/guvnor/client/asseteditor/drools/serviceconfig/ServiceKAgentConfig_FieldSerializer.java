package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ServiceKAgentConfig_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getName(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig::name;
  }-*/;
  
  private static native void setName(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig::name = value;
  }-*/;
  
  private static native java.lang.Boolean getNewInstance(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig::newInstance;
  }-*/;
  
  private static native void setNewInstance(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig instance, java.lang.Boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig::newInstance = value;
  }-*/;
  
  private static native java.util.Set getResources(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig::resources;
  }-*/;
  
  private static native void setResources(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig instance, java.util.Set value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig::resources = value;
  }-*/;
  
  private static native java.lang.Boolean getUseKBaseClassloader(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig::useKBaseClassloader;
  }-*/;
  
  private static native void setUseKBaseClassloader(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig instance, java.lang.Boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig::useKBaseClassloader = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig instance) throws SerializationException {
    setName(instance, streamReader.readString());
    setNewInstance(instance, (java.lang.Boolean) streamReader.readObject());
    setResources(instance, (java.util.Set) streamReader.readObject());
    setUseKBaseClassloader(instance, (java.lang.Boolean) streamReader.readObject());
    
  }
  
  public static org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig instance) throws SerializationException {
    streamWriter.writeString(getName(instance));
    streamWriter.writeObject(getNewInstance(instance));
    streamWriter.writeObject(getResources(instance));
    streamWriter.writeObject(getUseKBaseClassloader(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKAgentConfig)object);
  }
  
}
