package org.drools.ide.common.client.modeldriven.testing;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class FactData_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native boolean getIsModify(org.drools.ide.common.client.modeldriven.testing.FactData instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.FactData::isModify;
  }-*/;
  
  private static native void setIsModify(org.drools.ide.common.client.modeldriven.testing.FactData instance, boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.FactData::isModify = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.ide.common.client.modeldriven.testing.FactData instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.FactData::name;
  }-*/;
  
  private static native void setName(org.drools.ide.common.client.modeldriven.testing.FactData instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.FactData::name = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.testing.FactData instance) throws SerializationException {
    setIsModify(instance, streamReader.readBoolean());
    setName(instance, streamReader.readString());
    
    org.drools.ide.common.client.modeldriven.testing.Fact_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.testing.FactData instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.testing.FactData();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.testing.FactData instance) throws SerializationException {
    streamWriter.writeBoolean(getIsModify(instance));
    streamWriter.writeString(getName(instance));
    
    org.drools.ide.common.client.modeldriven.testing.Fact_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.testing.FactData_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.testing.FactData_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.testing.FactData)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.testing.FactData_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.testing.FactData)object);
  }
  
}
