package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AssetPageRequest_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getFormatInList(org.drools.guvnor.client.rpc.AssetPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AssetPageRequest::formatInList;
  }-*/;
  
  private static native void setFormatInList(org.drools.guvnor.client.rpc.AssetPageRequest instance, java.util.List value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AssetPageRequest::formatInList = value;
  }-*/;
  
  private static native java.lang.Boolean getFormatIsRegistered(org.drools.guvnor.client.rpc.AssetPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AssetPageRequest::formatIsRegistered;
  }-*/;
  
  private static native void setFormatIsRegistered(org.drools.guvnor.client.rpc.AssetPageRequest instance, java.lang.Boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AssetPageRequest::formatIsRegistered = value;
  }-*/;
  
  private static native java.lang.String getPackageUuid(org.drools.guvnor.client.rpc.AssetPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AssetPageRequest::packageUuid;
  }-*/;
  
  private static native void setPackageUuid(org.drools.guvnor.client.rpc.AssetPageRequest instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AssetPageRequest::packageUuid = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.AssetPageRequest instance) throws SerializationException {
    setFormatInList(instance, (java.util.List) streamReader.readObject());
    setFormatIsRegistered(instance, (java.lang.Boolean) streamReader.readObject());
    setPackageUuid(instance, streamReader.readString());
    
    org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.AssetPageRequest instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.AssetPageRequest();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.AssetPageRequest instance) throws SerializationException {
    streamWriter.writeObject(getFormatInList(instance));
    streamWriter.writeObject(getFormatIsRegistered(instance));
    streamWriter.writeString(getPackageUuid(instance));
    
    org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.AssetPageRequest_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.AssetPageRequest_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.AssetPageRequest)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.AssetPageRequest_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.AssetPageRequest)object);
  }
  
}
