package org.drools.ide.common.client.modeldriven.testing;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class Fact_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getFieldData(org.drools.ide.common.client.modeldriven.testing.Fact instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.Fact::fieldData;
  }-*/;
  
  private static native void setFieldData(org.drools.ide.common.client.modeldriven.testing.Fact instance, java.util.List value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.Fact::fieldData = value;
  }-*/;
  
  private static native java.lang.String getType(org.drools.ide.common.client.modeldriven.testing.Fact instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.Fact::type;
  }-*/;
  
  private static native void setType(org.drools.ide.common.client.modeldriven.testing.Fact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.Fact::type = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.testing.Fact instance) throws SerializationException {
    setFieldData(instance, (java.util.List) streamReader.readObject());
    setType(instance, streamReader.readString());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.testing.Fact instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.testing.Fact();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.testing.Fact instance) throws SerializationException {
    streamWriter.writeObject(getFieldData(instance));
    streamWriter.writeString(getType(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.testing.Fact_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.testing.Fact_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.testing.Fact)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.testing.Fact_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.testing.Fact)object);
  }
  
}
