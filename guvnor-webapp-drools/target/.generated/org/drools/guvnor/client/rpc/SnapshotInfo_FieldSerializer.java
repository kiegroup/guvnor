package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SnapshotInfo_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getComment(org.drools.guvnor.client.rpc.SnapshotInfo instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.SnapshotInfo::comment;
  }-*/;
  
  private static native void setComment(org.drools.guvnor.client.rpc.SnapshotInfo instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.SnapshotInfo::comment = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.guvnor.client.rpc.SnapshotInfo instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.SnapshotInfo::name;
  }-*/;
  
  private static native void setName(org.drools.guvnor.client.rpc.SnapshotInfo instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.SnapshotInfo::name = value;
  }-*/;
  
  private static native java.lang.String getUuid(org.drools.guvnor.client.rpc.SnapshotInfo instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.SnapshotInfo::uuid;
  }-*/;
  
  private static native void setUuid(org.drools.guvnor.client.rpc.SnapshotInfo instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.SnapshotInfo::uuid = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.SnapshotInfo instance) throws SerializationException {
    setComment(instance, streamReader.readString());
    setName(instance, streamReader.readString());
    setUuid(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.rpc.SnapshotInfo instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.SnapshotInfo();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.SnapshotInfo instance) throws SerializationException {
    streamWriter.writeString(getComment(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeString(getUuid(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.SnapshotInfo_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.SnapshotInfo_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.SnapshotInfo)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.SnapshotInfo_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.SnapshotInfo)object);
  }
  
}
