package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DiscussionRecord_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.DiscussionRecord instance) throws SerializationException {
    instance.author = streamReader.readString();
    instance.note = streamReader.readString();
    instance.timestamp = streamReader.readLong();
    
  }
  
  public static org.drools.guvnor.client.rpc.DiscussionRecord instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.DiscussionRecord();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.DiscussionRecord instance) throws SerializationException {
    streamWriter.writeString(instance.author);
    streamWriter.writeString(instance.note);
    streamWriter.writeLong(instance.timestamp);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.DiscussionRecord_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.DiscussionRecord_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.DiscussionRecord)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.DiscussionRecord_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.DiscussionRecord)object);
  }
  
}
