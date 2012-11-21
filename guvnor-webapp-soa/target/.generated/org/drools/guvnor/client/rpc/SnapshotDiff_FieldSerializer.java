package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SnapshotDiff_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.SnapshotDiff instance) throws SerializationException {
    instance.diffType = streamReader.readString();
    instance.leftUuid = streamReader.readString();
    instance.name = streamReader.readString();
    instance.rightUuid = streamReader.readString();
    
  }
  
  public static org.drools.guvnor.client.rpc.SnapshotDiff instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.SnapshotDiff();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.SnapshotDiff instance) throws SerializationException {
    streamWriter.writeString(instance.diffType);
    streamWriter.writeString(instance.leftUuid);
    streamWriter.writeString(instance.name);
    streamWriter.writeString(instance.rightUuid);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.SnapshotDiff_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.SnapshotDiff_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.SnapshotDiff)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.SnapshotDiff_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.SnapshotDiff)object);
  }
  
}
