package org.drools.ide.common.client.modeldriven.testing;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class FactAssignmentField_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.ide.common.client.modeldriven.testing.Fact getFact(org.drools.ide.common.client.modeldriven.testing.FactAssignmentField instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.FactAssignmentField::fact;
  }-*/;
  
  private static native void setFact(org.drools.ide.common.client.modeldriven.testing.FactAssignmentField instance, org.drools.ide.common.client.modeldriven.testing.Fact value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.FactAssignmentField::fact = value;
  }-*/;
  
  private static native java.lang.String getFieldName(org.drools.ide.common.client.modeldriven.testing.FactAssignmentField instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.FactAssignmentField::fieldName;
  }-*/;
  
  private static native void setFieldName(org.drools.ide.common.client.modeldriven.testing.FactAssignmentField instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.FactAssignmentField::fieldName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.testing.FactAssignmentField instance) throws SerializationException {
    setFact(instance, (org.drools.ide.common.client.modeldriven.testing.Fact) streamReader.readObject());
    setFieldName(instance, streamReader.readString());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.testing.FactAssignmentField instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.testing.FactAssignmentField();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.testing.FactAssignmentField instance) throws SerializationException {
    streamWriter.writeObject(getFact(instance));
    streamWriter.writeString(getFieldName(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.testing.FactAssignmentField_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.testing.FactAssignmentField_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.testing.FactAssignmentField)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.testing.FactAssignmentField_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.testing.FactAssignmentField)object);
  }
  
}
