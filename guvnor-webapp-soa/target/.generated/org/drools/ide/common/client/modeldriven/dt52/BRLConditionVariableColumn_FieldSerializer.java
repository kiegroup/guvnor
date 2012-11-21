package org.drools.ide.common.client.modeldriven.dt52;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class BRLConditionVariableColumn_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getFactType(org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn::factType;
  }-*/;
  
  private static native void setFactType(org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn::factType = value;
  }-*/;
  
  private static native java.lang.String getVarName(org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn::varName;
  }-*/;
  
  private static native void setVarName(org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn::varName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn instance) throws SerializationException {
    setFactType(instance, streamReader.readString());
    setVarName(instance, streamReader.readString());
    
    org.drools.ide.common.client.modeldriven.dt52.ConditionCol52_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn instance) throws SerializationException {
    streamWriter.writeString(getFactType(instance));
    streamWriter.writeString(getVarName(instance));
    
    org.drools.ide.common.client.modeldriven.dt52.ConditionCol52_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt52.BRLConditionVariableColumn)object);
  }
  
}
