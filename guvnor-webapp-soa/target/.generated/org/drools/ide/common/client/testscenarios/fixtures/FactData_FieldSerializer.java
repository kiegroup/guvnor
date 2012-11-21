package org.drools.ide.common.client.testscenarios.fixtures;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class FactData_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getFieldData(org.drools.ide.common.client.testscenarios.fixtures.FactData instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.FactData::fieldData;
  }-*/;
  
  private static native void setFieldData(org.drools.ide.common.client.testscenarios.fixtures.FactData instance, java.util.List value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.FactData::fieldData = value;
  }-*/;
  
  private static native boolean getIsModify(org.drools.ide.common.client.testscenarios.fixtures.FactData instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.FactData::isModify;
  }-*/;
  
  private static native void setIsModify(org.drools.ide.common.client.testscenarios.fixtures.FactData instance, boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.FactData::isModify = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.ide.common.client.testscenarios.fixtures.FactData instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.FactData::name;
  }-*/;
  
  private static native void setName(org.drools.ide.common.client.testscenarios.fixtures.FactData instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.FactData::name = value;
  }-*/;
  
  private static native java.lang.String getType(org.drools.ide.common.client.testscenarios.fixtures.FactData instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.FactData::type;
  }-*/;
  
  private static native void setType(org.drools.ide.common.client.testscenarios.fixtures.FactData instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.FactData::type = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.testscenarios.fixtures.FactData instance) throws SerializationException {
    setFieldData(instance, (java.util.List) streamReader.readObject());
    setIsModify(instance, streamReader.readBoolean());
    setName(instance, streamReader.readString());
    setType(instance, streamReader.readString());
    
  }
  
  public static org.drools.ide.common.client.testscenarios.fixtures.FactData instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.testscenarios.fixtures.FactData();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.testscenarios.fixtures.FactData instance) throws SerializationException {
    streamWriter.writeObject(getFieldData(instance));
    streamWriter.writeBoolean(getIsModify(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeString(getType(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.testscenarios.fixtures.FactData_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.testscenarios.fixtures.FactData_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.testscenarios.fixtures.FactData)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.testscenarios.fixtures.FactData_FieldSerializer.serialize(writer, (org.drools.ide.common.client.testscenarios.fixtures.FactData)object);
  }
  
}
