package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class InboxPageRequest_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getInboxName(org.drools.guvnor.client.rpc.InboxPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.InboxPageRequest::inboxName;
  }-*/;
  
  private static native void setInboxName(org.drools.guvnor.client.rpc.InboxPageRequest instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.InboxPageRequest::inboxName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.InboxPageRequest instance) throws SerializationException {
    setInboxName(instance, streamReader.readString());
    
    org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.InboxPageRequest instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.InboxPageRequest();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.InboxPageRequest instance) throws SerializationException {
    streamWriter.writeString(getInboxName(instance));
    
    org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.InboxPageRequest_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.InboxPageRequest_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.InboxPageRequest)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.InboxPageRequest_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.InboxPageRequest)object);
  }
  
}
