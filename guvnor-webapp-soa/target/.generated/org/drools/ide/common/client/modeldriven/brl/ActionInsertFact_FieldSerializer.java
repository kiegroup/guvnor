package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActionInsertFact_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getBoundName(org.drools.ide.common.client.modeldriven.brl.ActionInsertFact instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ActionInsertFact::boundName;
  }-*/;
  
  private static native void setBoundName(org.drools.ide.common.client.modeldriven.brl.ActionInsertFact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ActionInsertFact::boundName = value;
  }-*/;
  
  private static native boolean getIsBound(org.drools.ide.common.client.modeldriven.brl.ActionInsertFact instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.ActionInsertFact::isBound;
  }-*/;
  
  private static native void setIsBound(org.drools.ide.common.client.modeldriven.brl.ActionInsertFact instance, boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.ActionInsertFact::isBound = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ActionInsertFact instance) throws SerializationException {
    setBoundName(instance, streamReader.readString());
    instance.factType = streamReader.readString();
    setIsBound(instance, streamReader.readBoolean());
    
    org.drools.ide.common.client.modeldriven.brl.ActionFieldList_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.ActionInsertFact instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.ActionInsertFact();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ActionInsertFact instance) throws SerializationException {
    streamWriter.writeString(getBoundName(instance));
    streamWriter.writeString(instance.factType);
    streamWriter.writeBoolean(getIsBound(instance));
    
    org.drools.ide.common.client.modeldriven.brl.ActionFieldList_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.ActionInsertFact_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ActionInsertFact_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.ActionInsertFact)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ActionInsertFact_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.ActionInsertFact)object);
  }
  
}
