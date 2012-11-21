package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SnapshotComparisonPageRequest_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getFirstSnapshotName(org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest::firstSnapshotName;
  }-*/;
  
  private static native void setFirstSnapshotName(org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest::firstSnapshotName = value;
  }-*/;
  
  private static native java.lang.String getPackageName(org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest::packageName;
  }-*/;
  
  private static native void setPackageName(org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest::packageName = value;
  }-*/;
  
  private static native java.lang.String getSecondSnapshotName(org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest::secondSnapshotName;
  }-*/;
  
  private static native void setSecondSnapshotName(org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest::secondSnapshotName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest instance) throws SerializationException {
    setFirstSnapshotName(instance, streamReader.readString());
    setPackageName(instance, streamReader.readString());
    setSecondSnapshotName(instance, streamReader.readString());
    
    org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest instance) throws SerializationException {
    streamWriter.writeString(getFirstSnapshotName(instance));
    streamWriter.writeString(getPackageName(instance));
    streamWriter.writeString(getSecondSnapshotName(instance));
    
    org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.SnapshotComparisonPageRequest)object);
  }
  
}
