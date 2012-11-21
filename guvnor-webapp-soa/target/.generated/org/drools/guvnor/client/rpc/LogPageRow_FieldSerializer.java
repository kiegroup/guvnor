package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class LogPageRow_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getMessage(org.drools.guvnor.client.rpc.LogPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.LogPageRow::message;
  }-*/;
  
  private static native void setMessage(org.drools.guvnor.client.rpc.LogPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.LogPageRow::message = value;
  }-*/;
  
  private static native int getSeverity(org.drools.guvnor.client.rpc.LogPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.LogPageRow::severity;
  }-*/;
  
  private static native void setSeverity(org.drools.guvnor.client.rpc.LogPageRow instance, int value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.LogPageRow::severity = value;
  }-*/;
  
  private static native java.util.Date getTimestamp(org.drools.guvnor.client.rpc.LogPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.LogPageRow::timestamp;
  }-*/;
  
  private static native void setTimestamp(org.drools.guvnor.client.rpc.LogPageRow instance, java.util.Date value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.LogPageRow::timestamp = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.LogPageRow instance) throws SerializationException {
    setMessage(instance, streamReader.readString());
    setSeverity(instance, streamReader.readInt());
    setTimestamp(instance, (java.util.Date) streamReader.readObject());
    
    org.drools.guvnor.client.rpc.AbstractPageRow_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.LogPageRow instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.LogPageRow();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.LogPageRow instance) throws SerializationException {
    streamWriter.writeString(getMessage(instance));
    streamWriter.writeInt(getSeverity(instance));
    streamWriter.writeObject(getTimestamp(instance));
    
    org.drools.guvnor.client.rpc.AbstractPageRow_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.LogPageRow_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.LogPageRow_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.LogPageRow)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.LogPageRow_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.LogPageRow)object);
  }
  
}
