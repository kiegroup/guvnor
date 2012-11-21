package org.drools.ide.common.client.testscenarios.fixtures;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class FieldData_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getName(org.drools.ide.common.client.testscenarios.fixtures.FieldData instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.FieldData::name;
  }-*/;
  
  private static native void setName(org.drools.ide.common.client.testscenarios.fixtures.FieldData instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.FieldData::name = value;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native long getNature(org.drools.ide.common.client.testscenarios.fixtures.FieldData instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.FieldData::nature;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native void setNature(org.drools.ide.common.client.testscenarios.fixtures.FieldData instance, long value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.FieldData::nature = value;
  }-*/;
  
  private static native java.lang.String getValue(org.drools.ide.common.client.testscenarios.fixtures.FieldData instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.FieldData::value;
  }-*/;
  
  private static native void setValue(org.drools.ide.common.client.testscenarios.fixtures.FieldData instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.FieldData::value = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.testscenarios.fixtures.FieldData instance) throws SerializationException {
    instance.collectionFieldList = (java.util.List) streamReader.readObject();
    instance.collectionType = streamReader.readString();
    setName(instance, streamReader.readString());
    setNature(instance, streamReader.readLong());
    setValue(instance, streamReader.readString());
    
  }
  
  public static org.drools.ide.common.client.testscenarios.fixtures.FieldData instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.testscenarios.fixtures.FieldData();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.testscenarios.fixtures.FieldData instance) throws SerializationException {
    streamWriter.writeObject(instance.collectionFieldList);
    streamWriter.writeString(instance.collectionType);
    streamWriter.writeString(getName(instance));
    streamWriter.writeLong(getNature(instance));
    streamWriter.writeString(getValue(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.testscenarios.fixtures.FieldData_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.testscenarios.fixtures.FieldData_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.testscenarios.fixtures.FieldData)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.testscenarios.fixtures.FieldData_FieldSerializer.serialize(writer, (org.drools.ide.common.client.testscenarios.fixtures.FieldData)object);
  }
  
}
