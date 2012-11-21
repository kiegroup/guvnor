package org.drools.ide.common.client.modeldriven.dt52;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class BRLActionColumn_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getChildColumns(org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn::childColumns;
  }-*/;
  
  private static native void setChildColumns(org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn instance, java.util.List value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn::childColumns = value;
  }-*/;
  
  private static native java.util.List getDefinition(org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn::definition;
  }-*/;
  
  private static native void setDefinition(org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn instance, java.util.List value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn::definition = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn instance) throws SerializationException {
    setChildColumns(instance, (java.util.List) streamReader.readObject());
    setDefinition(instance, (java.util.List) streamReader.readObject());
    
    org.drools.ide.common.client.modeldriven.dt52.ActionCol52_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn instance) throws SerializationException {
    streamWriter.writeObject(getChildColumns(instance));
    streamWriter.writeObject(getDefinition(instance));
    
    org.drools.ide.common.client.modeldriven.dt52.ActionCol52_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt52.BRLActionColumn)object);
  }
  
}
