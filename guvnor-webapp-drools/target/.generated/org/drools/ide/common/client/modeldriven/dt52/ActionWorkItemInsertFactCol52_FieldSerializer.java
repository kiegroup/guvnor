package org.drools.ide.common.client.modeldriven.dt52;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActionWorkItemInsertFactCol52_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getParameterClassName(org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52::parameterClassName;
  }-*/;
  
  private static native void setParameterClassName(org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52::parameterClassName = value;
  }-*/;
  
  private static native java.lang.String getWorkItemName(org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52::workItemName;
  }-*/;
  
  private static native void setWorkItemName(org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52::workItemName = value;
  }-*/;
  
  private static native java.lang.String getWorkItemResultParameterName(org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52::workItemResultParameterName;
  }-*/;
  
  private static native void setWorkItemResultParameterName(org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52::workItemResultParameterName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52 instance) throws SerializationException {
    setParameterClassName(instance, streamReader.readString());
    setWorkItemName(instance, streamReader.readString());
    setWorkItemResultParameterName(instance, streamReader.readString());
    
    org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52 instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52 instance) throws SerializationException {
    streamWriter.writeString(getParameterClassName(instance));
    streamWriter.writeString(getWorkItemName(instance));
    streamWriter.writeString(getWorkItemResultParameterName(instance));
    
    org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52)object);
  }
  
}
