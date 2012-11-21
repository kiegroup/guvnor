package org.drools.guvnor.client.common;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class RdbmsConfigurable_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getDbDriver(org.drools.guvnor.client.common.RdbmsConfigurable instance) /*-{
    return instance.@org.drools.guvnor.client.common.RdbmsConfigurable::dbDriver;
  }-*/;
  
  private static native void setDbDriver(org.drools.guvnor.client.common.RdbmsConfigurable instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.common.RdbmsConfigurable::dbDriver = value;
  }-*/;
  
  private static native java.lang.String getDbPass(org.drools.guvnor.client.common.RdbmsConfigurable instance) /*-{
    return instance.@org.drools.guvnor.client.common.RdbmsConfigurable::dbPass;
  }-*/;
  
  private static native void setDbPass(org.drools.guvnor.client.common.RdbmsConfigurable instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.common.RdbmsConfigurable::dbPass = value;
  }-*/;
  
  private static native java.lang.String getDbType(org.drools.guvnor.client.common.RdbmsConfigurable instance) /*-{
    return instance.@org.drools.guvnor.client.common.RdbmsConfigurable::dbType;
  }-*/;
  
  private static native void setDbType(org.drools.guvnor.client.common.RdbmsConfigurable instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.common.RdbmsConfigurable::dbType = value;
  }-*/;
  
  private static native java.lang.String getDbUrl(org.drools.guvnor.client.common.RdbmsConfigurable instance) /*-{
    return instance.@org.drools.guvnor.client.common.RdbmsConfigurable::dbUrl;
  }-*/;
  
  private static native void setDbUrl(org.drools.guvnor.client.common.RdbmsConfigurable instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.common.RdbmsConfigurable::dbUrl = value;
  }-*/;
  
  private static native java.lang.String getDbUser(org.drools.guvnor.client.common.RdbmsConfigurable instance) /*-{
    return instance.@org.drools.guvnor.client.common.RdbmsConfigurable::dbUser;
  }-*/;
  
  private static native void setDbUser(org.drools.guvnor.client.common.RdbmsConfigurable instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.common.RdbmsConfigurable::dbUser = value;
  }-*/;
  
  private static native boolean getJndi(org.drools.guvnor.client.common.RdbmsConfigurable instance) /*-{
    return instance.@org.drools.guvnor.client.common.RdbmsConfigurable::jndi;
  }-*/;
  
  private static native void setJndi(org.drools.guvnor.client.common.RdbmsConfigurable instance, boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.common.RdbmsConfigurable::jndi = value;
  }-*/;
  
  private static native java.lang.String getJndiDsName(org.drools.guvnor.client.common.RdbmsConfigurable instance) /*-{
    return instance.@org.drools.guvnor.client.common.RdbmsConfigurable::jndiDsName;
  }-*/;
  
  private static native void setJndiDsName(org.drools.guvnor.client.common.RdbmsConfigurable instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.common.RdbmsConfigurable::jndiDsName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.common.RdbmsConfigurable instance) throws SerializationException {
    setDbDriver(instance, streamReader.readString());
    setDbPass(instance, streamReader.readString());
    setDbType(instance, streamReader.readString());
    setDbUrl(instance, streamReader.readString());
    setDbUser(instance, streamReader.readString());
    setJndi(instance, streamReader.readBoolean());
    setJndiDsName(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.common.RdbmsConfigurable instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.common.RdbmsConfigurable();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.common.RdbmsConfigurable instance) throws SerializationException {
    streamWriter.writeString(getDbDriver(instance));
    streamWriter.writeString(getDbPass(instance));
    streamWriter.writeString(getDbType(instance));
    streamWriter.writeString(getDbUrl(instance));
    streamWriter.writeString(getDbUser(instance));
    streamWriter.writeBoolean(getJndi(instance));
    streamWriter.writeString(getJndiDsName(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.common.RdbmsConfigurable_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.common.RdbmsConfigurable_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.common.RdbmsConfigurable)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.common.RdbmsConfigurable_FieldSerializer.serialize(writer, (org.drools.guvnor.client.common.RdbmsConfigurable)object);
  }
  
}
