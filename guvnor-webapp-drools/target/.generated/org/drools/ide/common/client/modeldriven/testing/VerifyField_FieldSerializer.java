package org.drools.ide.common.client.modeldriven.testing;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class VerifyField_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getActualResult(org.drools.ide.common.client.modeldriven.testing.VerifyField instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyField::actualResult;
  }-*/;
  
  private static native void setActualResult(org.drools.ide.common.client.modeldriven.testing.VerifyField instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyField::actualResult = value;
  }-*/;
  
  private static native java.lang.String getExpected(org.drools.ide.common.client.modeldriven.testing.VerifyField instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyField::expected;
  }-*/;
  
  private static native void setExpected(org.drools.ide.common.client.modeldriven.testing.VerifyField instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyField::expected = value;
  }-*/;
  
  private static native java.lang.String getExplanation(org.drools.ide.common.client.modeldriven.testing.VerifyField instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyField::explanation;
  }-*/;
  
  private static native void setExplanation(org.drools.ide.common.client.modeldriven.testing.VerifyField instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyField::explanation = value;
  }-*/;
  
  private static native java.lang.String getFieldName(org.drools.ide.common.client.modeldriven.testing.VerifyField instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyField::fieldName;
  }-*/;
  
  private static native void setFieldName(org.drools.ide.common.client.modeldriven.testing.VerifyField instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyField::fieldName = value;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native long getNature(org.drools.ide.common.client.modeldriven.testing.VerifyField instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyField::nature;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native void setNature(org.drools.ide.common.client.modeldriven.testing.VerifyField instance, long value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyField::nature = value;
  }-*/;
  
  private static native java.lang.String getOperator(org.drools.ide.common.client.modeldriven.testing.VerifyField instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyField::operator;
  }-*/;
  
  private static native void setOperator(org.drools.ide.common.client.modeldriven.testing.VerifyField instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyField::operator = value;
  }-*/;
  
  private static native java.lang.Boolean getSuccessResult(org.drools.ide.common.client.modeldriven.testing.VerifyField instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.VerifyField::successResult;
  }-*/;
  
  private static native void setSuccessResult(org.drools.ide.common.client.modeldriven.testing.VerifyField instance, java.lang.Boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.VerifyField::successResult = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.testing.VerifyField instance) throws SerializationException {
    setActualResult(instance, streamReader.readString());
    setExpected(instance, streamReader.readString());
    setExplanation(instance, streamReader.readString());
    setFieldName(instance, streamReader.readString());
    setNature(instance, streamReader.readLong());
    setOperator(instance, streamReader.readString());
    setSuccessResult(instance, (java.lang.Boolean) streamReader.readObject());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.testing.VerifyField instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.testing.VerifyField();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.testing.VerifyField instance) throws SerializationException {
    streamWriter.writeString(getActualResult(instance));
    streamWriter.writeString(getExpected(instance));
    streamWriter.writeString(getExplanation(instance));
    streamWriter.writeString(getFieldName(instance));
    streamWriter.writeLong(getNature(instance));
    streamWriter.writeString(getOperator(instance));
    streamWriter.writeObject(getSuccessResult(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.testing.VerifyField_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.testing.VerifyField_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.testing.VerifyField)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.testing.VerifyField_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.testing.VerifyField)object);
  }
  
}
