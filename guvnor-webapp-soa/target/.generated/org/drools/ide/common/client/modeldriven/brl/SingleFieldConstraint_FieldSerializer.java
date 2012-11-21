package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SingleFieldConstraint_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getFieldBinding(org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint::fieldBinding;
  }-*/;
  
  private static native void setFieldBinding(org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint::fieldBinding = value;
  }-*/;
  
  private static native java.lang.String getFieldName(org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint::fieldName;
  }-*/;
  
  private static native void setFieldName(org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint::fieldName = value;
  }-*/;
  
  private static native java.lang.String getFieldType(org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint::fieldType;
  }-*/;
  
  private static native void setFieldType(org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint::fieldType = value;
  }-*/;
  
  private static native java.lang.String getId(org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint::id;
  }-*/;
  
  private static native void setId(org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint::id = value;
  }-*/;
  
  private static native org.drools.ide.common.client.modeldriven.brl.FieldConstraint getParent(org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint::parent;
  }-*/;
  
  private static native void setParent(org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint instance, org.drools.ide.common.client.modeldriven.brl.FieldConstraint value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint::parent = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint instance) throws SerializationException {
    instance.connectives = (org.drools.ide.common.client.modeldriven.brl.ConnectiveConstraint[]) streamReader.readObject();
    setFieldBinding(instance, streamReader.readString());
    setFieldName(instance, streamReader.readString());
    setFieldType(instance, streamReader.readString());
    setId(instance, streamReader.readString());
    setParent(instance, (org.drools.ide.common.client.modeldriven.brl.FieldConstraint) streamReader.readObject());
    
    org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint instance) throws SerializationException {
    streamWriter.writeObject(instance.connectives);
    streamWriter.writeString(getFieldBinding(instance));
    streamWriter.writeString(getFieldName(instance));
    streamWriter.writeString(getFieldType(instance));
    streamWriter.writeString(getId(instance));
    streamWriter.writeObject(getParent(instance));
    
    org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint)object);
  }
  
}
