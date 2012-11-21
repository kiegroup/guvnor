package org.drools.ide.common.client.modeldriven;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class MethodInfo_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getGenericType(org.drools.ide.common.client.modeldriven.MethodInfo instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.MethodInfo::genericType;
  }-*/;
  
  private static native void setGenericType(org.drools.ide.common.client.modeldriven.MethodInfo instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.MethodInfo::genericType = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.ide.common.client.modeldriven.MethodInfo instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.MethodInfo::name;
  }-*/;
  
  private static native void setName(org.drools.ide.common.client.modeldriven.MethodInfo instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.MethodInfo::name = value;
  }-*/;
  
  private static native java.lang.String getParametricReturnType(org.drools.ide.common.client.modeldriven.MethodInfo instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.MethodInfo::parametricReturnType;
  }-*/;
  
  private static native void setParametricReturnType(org.drools.ide.common.client.modeldriven.MethodInfo instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.MethodInfo::parametricReturnType = value;
  }-*/;
  
  private static native java.util.List getParams(org.drools.ide.common.client.modeldriven.MethodInfo instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.MethodInfo::params;
  }-*/;
  
  private static native void setParams(org.drools.ide.common.client.modeldriven.MethodInfo instance, java.util.List value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.MethodInfo::params = value;
  }-*/;
  
  private static native java.lang.String getReturnClassType(org.drools.ide.common.client.modeldriven.MethodInfo instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.MethodInfo::returnClassType;
  }-*/;
  
  private static native void setReturnClassType(org.drools.ide.common.client.modeldriven.MethodInfo instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.MethodInfo::returnClassType = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.MethodInfo instance) throws SerializationException {
    setGenericType(instance, streamReader.readString());
    setName(instance, streamReader.readString());
    setParametricReturnType(instance, streamReader.readString());
    setParams(instance, (java.util.List) streamReader.readObject());
    setReturnClassType(instance, streamReader.readString());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.MethodInfo instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.MethodInfo();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.MethodInfo instance) throws SerializationException {
    streamWriter.writeString(getGenericType(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeString(getParametricReturnType(instance));
    streamWriter.writeObject(getParams(instance));
    streamWriter.writeString(getReturnClassType(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.MethodInfo_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.MethodInfo_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.MethodInfo)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.MethodInfo_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.MethodInfo)object);
  }
  
}
