package org.drools.ide.common.client.modeldriven.dt52;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ConditionCol52_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getBinding(org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ConditionCol52::binding;
  }-*/;
  
  private static native void setBinding(org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ConditionCol52::binding = value;
  }-*/;
  
  private static native int getConstraintValueType(org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ConditionCol52::constraintValueType;
  }-*/;
  
  private static native void setConstraintValueType(org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance, int value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ConditionCol52::constraintValueType = value;
  }-*/;
  
  private static native java.lang.String getFactField(org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ConditionCol52::factField;
  }-*/;
  
  private static native void setFactField(org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ConditionCol52::factField = value;
  }-*/;
  
  private static native java.lang.String getFieldType(org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ConditionCol52::fieldType;
  }-*/;
  
  private static native void setFieldType(org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ConditionCol52::fieldType = value;
  }-*/;
  
  private static native java.lang.String getOperator(org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ConditionCol52::operator;
  }-*/;
  
  private static native void setOperator(org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ConditionCol52::operator = value;
  }-*/;
  
  private static native java.util.Map getParameters(org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ConditionCol52::parameters;
  }-*/;
  
  private static native void setParameters(org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ConditionCol52::parameters = value;
  }-*/;
  
  private static native java.lang.String getValueList(org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ConditionCol52::valueList;
  }-*/;
  
  private static native void setValueList(org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ConditionCol52::valueList = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance) throws SerializationException {
    setBinding(instance, streamReader.readString());
    setConstraintValueType(instance, streamReader.readInt());
    setFactField(instance, streamReader.readString());
    setFieldType(instance, streamReader.readString());
    setOperator(instance, streamReader.readString());
    setParameters(instance, (java.util.Map) streamReader.readObject());
    setValueList(instance, streamReader.readString());
    
    org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt52.ConditionCol52();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt52.ConditionCol52 instance) throws SerializationException {
    streamWriter.writeString(getBinding(instance));
    streamWriter.writeInt(getConstraintValueType(instance));
    streamWriter.writeString(getFactField(instance));
    streamWriter.writeString(getFieldType(instance));
    streamWriter.writeString(getOperator(instance));
    streamWriter.writeObject(getParameters(instance));
    streamWriter.writeString(getValueList(instance));
    
    org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt52.ConditionCol52_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.ConditionCol52_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt52.ConditionCol52)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.ConditionCol52_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt52.ConditionCol52)object);
  }
  
}
