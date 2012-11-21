package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class UserSecurityContext_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getUserName(org.drools.guvnor.client.rpc.UserSecurityContext instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.UserSecurityContext::userName;
  }-*/;
  
  private static native void setUserName(org.drools.guvnor.client.rpc.UserSecurityContext instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.UserSecurityContext::userName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.UserSecurityContext instance) throws SerializationException {
    setUserName(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.rpc.UserSecurityContext instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.UserSecurityContext();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.UserSecurityContext instance) throws SerializationException {
    streamWriter.writeString(getUserName(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.UserSecurityContext_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.UserSecurityContext_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.UserSecurityContext)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.UserSecurityContext_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.UserSecurityContext)object);
  }
  
}
