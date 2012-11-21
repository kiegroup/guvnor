package org.drools.ide.common.client.modeldriven.dt52;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class BRLActionVariableColumn_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getFactField(org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn::factField;
  }-*/;
  
  private static native void setFactField(org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn::factField = value;
  }-*/;
  
  private static native java.lang.String getFactType(org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn::factType;
  }-*/;
  
  private static native void setFactType(org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn::factType = value;
  }-*/;
  
  private static native java.lang.String getFieldType(org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn::fieldType;
  }-*/;
  
  private static native void setFieldType(org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn::fieldType = value;
  }-*/;
  
  private static native java.lang.String getVarName(org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn::varName;
  }-*/;
  
  private static native void setVarName(org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn::varName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn instance) throws SerializationException {
    setFactField(instance, streamReader.readString());
    setFactType(instance, streamReader.readString());
    setFieldType(instance, streamReader.readString());
    setVarName(instance, streamReader.readString());
    
    org.drools.ide.common.client.modeldriven.dt52.ActionCol52_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn instance) throws SerializationException {
    streamWriter.writeString(getFactField(instance));
    streamWriter.writeString(getFactType(instance));
    streamWriter.writeString(getFieldType(instance));
    streamWriter.writeString(getVarName(instance));
    
    org.drools.ide.common.client.modeldriven.dt52.ActionCol52_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt52.BRLActionVariableColumn)object);
  }
  
}
