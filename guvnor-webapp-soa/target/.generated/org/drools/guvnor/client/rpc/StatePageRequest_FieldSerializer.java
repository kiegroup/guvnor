package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class StatePageRequest_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getStateName(org.drools.guvnor.client.rpc.StatePageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.StatePageRequest::stateName;
  }-*/;
  
  private static native void setStateName(org.drools.guvnor.client.rpc.StatePageRequest instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.StatePageRequest::stateName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.StatePageRequest instance) throws SerializationException {
    setStateName(instance, streamReader.readString());
    
    org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.StatePageRequest instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.StatePageRequest();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.StatePageRequest instance) throws SerializationException {
    streamWriter.writeString(getStateName(instance));
    
    org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.StatePageRequest_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.StatePageRequest_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.StatePageRequest)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.StatePageRequest_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.StatePageRequest)object);
  }
  
}
