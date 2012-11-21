package org.drools.ide.common.client.factconstraints.config;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SimpleConstraintConfigurationImpl_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.Map getArgs(org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl instance) /*-{
    return instance.@org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl::args;
  }-*/;
  
  private static native void setArgs(org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl::args = value;
  }-*/;
  
  private static native java.lang.String getConstraintName(org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl instance) /*-{
    return instance.@org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl::constraintName;
  }-*/;
  
  private static native void setConstraintName(org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl::constraintName = value;
  }-*/;
  
  private static native java.lang.String getFactType(org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl instance) /*-{
    return instance.@org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl::factType;
  }-*/;
  
  private static native void setFactType(org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl::factType = value;
  }-*/;
  
  private static native java.lang.String getFieldName(org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl instance) /*-{
    return instance.@org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl::fieldName;
  }-*/;
  
  private static native void setFieldName(org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl::fieldName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl instance) throws SerializationException {
    setArgs(instance, (java.util.Map) streamReader.readObject());
    setConstraintName(instance, streamReader.readString());
    setFactType(instance, streamReader.readString());
    setFieldName(instance, streamReader.readString());
    
  }
  
  public static org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl instance) throws SerializationException {
    streamWriter.writeObject(getArgs(instance));
    streamWriter.writeString(getConstraintName(instance));
    streamWriter.writeString(getFactType(instance));
    streamWriter.writeString(getFieldName(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl_FieldSerializer.serialize(writer, (org.drools.ide.common.client.factconstraints.config.SimpleConstraintConfigurationImpl)object);
  }
  
}
