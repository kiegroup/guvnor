package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ConnectiveConstraint_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getFactType(org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint::factType;
  }-*/;
  
  private static native void setFactType(org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint::factType = value;
  }-*/;
  
  private static native java.lang.String getFieldName(org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint::fieldName;
  }-*/;
  
  private static native void setFieldName(org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint::fieldName = value;
  }-*/;
  
  private static native java.lang.String getFieldType(org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint::fieldType;
  }-*/;
  
  private static native void setFieldType(org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint::fieldType = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint instance) throws SerializationException {
    setFactType(instance, streamReader.readString());
    setFieldName(instance, streamReader.readString());
    setFieldType(instance, streamReader.readString());
    
    org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint instance) throws SerializationException {
    streamWriter.writeString(getFactType(instance));
    streamWriter.writeString(getFieldName(instance));
    streamWriter.writeString(getFieldType(instance));
    
    org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint)object);
  }
  
}
