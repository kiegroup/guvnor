package org.drools.ide.common.client.modeldriven;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ModelField_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getClassName(org.drools.ide.common.client.modeldriven.ModelField instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.ModelField::className;
  }-*/;
  
  private static native void setClassName(org.drools.ide.common.client.modeldriven.ModelField instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.ModelField::className = value;
  }-*/;
  
  private static native org.drools.ide.common.client.modeldriven.ModelField.FIELD_CLASS_TYPE getClassType(org.drools.ide.common.client.modeldriven.ModelField instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.ModelField::classType;
  }-*/;
  
  private static native void setClassType(org.drools.ide.common.client.modeldriven.ModelField instance, org.drools.ide.common.client.modeldriven.ModelField.FIELD_CLASS_TYPE value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.ModelField::classType = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.ide.common.client.modeldriven.ModelField instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.ModelField::name;
  }-*/;
  
  private static native void setName(org.drools.ide.common.client.modeldriven.ModelField instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.ModelField::name = value;
  }-*/;
  
  private static native java.lang.String getType(org.drools.ide.common.client.modeldriven.ModelField instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.ModelField::type;
  }-*/;
  
  private static native void setType(org.drools.ide.common.client.modeldriven.ModelField instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.ModelField::type = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.ModelField instance) throws SerializationException {
    setClassName(instance, streamReader.readString());
    setClassType(instance, (org.drools.ide.common.client.modeldriven.ModelField.FIELD_CLASS_TYPE) streamReader.readObject());
    setName(instance, streamReader.readString());
    setType(instance, streamReader.readString());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.ModelField instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.ModelField();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.ModelField instance) throws SerializationException {
    streamWriter.writeString(getClassName(instance));
    streamWriter.writeObject(getClassType(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeString(getType(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.ModelField_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.ModelField_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.ModelField)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.ModelField_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.ModelField)object);
  }
  
}
