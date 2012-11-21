package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class InboxPageRow_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getNote(org.drools.guvnor.client.rpc.InboxPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.InboxPageRow::note;
  }-*/;
  
  private static native void setNote(org.drools.guvnor.client.rpc.InboxPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.InboxPageRow::note = value;
  }-*/;
  
  private static native java.util.Date getTimestamp(org.drools.guvnor.client.rpc.InboxPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.InboxPageRow::timestamp;
  }-*/;
  
  private static native void setTimestamp(org.drools.guvnor.client.rpc.InboxPageRow instance, java.util.Date value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.InboxPageRow::timestamp = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.InboxPageRow instance) throws SerializationException {
    setNote(instance, streamReader.readString());
    setTimestamp(instance, (java.util.Date) streamReader.readObject());
    
    org.drools.guvnor.client.rpc.AbstractAssetPageRow_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.InboxPageRow instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.InboxPageRow();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.InboxPageRow instance) throws SerializationException {
    streamWriter.writeString(getNote(instance));
    streamWriter.writeObject(getTimestamp(instance));
    
    org.drools.guvnor.client.rpc.AbstractAssetPageRow_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.InboxPageRow_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.InboxPageRow_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.InboxPageRow)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.InboxPageRow_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.InboxPageRow)object);
  }
  
}
