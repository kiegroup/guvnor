package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ServiceKSessionConfig_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.guvnor.client.asseteditor.drools.serviceconfig.ClockType getClockType(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::clockType;
  }-*/;
  
  private static native void setClockType(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance, org.drools.guvnor.client.asseteditor.drools.serviceconfig.ClockType value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::clockType = value;
  }-*/;
  
  private static native java.lang.Boolean getKeepReference(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::keepReference;
  }-*/;
  
  private static native void setKeepReference(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance, java.lang.Boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::keepReference = value;
  }-*/;
  
  private static native java.util.Map getListeners(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::listeners;
  }-*/;
  
  private static native void setListeners(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance, java.util.Map value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::listeners = value;
  }-*/;
  
  private static native org.drools.guvnor.client.asseteditor.drools.serviceconfig.MarshallingOption getMarshalling(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::marshalling;
  }-*/;
  
  private static native void setMarshalling(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance, org.drools.guvnor.client.asseteditor.drools.serviceconfig.MarshallingOption value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::marshalling = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::name;
  }-*/;
  
  private static native void setName(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::name = value;
  }-*/;
  
  private static native org.drools.guvnor.client.asseteditor.drools.serviceconfig.ProtocolOption getProtocol(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::protocol;
  }-*/;
  
  private static native void setProtocol(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance, org.drools.guvnor.client.asseteditor.drools.serviceconfig.ProtocolOption value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::protocol = value;
  }-*/;
  
  private static native org.drools.guvnor.client.asseteditor.drools.serviceconfig.SessionType getType(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::type;
  }-*/;
  
  private static native void setType(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance, org.drools.guvnor.client.asseteditor.drools.serviceconfig.SessionType value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::type = value;
  }-*/;
  
  private static native java.lang.String getUrl(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::url;
  }-*/;
  
  private static native void setUrl(org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig::url = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance) throws SerializationException {
    setClockType(instance, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.ClockType) streamReader.readObject());
    setKeepReference(instance, (java.lang.Boolean) streamReader.readObject());
    setListeners(instance, (java.util.Map) streamReader.readObject());
    setMarshalling(instance, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.MarshallingOption) streamReader.readObject());
    setName(instance, streamReader.readString());
    setProtocol(instance, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.ProtocolOption) streamReader.readObject());
    setType(instance, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.SessionType) streamReader.readObject());
    setUrl(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig instance) throws SerializationException {
    streamWriter.writeObject(getClockType(instance));
    streamWriter.writeObject(getKeepReference(instance));
    streamWriter.writeObject(getListeners(instance));
    streamWriter.writeObject(getMarshalling(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeObject(getProtocol(instance));
    streamWriter.writeObject(getType(instance));
    streamWriter.writeString(getUrl(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.ServiceKSessionConfig)object);
  }
  
}
