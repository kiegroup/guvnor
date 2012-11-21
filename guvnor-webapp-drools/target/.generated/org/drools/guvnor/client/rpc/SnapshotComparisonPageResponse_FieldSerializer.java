package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SnapshotComparisonPageResponse_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getLeftSnapshotName(org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse::leftSnapshotName;
  }-*/;
  
  private static native void setLeftSnapshotName(org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse::leftSnapshotName = value;
  }-*/;
  
  private static native java.lang.String getRightSnapshotName(org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse::rightSnapshotName;
  }-*/;
  
  private static native void setRightSnapshotName(org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse::rightSnapshotName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse instance) throws SerializationException {
    setLeftSnapshotName(instance, streamReader.readString());
    setRightSnapshotName(instance, streamReader.readString());
    
    org.drools.guvnor.client.rpc.PageResponse_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse instance) throws SerializationException {
    streamWriter.writeString(getLeftSnapshotName(instance));
    streamWriter.writeString(getRightSnapshotName(instance));
    
    org.drools.guvnor.client.rpc.PageResponse_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.SnapshotComparisonPageResponse)object);
  }
  
}
