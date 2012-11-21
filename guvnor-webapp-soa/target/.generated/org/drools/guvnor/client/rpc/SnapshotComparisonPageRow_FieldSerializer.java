package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SnapshotComparisonPageRow_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.guvnor.client.rpc.SnapshotDiff getDiff(org.drools.guvnor.client.rpc.SnapshotComparisonPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.SnapshotComparisonPageRow::diff;
  }-*/;
  
  private static native void setDiff(org.drools.guvnor.client.rpc.SnapshotComparisonPageRow instance, org.drools.guvnor.client.rpc.SnapshotDiff value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.SnapshotComparisonPageRow::diff = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.SnapshotComparisonPageRow instance) throws SerializationException {
    setDiff(instance, (org.drools.guvnor.client.rpc.SnapshotDiff) streamReader.readObject());
    
    org.drools.guvnor.client.rpc.AbstractPageRow_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.SnapshotComparisonPageRow instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.SnapshotComparisonPageRow();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.SnapshotComparisonPageRow instance) throws SerializationException {
    streamWriter.writeObject(getDiff(instance));
    
    org.drools.guvnor.client.rpc.AbstractPageRow_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.SnapshotComparisonPageRow_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.SnapshotComparisonPageRow_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.SnapshotComparisonPageRow)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.SnapshotComparisonPageRow_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.SnapshotComparisonPageRow)object);
  }
  
}
