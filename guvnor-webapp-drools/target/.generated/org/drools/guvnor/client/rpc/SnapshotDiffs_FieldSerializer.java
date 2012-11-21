package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SnapshotDiffs_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.SnapshotDiffs instance) throws SerializationException {
    instance.diffs = (org.drools.guvnor.client.rpc.SnapshotDiff[]) streamReader.readObject();
    instance.leftName = streamReader.readString();
    instance.rightName = streamReader.readString();
    
  }
  
  public static org.drools.guvnor.client.rpc.SnapshotDiffs instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.SnapshotDiffs();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.SnapshotDiffs instance) throws SerializationException {
    streamWriter.writeObject(instance.diffs);
    streamWriter.writeString(instance.leftName);
    streamWriter.writeString(instance.rightName);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.SnapshotDiffs_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.SnapshotDiffs_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.SnapshotDiffs)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.SnapshotDiffs_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.SnapshotDiffs)object);
  }
  
}
