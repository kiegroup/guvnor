package org.drools.ide.common.client.modeldriven.dt52;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActionWorkItemCol52_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.ide.common.shared.workitems.PortableWorkDefinition getWorkItemDefinition(org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52::workItemDefinition;
  }-*/;
  
  private static native void setWorkItemDefinition(org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52 instance, org.drools.ide.common.shared.workitems.PortableWorkDefinition value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52::workItemDefinition = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52 instance) throws SerializationException {
    setWorkItemDefinition(instance, (org.drools.ide.common.shared.workitems.PortableWorkDefinition) streamReader.readObject());
    
    org.drools.ide.common.client.modeldriven.dt52.ActionCol52_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52 instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52 instance) throws SerializationException {
    streamWriter.writeObject(getWorkItemDefinition(instance));
    
    org.drools.ide.common.client.modeldriven.dt52.ActionCol52_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52)object);
  }
  
}
