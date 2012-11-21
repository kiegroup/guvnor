package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class InboxIncomingPageRow_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getFrom(org.drools.guvnor.client.rpc.InboxIncomingPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.InboxIncomingPageRow::from;
  }-*/;
  
  private static native void setFrom(org.drools.guvnor.client.rpc.InboxIncomingPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.InboxIncomingPageRow::from = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.InboxIncomingPageRow instance) throws SerializationException {
    setFrom(instance, streamReader.readString());
    
    org.drools.guvnor.client.rpc.InboxPageRow_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.InboxIncomingPageRow instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.InboxIncomingPageRow();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.InboxIncomingPageRow instance) throws SerializationException {
    streamWriter.writeString(getFrom(instance));
    
    org.drools.guvnor.client.rpc.InboxPageRow_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.InboxIncomingPageRow_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.InboxIncomingPageRow_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.InboxIncomingPageRow)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.InboxIncomingPageRow_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.InboxIncomingPageRow)object);
  }
  
}
