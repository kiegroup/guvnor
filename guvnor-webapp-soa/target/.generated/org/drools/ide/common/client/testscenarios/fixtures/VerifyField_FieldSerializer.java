package org.drools.ide.common.client.testscenarios.fixtures;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class VerifyField_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getActualResult(org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.VerifyField::actualResult;
  }-*/;
  
  private static native void setActualResult(org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.VerifyField::actualResult = value;
  }-*/;
  
  private static native java.lang.String getExpected(org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.VerifyField::expected;
  }-*/;
  
  private static native void setExpected(org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.VerifyField::expected = value;
  }-*/;
  
  private static native java.lang.String getExplanation(org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.VerifyField::explanation;
  }-*/;
  
  private static native void setExplanation(org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.VerifyField::explanation = value;
  }-*/;
  
  private static native java.lang.String getFieldName(org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.VerifyField::fieldName;
  }-*/;
  
  private static native void setFieldName(org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.VerifyField::fieldName = value;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native long getNature(org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.VerifyField::nature;
  }-*/;
  
  @com.google.gwt.core.client.UnsafeNativeLong
  private static native void setNature(org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance, long value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.VerifyField::nature = value;
  }-*/;
  
  private static native java.lang.String getOperator(org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.VerifyField::operator;
  }-*/;
  
  private static native void setOperator(org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.VerifyField::operator = value;
  }-*/;
  
  private static native java.lang.Boolean getSuccessResult(org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance) /*-{
    return instance.@org.drools.ide.common.client.testscenarios.fixtures.VerifyField::successResult;
  }-*/;
  
  private static native void setSuccessResult(org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance, java.lang.Boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.testscenarios.fixtures.VerifyField::successResult = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance) throws SerializationException {
    setActualResult(instance, streamReader.readString());
    setExpected(instance, streamReader.readString());
    setExplanation(instance, streamReader.readString());
    setFieldName(instance, streamReader.readString());
    setNature(instance, streamReader.readLong());
    setOperator(instance, streamReader.readString());
    setSuccessResult(instance, (java.lang.Boolean) streamReader.readObject());
    
  }
  
  public static org.drools.ide.common.client.testscenarios.fixtures.VerifyField instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.testscenarios.fixtures.VerifyField();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.testscenarios.fixtures.VerifyField instance) throws SerializationException {
    streamWriter.writeString(getActualResult(instance));
    streamWriter.writeString(getExpected(instance));
    streamWriter.writeString(getExplanation(instance));
    streamWriter.writeString(getFieldName(instance));
    streamWriter.writeLong(getNature(instance));
    streamWriter.writeString(getOperator(instance));
    streamWriter.writeObject(getSuccessResult(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.testscenarios.fixtures.VerifyField_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.testscenarios.fixtures.VerifyField_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.testscenarios.fixtures.VerifyField)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.testscenarios.fixtures.VerifyField_FieldSerializer.serialize(writer, (org.drools.ide.common.client.testscenarios.fixtures.VerifyField)object);
  }
  
}
