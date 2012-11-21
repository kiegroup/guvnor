package org.drools.ide.common.client.modeldriven.dt;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class GuidedDecisionTable_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getMetadataCols(org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable::metadataCols;
  }-*/;
  
  private static native void setMetadataCols(org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable instance, java.util.List value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable::metadataCols = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable instance) throws SerializationException {
    instance.actionCols = (java.util.List) streamReader.readObject();
    instance.attributeCols = (java.util.List) streamReader.readObject();
    instance.conditionCols = (java.util.List) streamReader.readObject();
    instance.data = (java.lang.String[][]) streamReader.readObject();
    instance.descriptionWidth = streamReader.readInt();
    instance.groupField = streamReader.readString();
    setMetadataCols(instance, (java.util.List) streamReader.readObject());
    instance.parentName = streamReader.readString();
    instance.tableName = streamReader.readString();
    
  }
  
  public static org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable instance) throws SerializationException {
    streamWriter.writeObject(instance.actionCols);
    streamWriter.writeObject(instance.attributeCols);
    streamWriter.writeObject(instance.conditionCols);
    streamWriter.writeObject(instance.data);
    streamWriter.writeInt(instance.descriptionWidth);
    streamWriter.writeString(instance.groupField);
    streamWriter.writeObject(getMetadataCols(instance));
    streamWriter.writeString(instance.parentName);
    streamWriter.writeString(instance.tableName);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable)object);
  }
  
}
