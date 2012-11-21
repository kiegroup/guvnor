package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DetailedSerializationException_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getErrs(org.drools.guvnor.client.rpc.DetailedSerializationException instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.DetailedSerializationException::errs;
  }-*/;
  
  private static native void setErrs(org.drools.guvnor.client.rpc.DetailedSerializationException instance, java.util.List value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.DetailedSerializationException::errs = value;
  }-*/;
  
  private static native java.lang.String getLongDescription(org.drools.guvnor.client.rpc.DetailedSerializationException instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.DetailedSerializationException::longDescription;
  }-*/;
  
  private static native void setLongDescription(org.drools.guvnor.client.rpc.DetailedSerializationException instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.DetailedSerializationException::longDescription = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.DetailedSerializationException instance) throws SerializationException {
    setErrs(instance, (java.util.List) streamReader.readObject());
    setLongDescription(instance, streamReader.readString());
    
    com.google.gwt.user.client.rpc.SerializationException_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.DetailedSerializationException instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.DetailedSerializationException();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.DetailedSerializationException instance) throws SerializationException {
    streamWriter.writeObject(getErrs(instance));
    streamWriter.writeString(getLongDescription(instance));
    
    com.google.gwt.user.client.rpc.SerializationException_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.DetailedSerializationException_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.DetailedSerializationException_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.DetailedSerializationException)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.DetailedSerializationException_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.DetailedSerializationException)object);
  }
  
}
