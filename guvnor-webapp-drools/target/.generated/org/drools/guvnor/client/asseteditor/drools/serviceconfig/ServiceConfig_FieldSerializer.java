package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ServiceConfig_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.Set getExcludedArtifacts(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig::excludedArtifacts;
  }-*/;
  
  private static native void setExcludedArtifacts(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig instance, java.util.Set value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig::excludedArtifacts = value;
  }-*/;
  
  private static native java.util.Map getKbases(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig::kbases;
  }-*/;
  
  private static native void setKbases(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig instance, java.util.Map value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig::kbases = value;
  }-*/;
  
  private static native java.lang.Integer getPollingFrequency(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig::pollingFrequency;
  }-*/;
  
  private static native void setPollingFrequency(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig instance, java.lang.Integer value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig::pollingFrequency = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig instance) throws SerializationException {
    setExcludedArtifacts(instance, (java.util.Set) streamReader.readObject());
    setKbases(instance, (java.util.Map) streamReader.readObject());
    setPollingFrequency(instance, (java.lang.Integer) streamReader.readObject());
    
  }
  
  public static org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig instance) throws SerializationException {
    streamWriter.writeObject(getExcludedArtifacts(instance));
    streamWriter.writeObject(getKbases(instance));
    streamWriter.writeObject(getPollingFrequency(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceConfig)object);
  }
  
}
