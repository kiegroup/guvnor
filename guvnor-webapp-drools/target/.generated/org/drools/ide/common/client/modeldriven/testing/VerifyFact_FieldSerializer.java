package org.drools.ide.common.client.modeldriven.testing;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class VerifyFact_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getDescription(org.drools.ide.common.client.modeldriven.testing.VerifyFact instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyFact::description;
  }-*/;
  
  private static native void setDescription(org.drools.ide.common.client.modeldriven.testing.VerifyFact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyFact::description = value;
  }-*/;
  
  private static native java.util.List getFieldValues(org.drools.ide.common.client.modeldriven.testing.VerifyFact instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyFact::fieldValues;
  }-*/;
  
  private static native void setFieldValues(org.drools.ide.common.client.modeldriven.testing.VerifyFact instance, java.util.List value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyFact::fieldValues = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.ide.common.client.modeldriven.testing.VerifyFact instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyFact::name;
  }-*/;
  
  private static native void setName(org.drools.ide.common.client.modeldriven.testing.VerifyFact instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyFact::name = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.testing.VerifyFact instance) throws SerializationException {
    instance.anonymous = streamReader.readBoolean();
    setDescription(instance, streamReader.readString());
    setFieldValues(instance, (java.util.List) streamReader.readObject());
    setName(instance, streamReader.readString());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.testing.VerifyFact instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.testing.VerifyFact();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.testing.VerifyFact instance) throws SerializationException {
    streamWriter.writeBoolean(instance.anonymous);
    streamWriter.writeString(getDescription(instance));
    streamWriter.writeObject(getFieldValues(instance));
    streamWriter.writeString(getName(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.testing.VerifyFact_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.testing.VerifyFact_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.testing.VerifyFact)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.testing.VerifyFact_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.testing.VerifyFact)object);
  }
  
}
