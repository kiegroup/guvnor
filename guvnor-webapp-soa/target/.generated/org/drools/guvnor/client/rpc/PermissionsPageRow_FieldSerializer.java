package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PermissionsPageRow_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getUserName(org.drools.guvnor.client.rpc.PermissionsPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.PermissionsPageRow::userName;
  }-*/;
  
  private static native void setUserName(org.drools.guvnor.client.rpc.PermissionsPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.PermissionsPageRow::userName = value;
  }-*/;
  
  private static native java.util.List getUserPermissions(org.drools.guvnor.client.rpc.PermissionsPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.PermissionsPageRow::userPermissions;
  }-*/;
  
  private static native void setUserPermissions(org.drools.guvnor.client.rpc.PermissionsPageRow instance, java.util.List value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.PermissionsPageRow::userPermissions = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.PermissionsPageRow instance) throws SerializationException {
    setUserName(instance, streamReader.readString());
    setUserPermissions(instance, (java.util.List) streamReader.readObject());
    
    org.drools.guvnor.client.rpc.AbstractPageRow_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.PermissionsPageRow instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.PermissionsPageRow();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.PermissionsPageRow instance) throws SerializationException {
    streamWriter.writeString(getUserName(instance));
    streamWriter.writeObject(getUserPermissions(instance));
    
    org.drools.guvnor.client.rpc.AbstractPageRow_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.PermissionsPageRow_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.PermissionsPageRow_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.PermissionsPageRow)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.PermissionsPageRow_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.PermissionsPageRow)object);
  }
  
}
