package org.drools.ide.common.client.modeldriven.dt52;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class GuidedDecisionTable52_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getActionCols(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::actionCols;
  }-*/;
  
  private static native void setActionCols(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance, java.util.List value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::actionCols = value;
  }-*/;
  
  private static native java.util.List getAttributeCols(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::attributeCols;
  }-*/;
  
  private static native void setAttributeCols(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance, java.util.List value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::attributeCols = value;
  }-*/;
  
  private static native java.util.List getConditionPatterns(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::conditionPatterns;
  }-*/;
  
  private static native void setConditionPatterns(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance, java.util.List value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::conditionPatterns = value;
  }-*/;
  
  private static native java.util.List getData(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::data;
  }-*/;
  
  private static native void setData(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance, java.util.List value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::data = value;
  }-*/;
  
  private static native org.drools.ide.common.client.modeldriven.dt52.DescriptionCol52 getDescriptionCol(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::descriptionCol;
  }-*/;
  
  private static native void setDescriptionCol(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance, org.drools.ide.common.client.modeldriven.dt52.DescriptionCol52 value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::descriptionCol = value;
  }-*/;
  
  private static native java.util.List getMetadataCols(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::metadataCols;
  }-*/;
  
  private static native void setMetadataCols(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance, java.util.List value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::metadataCols = value;
  }-*/;
  
  private static native java.lang.String getParentName(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::parentName;
  }-*/;
  
  private static native void setParentName(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::parentName = value;
  }-*/;
  
  private static native org.drools.ide.common.client.modeldriven.dt52.RowNumberCol52 getRowNumberCol(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::rowNumberCol;
  }-*/;
  
  private static native void setRowNumberCol(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance, org.drools.ide.common.client.modeldriven.dt52.RowNumberCol52 value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::rowNumberCol = value;
  }-*/;
  
  private static native org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat getTableFormat(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::tableFormat;
  }-*/;
  
  private static native void setTableFormat(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance, org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::tableFormat = value;
  }-*/;
  
  private static native java.lang.String getTableName(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::tableName;
  }-*/;
  
  private static native void setTableName(org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52::tableName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance) throws SerializationException {
    setActionCols(instance, (java.util.List) streamReader.readObject());
    setAttributeCols(instance, (java.util.List) streamReader.readObject());
    setConditionPatterns(instance, (java.util.List) streamReader.readObject());
    setData(instance, (java.util.List) streamReader.readObject());
    setDescriptionCol(instance, (org.drools.ide.common.client.modeldriven.dt52.DescriptionCol52) streamReader.readObject());
    setMetadataCols(instance, (java.util.List) streamReader.readObject());
    setParentName(instance, streamReader.readString());
    setRowNumberCol(instance, (org.drools.ide.common.client.modeldriven.dt52.RowNumberCol52) streamReader.readObject());
    setTableFormat(instance, (org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat) streamReader.readObject());
    setTableName(instance, streamReader.readString());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52 instance) throws SerializationException {
    streamWriter.writeObject(getActionCols(instance));
    streamWriter.writeObject(getAttributeCols(instance));
    streamWriter.writeObject(getConditionPatterns(instance));
    streamWriter.writeObject(getData(instance));
    streamWriter.writeObject(getDescriptionCol(instance));
    streamWriter.writeObject(getMetadataCols(instance));
    streamWriter.writeString(getParentName(instance));
    streamWriter.writeObject(getRowNumberCol(instance));
    streamWriter.writeObject(getTableFormat(instance));
    streamWriter.writeString(getTableName(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52)object);
  }
  
}
