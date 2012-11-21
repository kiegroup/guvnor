package org.drools.ide.common.client.modeldriven.dt52;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class LimitedEntryConditionCol52_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 getValue(org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52::value;
  }-*/;
  
  private static native void setValue(org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52 instance, org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52::value = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52 instance) throws SerializationException {
    setValue(instance, (org.drools.ide.common.client.modeldriven.dt52.DTCellValue52) streamReader.readObject());
    
    org.drools.ide.common.client.modeldriven.dt52.ConditionCol52_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52 instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52 instance) throws SerializationException {
    streamWriter.writeObject(getValue(instance));
    
    org.drools.ide.common.client.modeldriven.dt52.ConditionCol52_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt52.LimitedEntryConditionCol52)object);
  }
  
}
