package org.drools.ide.common.client.modeldriven.brl.templates;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class TemplateModel_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native long getIdCol(org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel::idCol;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native void setIdCol(org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel instance, long value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel::idCol = value;
  }-*/;
  
  private static native int getRowsCount(org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel::rowsCount;
  }-*/;
  
  private static native void setRowsCount(org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel instance, int value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel::rowsCount = value;
  }-*/;
  
  private static native java.util.Map getTable(org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel::table;
  }-*/;
  
  private static native void setTable(org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel::table = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel instance) throws SerializationException {
    setIdCol(instance, streamReader.readLong());
    setRowsCount(instance, streamReader.readInt());
    setTable(instance, (java.util.Map) streamReader.readObject());
    
    org.drools.ide.common.client.modeldriven.brl.RuleModel_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel instance) throws SerializationException {
    streamWriter.writeLong(getIdCol(instance));
    streamWriter.writeInt(getRowsCount(instance));
    streamWriter.writeObject(getTable(instance));
    
    org.drools.ide.common.client.modeldriven.brl.RuleModel_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel)object);
  }
  
}
