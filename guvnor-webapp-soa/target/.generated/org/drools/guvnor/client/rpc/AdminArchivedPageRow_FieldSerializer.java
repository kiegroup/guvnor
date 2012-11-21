package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AdminArchivedPageRow_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getLastContributor(org.drools.guvnor.client.rpc.AdminArchivedPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AdminArchivedPageRow::lastContributor;
  }-*/;
  
  private static native void setLastContributor(org.drools.guvnor.client.rpc.AdminArchivedPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AdminArchivedPageRow::lastContributor = value;
  }-*/;
  
  private static native java.util.Date getLastModified(org.drools.guvnor.client.rpc.AdminArchivedPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AdminArchivedPageRow::lastModified;
  }-*/;
  
  private static native void setLastModified(org.drools.guvnor.client.rpc.AdminArchivedPageRow instance, java.util.Date value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AdminArchivedPageRow::lastModified = value;
  }-*/;
  
  private static native java.lang.String getPackageName(org.drools.guvnor.client.rpc.AdminArchivedPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AdminArchivedPageRow::packageName;
  }-*/;
  
  private static native void setPackageName(org.drools.guvnor.client.rpc.AdminArchivedPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AdminArchivedPageRow::packageName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.AdminArchivedPageRow instance) throws SerializationException {
    setLastContributor(instance, streamReader.readString());
    setLastModified(instance, (java.util.Date) streamReader.readObject());
    setPackageName(instance, streamReader.readString());
    
    org.drools.guvnor.client.rpc.AbstractAssetPageRow_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.AdminArchivedPageRow instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.AdminArchivedPageRow();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.AdminArchivedPageRow instance) throws SerializationException {
    streamWriter.writeString(getLastContributor(instance));
    streamWriter.writeObject(getLastModified(instance));
    streamWriter.writeString(getPackageName(instance));
    
    org.drools.guvnor.client.rpc.AbstractAssetPageRow_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.AdminArchivedPageRow_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.AdminArchivedPageRow_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.AdminArchivedPageRow)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.AdminArchivedPageRow_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.AdminArchivedPageRow)object);
  }
  
}
